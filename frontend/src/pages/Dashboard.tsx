import StatCard from "../components/StatCard";
import ProgressBar from "../components/ProgressBar";
import RadialMeter from "../components/RadialMeter";
import Tag from "../components/Tag";

const stats = [
  {
    label: "Events/min",
    value: "42.8k",
    delta: "+6.2%",
    detail: "30m trend",
    trend: "up" as const,
    points: [18, 28, 24, 32, 30, 35, 42, 40, 46, 43],
  },
  {
    label: "P95 latency",
    value: "58ms",
    delta: "-4.1%",
    detail: "optimized",
    trend: "up" as const,
    points: [70, 64, 62, 59, 58, 56, 57, 58, 56, 58],
  },
  {
    label: "Schema drift",
    value: "0.9%",
    delta: "+0.2%",
    detail: "watchlist",
    trend: "down" as const,
    points: [0.6, 0.8, 1.1, 0.9, 0.7, 0.9, 1.0, 0.9],
  },
  {
    label: "Quarantine",
    value: "214",
    delta: "+12%",
    detail: "awaiting action",
    trend: "down" as const,
    points: [120, 140, 160, 180, 210, 204, 216, 214],
  },
];

const pipelines = [
  {
    name: "Core ingest",
    status: "Live",
    latency: "48ms",
    throughput: "18.3k/s",
    drop: "0.02%",
  },
  {
    name: "Mobile telemetry",
    status: "Live",
    latency: "64ms",
    throughput: "11.1k/s",
    drop: "0.05%",
  },
  {
    name: "Partner sync",
    status: "Throttled",
    latency: "92ms",
    throughput: "4.8k/s",
    drop: "0.18%",
  },
];

const alerts = [
  {
    id: "Q-192",
    project: "Aurora Ops",
    reason: "PII fingerprint detected",
    severity: "high",
    time: "2m ago",
  },
  {
    id: "Q-193",
    project: "Helix Games",
    reason: "Schema field removed",
    severity: "med",
    time: "8m ago",
  },
  {
    id: "Q-194",
    project: "Orbit Wallet",
    reason: "Payload size spike",
    severity: "med",
    time: "12m ago",
  },
];

const regions = [
  { name: "EU West", value: "38%", tone: "teal" as const },
  { name: "US East", value: "27%", tone: "amber" as const },
  { name: "AP South", value: "19%", tone: "teal" as const },
  { name: "LATAM", value: "16%", tone: "red" as const },
];

export default function Dashboard() {
  return (
    <div className="page">
      <section className="hero">
        <div className="hero-copy">
          <div className="hero-badge">UAT - Live ingestion</div>
          <h1>Control the signal layer.</h1>
          <p>
            Real-time telemetry protection for every project, with adaptive
            schema enforcement and AI quarantine.
          </p>
          <div className="hero-actions">
            <button className="btn primary">Launch new pipeline</button>
            <button className="btn ghost">Open runbook</button>
          </div>
        </div>
        <div className="hero-panel card">
          <RadialMeter value={92} label="Pulse" caption="Global health score" />
          <div className="hero-metrics">
            <div>
              <div className="metric-value">12</div>
              <div className="metric-label">active projects</div>
            </div>
            <div>
              <div className="metric-value">18.2k</div>
              <div className="metric-label">events/sec</div>
            </div>
            <div>
              <div className="metric-value">3.2</div>
              <div className="metric-label">AI actions/min</div>
            </div>
          </div>
          <div className="hero-bars">
            <div className="bar-row">
              <span>Schema stability</span>
              <ProgressBar value={96} />
            </div>
            <div className="bar-row">
              <span>PII scrub rate</span>
              <ProgressBar value={88} tone="amber" />
            </div>
            <div className="bar-row">
              <span>Queue pressure</span>
              <ProgressBar value={42} tone="red" />
            </div>
          </div>
        </div>
      </section>

      <section className="stat-grid">
        {stats.map((stat, index) => (
          <div key={stat.label} className={`stagger-${index + 1}`}>
            <StatCard {...stat} />
          </div>
        ))}
      </section>

      <section className="grid-2">
        <div className="card">
          <div className="card-header">
            <div>
              <div className="card-title">Active pipelines</div>
              <div className="card-subtitle">Live ingestion status</div>
            </div>
            <button className="btn ghost small">View all</button>
          </div>
          <div className="table">
            <div className="table-row head cols-5">
              <span>Pipeline</span>
              <span>Status</span>
              <span>Latency</span>
              <span>Throughput</span>
              <span>Drop</span>
            </div>
            {pipelines.map((pipe) => (
              <div key={pipe.name} className="table-row cols-5">
                <span>{pipe.name}</span>
                <span>
                  <Tag tone={pipe.status === "Live" ? "safe" : "warn"}>
                    {pipe.status}
                  </Tag>
                </span>
                <span>{pipe.latency}</span>
                <span>{pipe.throughput}</span>
                <span>{pipe.drop}</span>
              </div>
            ))}
          </div>
        </div>
        <div className="card">
          <div className="card-header">
            <div>
              <div className="card-title">Quarantine queue</div>
              <div className="card-subtitle">Latest flagged events</div>
            </div>
            <button className="btn ghost small">Resolve</button>
          </div>
          <div className="list">
            {alerts.map((alert) => (
              <div key={alert.id} className="list-item">
                <div>
                  <div className="list-title">{alert.project}</div>
                  <div className="list-subtitle">{alert.reason}</div>
                </div>
                <div className="list-meta">
                  <Tag tone={alert.severity === "high" ? "risk" : "warn"}>
                    {alert.severity}
                  </Tag>
                  <span className="muted">{alert.time}</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section className="grid-3">
        <div className="card">
          <div className="card-title">Region share</div>
          <div className="card-subtitle">Event volume by edge</div>
          <div className="region-list">
            {regions.map((region) => (
              <div key={region.name} className="region-item">
                <div>
                  <div className="region-name">{region.name}</div>
                  <div className="region-value">{region.value}</div>
                </div>
                <ProgressBar
                  value={parseInt(region.value, 10)}
                  tone={region.tone}
                />
              </div>
            ))}
          </div>
        </div>
        <div className="card">
          <div className="card-title">AI actions</div>
          <div className="card-subtitle">Autonomous enforcement</div>
          <div className="action-tiles">
            <div className="tile">
              <div className="tile-value">312</div>
              <div className="tile-label">PII scrubs</div>
            </div>
            <div className="tile">
              <div className="tile-value">86</div>
              <div className="tile-label">auto retries</div>
            </div>
            <div className="tile">
              <div className="tile-value">41</div>
              <div className="tile-label">schema patches</div>
            </div>
            <div className="tile">
              <div className="tile-value">7</div>
              <div className="tile-label">escalations</div>
            </div>
          </div>
        </div>
        <div className="card">
          <div className="card-title">Model cost</div>
          <div className="card-subtitle">AI inspection spend</div>
          <div className="cost-stack">
            <div className="cost-row">
              <span>PII check</span>
              <span>$312</span>
            </div>
            <div className="cost-row">
              <span>Schema drift</span>
              <span>$188</span>
            </div>
            <div className="cost-row">
              <span>Anomaly detect</span>
              <span>$96</span>
            </div>
            <div className="cost-total">
              <span>Monthly</span>
              <span>$596</span>
            </div>
          </div>
          <button className="btn ghost small">Tune budget</button>
        </div>
      </section>
    </div>
  );
}
