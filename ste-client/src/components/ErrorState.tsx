// src/components/ErrorState.tsx
export default function ErrorState({
  message = "Something went wrong.",
}: {
  message?: string;
}) {
  return (
    <div role="alert" className="alert alert-error">
      <span>{message}</span>
    </div>
  );
}
