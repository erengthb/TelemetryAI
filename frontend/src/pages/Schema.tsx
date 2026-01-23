import { useEffect, useState } from "react";
import { api } from "../api";
import { ProjectResponse } from "../api/types";
import { pickProjectId, saveProjectId } from "../utils/selection";

const samplePayload = `{
  "olay_adi": "mac_baslangic",
  "oyuncu_id": "8f1c...d2",
  "oturum_id": "018d-7d2c-9a",
  "cuzdan_adresi": "0x92f1...44c2",
  "cihaz_parmak_izi": "fp_1239_99",
  "bolge": "ab-bati"
}`;

export default function Schema() {
  const [projects, setProjects] = useState<ProjectResponse[]>([]);
  const [selectedProjectId, setSelectedProjectId] = useState("");
  const [schema, setSchema] = useState<unknown | null>(null);
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
        setSelectedProjectId(initialProjectId);
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
    if (!selectedProjectId) {
      return;
    }
    let active = true;
    const loadSchema = async () => {
      setLoading(true);
      setError(null);
      try {
        const current = await api.getSchemaCurrent(selectedProjectId);
        if (active) {
          setSchema(current);
        }
      } catch (err) {
        if (active) {
          setSchema(null);
          setError("Sema alinamadi.");
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    };
    loadSchema();
    return () => {
      active = false;
    };
  }, [selectedProjectId]);

  const handleImport = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (!file || !selectedProjectId) {
      return;
    }
    try {
      const text = await file.text();
      const parsed = JSON.parse(text);
      const result = await api.importSchema(selectedProjectId, parsed);
      setSchema(result);
      setError(null);
    } catch (err) {
      setError("Sema import basarisiz.");
    }
  };

  const handleExport = async () => {
    if (!selectedProjectId) {
      return;
    }
    try {
      const exported = await api.exportSchema(selectedProjectId);
      const blob = new Blob([JSON.stringify(exported, null, 2)], { type: "application/json" });
      const url = URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = url;
      link.download = "schema.json";
      link.click();
      URL.revokeObjectURL(url);
    } catch (err) {
      setError("Sema export basarisiz.");
    }
  };

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h2>Sema Laboratuvari</h2>
          <p>Sema import/export ve kisisel veri koruma kurallari.</p>
        </div>
        <button className="btn primary" onClick={handleExport}>
          Sema export
        </button>
      </div>

      <div className="grid-2">
        <div className="card">
          <div className="card-title">Sema secimi</div>
          <div className="form-stack">
            <label>
              Proje
              <select
                value={selectedProjectId}
                onChange={(event) => {
                  const next = event.target.value;
                  setSelectedProjectId(next);
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
              Sema import
              <input type="file" accept="application/json" onChange={handleImport} />
            </label>
            {error ? <div className="helper">{error}</div> : null}
          </div>
        </div>

        <div className="card">
          <div className="card-title">Ornek veri paketi</div>
          <div className="card-subtitle">
            Temizlenmis alanlarla canli sema onizleme.
          </div>
          <pre className="code-block">{samplePayload}</pre>
          <div className="schema-actions">
            <button className="btn ghost small">Dogrulama calistir</button>
            <button className="btn ghost small" onClick={handleExport}>
              JSON disari aktar
            </button>
          </div>
        </div>
      </div>

      <div className="card">
        <div className="card-title">Guncel sema</div>
        <div className="card-subtitle">
          {loading ? "Yukleniyor..." : "Backend'den cekilen sema."}
        </div>
        <pre className="code-block">
          {schema ? JSON.stringify(schema, null, 2) : "Sema verisi yok."}
        </pre>
      </div>
    </div>
  );
}
