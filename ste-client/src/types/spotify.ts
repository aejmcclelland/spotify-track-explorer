// Shared Spotify-related types for the client app

// /api/spotify/authorize response
export type AuthorizeResponse = {
  authorize_url: string;
  state: string;
};

// /api/spotify/exchange response
export type SpotifyExchangeResponse = {
  linked: boolean;
};

// Minimal profile fields we actually render
export type SpotifyProfile = {
  id: string;
  display_name?: string;
  email?: string;
  product?: string;
  images?: Array<{ url: string; height?: number; width?: number }>;
};

// A single playlist card item (already trimmed on the server)
export type PlaylistItem = {
  id: string;
  name: string;
  owner: string;
  tracks: number;
  imageUrl?: string | null;
  externalUrl?: string | null;
};

// Paginated playlists response from our API
export type PlaylistsResponse = {
  items: PlaylistItem[];
  nextOffset: number | null;
  hasMore: boolean;
};

// Common API error envelope (if you choose to use it)
export type ApiError = {
  error: string;
  message?: string;
};
