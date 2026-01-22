import StatCard from "../components/StatCard";
import ProgressBar from "../components/ProgressBar";
import RadialMeter from "../components/RadialMeter";
import Tag from "../components/Tag";

const stats = [
  {
    label: "Olay/dk",
    value: "42.8k",
    delta: "+6.2%",
    detail: "30dk trend",
    trend: "up" as const,
    points: [18, 28, 24, 32, 30, 35, 42, 40, 46, 43],
  },
  {
    label: "P95 gecikme",
    value: "58ms",
    delta: "-4.1%",
    detail: "optimize",
    trend: "up" as const,
    points: [70, 64, 62, 59, 58, 56, 57, 58, 56, 58],
  },
  {
    label: "Sema sapmasi",
    value: "0.9%",
    delta: "+0.2%",
    detail: "izleme listesi",
    trend: "down" as const,
    points: [0.6, 0.8, 1.1, 0.9, 0.7, 0.9, 1.0, 0.9],
  },
  {
    label: "Karantina",
    value: "214",
    delta: "+12%",
    detail: "aksiyon bekliyor",
    trend: "down" as const,
    points: [120, 140, 160, 180, 210, 204, 216, 214],
  },
];

const pipelines = [
  {
    name: "Ana alim",
    status: "Canli",
    latency: "48ms",
    throughput: "18.3k/sn",
    drop: "0.02%",
  },
  {
    name: "Mobil telemetri",
    status: "Canli",
    latency: "64ms",
    throughput: "11.1k/sn",
    drop: "0.05%",
  },
  {
    name: "Is ortak senkron",
    status: "Kisitli",
    latency: "92ms",
    throughput: "4.8k/sn",
    drop: "0.18%",
  },
];

const alerts = [
  {
    id: "Q-192",
    project: "Aurora Operasyon",
    reason: "kisisel veri parmak izi tespit edildi",
    severity: "yuksek",
    time: "2dk once",
  },
  {
    id: "Q-193",
    project: "Helix Oyunlar",
    reason: "Sema alani kaldirildi",
    severity: "orta",
    time: "8dk once",
  },
  {
    id: "Q-194",
    project: "Orbit Cuzdan",
    reason: "Veri paketi boyutu sicradi",
    severity: "orta",
    time: "12dk once",
  },
];

const regions = [
  { name: "AB Bati", value: "38%", tone: "teal" as const },
  { name: "ABD Dogu", value: "27%", tone: "amber" as const },
  { name: "AP Guney", value: "19%", tone: "teal" as const },
  { name: "LATAM", value: "16%", tone: "red" as const },
];

export default function Dashboard() {
  return (
    <div className="page">
      <section className="hero">
        <div className="hero-copy">
        <div className="hero-badge">UAT - Canli alim</div>
        <h1>Sinyal katmanini kontrol et.</h1>
        <p>
          Her proje icin gercek zamanli telemetri korumasi, uyarlanabilir sema
          uygulamasi ve Yapay Zeka karantinasi.
        </p>
        <div className="hero-actions">
          <button className="btn primary">Yeni hat baslat</button>
          <button className="btn ghost">Calisma rehberi ac</button>
        </div>
      </div>
      <div className="hero-panel card">
        <RadialMeter value={92} label="Nabiz" caption="Genel saglik skoru" />
        <div className="hero-metrics">
          <div>
            <div className="metric-value">12</div>
            <div className="metric-label">aktif proje</div>
          </div>
          <div>
            <div className="metric-value">18.2k</div>
            <div className="metric-label">olay/sn</div>
          </div>
          <div>
            <div className="metric-value">3.2</div>
            <div className="metric-label">Yapay Zeka aksiyon/dk</div>
          </div>
        </div>
        <div className="hero-bars">
          <div className="bar-row">
            <span>Sema istikrari</span>
            <ProgressBar value={96} />
          </div>
          <div className="bar-row">
            <span>Kisisel veri temizleme orani</span>
            <ProgressBar value={88} tone="amber" />
          </div>
          <div className="bar-row">
            <span>Kuyruk baskisi</span>
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
              <div className="card-title">Aktif hatlar</div>
              <div className="card-subtitle">Canli alim durumu</div>
            </div>
            <button className="btn ghost small">Tumunu gor</button>
          </div>
          <div className="table">
            <div className="table-row head cols-5">
              <span>Hat</span>
              <span>Durum</span>
              <span>Gecikme</span>
              <span>Akis</span>
              <span>Kayip</span>
            </div>
            {pipelines.map((pipe) => (
              <div key={pipe.name} className="table-row cols-5">
                <span>{pipe.name}</span>
                <span>
                  <Tag tone={pipe.status === "Canli" ? "safe" : "warn"}>
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
              <div className="card-title">Karantina kuyrugu</div>
              <div className="card-subtitle">Son isaretlenen olaylar</div>
            </div>
            <button className="btn ghost small">Coz</button>
          </div>
          <div className="list">
            {alerts.map((alert) => (
              <div key={alert.id} className="list-item">
                <div>
                  <div className="list-title">{alert.project}</div>
                  <div className="list-subtitle">{alert.reason}</div>
                </div>
                <div className="list-meta">
                  <Tag tone={alert.severity === "yuksek" ? "risk" : "warn"}>
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
          <div className="card-title">Bolge payi</div>
          <div className="card-subtitle">Uc nokta olay hacmi</div>
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
          <div className="card-title">Yapay Zeka aksiyonlari</div>
          <div className="card-subtitle">Otonom uygulama</div>
          <div className="action-tiles">
            <div className="tile">
              <div className="tile-value">312</div>
              <div className="tile-label">Kisisel veri temizleme</div>
            </div>
            <div className="tile">
              <div className="tile-value">86</div>
              <div className="tile-label">otomatik tekrar</div>
            </div>
            <div className="tile">
              <div className="tile-value">41</div>
              <div className="tile-label">sema yamalari</div>
            </div>
            <div className="tile">
              <div className="tile-value">7</div>
              <div className="tile-label">eskalasyon</div>
            </div>
          </div>
        </div>
        <div className="card">
          <div className="card-title">Model maliyeti</div>
          <div className="card-subtitle">Yapay Zeka inceleme harcamasi</div>
          <div className="cost-stack">
            <div className="cost-row">
              <span>Kisisel veri kontrol</span>
              <span>$312</span>
            </div>
            <div className="cost-row">
              <span>Sema sapmasi</span>
              <span>$188</span>
            </div>
            <div className="cost-row">
              <span>Anomali tespit</span>
              <span>$96</span>
            </div>
            <div className="cost-total">
              <span>Aylik</span>
              <span>$596</span>
            </div>
          </div>
          <button className="btn ghost small">Butceyi ayarla</button>
        </div>
      </section>
    </div>
  );
}
