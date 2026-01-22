import Tag from "../components/Tag";

const keys = [
  {
    name: "Aurora Operasyon - alim",
    last4: "4812",
    scope: "Alim + Okuma",
    usage: "2.9M cagri",
    limit: "30k/dk",
    created: "2026-01-12",
    status: "Aktif",
    mask: "7Q3M-9T2Q-****-4812",
  },
  {
    name: "Orbit Cuzdan - uc",
    last4: "9021",
    scope: "Alim",
    usage: "1.1M cagri",
    limit: "18k/dk",
    created: "2026-01-08",
    status: "Aktif",
    mask: "9B1X-8LPQ-****-9021",
  },
  {
    name: "Helix Oyunlar - toplu",
    last4: "4410",
    scope: "Sadece okuma",
    usage: "412k cagri",
    limit: "8k/dk",
    created: "2025-12-22",
    status: "Rotasyonda",
    mask: "2K8V-4Z1J-****-4410",
  },
];

export default function ApiKeys() {
  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h2>API Anahtarlari</h2>
          <p>Proje anahtarlari, rotasyon ve hiz limitlerini yonet.</p>
        </div>
        <button className="btn primary">Anahtar olustur</button>
      </div>

      <div className="grid-2">
        <div className="card">
          <div className="card-title">Aktif anahtarlar</div>
          <div className="table">
            <div className="table-row head">
              <span>Ad</span>
              <span>Maske</span>
              <span>Kapsam</span>
              <span>Kullanim</span>
              <span>Limit</span>
              <span>Durum</span>
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
                  <Tag tone={key.status === "Aktif" ? "safe" : "warn"}>
                    {key.status}
                  </Tag>
                </span>
              </div>
            ))}
          </div>
        </div>

        <div className="card">
          <div className="card-title">Anahtar sinirlari</div>
          <div className="card-subtitle">
            Kesinti olmadan gizli anahtarlari dondur.
          </div>
          <div className="form-stack">
            <label>
              Proje
              <select>
                <option>Aurora Operasyon</option>
                <option>Orbit Cuzdan</option>
                <option>Helix Oyunlar</option>
              </select>
            </label>
            <label>
              Kapsam
              <select>
                <option>Alim + Okuma</option>
                <option>Sadece alim</option>
                <option>Sadece okuma</option>
              </select>
            </label>
            <label>
              Hiz limiti
              <input type="text" placeholder="30000" />
            </label>
            <label>
              Rotasyon suresi
              <input type="text" placeholder="7 gun" />
            </label>
            <button className="btn primary">Anahtar uret</button>
            <div className="helper">
              Son4 gosterim icin saklanir; tam anahtar sadece bir kez gosterilir.
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
