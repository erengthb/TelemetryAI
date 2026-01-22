export type LoginRequest = {
  email: string;
  password: string;
};

export type LoginResponse = {
  token: string;
  userId: string;
  email: string;
  role: string;
};

export type MeResponse = {
  userId: string;
  email: string;
  role: string;
};

export type OrgResponse = {
  id: string;
  name: string;
  status: string;
  createdAt: string;
};

export type ProjectResponse = {
  id: string;
  orgId: string;
  name: string;
  createdAt: string;
};

export type CreateProjectRequest = {
  orgId: string;
  name: string;
};

export type EnvironmentResponse = {
  id: string;
  projectId: string;
  envName: string;
};

export type ApiKeyResponse = {
  id: string;
  envName: string;
  maskedKey: string;
  status: string;
  createdAt: string;
  revokedAt: string | null;
};

export type ApiKeyCreateResponse = {
  id: string;
  envName: string;
  apiKey: string;
  maskedKey: string;
  createdAt: string;
};

export type DailyMetricPoint = {
  date: string;
  totalEvents: number;
  uniquePlayers: number;
  sessionsStarted: number;
};

export type DashboardOverviewResponse = {
  totalEvents: number;
  uniquePlayers: number;
  sessionsStarted: number;
  series: DailyMetricPoint[];
};

export type FunnelRowResponse = {
  levelId: string;
  starts: number;
  endsSuccess: number;
  endsFail: number;
  endsQuit: number;
  avgDurationSec: number | null;
};

export type DashboardFunnelResponse = {
  rows: FunnelRowResponse[];
};

export type QuarantineItemResponse = {
  id: number;
  envName: string;
  eventId: string;
  eventName: string;
  receivedAt: string;
  reasons: string[];
  schemaVersion: number | null;
  clientSdk: string | null;
  clientSdkVersion: string | null;
  engine: string | null;
  engineVersion: string | null;
  buildVersion: string | null;
  platform: string | null;
  rawEvent: unknown;
};

export type QuarantineListResponse = {
  items: QuarantineItemResponse[];
};

export type AiReportJson = Record<string, unknown>;
