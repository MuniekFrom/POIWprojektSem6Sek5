document.addEventListener("DOMContentLoaded", async () => {
    requireAuth("DOCTOR");

    setupTimeRounding();
    setupSlotForm();
    await loadDoctorProfile();
    await loadDoctorSlots();
});

function setupTimeRounding() {
    const startTimeInput = document.getElementById("startTime");
    const endTimeInput = document.getElementById("endTime");

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
            container.innerHTML = "<p>Brak slotów.</p>";
            return;
        }

        container.innerHTML = slots.map(slot => `
            <div class="slot-card">
                <p><strong>ID:</strong> ${slot.id}</p>
                <p><strong>Start:</strong> ${formatDate(slot.startTime)}</p>
                <p><strong>Koniec:</strong> ${formatDate(slot.endTime)}</p>
                <p>
                    <strong>Status:</strong>
                    <span class="${slot.status === "BOOKED" ? "status-booked" : "status-available"}">
                        ${translateStatus(slot.status)}
                    </span>
                </p>

                ${slot.status === "AVAILABLE"
                    ? `<button class="delete-btn" onclick="deleteSlot(${slot.id})">Usuń</button>`
                    : ""
                }
            </div>
        `).join("");

    } catch (error) {
        container.innerHTML = "Nie udało się pobrać slotów.";
    }
}

function setupSlotForm() {
    const form = document.getElementById("slotForm");
    const message = document.getElementById("slotMessage");

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        message.textContent = "";

        const startTime = document.getElementById("startTime").value;
        const endTime = document.getElementById("endTime").value;

        if (!startTime || !endTime) {
            message.textContent = "Uzupełnij datę początku i końca.";
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
                    errorMessage = errorData.message || errorMessage;
                } catch (e) {
                }

                throw new Error(errorMessage);
            }

            message.textContent = "Slot został dodany.";
            form.reset();

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
                errorMessage = errorData.message || errorMessage;
            } catch (e) {
            }

            throw new Error(errorMessage);
        }

        await loadDoctorSlots();

    } catch (error) {
        alert(error.message);
    }
}

function translateStatus(status) {
    switch (status) {
        case "BOOKED":
            return "Zarezerwowany";
        case "AVAILABLE":
            return "Dostępny";
        case "CANCELLED":
            return "Anulowany";
        default:
            return status;
    }
}