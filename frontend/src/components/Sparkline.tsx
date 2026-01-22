type SparklineProps = {
  points: number[];
};

export default function Sparkline({ points }: SparklineProps) {
  if (points.length < 2) {
    return null;
  }

  const max = Math.max(...points);
  const min = Math.min(...points);
  const range = max - min || 1;

  const coords = points.map((point, index) => {
    const x = (index / (points.length - 1)) * 100;
    const y = 36 - ((point - min) / range) * 28;
    return `${x.toFixed(2)} ${y.toFixed(2)}`;
  });

  const d = `M ${coords.join(" L ")}`;

  return (
    <svg className="sparkline" viewBox="0 0 100 40" aria-hidden="true">
      <path d={d} />
    </svg>
  );
}
