"use client";
import { useState, useEffect } from "react";
import { postJson } from "@/lib/api";
import { useAuth } from "@/lib/useAuth";
import { useRouter } from "next/navigation";
import { setToken } from "@/lib/auth";

export default function LoginPage() {
  const router = useRouter();
  const { authed } = useAuth();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [email, setEmail] = useState("");
  const [msg, setMsg] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (authed) router.replace("/");
  }, [authed, router]);

  async function onSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setLoading(true);
    setMsg(null);
    try {
      const data = await postJson<{ access_token: string; token_type: string }>(
        "/api/auth/token",
        { username, password }
      );
      setToken(data.access_token);
      router.replace("/"); // land on Home after login
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : "Login failed";
      setMsg(
        msg.includes("Invalid email or password")
          ? "Invalid email or password"
          : msg || "Login failed"
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="max-w-md mx-auto">
      <h1 className="text-2xl font-semibold mb-4">Sign in</h1>
      <form className="space-y-4" onSubmit={onSubmit}>
        <input
          className="input input-bordered w-full"
          placeholder="email"
          type="email"
          value={username}
          onChange={(e: React.ChangeEvent<HTMLInputElement>) =>
            setEmail(e.target.value)
          }
          required
        />
        <input
          className="input input-bordered w-full"
          placeholder="password"
          type="password"
          value={password}
          onChange={(e: React.ChangeEvent<HTMLInputElement>) =>
            setPassword(e.target.value)
          }
          required
        />
        <button
          className={`btn btn-primary w-full ${loading ? "loading" : ""}`}
          disabled={loading}
        >
          Sign in
        </button>
      </form>
      {msg && <div className="alert mt-4">{msg}</div>}
    </div>
  );
}
