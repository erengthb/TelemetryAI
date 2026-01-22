import { NavLink } from "react-router-dom";
import Icon, { type IconName } from "./Icon";

const navItems: { to: string; label: string; icon: IconName }[] = [
  { to: "/", label: "Home", icon: "dashboard" },
  { to: "/projects", label: "Projects", icon: "projects" },
  { to: "/api-keys", label: "Keys", icon: "api-keys" },
  { to: "/quarantine", label: "Risk", icon: "quarantine" },
  { to: "/reports", label: "Reports", icon: "reports" },
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
