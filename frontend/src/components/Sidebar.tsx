import { NavLink } from "react-router-dom";
import Icon, { type IconName } from "./Icon";

const navItems: { to: string; label: string; icon: IconName }[] = [
  { to: "/", label: "Genel Bakis", icon: "dashboard" },
  { to: "/projects", label: "Projeler", icon: "projects" },
  { to: "/api-keys", label: "API Anahtarlari", icon: "api-keys" },
  { to: "/schema", label: "Sema Laboratuvari", icon: "schema" },
  { to: "/quarantine", label: "Karantina", icon: "quarantine" },
  { to: "/reports", label: "Yapay Zeka Raporlari", icon: "reports" },
  { to: "/settings", label: "Ayarlar", icon: "settings" },
];

export default function Sidebar() {
  return (
    <aside className="sidebar">
      <div className="sidebar-brand">
        <div className="logo-mark">TA</div>
        <div>
          <div className="logo-title">TelemetryAI</div>
          <div className="logo-subtitle">Sinyal Kontrol Guvertesi</div>
        </div>
      </div>
      <div className="sidebar-section">
        <div className="sidebar-label">Yonlendirme</div>
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
        <div className="sidebar-label">Sistem Nabzi</div>
        <div className="pulse-card">
          <div>
            <div className="pulse-title">Test Kumesi</div>
            <div className="pulse-subtitle">Saglikli - 99.98%</div>
          </div>
          <div className="pulse-dot" />
        </div>
        <div className="pulse-meta">
          <span>Ana alim</span>
          <span>48ms P95</span>
        </div>
      </div>
    </aside>
  );
}
