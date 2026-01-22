import Tag from "../components/Tag";

const fields = [
  {
    name: "olay_adi",
    type: "metin",
    policy: "guvenli",
    note: "Kilitli",
  },
  {
    name: "oyuncu_id",
    type: "metin",
    policy: "kisisel",
    note: "Alimdan once hashle",
  },
  {
    name: "oturum_id",
    type: "metin",
    policy: "guvenli",
    note: "UUIDv7",
  },
  {
    name: "cuzdan_adresi",
    type: "metin",
    policy: "kisisel",
    note: "Analiz icin maskele",
  },
  {
    name: "cihaz_parmak_izi",
    type: "metin",
    policy: "risk",
    note: "Yeni ise karantina",
  },
];

const samplePayload = `{
  "olay_adi": "mac_baslangic",
  "oyuncu_id": "8f1c...d2",
  "oturum_id": "018d-7d2c-9a",
  "cuzdan_adresi": "0x92f1...44c2",
  "cihaz_parmak_izi": "fp_1239_99",
  "bolge": "ab-bati"
}`;

export default function Schema() {
  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h2>Sema Laboratuvari</h2>
          <p>Veri paketi yapisini tasarla ve kisisel veri koruma kurallarini uygula.</p>
        </div>
        <button className="btn primary">Guncelleme oner</button>
      </div>

      <div className="grid-2">
        <div className="card">
          <div className="card-title">Alan politika haritasi</div>
          <div className="table">
            <div className="table-row head cols-4">
              <span>Alan</span>
              <span>Tip</span>
              <span>Politika</span>
              <span>Not</span>
            </div>
            {fields.map((field) => (
              <div key={field.name} className="table-row cols-4">
                <span className="mono">{field.name}</span>
                <span>{field.type}</span>
                <span>
                  <Tag
                    tone={
                      field.policy === "guvenli"
                        ? "safe"
                        : field.policy === "kisisel"
                        ? "warn"
                        : "risk"
                    }
                  >
                    {field.policy}
                  </Tag>
                </span>
                <span>{field.note}</span>
              </div>
            ))}
          </div>
        </div>

        <div className="card">
          <div className="card-title">Ornek veri paketi</div>
          <div className="card-subtitle">
            Temizlenmis alanlarla canli sema onizleme.
          </div>
          <pre className="code-block">{samplePayload}</pre>
          <div className="schema-actions">
            <button className="btn ghost small">Dogrulama calistir</button>
            <button className="btn ghost small">JSON disari aktar</button>
          </div>
        </div>
      </div>

      <div className="card">
        <div className="card-title">Sema degisim gunlugu</div>
        <div className="timeline">
          <div className="timeline-item">
            <div className="timeline-time">2 saat once</div>
            <div className="timeline-body">
              <span className="mono">bolge</span> alani guvenli politikayla eklendi.
            </div>
          </div>
          <div className="timeline-item">
            <div className="timeline-time">Dun</div>
            <div className="timeline-body">
              <span className="mono">cuzdan_adresi</span> maskeleme kurali guncellendi.
            </div>
          </div>
          <div className="timeline-item">
            <div className="timeline-time">3 gun once</div>
            <div className="timeline-body">
              Kullanimdan kaldirilan <span className="mono">istemci_ip</span> alani kaldirildi.
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
