document.addEventListener("DOMContentLoaded", async () => {
    requireAuth("ADMIN");

    await loadAdminProfile();
    await loadUsers();
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
            </div>
        `).join("");

    } catch (error) {
        container.innerHTML = "Nie udało się pobrać użytkowników.";
    }
}