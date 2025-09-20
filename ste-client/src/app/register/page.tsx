"use client";
import { useState, useEffect } from "react";
import { postJson } from "@/lib/api";
import { useAuth } from "@/lib/useAuth";
import { useRouter } from "next/navigation";

export default function RegisterPage() {
  const [email, setEmail] = useState("");
  const router = useRouter();
  const { authed } = useAuth();
  const [password, setPassword] = useState("");
  const [msg, setMsg] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (authed) router.replace("/");
  }, [authed, router]);

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault();
    setLoading(true);
    setMsg(null);
    try {
      const data = await postJson<{ id: number; email: string; role: string }>(
        "/api/auth/register",
        { email, password }
      );
      router.replace("/login");
      setMsg(`Registered ${data.email}`);
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : "Registration failed";
      setMsg(
        msg.includes("409")
          ? "Email already registered"
          : msg || "Registration failed"
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="max-w-md mx-auto">
      <h1 className="text-2xl font-semibold mb-4">Create account</h1>
      <form className="space-y-4" onSubmit={onSubmit}>
        <input
          className="input input-bordered w-full"
          placeholder="email"
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
        <input
          className="input input-bordered w-full"
          placeholder="password"
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <button
          className={`btn btn-primary w-full ${loading ? "loading" : ""}`}
          disabled={loading}
        >
          Register
        </button>
      </form>
      {msg && <div className="alert mt-4">{msg}</div>}
    </div>
  );
}
