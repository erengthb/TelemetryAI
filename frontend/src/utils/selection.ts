const PROJECT_KEY = "telemetryai.projectId";
const ENV_KEY = "telemetryai.envName";

type IdItem = { id: string };
type EnvItem = { envName: string };

export function readProjectId() {
  return window.localStorage.getItem(PROJECT_KEY) ?? "";
}

export function saveProjectId(projectId: string) {
  if (!projectId) {
    return;
  }
  window.localStorage.setItem(PROJECT_KEY, projectId);
}

export function readEnvName() {
  return window.localStorage.getItem(ENV_KEY) ?? "";
}

export function saveEnvName(envName: string) {
  if (!envName) {
    return;
  }
  window.localStorage.setItem(ENV_KEY, envName);
}

export function pickProjectId(projects: IdItem[]) {
  const saved = readProjectId();
  if (saved && projects.some((project) => project.id === saved)) {
    return saved;
  }
  return projects[0]?.id ?? "";
}

export function pickEnvName(envs: EnvItem[]) {
  const saved = readEnvName();
  if (saved && envs.some((env) => env.envName === saved)) {
    return saved;
  }
  return envs[0]?.envName ?? "";
}
