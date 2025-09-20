// src/lib/toast.ts
import toast from "react-hot-toast";

export const toastSuccess = (msg: string) => toast.success(msg);
export const toastError = (msg: string) => toast.error(msg);
export const toastLoading = (msg: string) => toast.loading(msg);
