import Tag from "../components/Tag";

const keys = [
  {
    name: "Aurora Ops - ingest",
    last4: "4812",
    scope: "Ingest + Read",
    usage: "2.9M calls",
    limit: "30k/min",
    created: "2026-01-12",
    status: "Active",
    mask: "7Q3M-9T2Q-****-4812",
  },
  {
    name: "Orbit Wallet - edge",
    last4: "9021",
    scope: "Ingest",
    usage: "1.1M calls",
    limit: "18k/min",
    created: "2026-01-08",
    status: "Active",
    mask: "9B1X-8LPQ-****-9021",
  },
  {
    name: "Helix Games - batch",
    last4: "4410",
    scope: "Read only",
    usage: "412k calls",
    limit: "8k/min",
    created: "2025-12-22",
    status: "Rotating",
    mask: "2K8V-4Z1J-****-4410",
  },
];

export default function ApiKeys() {
  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h2>API Keys</h2>
          <p>Manage project keys, rotation, and rate limits.</p>
        </div>
        <button className="btn primary">Create key</button>
      </div>

      <div className="grid-2">
        <div className="card">
          <div className="card-title">Active keys</div>
          <div className="table">
            <div className="table-row head">
              <span>Name</span>
              <span>Mask</span>
              <span>Scope</span>
              <span>Usage</span>
              <span>Limit</span>
              <span>Status</span>
            </div>
            {keys.map((key) => (
              <div key={key.name} className="table-row">
                <span>
                  <div className="list-title">{key.name}</div>
                  <div className="list-subtitle">{key.created}</div>
                </span>
                <span className="mono">{key.mask}</span>
                <span>{key.scope}</span>
                <span>{key.usage}</span>
                <span>{key.limit}</span>
                <span>
                  <Tag tone={key.status === "Active" ? "safe" : "warn"}>
                    {key.status}
                  </Tag>
                </span>
              </div>
            ))}
          </div>
        </div>

        <div className="card">
          <div className="card-title">Key guardrails</div>
          <div className="card-subtitle">
            Rotate secrets without downtime.
          </div>
          <div className="form-stack">
            <label>
              Project
              <select>
                <option>Aurora Ops</option>
                <option>Orbit Wallet</option>
                <option>Helix Games</option>
              </select>
            </label>
            <label>
              Scope
              <select>
                <option>Ingest + Read</option>
                <option>Ingest only</option>
                <option>Read only</option>
              </select>
            </label>
            <label>
              Rate limit
              <input type="text" placeholder="30000" />
            </label>
            <label>
              Rotation window
              <input type="text" placeholder="7 days" />
            </label>
            <button className="btn primary">Generate key</button>
            <div className="helper">
              Last4 is stored for display; full key is shown once.
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
