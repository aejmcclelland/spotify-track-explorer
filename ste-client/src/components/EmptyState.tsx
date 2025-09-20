// src/components/EmptyState.tsx
export default function EmptyState({
  message = "Nothing to show yet.",
}: {
  message?: string;
}) {
  return (
    <div role="status" className="alert">
      <span>{message}</span>
    </div>
  );
}
