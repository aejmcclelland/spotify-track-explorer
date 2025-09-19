"use client";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { getJson } from "@/lib/api";
import Link from "next/link";

type Me = { username: string; roles: string[] };

export default function MePage() {
  const router = useRouter();
  const [me, setMe] = useState<Me | null>(null);
  const [err, setErr] = useState<string | null>(null);
  const [linking, setLinking] = useState(false);

  useEffect(() => {
    const token =
      typeof window !== "undefined"
        ? localStorage.getItem("access_token")
        : null;
    if (!token) {
      router.replace("/login");
      return;
    }

    let mounted = true;
    getJson<Me>("/api/me")
      .then((data) => {
        if (mounted) setMe(data);
      })
      .catch((e: Error) => {
        if (!mounted) return;
        const msg = e.message || "Failed to load";
        if (msg.startsWith("401")) {
          localStorage.removeItem("access_token");
          setErr("Not signed in");
          router.replace("/login");
        } else {
          setErr(msg);
        }
      });
    return () => {
      mounted = false;
    };
  }, [router]);

  if (err) return <div className="alert">{err}</div>;
  if (!me) return <div className="loading loading-spinner loading-md" />;

  async function connectSpotify() {
    try {
      setLinking(true);
      const { authorize_url } = await getJson<{ authorize_url: string }>(
        "/api/spotify/authorize"
      );
      window.location.href = authorize_url;
    } catch (e: any) {
      setLinking(false);
      alert(e?.message ?? "Failed to start Spotify link");
    }
  }

  return (
    <div className="prose">
      <h1>Account</h1>
      <p>
        <b>User:</b> {me.username}
      </p>
      <p>
        <b>Roles:</b> {me.roles && me.roles.length ? me.roles.join(", ") : "—"}
      </p>
      <button
        className="btn btn-outline"
        onClick={connectSpotify}
        disabled={linking}
      >
        {linking ? "Connecting…" : "Connect Spotify"}
      </button>
      <div className="mt-3">
        <Link className="btn" href="/spotify">
          View Spotify Playlists
        </Link>
      </div>
    </div>
  );
}
