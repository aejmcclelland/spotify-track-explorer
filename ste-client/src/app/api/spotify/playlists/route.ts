// src/app/api/spotify/playlists/route.ts
import { NextRequest } from "next/server";

const API_BASE = process.env.NEXT_PUBLIC_API_BASE ?? "http://localhost:8080"; // set NEXT_PUBLIC_API_BASE in ste-client/.env.local

async function toJsonResponse(res: Response) {
  const ct = res.headers.get("content-type") || "";
  const text = await res.text();
  if (ct.includes("application/json")) {
    return new Response(text, {
      status: res.status,
      headers: { "content-type": "application/json" },
    });
  }
  // Upstream returned HTML or something else – wrap into JSON so the client UI doesn't render a huge HTML blob
  const safe = text.length > 400 ? text.slice(0, 400) + "…" : text;
  return new Response(
    JSON.stringify({
      code: "UPSTREAM_ERROR",
      status: res.status,
      message: safe,
    }),
    { status: res.status, headers: { "content-type": "application/json" } }
  );
}

export async function GET(req: NextRequest) {
  const incomingAuth = req.headers.get("authorization") ?? req.headers.get("Authorization") ?? "";
  const incomingCookie = req.headers.get("cookie") ?? req.headers.get("Cookie") ?? "";

  const headers = new Headers();
  headers.set("accept", "application/json");
  if (incomingAuth) headers.set("authorization", incomingAuth);
  if (incomingCookie) headers.set("cookie", incomingCookie);

  try {
    // Try canonical backend path first
    let res = await fetch(`${API_BASE}/api/spotify/playlists`, {
      method: "GET",
      headers,
      cache: "no-store",
    });

    if (res.status === 404) {
      // Fallback to alternate path used by some servers
      res = await fetch(`${API_BASE}/api/playlists`, {
        method: "GET",
        headers,
        cache: "no-store",
      });
    }

    return toJsonResponse(res);
  } catch (e: any) {
    const message = e?.message || "Upstream unreachable";
    return new Response(
      JSON.stringify({ code: "UPSTREAM_UNREACHABLE", message }),
      { status: 502, headers: { "content-type": "application/json" } }
    );
  }
}
