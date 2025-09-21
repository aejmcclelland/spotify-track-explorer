"use client";
import { Suspense, useEffect, useState } from "react";
import { useSearchParams, useRouter } from "next/navigation";
import { getToken } from "@/lib/auth";

// Wrapper ensures useSearchParams is inside a Suspense boundary (Next.js requirement)
export default function SpotifyCallbackPage() {
  return (
    <Suspense fallback={<div className="p-6">Finishing Spotify link…</div>}>
      <CallbackClient />
    </Suspense>
  );
}

function CallbackClient() {
  const sp = useSearchParams();
  const router = useRouter();
  const [msg, setMsg] = useState("Finishing Spotify link…");
  const [error, setError] = useState<string | null>(null);
  const [done, setDone] = useState(false);

  useEffect(() => {
    if (done) return; // guard against double-run

    const code = sp.get("code");
    const state = sp.get("state");

    if (!code) {
      setError("Missing code in callback URL.");
      return;
    }

    (async () => {
      try {
        const token = getToken();
        const res = await fetch("/api/spotify/exchange", {
          method: "POST",
          headers: {
            "content-type": "application/json",
            ...(token ? { Authorization: `Bearer ${token}` } : {}),
          },
          credentials: "include",
          body: JSON.stringify({ code, state }),
        });

        const payload = await res.json().catch(() => null);
        if (!res.ok) {
          const msg = payload?.message || payload?.error || `Exchange failed (${res.status})`;
          throw new Error(msg);
        }

        if (payload?.linked) {
          setMsg("Spotify linked! Redirecting…");
          setDone(true);
          router.replace("/spotify?linked=1");
          return;
        }

        setError(payload?.error || "Unexpected response linking Spotify.");
      } catch (err: unknown) {
        const message = err instanceof Error ? err.message : "Failed to link Spotify";
        setError(message);
      }
    })();
  }, [sp, router, done]);

  return (
    <div className="p-6">
      {error ? (
        <div role="alert" className="alert alert-error">
          <span>{error}</span>
        </div>
      ) : (
        <span>{msg}</span>
      )}
    </div>
  );
}
