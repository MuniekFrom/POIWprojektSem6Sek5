document.addEventListener("DOMContentLoaded", async () => {
    requireAuth("PATIENT");

    await loadPatientProfile();
    await loadPatientAppointments();
    await loadDoctors();
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
            container.innerHTML = "<p>Nie masz jeszcze żadnych wizyt.</p>";
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
                <p><strong>Status:</strong> ${appointment.status}</p>

                <button class="delete-btn"
                    onclick="cancelAppointment(${appointment.id})">
                    Anuluj wizytę
                </button>
            </div>
        `).join("");

    } catch (error) {
        container.innerHTML = "Nie udało się pobrać wizyt.";
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

async function cancelAppointment(appointmentId) {
    const confirmed = confirm("Czy na pewno chcesz anulować tę wizytę?");

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
            throw new Error("Nie udało się anulować wizyty.");
        }

        alert("Wizyta została anulowana.");
        await loadPatientAppointments();

    } catch (error) {
        alert("Wystąpił nieoczekiwany błąd.");
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
            container.innerHTML = "Brak lekarzy.";
            return;
        }

        container.innerHTML = doctors.map(doctor => `
            <div class="slot-card">
                <p><strong>${doctor.firstName} ${doctor.lastName}</strong></p>
                <p>${doctor.specialization}</p>

                <button onclick="loadAvailableSlots(${doctor.id})">
                    Pokaż terminy
                </button>
            </div>
        `).join("");

    } catch (error) {
        container.innerHTML = "Nie udało się pobrać lekarzy.";
    }
}

async function loadAvailableSlots(doctorId) {
    const container = document.getElementById("slotsContainer");

    container.innerHTML = "Ładowanie terminów...";

    try {
        const response = await fetch(`${API_BASE_URL}/appointments/available?doctorId=${doctorId}`);

        if (!response.ok) {
            throw new Error();
        }

        const slots = await response.json();

        if (!slots.length) {
            container.innerHTML = "<p>Brak wolnych terminów dla tego lekarza.</p>";
            return;
        }

        container.innerHTML = slots.map(slot => `
            <div class="slot-card">
                <p><strong>ID slotu:</strong> ${slot.id}</p>
                <p><strong>Lekarz:</strong> ${slot.doctorName}</p>
                <p><strong>Specjalizacja:</strong> ${slot.specialization}</p>
                <p><strong>Start:</strong> ${formatDate(slot.startTime)}</p>
                <p><strong>Koniec:</strong> ${formatDate(slot.endTime)}</p>
                <p><strong>Status:</strong> ${slot.status}</p>

                <input type="text" id="reason-${slot.id}" placeholder="Powód wizyty">

                <button onclick="bookAppointment(${slot.id})">
                    Zarezerwuj
                </button>
            </div>
        `).join("");

    } catch (error) {
        container.innerHTML = "Nie udało się pobrać wolnych terminów.";
    }
}

async function bookAppointment(slotId) {
    const reasonInput = document.getElementById(`reason-${slotId}`);
    const reason = reasonInput.value.trim();

    if (!reason) {
        alert("Podaj powód wizyty.");
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
                errorMessage = errorData.message || errorMessage;
            } catch (e) {
            }

            throw new Error(errorMessage);
        }

                alert("Wizyta została zarezerwowana.");

                await loadPatientAppointments();
                document.getElementById("slotsContainer").innerHTML = "";

            } catch (error) {
                alert(error.message);
            }
}