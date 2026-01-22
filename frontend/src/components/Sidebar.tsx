import { NavLink } from "react-router-dom";
import Icon, { type IconName } from "./Icon";

const navItems: { to: string; label: string; icon: IconName }[] = [
  { to: "/", label: "Command", icon: "dashboard" },
  { to: "/projects", label: "Projects", icon: "projects" },
  { to: "/api-keys", label: "API Keys", icon: "api-keys" },
  { to: "/schema", label: "Schema Lab", icon: "schema" },
  { to: "/quarantine", label: "Quarantine", icon: "quarantine" },
  { to: "/reports", label: "Reports", icon: "reports" },
  { to: "/settings", label: "Settings", icon: "settings" },
];

export default function Sidebar() {
  return (
    <aside className="sidebar">
      <div className="sidebar-brand">
        <div className="logo-mark">TA</div>
        <div>
          <div className="logo-title">TelemetryAI</div>
          <div className="logo-subtitle">Signal Control Deck</div>
        </div>
      </div>
      <div className="sidebar-section">
        <div className="sidebar-label">Navigation</div>
        <nav className="sidebar-nav">
          {navItems.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) =>
                `nav-item ${isActive ? "active" : ""}`
              }
              end={item.to === "/"}
            >
              <Icon name={item.icon} />
              <span>{item.label}</span>
            </NavLink>
          ))}
        </nav>
      </div>
      <div className="sidebar-section">
        <div className="sidebar-label">System Pulse</div>
        <div className="pulse-card">
          <div>
            <div className="pulse-title">UAT Cluster</div>
            <div className="pulse-subtitle">Healthy - 99.98%</div>
          </div>
          <div className="pulse-dot" />
        </div>
        <div className="pulse-meta">
          <span>Core ingest</span>
          <span>48ms P95</span>
        </div>
      </div>
    </aside>
  );
}
