import Tag from "../components/Tag";

const projects = [
  {
    name: "Aurora Operasyon",
    env: "UAT",
    status: "Canli",
    events: "18.2M",
    drift: "0.6%",
    keys: "6",
    note: "12.4 yamasi sonrasi gecikme azaltildi",
  },
  {
    name: "Orbit Cuzdan",
    env: "Uretim",
    status: "Korumali",
    events: "9.4M",
    drift: "1.4%",
    keys: "4",
    note: "Ilk kayit veri paketinde kisisel veri bulundu",
  },
  {
    name: "Helix Oyunlar",
    env: "UAT",
    status: "Canli",
    events: "6.1M",
    drift: "0.2%",
    keys: "3",
    note: "Sezon lansmani icin sema kilitli",
  },
  {
    name: "Nova Perakende",
    env: "Gelistirme",
    status: "Duraklatildi",
    events: "1.9M",
    drift: "2.8%",
    keys: "2",
    note: "Alan beyaz listesi onay bekliyor",
  },
];

export default function Projects() {
  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h2>Projeler</h2>
          <p>Her proje icin hat, sema politikasi ve erisim anahtarlarini yonet.</p>
        </div>
        <button className="btn primary">Yeni proje</button>
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
                  project.status === "Canli"
                    ? "safe"
                    : project.status === "Korumali"
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
                <div className="metric-label">bu ayki olaylar</div>
              </div>
              <div>
                <div className="metric-value">{project.drift}</div>
                <div className="metric-label">sema sapmasi</div>
              </div>
              <div>
                <div className="metric-value">{project.keys}</div>
                <div className="metric-label">aktif anahtarlar</div>
              </div>
            </div>
            <div className="project-note">{project.note}</div>
            <div className="project-actions">
              <button className="btn ghost small">Paneli ac</button>
              <button className="btn ghost small">Anahtarlari dondur</button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
