import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "运筹学实验室 | Operational Research Lab",
  description: "用交互式计算器学习单纯形法、图算法、指派问题、动态规划、排队论和存储论。",
};

export default function RootLayout({ children }: Readonly<{ children: React.ReactNode }>) {
  return <html lang="zh-CN"><body>{children}</body></html>;
}
