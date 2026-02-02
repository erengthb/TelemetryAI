import { ReactNode, useEffect, useState } from "react";
import { Navigate, useLocation } from "react-router-dom";
import { api } from "../api";

type RequireAuthProps = {
  children: ReactNode;
};

export default function RequireAuth({ children }: RequireAuthProps) {
  const location = useLocation();
  const [status, setStatus] = useState<"loading" | "authed" | "unauth">("loading");

  useEffect(() => {
    let active = true;
    api
      .me()
      .then(() => {
        if (active) {
          setStatus("authed");
        }
      })
      .catch(() => {
        if (active) {
          setStatus("unauth");
        }
      });
    return () => {
      active = false;
    };
  }, []);

  if (status === "loading") {
    return <div className="page-loading">Yukleniyor...</div>;
  }

  if (status === "unauth") {
    return <Navigate to="/login" replace state={{ from: location.pathname }} />;
  }

  return <>{children}</>;
}
