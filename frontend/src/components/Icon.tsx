export type IconName =
  | "dashboard"
  | "projects"
  | "api-keys"
  | "schema"
  | "quarantine"
  | "reports"
  | "settings"
  | "search"
  | "bolt"
  | "shield"
  | "user"
  | "logout";

type IconProps = {
  name: IconName;
  className?: string;
};

export default function Icon({ name, className }: IconProps) {
  const classes = ["icon", className].filter(Boolean).join(" ");

  switch (name) {
    case "dashboard":
      return (
        <svg className={classes} viewBox="0 0 24 24">
          <path d="M4 4h7v7H4z" />
          <path d="M13 4h7v4h-7z" />
          <path d="M13 10h7v10h-7z" />
          <path d="M4 13h7v7H4z" />
        </svg>
      );
    case "projects":
      return (
        <svg className={classes} viewBox="0 0 24 24">
          <path d="M3 7h7l2-3h9v13H3z" />
          <path d="M3 10h18" />
        </svg>
      );
    case "api-keys":
      return (
        <svg className={classes} viewBox="0 0 24 24">
          <path d="M7 14a4 4 0 1 1 4-4" />
          <path d="M11 10h10l-2 2 2 2-2 2 2 2H11z" />
        </svg>
      );
    case "schema":
      return (
        <svg className={classes} viewBox="0 0 24 24">
          <path d="M4 5h6v6H4z" />
          <path d="M14 5h6v6h-6z" />
          <path d="M4 13h6v6H4z" />
          <path d="M14 13h6v6h-6z" />
        </svg>
      );
    case "quarantine":
      return (
        <svg className={classes} viewBox="0 0 24 24">
          <path d="M5 4h14v16H5z" />
          <path d="M9 8h6" />
          <path d="M9 12h6" />
          <path d="M9 16h6" />
        </svg>
      );
    case "reports":
      return (
        <svg className={classes} viewBox="0 0 24 24">
          <path d="M4 18V6" />
          <path d="M8 18V9" />
          <path d="M12 18V4" />
          <path d="M16 18v-6" />
          <path d="M20 18v-3" />
        </svg>
      );
    case "settings":
      return (
        <svg className={classes} viewBox="0 0 24 24">
          <path d="M12 8a4 4 0 1 0 0 8 4 4 0 0 0 0-8z" />
          <path d="M4 12h2" />
          <path d="M18 12h2" />
          <path d="M12 4v2" />
          <path d="M12 18v2" />
          <path d="M5.5 5.5l1.5 1.5" />
          <path d="M17 17l1.5 1.5" />
          <path d="M18.5 5.5L17 7" />
          <path d="M7 17l-1.5 1.5" />
        </svg>
      );
    case "search":
      return (
        <svg className={classes} viewBox="0 0 24 24">
          <path d="M11 18a7 7 0 1 1 0-14 7 7 0 0 1 0 14z" />
          <path d="M21 21l-4.3-4.3" />
        </svg>
      );
    case "bolt":
      return (
        <svg className={classes} viewBox="0 0 24 24">
          <path d="M13 2L4 14h6l-1 8 9-12h-6z" />
        </svg>
      );
    case "shield":
      return (
        <svg className={classes} viewBox="0 0 24 24">
          <path d="M12 3l7 3v6c0 5-3.1 8.6-7 9-3.9-.4-7-4-7-9V6z" />
          <path d="M9 12l2 2 4-4" />
        </svg>
      );
    case "user":
      return (
        <svg className={classes} viewBox="0 0 24 24">
          <path d="M12 12a4 4 0 1 0 0-8 4 4 0 0 0 0 8z" />
          <path d="M4 20c2-4 14-4 16 0" />
        </svg>
      );
    case "logout":
      return (
        <svg className={classes} viewBox="0 0 24 24">
          <path d="M10 7V5a2 2 0 0 1 2-2h6v18h-6a2 2 0 0 1-2-2v-2" />
          <path d="M4 12h12" />
          <path d="M8 9l-3 3 3 3" />
        </svg>
      );
    default:
      return null;
  }
}
