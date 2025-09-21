"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import Image from "next/image";
import { useRouter, useSearchParams } from "next/navigation";
import LoadingState from "@/components/LoadingState";
import ErrorState from "@/components/ErrorState";
import ConnectSpotifyCard from "@/components/ConnectSpotifyCard";
import EmptyState from "@/components/EmptyState";
import { getToken } from "@/lib/auth";
import { PlaylistsResponse } from "@/types/spotify";

export default function SpotifyPage() {
  const [data, setData] = useState<PlaylistsResponse | null>(null);
  const [err, setErr] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [mounted, setMounted] = useState(false);
  const [notLinked, setNotLinked] = useState(false);
  const router = useRouter();
  const sp = useSearchParams();
  const [linkedMsg, setLinkedMsg] = useState<string | null>(null);

  useEffect(() => {
    setMounted(true);
  }, []);

  useEffect(() => {
    if (!mounted) return;
    if (sp.get("linked") === "1") {
      setLinkedMsg("Spotify linked successfully 🎉");
      // Clean the URL so refreshes don't keep showing the toast
      router.replace("/spotify");
    }
  }, [mounted, sp, router]);

  useEffect(() => {
    if (!mounted) return;

    (async () => {
      try {
        const token = getToken();
        const res = await fetch("/api/spotify/playlists", {
          headers: token ? { Authorization: `Bearer ${token}` } : undefined,
          credentials: "include",
        });

        // Handle auth + link states by status code only
        if (res.status === 409) {
          // not linked -> send to profile to link
          router.replace("/me");
          return;
        }
        if (res.status === 401) {
          // unauthorized → send home/login
          router.replace("/");
          return;
        }
        if (!res.ok) {
          // Expect JSON from our proxy even on errors
          let payload: any = null;
          try {
            payload = await res.json();
          } catch {
            /* ignore */
          }
          const msg =
            payload?.message ||
            payload?.error ||
            `Failed to load playlists (${res.status})`;
          throw new Error(msg);
        }

        // Parse the trimmed DTO our server returns
        const json = (await res.json()) as PlaylistsResponse;
        setData(json);
        return;
      } catch (err: unknown) {
        const msg =
          err instanceof Error ? err.message : "Failed to load playlists";
        // Do not redirect on guessed errors; surface the message instead
        if (msg.includes("SPOTIFY_NOT_LINKED")) {
          setNotLinked(true);
        } else {
          setErr(msg);
        }
      } finally {
        setLoading(false);
      }
    })();
  }, [mounted, router]);

  if (!mounted) return <LoadingState label="Checking your session…" />;
  if (loading) return <LoadingState label="Loading your Spotify data…" />;
  if (notLinked) return <ConnectSpotifyCard />;
  if (err) return <ErrorState message={err} />;
  if (data && Array.isArray(data.items) && data.items.length === 0) {
    return <EmptyState message="No playlists found yet." />;
  }
  if (!data) return <LoadingState label="Preparing your playlists…" />;

  return (
    <div className="p-4">
      <h1 className="text-2xl font-bold mb-4">Your Spotify Playlists</h1>
      {linkedMsg && (
        <div role="alert" className="alert alert-success mb-4">
          <span>{linkedMsg}</span>
        </div>
      )}
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
