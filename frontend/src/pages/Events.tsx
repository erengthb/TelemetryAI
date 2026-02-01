import { useEffect, useState } from "react";
import { api } from "../api";
import {
  EnvironmentResponse,
  EventItemResponse,
  ProjectResponse,
} from "../api/types";
import { formatDateShort, formatEnvLabel } from "../utils/format";
import { pickEnvName, pickProjectId, saveEnvName, saveProjectId } from "../utils/selection";

const ranges = [
  { label: "7 gun", value: "7d" },
  { label: "30 gun", value: "30d" },
];

export default function Events() {
  const [projects, setProjects] = useState<ProjectResponse[]>([]);
  const [envs, setEnvs] = useState<EnvironmentResponse[]>([]);
  const [projectId, setProjectId] = useState("");
  const [envName, setEnvName] = useState("");
  const [range, setRange] = useState("7d");
  const [items, setItems] = useState<EventItemResponse[]>([]);
  const [selected, setSelected] = useState<EventItemResponse | null>(null);
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
    const loadItems = async () => {
      setLoading(true);
      setError(null);
      try {
        const response = await api.listEvents(projectId, envName, range);
        if (!active) {
          return;
        }
        setItems(response.items);
        setSelected(response.items[0] ?? null);
      } catch (err) {
        if (active) {
          setError("Olaylar alinamadi.");
          setItems([]);
          setSelected(null);
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    };
    loadItems();
    return () => {
      active = false;
    };
  }, [projectId, envName, range]);

  if (!loading && projects.length === 0) {
    return (
      <div className="page">
        <div className="card">
          <div className="card-title">Proje yok</div>
          <div className="card-subtitle">Olaylari gormek icin once proje olustur.</div>
        </div>
      </div>
    );
  }

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h2>Olaylar</h2>
          <p>Secili proje ve ortam icin son event akisini gor.</p>
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
            {error ? <div className="helper">{error}</div> : null}
          </div>
        </div>
        <div className="card">
          <div className="card-title">Secili olay</div>
          <div className="card-subtitle">
            {selected ? selected.eventName : "Olay secilmedi."}
          </div>
          <div className="list">
            <div className="list-item">
              <div>
                <div className="list-title">Ortam</div>
                <div className="list-subtitle">{formatEnvLabel(selected?.envName)}</div>
              </div>
              <div>
                <div className="list-title">Zaman</div>
                <div className="list-subtitle">{formatDateShort(selected?.tsServer)}</div>
              </div>
            </div>
            <div className="list-item">
              <div>
                <div className="list-title">Oyuncu</div>
                <div className="list-subtitle">{selected?.playerId ?? "-"}</div>
              </div>
              <div>
                <div className="list-title">Oturum</div>
                <div className="list-subtitle">{selected?.sessionId ?? "-"}</div>
              </div>
            </div>
            <div className="list-item">
              <div>
                <div className="list-title">Build</div>
                <div className="list-subtitle">{selected?.buildVersion ?? "-"}</div>
              </div>
              <div>
                <div className="list-title">Platform</div>
                <div className="list-subtitle">{selected?.platform ?? "-"}</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="card">
        <div className="card-title">Olay akisi</div>
        <div className="table">
          <div className="table-row head cols-5">
            <span>Olay</span>
            <span>Ortam</span>
            <span>Oyuncu</span>
            <span>Oturum</span>
            <span>Zaman</span>
          </div>
          {loading ? (
            <div className="table-row cols-5">
              <span>Yukleniyor...</span>
            </div>
          ) : items.length === 0 ? (
            <div className="table-row cols-5">
              <span>Kayit yok.</span>
            </div>
          ) : (
            items.map((item) => (
              <div
                key={item.id}
                className="table-row cols-5"
                onClick={() => setSelected(item)}
                role="button"
              >
                <span>{item.eventName}</span>
                <span>{formatEnvLabel(item.envName)}</span>
                <span className="mono">{item.playerId}</span>
                <span className="mono">{item.sessionId}</span>
                <span>{formatDateShort(item.tsServer)}</span>
              </div>
            ))
          )}
        </div>
      </div>

      <div className="grid-2">
        <div className="card">
          <div className="card-title">Properties</div>
          <pre className="code-block">
            {selected?.properties
              ? JSON.stringify(selected.properties, null, 2)
              : "Properties yok."}
          </pre>
        </div>
        <div className="card">
          <div className="card-title">Device</div>
          <pre className="code-block">
            {selected?.device
              ? JSON.stringify(selected.device, null, 2)
              : "Device yok."}
          </pre>
        </div>
      </div>
    </div>
  );
}
