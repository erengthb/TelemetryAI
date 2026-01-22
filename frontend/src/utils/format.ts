export function formatEnvLabel(envName?: string | null) {
  if (!envName) {
    return "-";
  }
  const key = envName.toLowerCase();
  if (key === "dev") {
    return "Gelistirme";
  }
  if (key === "stage") {
    return "Test";
  }
  if (key === "prod") {
    return "Uretim";
  }
  return envName;
}

export function formatDateShort(value?: string | null) {
  if (!value) {
    return "-";
  }
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return value;
  }
  return date.toISOString().slice(0, 10);
}

export function formatNumber(value?: number | null) {
  if (value === null || value === undefined) {
    return "0";
  }
  return value.toLocaleString("en-US");
}
