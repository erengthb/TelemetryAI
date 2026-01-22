type ProgressBarProps = {
  value: number;
  tone?: "teal" | "amber" | "red";
};

export default function ProgressBar({ value, tone = "teal" }: ProgressBarProps) {
  const clamped = Math.max(0, Math.min(100, value));
  return (
    <div className={`progress ${tone}`}>
      <div className="progress-fill" style={{ width: `${clamped}%` }} />
    </div>
  );
}
