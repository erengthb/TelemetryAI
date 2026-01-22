import Tag from "../components/Tag";
import ProgressBar from "../components/ProgressBar";

const incidents = [
  {
    id: "Q-192",
    project: "Aurora Ops",
    reason: "PII fingerprint detected",
    severity: "high",
    status: "Hold",
    time: "2m ago",
  },
  {
    id: "Q-193",
    project: "Helix Games",
    reason: "Schema field removed",
    severity: "med",
    status: "Review",
    time: "8m ago",
  },
  {
    id: "Q-194",
    project: "Orbit Wallet",
    reason: "Payload size spike",
    severity: "med",
    status: "Hold",
    time: "12m ago",
  },
  {
    id: "Q-195",
    project: "Nova Retail",
    reason: "Unknown device fingerprint",
    severity: "high",
    status: "Escalated",
    time: "21m ago",
  },
];

export default function Quarantine() {
  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h2>Quarantine</h2>
          <p>Investigate suspicious payloads and enforce protections.</p>
        </div>
        <button className="btn primary">Resolve all safe</button>
      </div>

      <div className="grid-2">
        <div className="card">
          <div className="card-title">Risk queue</div>
          <div className="table">
            <div className="table-row head">
              <span>ID</span>
              <span>Project</span>
              <span>Reason</span>
              <span>Severity</span>
              <span>Status</span>
              <span>Time</span>
            </div>
            {incidents.map((incident) => (
              <div key={incident.id} className="table-row">
                <span className="mono">{incident.id}</span>
                <span>{incident.project}</span>
                <span>{incident.reason}</span>
                <span>
                  <Tag
                    tone={incident.severity === "high" ? "risk" : "warn"}
                  >
                    {incident.severity}
                  </Tag>
                </span>
                <span>{incident.status}</span>
                <span>{incident.time}</span>
              </div>
            ))}
          </div>
        </div>

        <div className="card">
          <div className="card-title">Auto response rules</div>
          <div className="card-subtitle">
            AI will take action when thresholds are crossed.
          </div>
          <div className="rule-list">
            <div className="rule-item">
              <div>
                <div className="rule-title">PII fingerprint confidence</div>
                <div className="rule-subtitle">Block if above 0.78</div>
              </div>
              <ProgressBar value={78} tone="red" />
            </div>
            <div className="rule-item">
              <div>
                <div className="rule-title">Schema drift delta</div>
                <div className="rule-subtitle">Quarantine if above 1.2%</div>
              </div>
              <ProgressBar value={62} tone="amber" />
            </div>
            <div className="rule-item">
              <div>
                <div className="rule-title">Payload size anomaly</div>
                <div className="rule-subtitle">Throttle if above 1.6x</div>
              </div>
              <ProgressBar value={54} tone="teal" />
            </div>
          </div>
          <button className="btn ghost small">Edit rules</button>
        </div>
      </div>

      <div className="card">
        <div className="card-title">Live payload snapshot</div>
        <div className="card-subtitle">
          Sanitized view of a quarantined event.
        </div>
        <pre className="code-block">
{`{
  "event_name": "checkout_submit",
  "project": "Orbit Wallet",
  "risk_score": 0.86,
  "reasons": ["pii_fingerprint", "schema_delta"],
  "payload": {
    "wallet_address": "0x92f1...44c2",
    "device_fingerprint": "fp_1239_99",
    "region": "us-east"
  }
}`}
        </pre>
      </div>
    </div>
  );
}
