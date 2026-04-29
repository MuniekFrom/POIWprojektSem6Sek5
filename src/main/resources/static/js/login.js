document.addEventListener("DOMContentLoaded", () => {
    if (isLoggedIn()) {
        redirectByRole();
        return;
    }

    const form = document.getElementById("loginForm");
    const error = document.getElementById("error");

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

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

            saveAuthData(data.token, data.role);

            console.log("Zalogowano jako:", data.role);

            redirectByRole();

        } catch (err) {
            error.textContent = "Nieprawidłowe dane logowania";
        }
    });
});