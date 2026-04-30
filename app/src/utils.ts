export function saveCurrentPath() {
    if (window.location.pathname === "/login" || window.location.hash.includes("state") || window.location.hash.includes("code")) {
        return;
    }

    sessionStorage.setItem("redirectPath", window.location.pathname + window.location.search + window.location.hash);
}