import Icon from "./Icon";

export default function Topbar() {
  return (
    <header className="topbar">
      <div className="topbar-left">
        <div className="topbar-title">
          <span className="pill outline">Test</span>
          <span className="topbar-title-text">Canli Telemetri Ozeti</span>
        </div>
        <div className="search">
          <Icon name="search" />
          <input
            type="search"
            placeholder="Projeler, anahtarlar, veri paketleri ara"
            aria-label="Ara"
          />
        </div>
      </div>
      <div className="topbar-right">
        <button className="pill soft">
          <Icon name="bolt" />
          Hat olustur
        </button>
        <button className="pill ghost">
          <Icon name="shield" />
          Tehdit raporu
        </button>
        <div className="avatar">
          <Icon name="user" />
        </div>
      </div>
    </header>
  );
}
