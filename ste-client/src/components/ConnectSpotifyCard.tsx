// src/components/ConnectSpotifyCard.tsx
"use client";

import { useState } from "react";

export default function ConnectSpotifyCard() {
  const [loading, setLoading] = useState(false);

  const startLink = async () => {
    try {
      setLoading(true);
      const res = await fetch("/api/oauth/spotify/authorize", {
        credentials: "include",
      });
      if (!res.ok) throw new Error("Failed to get authorize URL");
      const data = (await res.json()) as { authorize_url: string };
      // Include redirect to /spotify with success marker
      window.location.href = data.authorize_url;
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="card bg-base-100 shadow">
      <div className="card-body">
        <h2 className="card-title">Connect your Spotify account</h2>
        <p className="opacity-80">
          Link your Spotify so we can fetch your profile and playlists.
        </p>
        <div className="card-actions">
          <button
            onClick={startLink}
            className="btn btn-primary"
            disabled={loading}
          >
            {loading ? "Opening Spotify…" : "Connect Spotify"}
          </button>
        </div>
      </div>
    </div>
  );
}
