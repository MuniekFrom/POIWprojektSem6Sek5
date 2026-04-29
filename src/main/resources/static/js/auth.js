function saveAuthData(token, role) {
    localStorage.setItem("token", token);
    localStorage.setItem("role", role);
}

function getToken() {
    return localStorage.getItem("token");
}

function getRole() {
    return localStorage.getItem("role");
}

function isLoggedIn() {
    return !!getToken();
}

function logout() {
    localStorage.clear();
    window.location.href = "/login.html";
}

function redirectByRole() {
    const role = getRole();

    if (role === "DOCTOR") {
        window.location.href = "/doctor-dashboard.html";
    } else if (role === "PATIENT") {
        window.location.href = "/patient-dashboard.html";
    } else if (role === "ADMIN") {
        window.location.href = "/admin-dashboard.html";
    } else {
        window.location.href = "/login.html";
    }
}

function requireAuth(requiredRole = null) {
    const token = getToken();
    const role = getRole();

    if (!token) {
        window.location.href = "/login.html";
        return;
    }

    if (requiredRole && role !== requiredRole) {
        window.location.href = "/login.html";
    }
}