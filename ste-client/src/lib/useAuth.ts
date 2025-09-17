"use client";
import { useEffect, useState } from "react";
import { isLoggedIn, getToken } from "@/lib/auth";

export function useAuth() {
  const [authed, setAuthed] = useState<boolean>(isLoggedIn());
  const [token, setTokenState] = useState<string | null>(getToken());

  useEffect(() => {
    const update = () => {
      setAuthed(isLoggedIn());
      setTokenState(getToken());
    };
    window.addEventListener("ste-auth", update);
    window.addEventListener("storage", update);
    return () => {
      window.removeEventListener("ste-auth", update);
      window.removeEventListener("storage", update);
    };
  }, []);

  return { authed, token };
}
