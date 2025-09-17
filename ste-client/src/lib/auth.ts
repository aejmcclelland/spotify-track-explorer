export const TOKEN_KEY = "access_token";

export function setToken(token: string) {
  if (typeof window === "undefined") return;
  localStorage.setItem(TOKEN_KEY, token);
  // notify all tabs/components
  window.dispatchEvent(new CustomEvent("ste-auth"));
}

export function getToken(): string | null {
  if (typeof window === "undefined") return null;
  return localStorage.getItem(TOKEN_KEY);
}

export function clearToken() {
  if (typeof window === "undefined") return;
  localStorage.removeItem(TOKEN_KEY);
  window.dispatchEvent(new CustomEvent("ste-auth"));
}

export function isLoggedIn() {
  return !!getToken();
}
