"use client";

import { useEffect, useState } from "react";
import { getJson } from "@/lib/api";
import Link from "next/link";

type Playlist = {
  id: string;
  name: string;
  images?: { url: string; width?: number; height?: number }[];
  tracks?: { total: number };
  external_urls?: { spotify?: string };
  owner?: { display_name?: string };
};
type SpotifyPlaylists = { items: Playlist[] };

export default function SpotifyPage() {
  const [data, setData] = useState<SpotifyPlaylists | null>(null);
  const [err, setErr] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    (async () => {
      try {
        const res = await getJson<SpotifyPlaylists>("/api/spotify/playlists");
        setData(res);
      } catch (e: any) {
        setErr(e?.message ?? "Failed to load playlists");
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  if (loading) return <div className="loading loading-spinner loading-md" />;
  if (err) return <div className="alert alert-error">{err}</div>;
  if (!data || !data.items?.length)
    return <div className="alert">No playlists found.</div>;

  return (
    <div className="p-4">
      <h1 className="text-2xl font-bold mb-4">Your Spotify Playlists</h1>
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
        {data.items.map((p) => {
          const img = p.images?.[0]?.url;
          return (
            <div key={p.id} className="card bg-base-200 shadow">
              {img && (
                <img
                  src={img}
                  alt={p.name}
                  className="w-full h-48 object-cover"
                />
              )}
              <div className="card-body">
                <h2 className="card-title">{p.name}</h2>
                <p className="text-sm opacity-80">
                  {p.owner?.display_name ? `by ${p.owner.display_name}` : ""}
                  {p.tracks?.total != null ? ` • ${p.tracks.total} tracks` : ""}
                </p>
                <div className="card-actions justify-end">
                  {p.external_urls?.spotify && (
                    <a
                      className="btn btn-sm"
                      href={p.external_urls.spotify}
                      target="_blank"
                      rel="noreferrer"
                    >
                      Open in Spotify
                    </a>
                  )}
                </div>
              </div>
            </div>
          );
        })}
      </div>
      <div className="mt-6">
        <Link className="btn btn-ghost" href="/me">
          Back to Account
        </Link>
      </div>
    </div>
  );
}
