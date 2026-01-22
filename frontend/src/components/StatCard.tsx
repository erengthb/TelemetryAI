import Sparkline from "./Sparkline";

type StatCardProps = {
  label: string;
  value: string;
  delta: string;
  detail: string;
  trend?: "up" | "down";
  points: number[];
};

export default function StatCard({
  label,
  value,
  delta,
  detail,
  trend = "up",
  points,
}: StatCardProps) {
  return (
    <div className={`card stat-card ${trend === "down" ? "stat-down" : ""}`}>
      <div className="stat-header">
        <span className="label">{label}</span>
        <span className={`delta ${trend}`}>{delta}</span>
      </div>
      <div className="stat-value">{value}</div>
      <div className="stat-footer">
        <span className="muted">{detail}</span>
        <Sparkline points={points} />
      </div>
    </div>
  );
}
