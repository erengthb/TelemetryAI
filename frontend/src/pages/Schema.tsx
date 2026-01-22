import Tag from "../components/Tag";

const fields = [
  {
    name: "event_name",
    type: "string",
    policy: "safe",
    note: "Locked",
  },
  {
    name: "player_id",
    type: "string",
    policy: "pii",
    note: "Hash before ingest",
  },
  {
    name: "session_id",
    type: "string",
    policy: "safe",
    note: "UUIDv7",
  },
  {
    name: "wallet_address",
    type: "string",
    policy: "pii",
    note: "Mask for analytics",
  },
  {
    name: "device_fingerprint",
    type: "string",
    policy: "risk",
    note: "Quarantine if new",
  },
];

const samplePayload = `{
  "event_name": "match_start",
  "player_id": "8f1c...d2",
  "session_id": "018d-7d2c-9a",
  "wallet_address": "0x92f1...44c2",
  "device_fingerprint": "fp_1239_99",
  "region": "eu-west"
}`;

export default function Schema() {
  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h2>Schema Lab</h2>
          <p>Design payload structure and apply PII protection rules.</p>
        </div>
        <button className="btn primary">Propose update</button>
      </div>

      <div className="grid-2">
        <div className="card">
          <div className="card-title">Field policy map</div>
          <div className="table">
            <div className="table-row head cols-4">
              <span>Field</span>
              <span>Type</span>
              <span>Policy</span>
              <span>Note</span>
            </div>
            {fields.map((field) => (
              <div key={field.name} className="table-row cols-4">
                <span className="mono">{field.name}</span>
                <span>{field.type}</span>
                <span>
                  <Tag
                    tone={
                      field.policy === "safe"
                        ? "safe"
                        : field.policy === "pii"
                        ? "warn"
                        : "risk"
                    }
                  >
                    {field.policy}
                  </Tag>
                </span>
                <span>{field.note}</span>
              </div>
            ))}
          </div>
        </div>

        <div className="card">
          <div className="card-title">Sample payload</div>
          <div className="card-subtitle">
            Live schema preview with sanitized fields.
          </div>
          <pre className="code-block">{samplePayload}</pre>
          <div className="schema-actions">
            <button className="btn ghost small">Run validation</button>
            <button className="btn ghost small">Export JSON</button>
          </div>
        </div>
      </div>

      <div className="card">
        <div className="card-title">Schema change log</div>
        <div className="timeline">
          <div className="timeline-item">
            <div className="timeline-time">2 hours ago</div>
            <div className="timeline-body">
              Added <span className="mono">region</span> field with safe policy.
            </div>
          </div>
          <div className="timeline-item">
            <div className="timeline-time">Yesterday</div>
            <div className="timeline-body">
              Updated <span className="mono">wallet_address</span> masking rule.
            </div>
          </div>
          <div className="timeline-item">
            <div className="timeline-time">3 days ago</div>
            <div className="timeline-body">
              Removed deprecated <span className="mono">client_ip</span> field.
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
