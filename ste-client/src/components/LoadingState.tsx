// src/components/LoadingState.tsx
export default function LoadingState({
  label = "Loading…",
}: {
  label?: string;
}) {
  return (
    <div className="flex items-center gap-3">
      <span className="loading loading-spinner loading-md" />
      <span>{label}</span>
    </div>
  );
}
