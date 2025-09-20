"use client";

export default function Footer() {
  return (
    <footer className="border-t bg-base-100">
      <div className="container mb-4 px-8 py-6 text-sm opacity-70">
        <p>
          &copy; {new Date().getFullYear()} Spotify Track Explorer by{" "}
          <a
            className="link link-success"
            href="https://amcclelland.net"
            target="_blank"
            rel="noopener noreferrer"
          >
            Andrew McClelland
          </a>
        </p>
      </div>
    </footer>
  );
}
