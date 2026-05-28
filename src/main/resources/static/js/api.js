async function apiRequest(endpoint, options = {}) {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        ...options,
        headers: {
            "Content-Type": "application/json",
            ...(options.headers || {})
        }
    });

    if (!response.ok) {
        throw new Error("Błąd żądania");
    }

    return response.json();
}

function showAppMessage(message, type = "info", title = null) {
    const oldOverlay = document.querySelector(".app-message-overlay");

    if (oldOverlay) {
        oldOverlay.remove();
    }

    let icon = "i";
    let finalTitle = title || "Informacja";

    if (type === "success") {
        icon = "✓";
        finalTitle = title || "Sukces";
    }

    if (type === "error") {
        icon = "!";
        finalTitle = title || "Błąd";
    }

    const overlay = document.createElement("div");
    overlay.className = "app-message-overlay";

    overlay.innerHTML = `
        <div class="app-message-box">
            <div class="app-message-icon ${type}">
                ${icon}
            </div>

            <h3>${finalTitle}</h3>
            <p>${message}</p>

            <button type="button" class="app-message-btn">
                OK
            </button>
        </div>
    `;

    document.body.appendChild(overlay);

    const closeButton = overlay.querySelector(".app-message-btn");

    closeButton.addEventListener("click", () => {
        overlay.remove();
    });

    overlay.addEventListener("click", (event) => {
        if (event.target === overlay) {
            overlay.remove();
        }
    });
}

function showAppConfirm(message, title = "Potwierdzenie") {
    return new Promise((resolve) => {
        const oldOverlay = document.querySelector(".app-message-overlay");

        if (oldOverlay) {
            oldOverlay.remove();
        }

        const overlay = document.createElement("div");
        overlay.className = "app-message-overlay";

        overlay.innerHTML = `
            <div class="app-message-box">
                <div class="app-message-icon info">
                    ?
                </div>

                <h3>${title}</h3>
                <p>${message}</p>

                <div class="app-confirm-actions">
                    <button type="button" class="app-message-btn app-confirm-yes">
                        Tak
                    </button>

                    <button type="button" class="app-message-btn app-confirm-no">
                        Anuluj
                    </button>
                </div>
            </div>
        `;

        document.body.appendChild(overlay);

        const yesButton = overlay.querySelector(".app-confirm-yes");
        const noButton = overlay.querySelector(".app-confirm-no");

        yesButton.addEventListener("click", () => {
            overlay.remove();
            resolve(true);
        });

        noButton.addEventListener("click", () => {
            overlay.remove();
            resolve(false);
        });

        overlay.addEventListener("click", (event) => {
            if (event.target === overlay) {
                overlay.remove();
                resolve(false);
            }
        });
    });
}