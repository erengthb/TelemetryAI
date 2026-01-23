import { useEffect, useMemo, useState } from "react";
import {
  Area,
  AreaChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";
import { api } from "../api";
import { AiReportJson, EnvironmentResponse, ProjectResponse } from "../api/types";
import { formatEnvLabel } from "../utils/format";
import { pickEnvName, pickProjectId, saveEnvName, saveProjectId } from "../utils/selection";

const dailyRanges = [
  { label: "7 gun", value: "7d" },
  { label: "30 gun", value: "30d" },
];

const weeklyRanges = [
  { label: "8 hafta", value: "8w" },
  { label: "12 hafta", value: "12w" },
];

function extractScore(report: AiReportJson) {
  const direct = report.score;
  if (typeof direct === "number") {
    return direct;
  }
  const summary = report.summary as { score?: unknown } | undefined;
  if (summary && typeof summary.score === "number") {
    return summary.score;
  }
  return 0;
}

function pickString(value: unknown) {
  return typeof value === "string" ? value : undefined;
}

function pickStringArray(value: unknown) {
  if (!Array.isArray(value)) {
    return [];
  }
  return value.filter((item) => typeof item === "string") as string[];
}

function pickRecord(value: unknown) {
  if (!value || typeof value !== "object") {
    return null;
  }
  return value as Record<string, unknown>;
}

function formatScore(score: number) {
  if (!Number.isFinite(score)) {
    return "0";
  }
  return score % 1 === 0 ? String(score) : score.toFixed(1);
}

function buildReportCard(report: AiReportJson, index: number, label: string) {
  const data = report as Record<string, unknown>;
  const summaryBlock = pickRecord(data.summary);
  const title =
    pickString(data.title) ??
    pickString(data.name) ??
    pickString(summaryBlock?.title) ??
    `${label} rapor #${index + 1}`;
  const subtitle =
    pickString(data.date) ??
    pickString(data.period) ??
    pickString(data.range) ??
    pickString(summaryBlock?.period);
  const summary =
    pickString(data.summary) ??
    pickString(summaryBlock?.text) ??
    pickString(summaryBlock?.overview);
  const bullets = [
    ...pickStringArray(data.highlights),
    ...pickStringArray(data.insights),
    ...pickStringArray(data.recommendations),
    ...pickStringArray(data.risks),
  ].slice(0, 4);
  const tags = [
    ...pickStringArray(data.tags),
    ...pickStringArray(data.topics),
  ].slice(0, 4);
  const rawPreview = summary
    ? ""
    : (() => {
        const raw = JSON.stringify(report);
        return raw.length > 220 ? `${raw.slice(0, 220)}...` : raw;
      })();
  return {
    title,
    subtitle,
    score: formatScore(extractScore(report)),
    summary: summary ?? rawPreview,
    bullets,
    tags,
  };
}

export default function Reports() {
  const [projects, setProjects] = useState<ProjectResponse[]>([]);
  const [envs, setEnvs] = useState<EnvironmentResponse[]>([]);
  const [projectId, setProjectId] = useState("");
  const [envName, setEnvName] = useState("");
  const [dailyRange, setDailyRange] = useState("30d");
  const [weeklyRange, setWeeklyRange] = useState("12w");
  const [dailyReports, setDailyReports] = useState<AiReportJson[]>([]);
  const [weeklyReports, setWeeklyReports] = useState<AiReportJson[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let active = true;
    const loadProjects = async () => {
      setLoading(true);
      setError(null);
      try {
        const projectList = await api.listProjects();
        if (!active) {
          return;
        }
        setProjects(projectList);
        const initialProjectId = pickProjectId(projectList);
        setProjectId(initialProjectId);
        if (initialProjectId) {
          saveProjectId(initialProjectId);
        }
      } catch (err) {
        if (active) {
          setError("Projeler alinamadi.");
          setLoading(false);
        }
      }
    };
    loadProjects();
    return () => {
      active = false;
    };
  }, []);

  useEffect(() => {
    if (!projectId) {
      return;
    }
    let active = true;
    const loadEnvs = async () => {
      try {
        const envList = await api.listEnvironments(projectId);
        if (!active) {
          return;
        }
        setEnvs(envList);
        const initialEnv = pickEnvName(envList);
        setEnvName(initialEnv);
        if (initialEnv) {
          saveEnvName(initialEnv);
        }
      } catch (err) {
        if (active) {
          setError("Ortamlar alinamadi.");
        }
      }
    };
    loadEnvs();
    return () => {
      active = false;
    };
  }, [projectId]);

  useEffect(() => {
    if (!projectId || !envName) {
      return;
    }
    let active = true;
    const loadReports = async () => {
      setLoading(true);
      setError(null);
      try {
        const [daily, weekly] = await Promise.all([
          api.listAiReportsDaily(projectId, envName, dailyRange),
          api.listAiReportsWeekly(projectId, envName, weeklyRange),
        ]);
        if (!active) {
          return;
        }
        setDailyReports(daily);
        setWeeklyReports(weekly);
      } catch (err) {
        if (active) {
          setError("YZ raporlari alinamadi.");
          setDailyReports([]);
          setWeeklyReports([]);
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    };
    loadReports();
    return () => {
      active = false;
    };
  }, [projectId, envName, dailyRange, weeklyRange]);

  const dailyChart = useMemo(
    () =>
      dailyReports.map((report, index) => ({
        label: `Gun ${index + 1}`,
        score: extractScore(report),
      })),
    [dailyReports]
  );

  const weeklyChart = useMemo(
    () =>
      weeklyReports.map((report, index) => ({
        label: `Hafta ${index + 1}`,
        score: extractScore(report),
      })),
    [weeklyReports]
  );

  const dailyCards = useMemo(
    () => dailyReports.map((report, index) => buildReportCard(report, index, "Gunluk")),
    [dailyReports]
  );

  const weeklyCards = useMemo(
    () => weeklyReports.map((report, index) => buildReportCard(report, index, "Haftalik")),
    [weeklyReports]
  );

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h2>Yapay Zeka Raporlari</h2>
          <p>Gunluk ve haftalik YZ raporlarini goruntule.</p>
        </div>
      </div>

      <div className="grid-2">
        <div className="card">
          <div className="card-title">Filtreler</div>
          <div className="form-stack">
            <label>
              Proje
              <select
                value={projectId}
                onChange={(event) => {
                  const next = event.target.value;
                  setProjectId(next);
                  setEnvName("");
                  saveProjectId(next);
                }}
              >
                {projects.map((project) => (
                  <option key={project.id} value={project.id}>
                    {project.name}
                  </option>
                ))}
              </select>
            </label>
            <label>
              Ortam
              <select
                value={envName}
                onChange={(event) => {
                  const next = event.target.value;
                  setEnvName(next);
                  saveEnvName(next);
                }}
              >
                {envs.map((env) => (
                  <option key={env.id} value={env.envName}>
                    {formatEnvLabel(env.envName)}
                  </option>
                ))}
              </select>
            </label>
            {error ? <div className="helper">{error}</div> : null}
          </div>
        </div>
        <div className="card">
          <div className="card-title">Durum</div>
          <div className="card-subtitle">
            {loading ? "Yukleniyor..." : "YZ raporlari hazir."}
          </div>
          <div className="cost-stack">
            <div className="cost-row">
              <span>Gunluk rapor</span>
              <span>{dailyReports.length}</span>
            </div>
            <div className="cost-row">
              <span>Haftalik rapor</span>
              <span>{weeklyReports.length}</span>
            </div>
          </div>
        </div>
      </div>

      <div className="grid-2">
        <div className="card">
          <div className="card-title">Gunluk skor trendi</div>
          <div className="hero-actions">
            {dailyRanges.map((item) => (
              <button
                key={item.value}
                className={`btn ${dailyRange === item.value ? "primary" : "ghost"}`}
                onClick={() => setDailyRange(item.value)}
              >
                {item.label}
              </button>
            ))}
          </div>
          <div style={{ width: "100%", height: 220 }}>
            <ResponsiveContainer>
              <AreaChart data={dailyChart}>
                <XAxis dataKey="label" />
                <YAxis />
                <Tooltip
                  contentStyle={{
                    background: "rgba(10, 16, 22, 0.95)",
                    border: "1px solid rgba(120, 160, 180, 0.2)",
                    borderRadius: 12,
                  }}
                />
                <Area type="monotone" dataKey="score" stroke="#4bc0ff" fill="rgba(75, 192, 255, 0.2)" />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="card">
          <div className="card-title">Haftalik skor trendi</div>
          <div className="hero-actions">
            {weeklyRanges.map((item) => (
              <button
                key={item.value}
                className={`btn ${weeklyRange === item.value ? "primary" : "ghost"}`}
                onClick={() => setWeeklyRange(item.value)}
              >
                {item.label}
              </button>
            ))}
          </div>
          <div style={{ width: "100%", height: 220 }}>
            <ResponsiveContainer>
              <AreaChart data={weeklyChart}>
                <XAxis dataKey="label" />
                <YAxis />
                <Tooltip
                  contentStyle={{
                    background: "rgba(10, 16, 22, 0.95)",
                    border: "1px solid rgba(120, 160, 180, 0.2)",
                    borderRadius: 12,
                  }}
                />
                <Area type="monotone" dataKey="score" stroke="#42d7b0" fill="rgba(66, 215, 176, 0.2)" />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>

      <div className="card">
        <div className="card-title">Gunluk raporlar</div>
        {dailyCards.length === 0 ? (
          <div className="helper">Gunluk rapor yok.</div>
        ) : (
          <div className="report-grid">
            {dailyCards.map((card, index) => (
              <div key={`daily-${index}`} className="card report-card">
                <div className="report-head">
                  <div>
                    <div className="card-title">{card.title}</div>
                    {card.subtitle ? <div className="card-subtitle">{card.subtitle}</div> : null}
                  </div>
                  <div className="pill soft">Skor {card.score}</div>
                </div>
                {card.summary ? <p className="report-summary">{card.summary}</p> : null}
                {card.tags.length ? (
                  <div className="report-tags">
                    {card.tags.map((tag, tagIndex) => (
                      <span key={`daily-tag-${index}-${tagIndex}`} className="pill ghost">
                        {tag}
                      </span>
                    ))}
                  </div>
                ) : null}
                {card.bullets.length ? (
                  <ul className="report-list">
                    {card.bullets.map((item, itemIndex) => (
                      <li key={`daily-item-${index}-${itemIndex}`}>{item}</li>
                    ))}
                  </ul>
                ) : (
                  <div className="report-empty">Ek not yok.</div>
                )}
              </div>
            ))}
          </div>
        )}
      </div>

      <div className="card">
        <div className="card-title">Haftalik raporlar</div>
        {weeklyCards.length === 0 ? (
          <div className="helper">Haftalik rapor yok.</div>
        ) : (
          <div className="report-grid">
            {weeklyCards.map((card, index) => (
              <div key={`weekly-${index}`} className="card report-card">
                <div className="report-head">
                  <div>
                    <div className="card-title">{card.title}</div>
                    {card.subtitle ? <div className="card-subtitle">{card.subtitle}</div> : null}
                  </div>
                  <div className="pill soft">Skor {card.score}</div>
                </div>
                {card.summary ? <p className="report-summary">{card.summary}</p> : null}
                {card.tags.length ? (
                  <div className="report-tags">
                    {card.tags.map((tag, tagIndex) => (
                      <span key={`weekly-tag-${index}-${tagIndex}`} className="pill ghost">
                        {tag}
                      </span>
                    ))}
                  </div>
                ) : null}
                {card.bullets.length ? (
                  <ul className="report-list">
                    {card.bullets.map((item, itemIndex) => (
                      <li key={`weekly-item-${index}-${itemIndex}`}>{item}</li>
                    ))}
                  </ul>
                ) : (
                  <div className="report-empty">Ek not yok.</div>
                )}
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
