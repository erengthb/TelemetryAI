type TagProps = {
  tone?: "safe" | "warn" | "risk" | "info";
  children: string;
};

export default function Tag({ tone = "info", children }: TagProps) {
  return <span className={`tag ${tone}`}>{children}</span>;
}
