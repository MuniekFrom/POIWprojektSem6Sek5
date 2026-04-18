document.addEventListener("DOMContentLoaded", () => {

    if (isLoggedIn()) {
        redirectByRole();
    }

    const form = document.getElementById("loginForm");
    const error = document.getElementById("error");

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const email = document.getElementById("email").value;
        const password = document.getElementById("password").value;

        try {
            const data = await apiRequest("/auth/login", {
                method: "POST",
                body: JSON.stringify({
                    email,
                    password
                })
            });

            saveAuthData(data.token, data.role);
            redirectByRole();

        } catch (err) {
            error.textContent = "Nieprawidłowe dane logowania";
        }
    });

});