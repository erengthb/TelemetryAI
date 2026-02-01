import { clearToken, getToken } from "./auth";

type QueryValue = string | number | boolean | null | undefined;

export class ApiError extends Error {
  status: number;
  payload?: unknown;

  constructor(status: number, message: string, payload?: unknown) {
    super(message);
    this.status = status;
    this.payload = payload;
  }
}

const BASE_URL =
  (import.meta as { env?: { VITE_API_BASE_URL?: string } }).env
    ?.VITE_API_BASE_URL ?? "http://localhost:8080";

function buildQuery(params?: Record<string, QueryValue>) {
  if (!params) {
    return "";
  }
  const entries = Object.entries(params).filter(([, value]) => value !== undefined && value !== null);
  if (!entries.length) {
    return "";
  }
  const query = entries
    .map(([key, value]) => `${encodeURIComponent(key)}=${encodeURIComponent(String(value))}`)
    .join("&");
  return `?${query}`;
}

export async function request<T>(
  path: string,
  options: RequestInit & { query?: Record<string, QueryValue>; auth?: boolean } = {}
) {
  const { query, auth = true, headers, ...rest } = options;
  const url = `${BASE_URL}${path}${buildQuery(query)}`;
  const token = auth ? getToken() : null;

  const response = await fetch(url, {
    ...rest,
    headers: {
      ...(headers ?? {}),
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...(rest.body ? { "Content-Type": "application/json" } : {}),
    },
  });

  const contentType = response.headers.get("content-type") ?? "";
  let payload: unknown = null;
  if (response.status !== 204) {
    payload = contentType.includes("application/json")
      ? await response.json()
      : await response.text();
  }

  if (!response.ok) {
    if (response.status === 401 || response.status === 403) {
      clearToken();
      if (!window.location.pathname.startsWith("/login")) {
        window.location.href = "/login";
      }
    }
    const message =
      typeof payload === "string" && payload.trim().length > 0
        ? payload
        : `HTTP ${response.status}`;
    throw new ApiError(response.status, message, payload);
  }

  return payload as T;
}
