import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  images: {
    remotePatterns: [
      {
        protocol: "https",
        hostname: "**.scdn.co",
      },
      {
        protocol: "https",
        hostname: "**.spotifycdn.com",
      },
      {
        protocol: "https",
        hostname: "i.scdn.co",
        port: "",
        pathname: "/image/**",
      },
      {
        protocol: "https",
        hostname: "mosaic.scdn.co",
        port: "",
        pathname: "/**",
      },
      {
        protocol: "https",
        hostname: "image-cdn-ak.spotifycdn.com",
        port: "",
        pathname: "/image/**",
      },
      {
        protocol: "https",
        hostname: "image-cdn-fa.spotifycdn.com",
        port: "",
        pathname: "/image/**",
      },
      {
        protocol: "https",
        hostname: "image-cdn.spotifycdn.com",
        port: "",
        pathname: "/image/**",
      },
    ],
  },
  // Optional: quiet the dev cross-origin warning (Next 15+)
  allowedDevOrigins: ["http://127.0.0.1:3000"],
};

export default nextConfig;
