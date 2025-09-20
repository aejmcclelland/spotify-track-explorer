import type { Metadata } from "next";
import { Nunito, Inter } from "next/font/google";
import { Toaster } from "react-hot-toast";
import Footer from "@/components/Footer";
import "@/app/globals.css";

import Navbar from "@/components/Navbar";

const nunito = Nunito({
  variable: "--font-nunito",
  subsets: ["latin"],
});
const inter = Inter({
  variable: "--font-inter",
  subsets: ["latin"],
  display: "swap",
});

export const metadata: Metadata = {
  title: "Spotify Track Explorer",
  description: "Explore your Spotify tracks with ease.",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html data-theme="cupcake" lang="en">
      <body
        className={`${inter.variable} ${nunito.variable} font-sans subpixel-antialiased flex flex-col min-h-screen`}
      >
        <Navbar />
         <main className="flex-1 container mx-auto px-4 py-6">
          {children}
        </main>
        <Footer />
        <Toaster position="top-center" reverseOrder={false} />
      </body>
    </html>
  );
}
