import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { api } from "../api";

export default function Login() {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    let active = true;
    api
      .me()
      .then(() => {
        if (active) {
          navigate("/", { replace: true });
        }
      })
      .catch(() => {
        // Not authenticated yet.
      });
    return () => {
      active = false;
    };
  }, [navigate]);

  const handleSubmit = async () => {
    setLoading(true);
    setError(null);
    try {
      await api.login({ email, password });
      navigate("/");
    } catch (err) {
      setError("Giris basarisiz. Bilgileri kontrol et.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-hero">
        <div className="auth-logo">
          <div className="logo-mark">TA</div>
          <div>
            <div className="logo-title">TelemetryAI</div>
            <div className="logo-subtitle">Sinyal Kontrol Guvertesi</div>
          </div>
        </div>
        <h1>Sinyal katmanini yonet.</h1>
        <p>
          Her olayi izle, sapmalari aninda yakala ve veri paketlerini uretim
          sistemlerine gitmeden once temiz tut.
        </p>
        <div className="auth-metrics">
          <div className="card soft">
            <div className="metric-value">42.8k</div>
            <div className="metric-label">dakikadaki olay</div>
          </div>
          <div className="card soft">
            <div className="metric-value">99.98%</div>
            <div className="metric-label">sema istikrari</div>
          </div>
          <div className="card soft">
            <div className="metric-value">18ms</div>
            <div className="metric-label">P95 inceleme</div>
          </div>
        </div>
        <div className="auth-footer">
          <span className="pill outline">Test</span>
          <span>Surum: 20.14</span>
        </div>
      </div>
      <div className="auth-panel card">
        <div className="auth-title">
          <h2>Giris yap</h2>
          <p>Guverteye girmek icin org erisim anahtarini kullan.</p>
        </div>
        <form className="auth-form">
          <label>
            E-posta
            <input
              type="email"
              placeholder="name@telemetryai.dev"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
            />
          </label>
          <label>
            Erisim anahtari
            <input
              type="password"
              placeholder="****************"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
            />
          </label>
          <label className="checkbox">
            <input type="checkbox" defaultChecked />
            Bu cihazi hatirla
          </label>
          <button type="button" className="btn primary" onClick={handleSubmit} disabled={loading}>
            {loading ? "Baglaniyor..." : "Kontrol guvertesine gir"}
          </button>
          <button type="button" className="btn ghost">
            Erisim iste
          </button>
        </form>
        {error ? <div className="auth-note">{error}</div> : null}
        <div className="auth-note">
          Devam ederek guvenlik politikasini kabul edersin.
        </div>
      </div>
    </div>
  );
}
