const API_BASE = process.env.NEXT_PUBLIC_API_BASE ?? "http://localhost:8080";

function getToken(): string | null {
  if (typeof window === "undefined") return null;
  return localStorage.getItem("access_token") ?? localStorage.getItem("token");
}

export async function postJson<T>(path: string, body: unknown): Promise<T> {
  const token = getToken();
  const headers: Record<string, string> = {
    "Content-Type": "application/json",
  };
  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }
  console.debug("API fetch", path, { hasAuth: !!headers["Authorization"] });
  const res = await fetch(`${API_BASE}${path}`, {
    method: "POST",
    headers,
    body: JSON.stringify(body),
  });
  if (!res.ok) {
    const text = await res.text();
    throw new Error(text || `HTTP ${res.status}`);
  }
  return res.json();
}

export async function getJson<T>(path: string): Promise<T> {
  const token = getToken();
  const headers: Record<string, string> = {};
  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }
  console.debug("API fetch", path, { hasAuth: !!headers["Authorization"] });
  const res = await fetch(`${API_BASE}${path}`, {
    headers,
    cache: "no-store",
  });
  if (!res.ok) {
    const text = await res.text();
    throw new Error(text || `HTTP ${res.status}`);
  }
  return res.json();
}

// DELETE /api/spotify/profile
export async function delJson(path: string): Promise<void> {
  const token = getToken();
  const headers: Record<string, string> = {};
  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  console.debug("API delete", path, { hasAuth: !!headers["Authorization"] });

  const res = await fetch(`${API_BASE}${path}`, {
    method: "DELETE",
    headers,
  });

  if (!res.ok) {
    const text = await res.text();
    throw new Error(text || `HTTP ${res.status}`);
  }
}
