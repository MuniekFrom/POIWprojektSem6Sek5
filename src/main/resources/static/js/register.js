let selectedRole = "PATIENT";

document.addEventListener("DOMContentLoaded", () => {
    const patientTab = document.getElementById("patientTab");
    const doctorTab = document.getElementById("doctorTab");

    const patientFields = document.getElementById("patientFields");
    const doctorFields = document.getElementById("doctorFields");

    const form = document.getElementById("registerForm");
    const message = document.getElementById("registerMessage");

    patientTab.addEventListener("click", () => {
        selectedRole = "PATIENT";

        patientTab.classList.add("active");
        doctorTab.classList.remove("active");

        patientFields.classList.remove("hidden");
        doctorFields.classList.add("hidden");

        document.getElementById("pesel").required = true;
        document.getElementById("phone").required = true;
        document.getElementById("specialization").required = false;

        message.textContent = "";
        message.className = "";
    });

    doctorTab.addEventListener("click", () => {
        selectedRole = "DOCTOR";

        doctorTab.classList.add("active");
        patientTab.classList.remove("active");

        doctorFields.classList.remove("hidden");
        patientFields.classList.add("hidden");

        document.getElementById("pesel").required = false;
        document.getElementById("phone").required = false;
        document.getElementById("specialization").required = true;

        message.textContent = "";
        message.className = "";
    });

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        message.textContent = "";
        message.className = "";

        const email = document.getElementById("email").value.trim();
        const password = document.getElementById("password").value.trim();
        const firstName = document.getElementById("firstName").value.trim();
        const lastName = document.getElementById("lastName").value.trim();

        let endpoint;
        let body;

        if (selectedRole === "PATIENT") {
            endpoint = "/auth/register/patient";

            body = {
                email,
                password,
                firstName,
                lastName,
                pesel: document.getElementById("pesel").value.trim(),
                phone: document.getElementById("phone").value.trim()
            };
        } else {
            endpoint = "/auth/register/doctor";

            body = {
                email,
                password,
                firstName,
                lastName,
                specialization: document.getElementById("specialization").value.trim()
            };
        }

        try {
            const response = await fetch(`${API_BASE_URL}${endpoint}`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(body)
            });

            if (!response.ok) {
                let errorMessage = "Nie udało się utworzyć konta.";

                try {
                    const errorData = await response.json();
                    errorMessage = translateError(errorData.message) || errorMessage;
                } catch (e) {
                }

                throw new Error(errorMessage);
            }

            if (selectedRole === "PATIENT") {
                message.textContent = "Konto pacjenta zostało utworzone. Możesz się zalogować.";
            } else {
                message.textContent = "Zgłoszenie lekarza zostało wysłane. Konto oczekuje na zatwierdzenie przez administratora.";
            }

            message.className = "success-message";
            form.reset();

        } catch (error) {
            message.textContent = error.message;
            message.className = "error-message";
        }
    });
});

function translateError(message) {
    switch (message) {
        case "Email is already used":
            return "Ten adres email jest już używany.";
        default:
            return message;
    }
}

