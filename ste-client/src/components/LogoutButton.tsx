"use client";
import { clearToken, isLoggedIn } from "@/lib/auth";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";

export default function LogoutButton() {
  const router = useRouter();
  const [loggedIn, setLoggedIn] = useState(false);

  useEffect(() => setLoggedIn(isLoggedIn()), []);

  if (!loggedIn) return null;
  return (
    <button
      className="btn btn-outline"
      onClick={() => {
        clearToken();
        router.push("/login");
      }}
    >
      Log out
    </button>
  );
}
