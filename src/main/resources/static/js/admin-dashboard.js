let allAppointments = [];

document.addEventListener("DOMContentLoaded", async () => {
    requireAuth("ADMIN");

    const appointmentStatusFilter = document.getElementById("appointmentStatusFilter");

    if (appointmentStatusFilter) {
        appointmentStatusFilter.addEventListener("change", renderAppointments);
    }

    await loadAdminProfile();
    await loadUsers();
    await loadStats();
    await loadPendingDoctors();
    await loadAllAppointments();
});

async function loadAdminProfile() {
    const container = document.getElementById("adminData");

    try {
        const token = getToken();

        const response = await fetch(`${API_BASE_URL}/admin/me`, {
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error("Błąd pobierania danych administratora");
        }

        const admin = await response.json();

        container.innerHTML = `
            <p><strong>Email:</strong> ${admin.email}</p>
            <p><strong>Rola:</strong> ${translateRole(admin.role)}</p>
        `;
    } catch (error) {
        container.innerHTML = "Nie udało się pobrać danych administratora.";
    }
}

async function loadUsers() {
    const container = document.getElementById("usersContainer");

    try {
        const token = getToken();

        const response = await fetch(`${API_BASE_URL}/admin/users`, {
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error("Błąd pobierania użytkowników");
        }

        const users = await response.json();

        if (!users.length) {
            container.innerHTML = `
                <div class="empty-state">
                    <p>Brak użytkowników.</p>
                </div>
            `;
            return;
        }

        container.innerHTML = users.map(user => `
            <div class="slot-card">
                <p><strong>ID:</strong> ${user.id}</p>
                <p><strong>Email:</strong> ${user.email}</p>
                <p><strong>Rola:</strong> ${translateRole(user.role)}</p>
                <p>
                    <strong>Status:</strong>
                    <span class="${getStatusClass(user.status)}">
                        ${translateStatus(user.status)}
                    </span>
                </p>

                ${user.role !== "ADMIN"
                    ? `<button class="delete-btn" onclick="deleteUser(${user.id})">Usuń użytkownika</button>`
                    : ""
                }
            </div>
        `).join("");

    } catch (error) {
        container.innerHTML = `
            <div class="empty-state error-state">
                <p>Nie udało się pobrać użytkowników.</p>
            </div>
        `;
    }
}

async function loadAllAppointments() {
    const container = document.getElementById("appointmentsContainer");

    try {
        const token = getToken();

        const response = await fetch(`${API_BASE_URL}/admin/appointments`, {
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error("Błąd pobierania wizyt");
        }

        allAppointments = await response.json();

        renderAppointments();

    } catch (error) {
        container.innerHTML = `
            <div class="empty-state error-state">
                <p>Nie udało się pobrać wizyt.</p>
            </div>
        `;
    }
}

function renderAppointments() {
    const container = document.getElementById("appointmentsContainer");
    const filterElement = document.getElementById("appointmentStatusFilter");
    const filter = filterElement ? filterElement.value : "ALL";

    const filteredAppointments = allAppointments.filter(appointment => {
        if (filter === "ALL") {
            return true;
        }

        return appointment.status === filter;
    });

    if (!filteredAppointments.length) {
        container.innerHTML = `
            <div class="empty-state">
                <p>Brak wizyt dla wybranego filtra.</p>
            </div>
        `;
        return;
    }

    container.innerHTML = filteredAppointments.map(appointment => `
        <div class="slot-card">
            <p><strong>ID:</strong> ${appointment.id}</p>
            <p><strong>Lekarz:</strong> ${appointment.doctorName}</p>
            <p><strong>Pacjent:</strong> ${appointment.patientName}</p>
            <p><strong>Start:</strong> ${formatDate(appointment.startTime)}</p>
            <p><strong>Koniec:</strong> ${formatDate(appointment.endTime)}</p>
            <p><strong>Powód:</strong> ${appointment.reason || "Brak powodu"}</p>

            <p>
                <strong>Status:</strong>
                <span class="${getStatusClass(appointment.status)}">
                    ${translateStatus(appointment.status)}
                </span>
            </p>

            ${renderAppointmentAction(appointment)}
        </div>
    `).join("");
}

function renderAppointmentAction(appointment) {
    if (appointment.status === "BOOKED") {
        return `
            <button class="delete-btn" onclick="deleteAppointment(${appointment.id})">
                Anuluj wizytę
            </button>
        `;
    }

    if (appointment.status === "COMPLETED") {
        return `<p class="completed-info">Wizyta odbyła się</p>`;
    }

    if (appointment.status === "CANCELLED") {
        return `<p class="cancelled-info">Wizyta anulowana</p>`;
    }

    return "";
}

async function deleteUser(userId) {
    if (!confirm("Na pewno usunąć tego użytkownika?")) {
        return;
    }

    try {
        const token = getToken();

        const response = await fetch(`${API_BASE_URL}/admin/users/${userId}`, {
            method: "DELETE",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (!response.ok) {
            let errorMessage = "Nie udało się usunąć użytkownika.";

            try {
                const errorData = await response.json();
                errorMessage = errorData.message || errorMessage;
            } catch (e) {}

            throw new Error(errorMessage);
        }

        alert("Użytkownik został usunięty.");

        await loadUsers();
        await loadStats();

    } catch (error) {
        alert(error.message);
    }
}

async function deleteAppointment(appointmentId) {
    if (!confirm("Na pewno anulować wizytę?")) {
        return;
    }

    try {
        const token = getToken();

        const response = await fetch(`${API_BASE_URL}/admin/appointments/${appointmentId}`, {
            method: "DELETE",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (!response.ok) {
            let errorMessage = "Nie udało się anulować wizyty.";

            try {
                const errorData = await response.json();
                errorMessage = translateBackendError(errorData.message) || errorMessage;
            } catch (e) {}

            throw new Error(errorMessage);
        }

        alert("Wizyta została anulowana.");

        await loadAllAppointments();
        await loadStats();

    } catch (error) {
        alert(error.message);
    }
}

async function loadStats() {
    const container = document.getElementById("statsContainer");

    try {
        const token = getToken();

        const response = await fetch(`${API_BASE_URL}/admin/stats`, {
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error("Błąd pobierania statystyk");
        }

        const stats = await response.json();

        container.innerHTML = `
            <div class="slot-card">
                <p><strong>Użytkownicy:</strong> ${stats.users}</p>
                <p><strong>Lekarze:</strong> ${stats.doctors}</p>
                <p><strong>Pacjenci:</strong> ${stats.patients}</p>
                <p><strong>Wizyty:</strong> ${stats.appointments}</p>
                <p><strong>Sloty:</strong> ${stats.slots}</p>
            </div>
        `;

    } catch (error) {
        container.innerHTML = `
            <div class="empty-state error-state">
                <p>Nie udało się pobrać statystyk.</p>
            </div>
        `;
    }
}

async function loadPendingDoctors() {
    const container = document.getElementById("pendingDoctorsContainer");

    if (!container) {
        return;
    }

    try {
        const token = getToken();

        const response = await fetch(`${API_BASE_URL}/admin/doctors/pending`, {
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error("Nie udało się pobrać oczekujących lekarzy.");
        }

        const doctors = await response.json();

        if (!doctors.length) {
            container.innerHTML = `
                <div class="empty-state">
                    <p>Brak oczekujących lekarzy.</p>
                </div>
            `;
            return;
        }

        container.innerHTML = doctors.map(doctor => `
            <div class="slot-card">
                <p><strong>ID:</strong> ${doctor.id}</p>
                <p><strong>Email:</strong> ${doctor.email}</p>
                <p><strong>Rola:</strong> ${translateRole(doctor.role)}</p>
                <p>
                    <strong>Status:</strong>
                    <span class="status-pending">${translateStatus(doctor.status)}</span>
                </p>

                <div class="action-buttons">
                    <button class="approve-btn" onclick="approveDoctor(${doctor.id})">
                        Zatwierdź
                    </button>

                    <button class="delete-btn" onclick="rejectDoctor(${doctor.id})">
                        Odrzuć
                    </button>
                </div>
            </div>
        `).join("");

    } catch (error) {
        container.innerHTML = `
            <div class="empty-state error-state">
                <p>Nie udało się pobrać oczekujących lekarzy.</p>
            </div>
        `;
    }
}

async function approveDoctor(userId) {
    if (!confirm("Czy na pewno chcesz zatwierdzić tego lekarza?")) {
        return;
    }

    try {
        const token = getToken();

        const response = await fetch(`${API_BASE_URL}/admin/doctors/${userId}/approve`, {
            method: "PUT",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error("Nie udało się zatwierdzić lekarza.");
        }

        alert("Lekarz został zatwierdzony.");

        await loadPendingDoctors();
        await loadUsers();
        await loadStats();

    } catch (error) {
        alert(error.message);
    }
}

async function rejectDoctor(userId) {
    if (!confirm("Czy na pewno chcesz odrzucić tego lekarza?")) {
        return;
    }

    try {
        const token = getToken();

        const response = await fetch(`${API_BASE_URL}/admin/doctors/${userId}/reject`, {
            method: "PUT",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error("Nie udało się odrzucić lekarza.");
        }

        alert("Lekarz został odrzucony.");

        await loadPendingDoctors();
        await loadUsers();
        await loadStats();

    } catch (error) {
        alert(error.message);
    }
}

function formatDate(dateString) {
    if (!dateString) {
        return "Brak danych";
    }

    const date = new Date(dateString);

    return date.toLocaleString("pl-PL", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit"
    });
}

function translateStatus(status) {
    switch (status) {
        case "BOOKED":
            return "Zarezerwowana";
        case "CANCELLED":
            return "Anulowana";
        case "COMPLETED":
            return "Odbyła się";
        case "AVAILABLE":
            return "Dostępny";
        case "PENDING":
            return "Oczekuje";
        case "ACTIVE":
            return "Aktywne";
        case "REJECTED":
            return "Odrzucone";
        default:
            return status;
    }
}

function getStatusClass(status) {
    switch (status) {
        case "BOOKED":
            return "status-booked";
        case "CANCELLED":
            return "status-cancelled";
        case "COMPLETED":
            return "status-completed";
        case "AVAILABLE":
            return "status-available";
        case "PENDING":
            return "status-pending";
        case "ACTIVE":
            return "status-available";
        case "REJECTED":
            return "status-cancelled";
        default:
            return "";
    }
}

function translateRole(role) {
    switch (role) {
        case "ADMIN":
            return "Administrator";
        case "DOCTOR":
            return "Lekarz";
        case "PATIENT":
            return "Pacjent";
        default:
            return role;
    }
}

function translateBackendError(message) {
    switch (message) {
        case "Cannot cancel completed appointment":
            return "Nie można anulować wizyty, która już się odbyła.";
        case "Cannot cancel appointment that has already ended":
            return "Nie można anulować wizyty, która już się zakończyła.";
        case "Appointment is already cancelled":
            return "Ta wizyta jest już anulowana.";
        default:
            return message;
    }
}