import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Tag from "../components/Tag";
import { api } from "../api";
import { OrgResponse, ProjectResponse } from "../api/types";
import { formatDateShort } from "../utils/format";
import { saveProjectId } from "../utils/selection";

export default function Projects() {
  const [projects, setProjects] = useState<ProjectResponse[]>([]);
  const [orgs, setOrgs] = useState<OrgResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [orgError, setOrgError] = useState<string | null>(null);
  const [orgName, setOrgName] = useState("");
  const [projectName, setProjectName] = useState("");
  const [orgId, setOrgId] = useState("");
  const navigate = useNavigate();

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

  const handleCreateOrg = async () => {
    if (!orgName.trim()) {
      setOrgError("Org adi gerekli.");
      return;
    }
    setOrgError(null);
    try {
      const created = await api.createOrg({ name: orgName.trim() });
      setOrgs((prev) => [created, ...prev]);
      setOrgId(created.id);
      setOrgName("");
    } catch (err) {
      setOrgError("Org olusturulamadi.");
    }
  };

  const handleDetails = (projectId: string) => {
    saveProjectId(projectId);
    navigate("/");
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
        <div className="card-title">Org olustur</div>
        <div className="form-stack">
          <label>
            Org adi
            <input
              type="text"
              placeholder="ornek: nova-studio"
              value={orgName}
              onChange={(event) => setOrgName(event.target.value)}
            />
          </label>
          <button className="btn primary" onClick={handleCreateOrg}>
            Org olustur
          </button>
          {orgError ? <div className="helper">{orgError}</div> : null}
        </div>
      </div>

      <div className="card">
        <div className="card-title">Proje olustur</div>
        <div className="form-stack">
          <label>
            Org
            <select value={orgId} onChange={(event) => setOrgId(event.target.value)}>
              {orgs.length === 0 ? (
                <option value="">Org yok</option>
              ) : (
                orgs.map((org) => (
                  <option key={org.id} value={org.id}>
                    {org.name}
                  </option>
                ))
              )}
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
              <button className="btn ghost small" onClick={() => handleDetails(project.id)}>
                Detaylar
              </button>
            </div>
          </div>
        ))
        )}
      </div>
    </div>
  );
}
