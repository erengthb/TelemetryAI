import { useEffect, useState } from "react";
import Tag from "../components/Tag";
import { api } from "../api";
import { ApiKeyCreateResponse, ApiKeyResponse, EnvironmentResponse, ProjectResponse } from "../api/types";
import { formatDateShort, formatEnvLabel } from "../utils/format";

export default function ApiKeys() {
  const [projects, setProjects] = useState<ProjectResponse[]>([]);
  const [envs, setEnvs] = useState<EnvironmentResponse[]>([]);
  const [selectedProjectId, setSelectedProjectId] = useState("");
  const [selectedEnv, setSelectedEnv] = useState("");
  const [keys, setKeys] = useState<ApiKeyResponse[]>([]);
  const [createdKey, setCreatedKey] = useState<ApiKeyCreateResponse | null>(null);
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
        if (projectList.length > 0) {
          setSelectedProjectId(projectList[0].id);
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
    if (!selectedProjectId) {
      return;
    }
    let active = true;
    const loadEnvs = async () => {
      try {
        const envList = await api.listEnvironments(selectedProjectId);
        if (!active) {
          return;
        }
        setEnvs(envList);
        if (envList.length > 0) {
          setSelectedEnv(envList[0].envName);
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
  }, [selectedProjectId]);

  useEffect(() => {
    if (!selectedProjectId || !selectedEnv) {
      return;
    }
    let active = true;
    const loadKeys = async () => {
      setLoading(true);
      setError(null);
      try {
        const list = await api.listApiKeys(selectedProjectId, selectedEnv);
        if (!active) {
          return;
        }
        setKeys(list);
      } catch (err) {
        if (active) {
          setError("Anahtarlar alinamadi.");
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    };
    loadKeys();
    return () => {
      active = false;
    };
  }, [selectedProjectId, selectedEnv]);

  const handleRotate = async () => {
    if (!selectedProjectId || !selectedEnv) {
      setError("Proje ve ortam sec.");
      return;
    }
    setError(null);
    try {
      const created = await api.rotateApiKey(selectedProjectId, selectedEnv);
      setCreatedKey(created);
      const list = await api.listApiKeys(selectedProjectId, selectedEnv);
      setKeys(list);
    } catch (err) {
      setError("Anahtar uretilmedi.");
    }
  };

  const handleCopy = async () => {
    if (!createdKey?.apiKey) {
      return;
    }
    try {
      await navigator.clipboard.writeText(createdKey.apiKey);
    } catch (err) {
      setError("Kopyalama basarisiz.");
    }
  };

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h2>API Anahtarlari</h2>
          <p>Proje anahtarlari ve ortama gore erisim yonetimi.</p>
        </div>
        <button className="btn primary" onClick={handleRotate}>
          Anahtar uret
        </button>
      </div>

      <div className="grid-2">
        <div className="card">
          <div className="card-title">Anahtarlar</div>
          <div className="form-stack">
            <label>
              Proje
              <select
                value={selectedProjectId}
                onChange={(event) => setSelectedProjectId(event.target.value)}
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
              <select value={selectedEnv} onChange={(event) => setSelectedEnv(event.target.value)}>
                {envs.map((env) => (
                  <option key={env.id} value={env.envName}>
                    {formatEnvLabel(env.envName)}
                  </option>
                ))}
              </select>
            </label>
          </div>
          <div className="table">
            <div className="table-row head cols-5">
              <span>Maske</span>
              <span>Ortam</span>
              <span>Durum</span>
              <span>Olusma</span>
              <span>Iptal</span>
            </div>
            {loading ? (
              <div className="table-row cols-5">
                <span>Yukleniyor...</span>
              </div>
            ) : keys.length === 0 ? (
              <div className="table-row cols-5">
                <span>Anahtar bulunamadi.</span>
              </div>
            ) : (
              keys.map((key) => (
                <div key={key.id} className="table-row cols-5">
                  <span className="mono">{key.maskedKey}</span>
                  <span>{formatEnvLabel(key.envName)}</span>
                  <span>
                    <Tag tone={key.status === "active" ? "safe" : "warn"}>
                      {key.status === "active" ? "aktif" : "iptal"}
                    </Tag>
                  </span>
                  <span>{formatDateShort(key.createdAt)}</span>
                  <span>{formatDateShort(key.revokedAt)}</span>
                </div>
              ))
            )}
          </div>
        </div>

        <div className="card">
          <div className="card-title">Yeni anahtar</div>
          <div className="card-subtitle">Anahtar sadece bir kez gosterilir.</div>
          {createdKey ? (
            <div className="form-stack">
              <label>
                Ortam
                <input type="text" value={formatEnvLabel(createdKey.envName)} readOnly />
              </label>
              <label>
                Anahtar
                <input type="text" value={createdKey.apiKey} readOnly />
              </label>
              <button className="btn ghost" onClick={handleCopy}>
                Kopyala
              </button>
            </div>
          ) : (
            <div className="helper">
              Anahtar uretmek icin "Anahtar uret" butonunu kullan.
            </div>
          )}
          {error ? <div className="helper">{error}</div> : null}
        </div>
      </div>
    </div>
  );
}
