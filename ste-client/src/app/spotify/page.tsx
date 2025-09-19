"use client";

import { useEffect, useState } from "react";
import { getJson } from "@/lib/api";
import Link from "next/link";
import Image from "next/image";

type Playlist = {
  id: string;
  name: string;
  owner?: string; // trimmed by backend
  tracks?: number; // trimmed total
  imageUrl?: string; // trimmed first image url
  externalUrl?: string; // trimmed external spotify url
};
type SpotifyPlaylists = {
  items: Playlist[];
  nextOffset?: number | null;
  hasMore: boolean;
};

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
          const img = p.imageUrl;
          return (
            <div key={p.id} className="card bg-base-200 shadow">
              {img ? (
                <div className="w-full h-48 relative">
                  <Image
                    src={img}
                    alt={p.name}
                    fill
                    sizes="(max-width: 768px) 100vw, (max-width: 1200px) 50vw, 33vw"
                    className="object-cover rounded-t-box"
                  />
                </div>
              ) : (
                <div className="w-full h-48 bg-base-300 grid place-items-center text-sm opacity-70">
                  No image
                </div>
              )}
              <div className="card-body">
                <h2 className="card-title">{p.name}</h2>
                <p className="text-sm opacity-80">
                  {p.owner ? `by ${p.owner}` : ""}
                  {typeof p.tracks === "number" ? ` • ${p.tracks} tracks` : ""}
                </p>
                <div className="card-actions justify-end">
                  {p.externalUrl && (
                    <a
                      className="btn btn-sm"
                      href={p.externalUrl}
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
