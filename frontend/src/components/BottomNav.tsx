import { NavLink } from "react-router-dom";
import Icon, { type IconName } from "./Icon";

const navItems: { to: string; label: string; icon: IconName }[] = [
  { to: "/", label: "Ana", icon: "dashboard" },
  { to: "/projects", label: "Projeler", icon: "projects" },
  { to: "/api-keys", label: "Anahtarlar", icon: "api-keys" },
  { to: "/quarantine", label: "Risk", icon: "quarantine" },
  { to: "/reports", label: "Raporlar", icon: "reports" },
];

export default function BottomNav() {
  return (
    <nav className="bottom-nav">
      {navItems.map((item) => (
        <NavLink
          key={item.to}
          to={item.to}
          className={({ isActive }) =>
            `bottom-nav-item ${isActive ? "active" : ""}`
          }
          end={item.to === "/"}
        >
          <Icon name={item.icon} />
          <span>{item.label}</span>
        </NavLink>
      ))}
    </nav>
  );
}
