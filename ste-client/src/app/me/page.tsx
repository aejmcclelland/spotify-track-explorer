"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { getJson, delJson } from "@/lib/api"; // you already have getJson; add a small delJson if needed

type Me = { username: string; roles: string[] };
type SpotifyProfile = { display_name?: string };

export default function MePage() {
  const [me, setMe] = useState<Me | null>(null);
  const [err, setErr] = useState<string | null>(null);
  const [linking, setLinking] = useState(false);
  const [spProfile, setSpProfile] = useState<SpotifyProfile | null>(null);
  const [disconnecting, setDisconnecting] = useState(false);

  useEffect(() => {
    (async () => {
      try {
        const user = await getJson<Me>("/api/me");
        setMe(user);
      } catch (err: unknown) {
        const msg = err instanceof Error ? err.message : "Not signed in";
        setErr(msg);
      }
    })();
  }, []);

  // check spotify link status after we know user is signed in
  useEffect(() => {
    if (!me) return;
    (async () => {
      try {
        const prof = await getJson<SpotifyProfile>("/api/spotify/profile");
        setSpProfile(prof);
      } catch {
        setSpProfile(null); // not linked or 401 from Spotify
      }
    })();
  }, [me]);

  const connectSpotify = async () => {
    setLinking(true);
    try {
      const data = await getJson<{ authorize_url: string; state: string }>(
        "/api/spotify/authorize"
      );
      window.location.href = data.authorize_url;
    } catch (err: unknown) {
      const msg =
        err instanceof Error ? err.message : "Failed to start Spotify Auth";
      setErr(msg);
    } finally {
      setLinking(false);
    }
  };

  const disconnectSpotify = async () => {
    setDisconnecting(true);
    try {
      await delJson("/api/spotify/link");
      setSpProfile(null);
    } catch (err: unknown) {
      const msg =
        err instanceof Error ? err.message : "Failed to disconnect";
      setErr(msg);
    } finally {
      setDisconnecting(false);
    }
  };

  if (err) return <div className="alert alert-error">{err}</div>;
  if (!me) return <div className="loading loading-spinner" />;

  return (
    <div className="p-4 space-y-4">
      <h1 className="text-2xl font-bold">Account</h1>
      <div>
        Username: <b>{me.username}</b>
      </div>
      <div>
        Roles: <b>{me.roles.join(", ")}</b>
      </div>

      <div className="divider" />

      <div className="flex items-center gap-3">
        {spProfile ? (
          <>
            <span className="badge badge-success">
              Linked as {spProfile.display_name ?? "Spotify user"}
            </span>
            <button
              className="btn btn-outline btn-sm"
              onClick={disconnectSpotify}
              disabled={disconnecting}
            >
              {disconnecting ? "Disconnecting…" : "Disconnect Spotify"}
            </button>
            <Link className="btn btn-sm" href="/spotify">
              View Spotify playlists
            </Link>
          </>
        ) : (
          <button
            className="btn btn-outline"
            onClick={connectSpotify}
            disabled={linking}
          >
            {linking ? "Connecting…" : "Connect Spotify"}
          </button>
        )}
      </div>
    </div>
  );
}
