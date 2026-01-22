import Icon from "./Icon";

export default function Topbar() {
  return (
    <header className="topbar">
      <div className="topbar-left">
        <div className="topbar-title">
          <span className="pill outline">UAT</span>
          <span className="topbar-title-text">Live Telemetry Overview</span>
        </div>
        <div className="search">
          <Icon name="search" />
          <input
            type="search"
            placeholder="Search projects, keys, payloads"
            aria-label="Search"
          />
        </div>
      </div>
      <div className="topbar-right">
        <button className="pill soft">
          <Icon name="bolt" />
          Create pipeline
        </button>
        <button className="pill ghost">
          <Icon name="shield" />
          Threat report
        </button>
        <div className="avatar">
          <Icon name="user" />
        </div>
      </div>
    </header>
  );
}
