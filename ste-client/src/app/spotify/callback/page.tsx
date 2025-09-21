"use client";
import { Suspense, useEffect, useState } from "react";
import { useSearchParams, useRouter } from "next/navigation";
import { postJson } from "@/lib/api";
import { SpotifyExchangeResponse } from "@/types/spotify";

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

  useEffect(() => {
    const code = sp.get("code");
    const state = sp.get("state");

    if (!code) {
      setError("Missing code in callback URL.");
      return;
    }

    (async () => {
      try {
        const res = await postJson<SpotifyExchangeResponse>("/api/spotify/exchange", { code, state });
        if (res.linked) {
          setMsg("Spotify linked! Redirecting…");
          router.replace("/spotify?linked=1"); // land on playlists page and show success toast
        } else if ((res as any)?.error) {
          setError((res as any).error);
        } else {
          setError("Unexpected response linking Spotify.");
        }
      } catch (err: unknown) {
        const message = err instanceof Error ? err.message : "Failed to link Spotify";
        setError(message);
      }
    })();
  }, [sp, router]);

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
