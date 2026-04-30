document.addEventListener("DOMContentLoaded", async () => {
    requireAuth("ADMIN");

    await loadAdminProfile();
    await loadUsers();
    await loadStats();
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
            <p><strong>Rola:</strong> ${admin.role}</p>
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
            container.innerHTML = "<p>Brak użytkowników.</p>";
            return;
        }

        container.innerHTML = users.map(user => `
            <div class="slot-card">
                <p><strong>ID:</strong> ${user.id}</p>
                <p><strong>Email:</strong> ${user.email}</p>
                <p><strong>Rola:</strong> ${user.role}</p>

                ${user.role !== "ADMIN"
                    ? `<button class="delete-btn" onclick="deleteUser(${user.id})">Usuń użytkownika</button>`
                    : ""
                }
            </div>
        `).join("");

    } catch (error) {
        container.innerHTML = "Nie udało się pobrać użytkowników.";
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

        const appointments = await response.json();

        if (!appointments.length) {
            container.innerHTML = "<p>Brak wizyt.</p>";
            return;
        }

        container.innerHTML = appointments.map(a => `
            <div class="slot-card">
                <p><strong>ID:</strong> ${a.id}</p>
                <p><strong>Lekarz:</strong> ${a.doctorName}</p>
                <p><strong>Pacjent:</strong> ${a.patientName}</p>
                <p><strong>Start:</strong> ${formatDate(a.startTime)}</p>
                <p><strong>Koniec:</strong> ${formatDate(a.endTime)}</p>
                <p><strong>Powód:</strong> ${a.reason}</p>
                <p><strong>Status:</strong> ${a.status}</p>

                <button class="delete-btn" onclick="deleteAppointment(${a.id})">
                    Usuń wizytę
                </button>
            </div>
        `).join("");

    } catch (error) {
        container.innerHTML = "Nie udało się pobrać wizyt.";
    }
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

    } catch (error) {
        alert(error.message);
    }
}

async function deleteAppointment(id) {
    if (!confirm("Na pewno usunąć wizytę?")) {
        return;
    }

    try {
        const token = getToken();

        const response = await fetch(`${API_BASE_URL}/admin/appointments/${id}`, {
            method: "DELETE",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error("Nie udało się usunąć wizyty");
        }

        alert("Wizyta usunięta");
        await loadAllAppointments();

    } catch (error) {
        alert(error.message);
    }
}

function formatDate(dateString) {
    const date = new Date(dateString);

    return date.toLocaleString("pl-PL", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit"
    });
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
        container.innerHTML = "Nie udało się pobrać statystyk.";
    }
}