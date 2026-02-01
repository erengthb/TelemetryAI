import { request } from "./client";
import {
  ApiKeyCreateResponse,
  ApiKeyResponse,
  CreateOrgRequest,
  CreateProjectRequest,
  DashboardFunnelResponse,
  DashboardOverviewResponse,
  EnvironmentResponse,
  EventListResponse,
  LoginRequest,
  LoginResponse,
  MeResponse,
  OrgResponse,
  ProjectResponse,
  QuarantineListResponse,
  AiReportJson,
} from "./types";

export const api = {
  login: (payload: LoginRequest) =>
    request<LoginResponse>("/v1/auth/login", {
      method: "POST",
      body: JSON.stringify(payload),
      auth: false,
    }),
  me: () => request<MeResponse>("/v1/auth/me"),

  listOrgs: () => request<OrgResponse[]>("/v1/orgs"),
  createOrg: (payload: CreateOrgRequest) =>
    request<OrgResponse>("/v1/orgs", {
      method: "POST",
      body: JSON.stringify(payload),
    }),
  listProjects: () => request<ProjectResponse[]>("/v1/projects"),
  createProject: (payload: CreateProjectRequest) =>
    request<ProjectResponse>("/v1/projects", {
      method: "POST",
      body: JSON.stringify(payload),
    }),
  listEnvironments: (projectId: string) =>
    request<EnvironmentResponse[]>(`/v1/projects/${projectId}/environments`),

  listApiKeys: (projectId: string, envName?: string) =>
    request<ApiKeyResponse[]>(`/v1/projects/${projectId}/keys`, {
      query: envName ? { env: envName } : undefined,
    }),
  rotateApiKey: (projectId: string, envName: string) =>
    request<ApiKeyCreateResponse>(`/v1/projects/${projectId}/keys/rotate`, {
      method: "POST",
      query: { env: envName },
    }),
  revokeApiKey: (projectId: string, envName: string) =>
    request<void>(`/v1/projects/${projectId}/keys/revoke`, {
      method: "POST",
      query: { env: envName },
    }),

  getDashboardOverview: (projectId: string, envName: string, range?: string) =>
    request<DashboardOverviewResponse>(`/v1/projects/${projectId}/dashboard/overview`, {
      query: { env: envName, range },
    }),
  getDashboardFunnel: (projectId: string, envName: string, range?: string) =>
    request<DashboardFunnelResponse>(`/v1/projects/${projectId}/dashboard/funnel`, {
      query: { env: envName, range },
    }),

  getSchemaCurrent: (projectId: string) =>
    request<unknown>(`/v1/projects/${projectId}/schema/current`),
  exportSchema: (projectId: string) =>
    request<unknown>(`/v1/projects/${projectId}/schema/export`),
  importSchema: (projectId: string, schema: unknown) =>
    request<unknown>(`/v1/projects/${projectId}/schema/import`, {
      method: "POST",
      body: JSON.stringify(schema),
    }),

  listQuarantine: (projectId: string, envName: string, range?: string) =>
    request<QuarantineListResponse>(`/v1/projects/${projectId}/quarantine`, {
      query: { env: envName, range },
    }),

  listEvents: (projectId: string, envName: string, range?: string) =>
    request<EventListResponse>(`/v1/projects/${projectId}/events`, {
      query: { env: envName, range },
    }),

  listAiReportsDaily: (projectId: string, envName: string, range?: string) =>
    request<AiReportJson[]>(`/v1/projects/${projectId}/reports/daily`, {
      query: { env: envName, range },
    }),
  listAiReportsWeekly: (projectId: string, envName: string, range?: string) =>
    request<AiReportJson[]>(`/v1/projects/${projectId}/reports/weekly`, {
      query: { env: envName, range },
    }),
};
