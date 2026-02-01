import { Route, Routes } from "react-router-dom";
import Shell from "./components/Shell";
import RequireAuth from "./components/RequireAuth";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import Projects from "./pages/Projects";
import Events from "./pages/Events";
import ApiKeys from "./pages/ApiKeys";
import Schema from "./pages/Schema";
import Quarantine from "./pages/Quarantine";
import Reports from "./pages/Reports";
import Settings from "./pages/Settings";
import NotFound from "./pages/NotFound";

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route
        path="/"
        element={
          <RequireAuth>
            <Shell />
          </RequireAuth>
        }
      >
        <Route index element={<Dashboard />} />
        <Route path="projects" element={<Projects />} />
        <Route path="events" element={<Events />} />
        <Route path="api-keys" element={<ApiKeys />} />
        <Route path="schema" element={<Schema />} />
        <Route path="quarantine" element={<Quarantine />} />
        <Route path="reports" element={<Reports />} />
        <Route path="settings" element={<Settings />} />
      </Route>
      <Route path="*" element={<NotFound />} />
    </Routes>
  );
}
