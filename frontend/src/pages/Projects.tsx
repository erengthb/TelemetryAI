import Tag from "../components/Tag";

const projects = [
  {
    name: "Aurora Ops",
    env: "UAT",
    status: "Live",
    events: "18.2M",
    drift: "0.6%",
    keys: "6",
    note: "Latency trimmed after patch 12.4",
  },
  {
    name: "Orbit Wallet",
    env: "Prod",
    status: "Guarded",
    events: "9.4M",
    drift: "1.4%",
    keys: "4",
    note: "PII found in onboarding payload",
  },
  {
    name: "Helix Games",
    env: "UAT",
    status: "Live",
    events: "6.1M",
    drift: "0.2%",
    keys: "3",
    note: "Schema locked for season launch",
  },
  {
    name: "Nova Retail",
    env: "Dev",
    status: "Paused",
    events: "1.9M",
    drift: "2.8%",
    keys: "2",
    note: "Field whitelist pending approval",
  },
];

export default function Projects() {
  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h2>Projects</h2>
          <p>Control pipelines, schema policy, and access keys per project.</p>
        </div>
        <button className="btn primary">New project</button>
      </div>
      <div className="card-grid">
        {projects.map((project) => (
          <div key={project.name} className="card project-card">
            <div className="project-header">
              <div>
                <div className="project-title">{project.name}</div>
                <div className="project-env">{project.env}</div>
              </div>
              <Tag
                tone={
                  project.status === "Live"
                    ? "safe"
                    : project.status === "Guarded"
                    ? "warn"
                    : "risk"
                }
              >
                {project.status}
              </Tag>
            </div>
            <div className="project-metrics">
              <div>
                <div className="metric-value">{project.events}</div>
                <div className="metric-label">events this month</div>
              </div>
              <div>
                <div className="metric-value">{project.drift}</div>
                <div className="metric-label">schema drift</div>
              </div>
              <div>
                <div className="metric-value">{project.keys}</div>
                <div className="metric-label">active keys</div>
              </div>
            </div>
            <div className="project-note">{project.note}</div>
            <div className="project-actions">
              <button className="btn ghost small">Open panel</button>
              <button className="btn ghost small">Rotate keys</button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
