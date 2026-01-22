export default function Settings() {
  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h2>Ayarlar</h2>
          <p>Ortam kontrolleri ve guvenlik tercihleri.</p>
        </div>
        <button className="btn primary">Degisiklikleri kaydet</button>
      </div>

      <div className="grid-2">
        <div className="card">
          <div className="card-title">Ortam</div>
          <div className="form-stack">
            <label>
              Varsayilan ortam
              <select>
                <option>Gelistirme</option>
                <option>UAT</option>
                <option>Uretim</option>
              </select>
            </label>
            <label>
              Bolge tercihi
              <select>
                <option>AB Bati</option>
                <option>ABD Dogu</option>
                <option>AP Guney</option>
              </select>
            </label>
            <label>
              Uyari esigi
              <input type="text" placeholder="0.72" />
            </label>
          </div>
        </div>

        <div className="card">
          <div className="card-title">Erisim kontrolu</div>
          <div className="form-stack">
            <label>
              MFA zorunlulugu
              <select>
                <option>Zorunlu</option>
                <option>Opsiyonel</option>
              </select>
            </label>
            <label>
              Oturum suresi
              <input type="text" placeholder="12 saat" />
            </label>
            <label>
              IP izin listesi
              <input type="text" placeholder="203.0.113.0/24" />
            </label>
          </div>
        </div>
      </div>

      <div className="card">
        <div className="card-title">Bildirim kanallari</div>
        <div className="table">
          <div className="table-row head cols-4">
            <span>Kanal</span>
            <span>Durum</span>
            <span>Hedef</span>
            <span>Aksiyon</span>
          </div>
          <div className="table-row cols-4">
            <span>Slack</span>
            <span>Etkin</span>
            <span>#telemetry-uyarilar</span>
            <span>
              <button className="btn ghost small">Duzenle</button>
            </span>
          </div>
          <div className="table-row cols-4">
            <span>E-posta</span>
            <span>Etkin</span>
            <span>ops@telemetryai.dev</span>
            <span>
              <button className="btn ghost small">Duzenle</button>
            </span>
          </div>
          <div className="table-row cols-4">
            <span>Cagri</span>
            <span>Beklemede</span>
            <span>Nobet rotasyonu</span>
            <span>
              <button className="btn ghost small">Duzenle</button>
            </span>
          </div>
        </div>
      </div>
    </div>
  );
}
