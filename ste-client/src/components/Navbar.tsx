// src/components/Navbar.tsx
"use client";

import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { useAuth } from "@/lib/useAuth";
import { clearToken } from "@/lib/auth";
import { useEffect, useState } from "react";

const NavLink = ({ href, label }: { href: string; label: string }) => {
  const pathname = usePathname();
  const isActive =
    pathname === href || (href !== "/" && pathname?.startsWith(href));
  return (
    <Link
      href={href}
      className={`btn btn-ghost ${isActive ? "btn-active" : ""}`}
    >
      {label}
    </Link>
  );
};

export default function Navbar() {
  const [mounted, setMounted] = useState(false);
  useEffect(() => {
    setMounted(true);
  }, []);

  const { authed } = useAuth();
  const router = useRouter();

  const onLogout = () => {
    clearToken(); // drop token & broadcast "ste-auth"
    router.replace("/login");
  };

  return (
    <div className="navbar bg-base-100 border-b">
      <div className="flex-1">
        <Link href="/" className="btn btn-ghost text-xl">
          Track Explorer
        </Link>
      </div>
      <div className="flex gap-2">
        {mounted && authed ? (
          <NavLink href="/spotify" label="Spotify" />
        ) : (
          <NavLink href="/" label="Spotify" />
        )}
        {!mounted ? (
          <>
            <span className="skeleton h-10 w-20" />
            <span className="skeleton h-10 w-24" />
          </>
        ) : !authed ? (
          <>
            <NavLink href="/login" label="Login" />
            <NavLink href="/register" label="Register" />
          </>
        ) : (
          <>
            <button onClick={onLogout} className="btn btn-ghost">
              Logout
            </button>
            <NavLink href="/me" label="Profile" />
          </>
        )}
      </div>
    </div>
  );
}
