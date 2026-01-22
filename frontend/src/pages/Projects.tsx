import { useEffect, useState } from "react";
import Tag from "../components/Tag";
import { api } from "../api";
import { OrgResponse, ProjectResponse } from "../api/types";
import { formatDateShort } from "../utils/format";

export default function Projects() {
  const [projects, setProjects] = useState<ProjectResponse[]>([]);
  const [orgs, setOrgs] = useState<OrgResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [projectName, setProjectName] = useState("");
  const [orgId, setOrgId] = useState("");

  useEffect(() => {
    let active = true;
    const load = async () => {
      setLoading(true);
      setError(null);
      try {
        const [orgList, projectList] = await Promise.all([
          api.listOrgs(),
          api.listProjects(),
        ]);
        if (!active) {
          return;
        }
        setOrgs(orgList);
        setProjects(projectList);
        if (!orgId && orgList.length > 0) {
          setOrgId(orgList[0].id);
        }
      } catch (err) {
        if (active) {
          setError("Projeler yuklenemedi. Backend calismiyor olabilir.");
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    };

    load();
    return () => {
      active = false;
    };
  }, []);

  const handleCreate = async () => {
    if (!orgId || !projectName.trim()) {
      setError("Org ve proje adi gerekli.");
      return;
    }
    setError(null);
    try {
      const created = await api.createProject({ orgId, name: projectName.trim() });
      setProjects((prev) => [created, ...prev]);
      setProjectName("");
    } catch (err) {
      setError("Proje olusturulamadi.");
    }
  };

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h2>Projeler</h2>
          <p>Her proje icin hat, sema politikasi ve erisim anahtarlarini yonet.</p>
        </div>
        <button className="btn primary" onClick={handleCreate}>
          Yeni proje
        </button>
      </div>
      <div className="card">
        <div className="card-title">Proje olustur</div>
        <div className="form-stack">
          <label>
            Org
            <select value={orgId} onChange={(event) => setOrgId(event.target.value)}>
              {orgs.map((org) => (
                <option key={org.id} value={org.id}>
                  {org.name}
                </option>
              ))}
            </select>
          </label>
          <label>
            Proje adi
            <input
              type="text"
              placeholder="ornek: nova-game"
              value={projectName}
              onChange={(event) => setProjectName(event.target.value)}
            />
          </label>
          <button className="btn primary" onClick={handleCreate}>
            Proje olustur
          </button>
          {error ? <div className="helper">{error}</div> : null}
        </div>
      </div>
      <div className="card-grid">
        {loading ? (
          <div className="card">
            <div className="card-title">Yukleniyor...</div>
          </div>
        ) : projects.length === 0 ? (
          <div className="card">
            <div className="card-title">Proje yok</div>
            <div className="card-subtitle">Ilk projeyi olusturarak basla.</div>
          </div>
        ) : (
          projects.map((project) => (
          <div key={project.id} className="card project-card">
            <div className="project-header">
              <div>
                <div className="project-title">{project.name}</div>
                <div className="project-env">{formatDateShort(project.createdAt)}</div>
              </div>
              <Tag tone="info">Aktif</Tag>
            </div>
            <div className="project-metrics">
              <div>
                <div className="metric-value">-</div>
                <div className="metric-label">olaylar</div>
              </div>
              <div>
                <div className="metric-value">-</div>
                <div className="metric-label">sema sapmasi</div>
              </div>
              <div>
                <div className="metric-value">-</div>
                <div className="metric-label">anahtarlar</div>
              </div>
            </div>
            <div className="project-actions">
              <button className="btn ghost small">Detaylar</button>
            </div>
          </div>
        ))
        )}
      </div>
    </div>
  );
}
