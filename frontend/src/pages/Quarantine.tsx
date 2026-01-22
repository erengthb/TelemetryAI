import Tag from "../components/Tag";
import ProgressBar from "../components/ProgressBar";

const incidents = [
  {
    id: "Q-192",
    project: "Aurora Operasyon",
    reason: "kisisel veri parmak izi tespit edildi",
    severity: "yuksek",
    status: "Beklet",
    time: "2dk once",
  },
  {
    id: "Q-193",
    project: "Helix Oyunlar",
    reason: "Sema alani kaldirildi",
    severity: "orta",
    status: "Incele",
    time: "8dk once",
  },
  {
    id: "Q-194",
    project: "Orbit Cuzdan",
    reason: "Veri paketi boyutu sicradi",
    severity: "orta",
    status: "Beklet",
    time: "12dk once",
  },
  {
    id: "Q-195",
    project: "Nova Perakende",
    reason: "Bilinmeyen cihaz parmak izi",
    severity: "yuksek",
    status: "Yukseltildi",
    time: "21dk once",
  },
];

export default function Quarantine() {
  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h2>Karantina</h2>
          <p>Supheli veri paketlerini incele ve korumalari uygula.</p>
        </div>
        <button className="btn primary">Guvenli olanlari coz</button>
      </div>

      <div className="grid-2">
        <div className="card">
          <div className="card-title">Risk kuyrugu</div>
          <div className="table">
            <div className="table-row head">
              <span>ID</span>
              <span>Proje</span>
              <span>Neden</span>
              <span>Seviye</span>
              <span>Durum</span>
              <span>Zaman</span>
            </div>
            {incidents.map((incident) => (
              <div key={incident.id} className="table-row">
                <span className="mono">{incident.id}</span>
                <span>{incident.project}</span>
                <span>{incident.reason}</span>
                <span>
                  <Tag
                    tone={incident.severity === "yuksek" ? "risk" : "warn"}
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
          <div className="card-title">Otomatik yanit kurallari</div>
          <div className="card-subtitle">
            Yapay Zeka, esikler asildiginda aksiyon alir.
          </div>
          <div className="rule-list">
            <div className="rule-item">
              <div>
                <div className="rule-title">Kisisel veri parmak izi guveni</div>
                <div className="rule-subtitle">0.78 uzeri engelle</div>
              </div>
              <ProgressBar value={78} tone="red" />
            </div>
            <div className="rule-item">
              <div>
                <div className="rule-title">Sema sapma deltasi</div>
                <div className="rule-subtitle">1.2% uzeri karantina</div>
              </div>
              <ProgressBar value={62} tone="amber" />
            </div>
            <div className="rule-item">
              <div>
                <div className="rule-title">Veri paketi boyut anomalligi</div>
                <div className="rule-subtitle">1.6x uzeri kis</div>
              </div>
              <ProgressBar value={54} tone="teal" />
            </div>
          </div>
          <button className="btn ghost small">Kurallari duzenle</button>
        </div>
      </div>

      <div className="card">
        <div className="card-title">Canli veri paketi goruntusu</div>
        <div className="card-subtitle">
          Karantinaya alinmis bir olayin temizlenmis gorunumu.
        </div>
        <pre className="code-block">
{`{
  "olay_adi": "odeme_gonder",
  "proje": "Orbit Cuzdan",
  "risk_puani": 0.86,
  "nedenler": ["kisisel_veri_parmak_izi", "sema_delta"],
  "veri": {
    "cuzdan_adresi": "0x92f1...44c2",
    "cihaz_parmak_izi": "fp_1239_99",
    "bolge": "abd-dogu"
  }
}`}
        </pre>
      </div>
    </div>
  );
}
