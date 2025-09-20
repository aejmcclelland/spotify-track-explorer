'use client'

export default function Footer() {
  return (
    <footer className="footer footer-center p-4 bg-base-200 text-base-content">
      <div>
        <p>
          &copy; {new Date().getFullYear()} Spotify Track Explorer by{"amcclelland.net"}. 
          <a
            className="link link-primary"
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
