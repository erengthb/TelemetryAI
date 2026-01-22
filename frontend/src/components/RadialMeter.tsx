type RadialMeterProps = {
  value: number;
  label: string;
  caption: string;
};

export default function RadialMeter({
  value,
  label,
  caption,
}: RadialMeterProps) {
  const clamped = Math.max(0, Math.min(100, value));
  const style = {
    background: `conic-gradient(var(--accent-1) 0% ${clamped}%, rgba(255,255,255,0.08) ${clamped}% 100%)`,
  } as const;

  return (
    <div className="radial">
      <div className="radial-ring" style={style}>
        <div className="radial-center">
          <div className="radial-value">{value}</div>
          <div className="radial-label">{label}</div>
        </div>
      </div>
      <div className="radial-caption">{caption}</div>
    </div>
  );
}
