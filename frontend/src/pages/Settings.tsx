export default function Settings() {
  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h2>Settings</h2>
          <p>Environment controls and security preferences.</p>
        </div>
        <button className="btn primary">Save changes</button>
      </div>

      <div className="grid-2">
        <div className="card">
          <div className="card-title">Environment</div>
          <div className="form-stack">
            <label>
              Default environment
              <select>
                <option>Dev</option>
                <option>UAT</option>
                <option>Prod</option>
              </select>
            </label>
            <label>
              Region preference
              <select>
                <option>EU West</option>
                <option>US East</option>
                <option>AP South</option>
              </select>
            </label>
            <label>
              Alert threshold
              <input type="text" placeholder="0.72" />
            </label>
          </div>
        </div>

        <div className="card">
          <div className="card-title">Access control</div>
          <div className="form-stack">
            <label>
              MFA enforcement
              <select>
                <option>Required</option>
                <option>Optional</option>
              </select>
            </label>
            <label>
              Session duration
              <input type="text" placeholder="12 hours" />
            </label>
            <label>
              IP allowlist
              <input type="text" placeholder="203.0.113.0/24" />
            </label>
          </div>
        </div>
      </div>

      <div className="card">
        <div className="card-title">Notification channels</div>
        <div className="table">
          <div className="table-row head cols-4">
            <span>Channel</span>
            <span>Status</span>
            <span>Target</span>
            <span>Action</span>
          </div>
          <div className="table-row cols-4">
            <span>Slack</span>
            <span>Enabled</span>
            <span>#telemetry-alerts</span>
            <span>
              <button className="btn ghost small">Edit</button>
            </span>
          </div>
          <div className="table-row cols-4">
            <span>Email</span>
            <span>Enabled</span>
            <span>ops@telemetryai.dev</span>
            <span>
              <button className="btn ghost small">Edit</button>
            </span>
          </div>
          <div className="table-row cols-4">
            <span>Pager</span>
            <span>Standby</span>
            <span>On-call rotation</span>
            <span>
              <button className="btn ghost small">Edit</button>
            </span>
          </div>
        </div>
      </div>
    </div>
  );
}
