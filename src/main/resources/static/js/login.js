document.addEventListener("DOMContentLoaded", () => {
    console.log("login.js działa");

    if (isLoggedIn()) {
        redirectByRole();
        return;
    }

    const form = document.getElementById("loginForm");
    const error = document.getElementById("errorMessage");

    if (!form) {
        console.error("Nie znaleziono formularza loginForm");
        return;
    }

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        console.log("Kliknięto Zaloguj");

        error.textContent = "";

        const email = document.getElementById("email").value;
        const password = document.getElementById("password").value;

        try {
            const data = await apiRequest("/auth/login", {
                method: "POST",
                body: JSON.stringify({
                    email: email,
                    password: password
                })
            });

            console.log("Zalogowano jako:", data.role);

            saveAuthData(data.token, data.role);

            redirectByRole();

        } catch (err) {
            console.error("Błąd logowania:", err);
            error.textContent = translateLoginError(err.message);
            }
    });
});

function translateLoginError(message) {
    switch (message) {
        case "Account is waiting for admin approval":
            return "Konto oczekuje na zatwierdzenie przez administratora.";
        case "Bad credentials":
        case "Invalid credentials":
            return "Nieprawidłowy email lub hasło.";
        default:
            return "Nieprawidłowe dane logowania.";
    }
}