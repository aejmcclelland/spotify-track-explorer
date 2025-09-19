"use client";

import { useEffect, useState } from "react";
import { useSearchParams, useRouter } from "next/navigation";
import { postJson } from "@/lib/api"; // uses your token-attaching fetch

export default function SpotifyCallbackPage() {
  const sp = useSearchParams();
  const router = useRouter();
  const [msg, setMsg] = useState("Finishing Spotify link…");

  useEffect(() => {
    const code = sp.get("code");
    const state = sp.get("state");

    if (!code) {
      setMsg("Missing code in callback URL.");
      return;
    }

    (async () => {
      try {
        const res = await postJson("/api/spotify/exchange", { code, state });
        if ((res as any).linked) {
          setMsg("Spotify linked! Redirecting…");
          router.replace("/me");
        } else {
          setMsg("Unexpected response linking Spotify.");
        }
      } catch (e: any) {
        setMsg(e?.message ?? "Failed to link Spotify.");
      }
    })();
  }, [sp, router]);

  return <div className="p-6">{msg}</div>;
}
