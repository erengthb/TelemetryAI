const usage = [
  { label: "Hafta 1", value: 72 },
  { label: "Hafta 2", value: 88 },
  { label: "Hafta 3", value: 64 },
  { label: "Hafta 4", value: 92 },
];

const endpoints = [
  { name: "/alim/olaylar", volume: "18.2M", cost: "$214" },
  { name: "/alim/mobil", volume: "11.4M", cost: "$162" },
  { name: "/sema/dogrula", volume: "4.1M", cost: "$86" },
  { name: "/karantina/gonder", volume: "780k", cost: "$41" },
];

export default function Reports() {
  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h2>Raporlar</h2>
          <p>Ortamlar arasindaki kullanim, maliyet ve performans trendleri.</p>
        </div>
        <button className="btn primary">CSV disari aktar</button>
      </div>

      <div className="grid-2">
        <div className="card">
          <div className="card-title">Aylik kullanim</div>
          <div className="card-subtitle">Haftalik incelenen olaylar</div>
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
          <div className="card-title">Maliyet dagilimi</div>
          <div className="card-subtitle">Yapay Zeka inceleme butcesi</div>
          <div className="cost-grid">
            <div className="cost-card">
              <div className="cost-value">$596</div>
              <div className="cost-label">toplam harcama</div>
            </div>
            <div className="cost-card">
              <div className="cost-value">$312</div>
              <div className="cost-label">Kisisel veri filtresi</div>
            </div>
            <div className="cost-card">
              <div className="cost-value">$188</div>
              <div className="cost-label">sema sapmasi</div>
            </div>
            <div className="cost-card">
              <div className="cost-value">$96</div>
              <div className="cost-label">anomali tarama</div>
            </div>
          </div>
          <button className="btn ghost small">Butce koruma ayarla</button>
        </div>
      </div>

      <div className="card">
        <div className="card-title">En cok uctan nokta</div>
        <div className="table">
          <div className="table-row head cols-4">
            <span>Uctan nokta</span>
            <span>Hacim</span>
            <span>Maliyet</span>
            <span>Aksiyon</span>
          </div>
          {endpoints.map((endpoint) => (
            <div key={endpoint.name} className="table-row cols-4">
              <span className="mono">{endpoint.name}</span>
              <span>{endpoint.volume}</span>
              <span>{endpoint.cost}</span>
              <span>
                <button className="btn ghost small">Incele</button>
              </span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
