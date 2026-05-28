let allAvailableSlots = [];
let selectedDateKey = null;

document.addEventListener("DOMContentLoaded", async () => {
    requireAuth("PATIENT");

    await loadPatientProfile();
    await loadPatientAppointments();
    await loadDoctors();
    await loadAllAvailableSlots();
});

async function loadPatientProfile() {
    const container = document.getElementById("patientData");

    try {
        const token = getToken();

        const response = await fetch(`${API_BASE_URL}/patients/me`, {
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error("Błąd pobierania danych pacjenta");
        }

        const patient = await response.json();

        container.innerHTML = `
            <p><strong>ID:</strong> ${patient.id}</p>
            <p><strong>Imię:</strong> ${patient.firstName}</p>
            <p><strong>Nazwisko:</strong> ${patient.lastName}</p>
            <p><strong>PESEL:</strong> ${patient.pesel}</p>
            <p><strong>Telefon:</strong> ${patient.phone}</p>
        `;
    } catch (error) {
        container.innerHTML = "Nie udało się pobrać danych.";
    }
}

async function loadPatientAppointments() {
    const container = document.getElementById("appointmentsContainer");

    try {
        const token = getToken();

        const response = await fetch(`${API_BASE_URL}/appointments/me`, {
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error("Błąd pobierania wizyt");
        }

        const appointments = await response.json();

        if (!appointments.length) {
            container.innerHTML = `
                <div class="empty-state">
                    <p>Nie masz jeszcze żadnych aktywnych wizyt.</p>
                </div>
            `;
            return;
        }

        container.innerHTML = appointments.map(appointment => `
            <div class="slot-card">
                <p><strong>ID:</strong> ${appointment.id}</p>
                <p><strong>Lekarz:</strong> ${appointment.doctorName}</p>
                <p><strong>Specjalizacja:</strong> ${appointment.specialization}</p>
                <p><strong>Start:</strong> ${formatDate(appointment.startTime)}</p>
                <p><strong>Koniec:</strong> ${formatDate(appointment.endTime)}</p>
                <p><strong>Powód:</strong> ${appointment.reason}</p>
                <p>
                    <strong>Status:</strong>
                    <span class="${getStatusClass(appointment.status)}">
                        ${translateStatus(appointment.status)}
                    </span>
                </p>

                ${appointment.status === "BOOKED"
                    ? `<button class="delete-btn" onclick="cancelAppointment(${appointment.id})">
                            Anuluj wizytę
                       </button>`
                    : `<p class="cancelled-info">${getAppointmentInfoText(appointment.status)}</p>`
                }
            </div>
        `).join("");

    } catch (error) {
        container.innerHTML = `
            <div class="empty-state error-state">
                <p>Nie udało się pobrać wizyt.</p>
            </div>
        `;
    }
}

async function loadDoctors() {
    const container = document.getElementById("doctorsContainer");

    try {
        const response = await fetch(`${API_BASE_URL}/doctors`);

        if (!response.ok) {
            throw new Error();
        }

        const doctors = await response.json();

        if (!doctors.length) {
            container.innerHTML = `
                <div class="empty-state">
                    <p>Brak lekarzy.</p>
                </div>
            `;
            return;
        }

        container.innerHTML = doctors.map(doctor => `
            <div class="slot-card">
                <p><strong>${doctor.firstName} ${doctor.lastName}</strong></p>
                <p>${doctor.specialization}</p>
            </div>
        `).join("");

    } catch (error) {
        container.innerHTML = `
            <div class="empty-state error-state">
                <p>Nie udało się pobrać lekarzy.</p>
            </div>
        `;
    }
}

async function loadAllAvailableSlots() {
    const datesContainer = document.getElementById("availableDatesContainer");
    const selectedDaySlotsContainer = document.getElementById("selectedDaySlotsContainer");

    datesContainer.innerHTML = "Ładowanie kalendarza...";

    try {
        const token = getToken();

        const response = await fetch(`${API_BASE_URL}/appointments/available/all`, {
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error("Nie udało się pobrać wolnych terminów.");
        }

        allAvailableSlots = await response.json();

        if (!allAvailableSlots.length) {
            datesContainer.innerHTML = `
                <div class="empty-state">
                    <p>Brak wolnych terminów w systemie.</p>
                </div>
            `;

            selectedDaySlotsContainer.innerHTML = `
                <div class="empty-state">
                    <p>Nie ma obecnie dostępnych terminów do rezerwacji.</p>
                </div>
            `;

            return;
        }

        renderAvailableDates();

    } catch (error) {
        datesContainer.innerHTML = `
            <div class="empty-state error-state">
                <p>Nie udało się pobrać kalendarza wolnych terminów.</p>
            </div>
        `;
    }
}

function renderAvailableDates() {
    const container = document.getElementById("availableDatesContainer");
    const groupedSlots = groupSlotsByDate(allAvailableSlots);
    const dateKeys = Object.keys(groupedSlots).sort();

    container.innerHTML = dateKeys.map(dateKey => {
        const slotsCount = groupedSlots[dateKey].length;
        const isActive = selectedDateKey === dateKey;

        return `
            <button
                type="button"
                class="calendar-day-card ${isActive ? "active" : ""}"
                onclick="selectAvailableDate('${dateKey}')"
            >
                <span class="calendar-day-name">${formatDateLabel(dateKey)}</span>
                <span class="calendar-day-count">${slotsCount} ${getSlotWord(slotsCount)}</span>
            </button>
        `;
    }).join("");
}

function selectAvailableDate(dateKey) {
    selectedDateKey = dateKey;

    renderAvailableDates();
    renderSlotsForSelectedDate(dateKey);
}

function renderSlotsForSelectedDate(dateKey) {
    const container = document.getElementById("selectedDaySlotsContainer");
    const title = document.getElementById("selectedDayTitle");

    const slots = allAvailableSlots
        .filter(slot => getDateKey(slot.startTime) === dateKey)
        .sort((a, b) => new Date(a.startTime) - new Date(b.startTime));

    title.textContent = `Terminy: ${formatDateLabel(dateKey)}`;

    if (!slots.length) {
        container.innerHTML = `
            <div class="empty-state">
                <p>Brak wolnych terminów w wybranym dniu.</p>
            </div>
        `;
        return;
    }

    container.innerHTML = slots.map(slot => `
        <div class="slot-card">
            <p><strong>ID slotu:</strong> ${slot.id}</p>
            <p><strong>Lekarz:</strong> ${slot.doctorName}</p>
            <p><strong>Specjalizacja:</strong> ${slot.specialization}</p>
            <p><strong>Godzina:</strong> ${formatTime(slot.startTime)} - ${formatTime(slot.endTime)}</p>
            <p>
                <strong>Status:</strong>
                <span class="${getStatusClass(slot.status)}">
                    ${translateStatus(slot.status)}
                </span>
            </p>

            <input type="text" id="reason-${slot.id}" placeholder="Powód wizyty">

            <button onclick="bookAppointment(${slot.id})">
                Zarezerwuj
            </button>
        </div>
    `).join("");
}

function groupSlotsByDate(slots) {
    return slots.reduce((groups, slot) => {
        const dateKey = getDateKey(slot.startTime);

        if (!groups[dateKey]) {
            groups[dateKey] = [];
        }

        groups[dateKey].push(slot);

        return groups;
    }, {});
}

function getDateKey(dateString) {
    const date = new Date(dateString);

    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");

    return `${year}-${month}-${day}`;
}

function formatDateLabel(dateKey) {
    const [year, month, day] = dateKey.split("-");
    const date = new Date(Number(year), Number(month) - 1, Number(day));

    return date.toLocaleDateString("pl-PL", {
        weekday: "long",
        day: "2-digit",
        month: "long",
        year: "numeric"
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

function formatTime(dateString) {
    const date = new Date(dateString);

    return date.toLocaleTimeString("pl-PL", {
        hour: "2-digit",
        minute: "2-digit"
    });
}

function getSlotWord(count) {
    if (count === 1) {
        return "termin";
    }

    if (count >= 2 && count <= 4) {
        return "terminy";
    }

    return "terminów";
}

async function bookAppointment(slotId) {
    const reasonInput = document.getElementById(`reason-${slotId}`);
    const reason = reasonInput.value.trim();

    if (!reason) {
        window.showAppMessage("Podaj powód wizyty.", "error");
        return;
    }

    try {
        const token = getToken();

        const response = await fetch(`${API_BASE_URL}/appointments/book`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify({
                slotId: slotId,
                reason: reason
            })
        });

        if (!response.ok) {
            let errorMessage = "Nie udało się zarezerwować wizyty.";

            try {
                const errorData = await response.json();
                errorMessage = translateBackendError(errorData.message) || errorMessage;
            } catch (e) {}

            throw new Error(errorMessage);
        }

        selectedDateKey = null;

        await loadPatientAppointments();
        await loadAllAvailableSlots();

        document.getElementById("selectedDayTitle").textContent = "Terminy wybranego dnia";
        document.getElementById("selectedDaySlotsContainer").innerHTML = `
            <div class="empty-state">
                <p>Wybierz dzień z kalendarza, aby zobaczyć wolne terminy.</p>
            </div>
        `;

        window.showAppMessage("Wizyta została zarezerwowana.", "success");

    } catch (error) {
        window.showAppMessage(error.message, "error");
    }
}

async function cancelAppointment(appointmentId) {
    const confirmed = await window.showAppConfirm(
        "Czy na pewno chcesz anulować tę wizytę?",
        "Anulowanie wizyty"
    );

    if (!confirmed) {
        return;
    }

    try {
        const token = getToken();

        const response = await fetch(`${API_BASE_URL}/appointments/${appointmentId}`, {
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

        await loadPatientAppointments();
        await loadAllAvailableSlots();

        window.showAppMessage("Wizyta została anulowana.", "success");

    } catch (error) {
        window.showAppMessage(error.message, "error");
    }
}

function getAppointmentInfoText(status) {
    switch (status) {
        case "CANCELLED":
            return "Wizyta anulowana";
        case "COMPLETED":
            return "Wizyta odbyła się";
        default:
            return "";
    }
}

function translateStatus(status) {
    switch (status) {
        case "BOOKED":
            return "Zarezerwowana";
        case "CANCELLED":
            return "Anulowana";
        case "AVAILABLE":
            return "Dostępny";
        case "COMPLETED":
            return "Odbyło się";
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
        case "AVAILABLE":
            return "status-available";
        case "CANCELLED":
            return "status-cancelled";
        case "COMPLETED":
            return "status-completed";
        case "PENDING":
            return "status-pending";
        default:
            return "";
    }
}

function translateBackendError(message) {
    switch (message) {
        case "Cannot book appointment in the past":
            return "Nie można zarezerwować terminu, który już minął.";
        case "Slot is not available":
            return "Ten termin nie jest już dostępny.";
        case "Appointment already exists for this slot":
            return "Dla tego terminu istnieje już wizyta.";
        case "Cannot cancel completed appointment":
            return "Nie można anulować wizyty, która już się odbyła.";
        case "Cannot cancel appointment that has already ended":
            return "Nie można anulować wizyty, która już się zakończyła.";
        default:
            return message;
    }
}