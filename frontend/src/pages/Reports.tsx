const usage = [
  { label: "Week 1", value: 72 },
  { label: "Week 2", value: 88 },
  { label: "Week 3", value: 64 },
  { label: "Week 4", value: 92 },
];

const endpoints = [
  { name: "/ingest/events", volume: "18.2M", cost: "$214" },
  { name: "/ingest/mobile", volume: "11.4M", cost: "$162" },
  { name: "/schema/validate", volume: "4.1M", cost: "$86" },
  { name: "/quarantine/push", volume: "780k", cost: "$41" },
];

export default function Reports() {
  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h2>Reports</h2>
          <p>Usage, cost, and performance trends across environments.</p>
        </div>
        <button className="btn primary">Export CSV</button>
      </div>

      <div className="grid-2">
        <div className="card">
          <div className="card-title">Monthly usage</div>
          <div className="card-subtitle">Events inspected per week</div>
          <div className="bar-chart">
            {usage.map((item) => (
              <div key={item.label} className="bar-item">
                <div
                  className="bar-fill"
                  style={{ height: `${item.value}%` }}
                />
                <span>{item.label}</span>
              </div>
            ))}
          </div>
        </div>
        <div className="card">
          <div className="card-title">Cost breakdown</div>
          <div className="card-subtitle">AI inspection budget</div>
          <div className="cost-grid">
            <div className="cost-card">
              <div className="cost-value">$596</div>
              <div className="cost-label">total spend</div>
            </div>
            <div className="cost-card">
              <div className="cost-value">$312</div>
              <div className="cost-label">PII filter</div>
            </div>
            <div className="cost-card">
              <div className="cost-value">$188</div>
              <div className="cost-label">schema drift</div>
            </div>
            <div className="cost-card">
              <div className="cost-value">$96</div>
              <div className="cost-label">anomaly scan</div>
            </div>
          </div>
          <button className="btn ghost small">Set budget guard</button>
        </div>
      </div>

      <div className="card">
        <div className="card-title">Top endpoints</div>
        <div className="table">
          <div className="table-row head cols-4">
            <span>Endpoint</span>
            <span>Volume</span>
            <span>Cost</span>
            <span>Action</span>
          </div>
          {endpoints.map((endpoint) => (
            <div key={endpoint.name} className="table-row cols-4">
              <span className="mono">{endpoint.name}</span>
              <span>{endpoint.volume}</span>
              <span>{endpoint.cost}</span>
              <span>
                <button className="btn ghost small">Inspect</button>
              </span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
