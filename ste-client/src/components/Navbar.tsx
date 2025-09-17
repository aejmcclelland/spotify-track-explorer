"use client";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useAuth } from "@/lib/useAuth";
import { clearToken } from "@/lib/auth";

export default function Navbar() {
  const router = useRouter();
  const { authed } = useAuth();

  return (
    <nav className="navbar bg-base-200">
      <div className="container mx-auto flex gap-2 p-2">
        <Link className="btn btn-ghost" href="/">
          Home
        </Link>
        {authed ? (
          <>
            <Link className="btn btn-ghost" href="/me">
              Me
            </Link>
            <button
              className="btn btn-outline"
              onClick={() => {
                clearToken();
                router.replace("/login");
              }}
            >
              Log out
            </button>
          </>
        ) : (
          <>
            <Link className="btn btn-ghost" href="/register">
              Register
            </Link>
            <Link className="btn btn-ghost" href="/login">
              Login
            </Link>
          </>
        )}
      </div>
    </nav>
  );
}
