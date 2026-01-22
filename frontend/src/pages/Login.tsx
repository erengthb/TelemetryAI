export default function Login() {
  return (
    <div className="auth-page">
      <div className="auth-hero">
        <div className="auth-logo">
          <div className="logo-mark">TA</div>
          <div>
            <div className="logo-title">TelemetryAI</div>
            <div className="logo-subtitle">Signal Control Deck</div>
          </div>
        </div>
        <h1>Command the signal layer.</h1>
        <p>
          Monitor every event, detect drift instantly, and keep payloads clean
          before they hit production systems.
        </p>
        <div className="auth-metrics">
          <div className="card soft">
            <div className="metric-value">42.8k</div>
            <div className="metric-label">events per minute</div>
          </div>
          <div className="card soft">
            <div className="metric-value">99.98%</div>
            <div className="metric-label">schema stability</div>
          </div>
          <div className="card soft">
            <div className="metric-value">18ms</div>
            <div className="metric-label">P95 inspection</div>
          </div>
        </div>
        <div className="auth-footer">
          <span className="pill outline">UAT</span>
          <span>Build: 20.14</span>
        </div>
      </div>
      <div className="auth-panel card">
        <div className="auth-title">
          <h2>Sign in</h2>
          <p>Use your org access key to enter the deck.</p>
        </div>
        <form className="auth-form">
          <label>
            Email
            <input type="email" placeholder="name@telemetryai.dev" />
          </label>
          <label>
            Access key
            <input type="password" placeholder="****************" />
          </label>
          <label className="checkbox">
            <input type="checkbox" defaultChecked />
            Remember this device
          </label>
          <button type="button" className="btn primary">
            Enter control deck
          </button>
          <button type="button" className="btn ghost">
            Request access
          </button>
        </form>
        <div className="auth-note">
          By continuing you agree to the security policy.
        </div>
      </div>
    </div>
  );
}
