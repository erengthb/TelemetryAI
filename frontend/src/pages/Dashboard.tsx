import { useEffect, useMemo, useState } from "react";
import {
  Area,
  AreaChart,
  Funnel,
  FunnelChart,
  LabelList,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";
import { api } from "../api";
import {
  DashboardFunnelResponse,
  DashboardOverviewResponse,
  EnvironmentResponse,
  ProjectResponse,
} from "../api/types";
import { formatDateShort, formatEnvLabel, formatNumber } from "../utils/format";
import { pickEnvName, pickProjectId, saveEnvName, saveProjectId } from "../utils/selection";

const ranges = [
  { label: "7 gun", value: "7d" },
  { label: "30 gun", value: "30d" },
];

export default function Dashboard() {
  const [projects, setProjects] = useState<ProjectResponse[]>([]);
  const [envs, setEnvs] = useState<EnvironmentResponse[]>([]);
  const [projectId, setProjectId] = useState("");
  const [envName, setEnvName] = useState("");
  const [range, setRange] = useState("7d");
  const [overview, setOverview] = useState<DashboardOverviewResponse | null>(null);
  const [funnel, setFunnel] = useState<DashboardFunnelResponse | null>(null);
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
          setError("Genel bakis verisi alinamadi.");
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
    const loadDashboard = async () => {
      setLoading(true);
      setError(null);
      try {
        const [overviewData, funnelData] = await Promise.all([
          api.getDashboardOverview(projectId, envName, range),
          api.getDashboardFunnel(projectId, envName, range),
        ]);
        if (!active) {
          return;
        }
        setOverview(overviewData);
        setFunnel(funnelData);
      } catch (err) {
        if (active) {
          setError("Genel bakis verisi alinamadi.");
          setOverview(null);
          setFunnel(null);
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    };
    loadDashboard();
    return () => {
      active = false;
    };
  }, [projectId, envName, range]);

  const funnelSummary = useMemo(() => {
    const rows = funnel?.rows ?? [];
    const summary = rows.reduce(
      (acc, row) => {
        acc.starts += row.starts;
        acc.endsSuccess += row.endsSuccess;
        acc.endsFail += row.endsFail;
        acc.endsQuit += row.endsQuit;
        return acc;
      },
      { starts: 0, endsSuccess: 0, endsFail: 0, endsQuit: 0 }
    );
    return [
      { name: "Baslangic", value: summary.starts },
      { name: "Basarili", value: summary.endsSuccess },
      { name: "Basarisiz", value: summary.endsFail },
      { name: "Cikis", value: summary.endsQuit },
    ];
  }, [funnel]);

  if (!loading && projects.length === 0) {
    return (
      <div className="page">
        <div className="card">
          <div className="card-title">Proje yok</div>
          <div className="card-subtitle">Dashboard icin once proje olustur.</div>
        </div>
      </div>
    );
  }

  return (
    <div className="page">
      <section className="hero">
        <div className="hero-copy">
          <div className="hero-badge">Genel Bakis - Ozet + Huni</div>
          <h1>Genel Bakis</h1>
          <p>Genel ozet ve huni metrikleri.</p>
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
            <div className="hero-actions">
              {ranges.map((item) => (
                <button
                  key={item.value}
                  className={`btn ${range === item.value ? "primary" : "ghost"}`}
                  onClick={() => setRange(item.value)}
                >
                  {item.label}
                </button>
              ))}
            </div>
          </div>
        </div>
        <div className="hero-panel card">
          <div className="card-title">Genel metrikler</div>
          <div className="hero-metrics">
            <div>
              <div className="metric-value">{formatNumber(overview?.totalEvents ?? 0)}</div>
              <div className="metric-label">toplam olay</div>
            </div>
            <div>
              <div className="metric-value">{formatNumber(overview?.uniquePlayers ?? 0)}</div>
              <div className="metric-label">tekil oyuncu</div>
            </div>
            <div>
              <div className="metric-value">{formatNumber(overview?.sessionsStarted ?? 0)}</div>
              <div className="metric-label">oturum basladi</div>
            </div>
          </div>
          {error ? <div className="helper">{error}</div> : null}
        </div>
      </section>

      <section className="grid-2">
        <div className="card">
          <div className="card-title">Genel trend</div>
          <div className="card-subtitle">Gunluk toplam olaylar</div>
          <div style={{ width: "100%", height: 240 }}>
            <ResponsiveContainer>
              <AreaChart data={overview?.series ?? []}>
                <XAxis dataKey="date" tickFormatter={formatDateShort} />
                <YAxis />
                <Tooltip
                  contentStyle={{
                    background: "rgba(10, 16, 22, 0.95)",
                    border: "1px solid rgba(120, 160, 180, 0.2)",
                    borderRadius: 12,
                  }}
                  labelFormatter={(value) => formatDateShort(String(value))}
                />
                <Area
                  type="monotone"
                  dataKey="totalEvents"
                  stroke="#42d7b0"
                  fill="rgba(66, 215, 176, 0.2)"
                />
              </AreaChart>
            </ResponsiveContainer>
          </div>
          {loading ? <div className="helper">Yukleniyor...</div> : null}
        </div>

        <div className="card">
          <div className="card-title">Huni ozeti</div>
          <div className="card-subtitle">Baslangic - bitis dagilimi</div>
          <div style={{ width: "100%", height: 240 }}>
            <ResponsiveContainer>
              <FunnelChart>
                <Funnel dataKey="value" data={funnelSummary} isAnimationActive>
                  <LabelList position="right" dataKey="name" fill="#e8f2f5" />
                </Funnel>
              </FunnelChart>
            </ResponsiveContainer>
          </div>
          {loading ? <div className="helper">Yukleniyor...</div> : null}
        </div>
      </section>

      <section className="card">
        <div className="card-title">Huni detaylari</div>
        <div className="table">
          <div className="table-row head cols-5">
            <span>Seviye</span>
            <span>Baslangic</span>
            <span>Basarili</span>
            <span>Basarisiz</span>
            <span>Cikis</span>
          </div>
          {funnel?.rows?.length ? (
            funnel.rows.map((row) => (
              <div key={row.levelId} className="table-row cols-5">
                <span className="mono">{row.levelId}</span>
                <span>{formatNumber(row.starts)}</span>
                <span>{formatNumber(row.endsSuccess)}</span>
                <span>{formatNumber(row.endsFail)}</span>
                <span>{formatNumber(row.endsQuit)}</span>
              </div>
            ))
          ) : (
            <div className="table-row cols-5">
              <span>Funnel verisi yok.</span>
            </div>
          )}
        </div>
      </section>
    </div>
  );
}
