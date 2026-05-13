document.addEventListener("DOMContentLoaded", async () => {
    requireAuth("DOCTOR");

    setupTimeRounding();
    setupSlotForm();
    setupMinDateTime();

    await loadDoctorProfile();
    await loadTodayAppointments();
    await loadDoctorSlots();
});

function setupMinDateTime() {
    const startTimeInput = document.getElementById("startTime");
    const endTimeInput = document.getElementById("endTime");

    const now = new Date();
    now.setMinutes(now.getMinutes() - now.getTimezoneOffset());

    const minDateTime = now.toISOString().slice(0, 16);

    if (startTimeInput) {
        startTimeInput.min = minDateTime;
    }

    if (endTimeInput) {
        endTimeInput.min = minDateTime;
    }
}

function setupTimeRounding() {
    const startTimeInput = document.getElementById("startTime");
    const endTimeInput = document.getElementById("endTime");

    if (!startTimeInput || !endTimeInput) {
        return;
    }

    startTimeInput.addEventListener("change", () => {
        startTimeInput.value = roundToNearestFiveMinutes(startTimeInput.value);
    });

    endTimeInput.addEventListener("change", () => {
        endTimeInput.value = roundToNearestFiveMinutes(endTimeInput.value);
    });
}

function roundToNearestFiveMinutes(dateTimeValue) {
    if (!dateTimeValue) {
        return "";
    }

    const date = new Date(dateTimeValue);
    const minutes = date.getMinutes();
    const roundedMinutes = Math.round(minutes / 5) * 5;

    date.setMinutes(roundedMinutes);
    date.setSeconds(0);
    date.setMilliseconds(0);

    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    const hours = String(date.getHours()).padStart(2, "0");
    const mins = String(date.getMinutes()).padStart(2, "0");

    return `${year}-${month}-${day}T${hours}:${mins}`;
}

async function loadDoctorProfile() {
    const container = document.getElementById("doctorData");

    try {
        const token = getToken();

        const response = await fetch(`${API_BASE_URL}/doctors/me`, {
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error("Błąd pobierania danych lekarza");
        }

        const doctor = await response.json();

        container.innerHTML = `
            <p><strong>ID:</strong> ${doctor.id}</p>
            <p><strong>Imię:</strong> ${doctor.firstName}</p>
            <p><strong>Nazwisko:</strong> ${doctor.lastName}</p>
            <p><strong>Email:</strong> ${doctor.email}</p>
            <p><strong>Specjalizacja:</strong> ${doctor.specialization}</p>
        `;
    } catch (error) {
        container.innerHTML = "Nie udało się pobrać danych.";
    }
}

async function loadTodayAppointments() {
    const container = document.getElementById("todayAppointmentsContainer");

    if (!container) {
        return;
    }

    try {
        const token = getToken();

        const response = await fetch(`${API_BASE_URL}/appointments/doctor/today`, {
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error("Błąd pobierania dzisiejszych wizyt");
        }

        const appointments = await response.json();

        if (!appointments.length) {
            container.innerHTML = `
                <div class="empty-state">
                    <p>Nie masz dzisiaj żadnych wizyt.</p>
                </div>
            `;
            return;
        }

        container.innerHTML = appointments.map(appointment => `
            <div class="slot-card today-appointment-card">
                <p><strong>ID:</strong> ${appointment.id}</p>
                <p><strong>Pacjent:</strong> ${appointment.patientName}</p>
                <p><strong>Start:</strong> ${formatDate(appointment.startTime)}</p>
                <p><strong>Koniec:</strong> ${formatDate(appointment.endTime)}</p>
                <p><strong>Powód:</strong> ${appointment.reason || "Brak powodu"}</p>
                <p>
                    <strong>Status:</strong>
                    <span class="${getStatusClass(appointment.status)}">
                        ${translateAppointmentStatus(appointment.status)}
                    </span>
                </p>
            </div>
        `).join("");

    } catch (error) {
        container.innerHTML = `
            <div class="empty-state error-state">
                <p>Nie udało się pobrać dzisiejszych wizyt.</p>
            </div>
        `;
    }
}

async function loadDoctorSlots() {
    const container = document.getElementById("slotsContainer");

    try {
        const token = getToken();

        const response = await fetch(`${API_BASE_URL}/slots/me`, {
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error("Błąd pobierania slotów");
        }

        const slots = await response.json();

        if (!slots.length) {
            container.innerHTML = `
                <div class="empty-state">
                    <p>Brak slotów.</p>
                </div>
            `;
            return;
        }

        container.innerHTML = slots.map(slot => `
            <div class="slot-card">
                <p><strong>ID:</strong> ${slot.id}</p>
                <p><strong>Start:</strong> ${formatDate(slot.startTime)}</p>
                <p><strong>Koniec:</strong> ${formatDate(slot.endTime)}</p>
                <p>
                    <strong>Status:</strong>
                    <span class="${getStatusClass(slot.status)}">
                        ${translateSlotStatus(slot.status)}
                    </span>
                </p>

                ${slot.status === "AVAILABLE"
                    ? `<button class="delete-btn" onclick="deleteSlot(${slot.id})">Usuń</button>`
                    : ""
                }
            </div>
        `).join("");

    } catch (error) {
        container.innerHTML = `
            <div class="empty-state error-state">
                <p>Nie udało się pobrać slotów.</p>
            </div>
        `;
    }
}

function setupSlotForm() {
    const form = document.getElementById("slotForm");
    const message = document.getElementById("slotMessage");

    if (!form) {
        return;
    }

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        message.textContent = "";

        const startTime = document.getElementById("startTime").value;
        const endTime = document.getElementById("endTime").value;

        if (!startTime || !endTime) {
            message.textContent = "Uzupełnij datę początku i końca.";
            return;
        }

        if (new Date(startTime) < new Date()) {
            message.textContent = "Nie można dodać slotu w przeszłości.";
            return;
        }

        if (new Date(endTime) <= new Date(startTime)) {
            message.textContent = "Koniec slotu musi być później niż początek.";
            return;
        }

        try {
            const token = getToken();

            const response = await fetch(`${API_BASE_URL}/slots`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify({
                    startTime: formatDateTimeForBackend(startTime),
                    endTime: formatDateTimeForBackend(endTime)
                })
            });

            if (!response.ok) {
                let errorMessage = "Nie udało się dodać slotu.";

                try {
                    const errorData = await response.json();
                    errorMessage = translateBackendError(errorData.message) || errorMessage;
                } catch (e) {}

                throw new Error(errorMessage);
            }

            message.textContent = "Slot został dodany.";
            form.reset();

            setupMinDateTime();
            await loadTodayAppointments();
            await loadDoctorSlots();

        } catch (error) {
            message.textContent = error.message;
        }
    });
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

function formatDateTimeForBackend(value) {
    return value + ":00";
}

async function deleteSlot(slotId) {
    const confirmed = confirm("Czy na pewno chcesz usunąć ten slot?");

    if (!confirmed) {
        return;
    }

    try {
        const token = getToken();

        const response = await fetch(`${API_BASE_URL}/slots/${slotId}`, {
            method: "DELETE",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (!response.ok) {
            let errorMessage = "Nie udało się usunąć slotu.";

            try {
                const errorData = await response.json();
                errorMessage = translateBackendError(errorData.message) || errorMessage;
            } catch (e) {}

            throw new Error(errorMessage);
        }

        await loadTodayAppointments();
        await loadDoctorSlots();

    } catch (error) {
        alert(error.message);
    }
}

function translateSlotStatus(status) {
    switch (status) {
        case "BOOKED":
            return "Zarezerwowany";
        case "AVAILABLE":
            return "Dostępny";
        case "CANCELLED":
            return "Anulowany";
        case "COMPLETED":
            return "Odbyło się";
        default:
            return status;
    }
}

function translateAppointmentStatus(status) {
    switch (status) {
        case "BOOKED":
            return "Zarezerwowana";
        case "CANCELLED":
            return "Anulowana";
        case "COMPLETED":
            return "Odbyła się";
        default:
            return status;
    }
}

function getStatusClass(status) {
    switch (status) {
        case "BOOKED":
            return "status-booked";
        case "AVAILABLE":
            return "status-available";
        case "CANCELLED":
            return "status-cancelled";
        case "COMPLETED":
            return "status-completed";
        default:
            return "";
    }
}

function translateBackendError(message) {
    switch (message) {
        case "Cannot create slot in the past":
            return "Nie można dodać terminu w przeszłości.";
        case "Cannot create slot that has already ended":
            return "Nie można dodać terminu, który już minął.";
        case "endTime must be after startTime":
            return "Koniec slotu musi być później niż początek.";
        case "Slot overlaps with existing doctor's slot":
            return "Ten termin nakłada się na inny slot lekarza.";
        case "Cannot delete booked slot":
            return "Nie można usunąć zarezerwowanego slotu.";
        case "Cannot delete completed slot":
            return "Nie można usunąć slotu, który już się odbył.";
        default:
            return message;
    }
}