"use client";

import { useState } from "react";
import { allocation, assignment, dijkstra, floyd, inventory, parseMatrix, prim, queue, simplex, type SolveResult, type SolverId } from "@/lib/solvers";

const modules: { id: SolverId; category: string; name: string; english: string; description: string; icon: string }[] = [
  { id: "simplex", category: "线性规划", name: "单纯形法", english: "Simplex Method", description: "求解标准形式的最大化线性规划", icon: "∑" },
  { id: "assignment", category: "组合优化", name: "指派问题", english: "Assignment", description: "找到一对一任务分配的最低成本", icon: "▦" },
  { id: "allocation", category: "动态规划", name: "资源分配", english: "Resource Allocation", description: "在多个项目间分配设备以最大化收益", icon: "◫" },
  { id: "prim", category: "图论", name: "Prim 算法", english: "Minimum Spanning Tree", description: "计算无向图的最小生成树", icon: "⌁" },
  { id: "dijkstra", category: "图论", name: "Dijkstra 算法", english: "Shortest Path", description: "从指定节点寻找单源最短路径", icon: "↗" },
  { id: "floyd", category: "图论", name: "Floyd 算法", english: "All Pairs Shortest Paths", description: "计算任意两点之间的最短距离", icon: "◎" },
  { id: "queue", category: "排队论", name: "M/M/1 排队", english: "Queueing Theory", description: "估算单服务台系统的稳态指标", icon: "≋" },
  { id: "inventory", category: "存储论", name: "经济订货批量", english: "Inventory · EOQ", description: "平衡订货与持有成本", icon: "⬡" },
];

const samples: Record<SolverId, { primary: string; secondary?: string; tertiary?: string; start?: string }> = {
  simplex: { primary: "3, 5", secondary: "1, 0\n0, 2\n3, 2", tertiary: "4, 12, 18" },
  assignment: { primary: "9, 2, 7\n6, 4, 3\n5, 8, 1" },
  allocation: { primary: "0, 3, 5, 6\n0, 4, 5, 8\n0, 2, 5, 7" },
  prim: { primary: "0, 2, x, 6\n2, 0, 3, 8\nx, 3, 0, 1\n6, 8, 1, 0", start: "1" },
  dijkstra: { primary: "0, 4, 1, x\nx, 0, x, 1\nx, 2, 0, 5\nx, 0, x, 0", start: "1" },
  floyd: { primary: "0, 3, x, 7\nx, 0, 2, x\nx, x, 0, 1\n4, x, x, 0" },
  queue: { primary: "3", secondary: "5" },
  inventory: { primary: "1200", secondary: "50", tertiary: "2" },
};

const fieldLabels: Record<SolverId, { primary: string; secondary?: string; tertiary?: string; hint: string }> = {
  simplex: { primary: "目标函数系数 c", secondary: "约束系数矩阵 A", tertiary: "右端值 b", hint: "最大化 c·x，满足 Ax ≤ b、x ≥ 0。每行一条约束；b 必须非负。" },
  assignment: { primary: "成本矩阵", hint: "每行是一个任务，每列是一位人员。支持最多 15 个任务。" },
  allocation: { primary: "收益矩阵", hint: "每行一个项目，依次填写分配 0、1、…、N 台设备的收益。" },
  prim: { primary: "无向图邻接矩阵", hint: "矩阵须对称。无边填 x，对角线填 0；起点从 1 开始编号。" },
  dijkstra: { primary: "有向图邻接矩阵", hint: "无边填 x，对角线填 0。边权必须非负；起点从 1 开始编号。" },
  floyd: { primary: "有向图邻接矩阵", hint: "无边填 x，对角线填 0。允许负权边，会检测负权环。" },
  queue: { primary: "到达率 λ", secondary: "服务率 μ", hint: "两者使用同一时间单位，例如每小时 3 人和每小时 5 人。" },
  inventory: { primary: "年需求量 D", secondary: "每次订货成本 S", tertiary: "单位年持有成本 H", hint: "经典 EOQ 模型：需求稳定、瞬时补货、不允许缺货。" },
};

const isGraph = (id: SolverId) => id === "prim" || id === "dijkstra" || id === "floyd";
const isNumber = (id: SolverId) => id === "queue" || id === "inventory";

export default function Home() {
  const [selected, setSelected] = useState<SolverId>("dijkstra");
  const [values, setValues] = useState(samples.dijkstra);
  const [result, setResult] = useState<SolveResult | null>(null);
  const [error, setError] = useState("");
  const current = modules.find(item => item.id === selected)!;
  const labels = fieldLabels[selected];

  function select(id: SolverId) {
    setSelected(id);
    setValues(samples[id]);
    setResult(null);
    setError("");
    document.getElementById("workspace")?.scrollIntoView({ behavior: "smooth", block: "start" });
  }

  function update(key: "primary" | "secondary" | "tertiary" | "start", value: string) {
    setValues(previous => ({ ...previous, [key]: value }));
    setResult(null);
    setError("");
  }

  function solve() {
    try {
      const primary = values.primary;
      const secondary = values.secondary ?? "";
      const tertiary = values.tertiary ?? "";
      const vector = (text: string) => {
        const matrix = parseMatrix(text);
        if (matrix.length !== 1) throw new Error("向量请写在同一行，用逗号或空格分隔。 ");
        return matrix[0];
      };
      let answer: SolveResult;
      switch (selected) {
        case "simplex": answer = simplex(vector(primary), parseMatrix(secondary), vector(tertiary)); break;
        case "assignment": answer = assignment(parseMatrix(primary)); break;
        case "allocation": answer = allocation(parseMatrix(primary)); break;
        case "prim": answer = prim(parseMatrix(primary, true), Number(values.start) - 1); break;
        case "dijkstra": answer = dijkstra(parseMatrix(primary, true), Number(values.start) - 1); break;
        case "floyd": answer = floyd(parseMatrix(primary, true)); break;
        case "queue": answer = queue(Number(primary), Number(secondary)); break;
        case "inventory": answer = inventory(Number(primary), Number(secondary), Number(tertiary)); break;
      }
      setResult(answer);
      setError("");
    } catch (cause) {
      setResult(null);
      setError(cause instanceof Error ? cause.message : "计算失败，请检查输入。 ");
    }
  }

  function field(key: "primary" | "secondary" | "tertiary", label: string) {
    const numeric = isNumber(selected);
    const value = values[key] ?? "";
    return <label className="field" key={key}>
      <span>{label}</span>
      {numeric ? <input type="number" step="any" value={value} onChange={event => update(key, event.target.value)} />
        : <textarea rows={key === "primary" ? (isGraph(selected) || selected === "assignment" || selected === "allocation" ? 6 : 2) : key === "secondary" && selected === "simplex" ? 4 : 2} spellCheck={false} value={value} onChange={event => update(key, event.target.value)} />}
    </label>;
  }

  return <div className="site-shell">
    <header className="topbar">
      <a className="brand" href="#top" aria-label="返回顶部"><span className="brand-mark">OR</span><span>运筹学实验室<small>OPERATIONAL RESEARCH LAB</small></span></a>
      <nav aria-label="主导航"><a href="#explore">算法目录</a><a href="#workspace">在线实验</a><a href="#about">关于项目</a></nav>
      <a className="github-link" href="https://github.com/Xingdong-Li/OperationalResearch" target="_blank" rel="noreferrer">原项目 ↗</a>
    </header>

    <main id="top">
      <section className="hero">
        <div className="hero-copy"><div className="eyebrow"><span className="dot" /> 运筹学，从公式走向实践</div>
          <h1>把复杂问题，<br /><em>算得清清楚楚。</em></h1>
          <p>从最短路径到最优分配，在浏览器里输入数据、观察步骤、理解结果。重新打开这间运筹学实验室。</p>
          <div className="hero-actions"><a className="button primary" href="#explore">探索算法 <span>↗</span></a><a className="button secondary" href="#workspace">直接开始计算 <span>→</span></a></div>
          <div className="hero-meta"><span><strong>08</strong> 个实验模块</span><span><strong>100%</strong> 浏览器运行</span><span><strong>2026</strong> 重新出发</span></div>
        </div>
        <div className="hero-art" aria-hidden="true"><div className="art-grid" /><div className="graph-line line-one" /><div className="graph-line line-two" /><div className="graph-line line-three" /><div className="graph-line line-four" /><span className="graph-node node-a">A</span><span className="graph-node node-b">B</span><span className="graph-node node-c">C</span><span className="graph-node node-d">D</span><div className="art-card"><span>最短路径 / SHORTEST PATH</span><strong>A → C → D</strong><small>总权重 6 · 已找到最优解</small></div><div className="art-label">OPTIMIZE<br />EVERYTHING.</div></div>
      </section>

      <section className="catalog section" id="explore"><div className="section-head"><div><span className="overline">01 / ALGORITHM LIBRARY</span><h2>选择一个问题，开始推演。</h2></div><p>覆盖原项目的八个核心主题。每个模块都有示例数据，可以直接修改并计算。</p></div>
        <div className="module-grid">{modules.map((item, index) => <button className={`module-card ${selected === item.id ? "active" : ""}`} onClick={() => select(item.id)} key={item.id} type="button"><span className="module-top"><span className="module-icon">{item.icon}</span><span className="module-index">{String(index + 1).padStart(2, "0")}</span></span><span className="module-category">{item.category}</span><strong>{item.name}</strong><small>{item.english}</small><span className="module-desc">{item.description}</span><span className="module-arrow">↗</span></button>)}</div>
      </section>

      <section className="lab section" id="workspace"><div className="section-head"><div><span className="overline">02 / INTERACTIVE WORKSPACE</span><h2>在这里，把问题交给算法。</h2></div><p>示例数据已预填。修改输入后点击运行，即可查看计算结果。</p></div>
        <div className="lab-layout"><aside className="lab-sidebar"><span className="sidebar-label">实验模块</span>{modules.map(item => <button key={item.id} className={selected === item.id ? "selected" : ""} onClick={() => select(item.id)} type="button"><span>{item.icon}</span>{item.name}<span className="aside-arrow">›</span></button>)}</aside>
          <div className="lab-main"><div className="lab-title"><div><span className="pill">{current.category}</span><h3>{current.name}</h3><p>{current.description}</p></div><span className="lab-english">{current.english}</span></div>
            <div className="input-panel"><div className="panel-heading"><span>输入数据</span><button type="button" onClick={() => { setValues(samples[selected]); setResult(null); setError(""); }}>恢复示例 ↺</button></div><p className="input-hint">{labels.hint}</p><div className="fields">{field("primary", labels.primary)}{labels.secondary && field("secondary", labels.secondary)}{labels.tertiary && field("tertiary", labels.tertiary)}{(selected === "prim" || selected === "dijkstra") && <label className="field small-field"><span>起点编号</span><input type="number" min="1" step="1" value={values.start ?? "1"} onChange={event => update("start", event.target.value)} /></label>}</div><div className="form-footer"><span>数字用逗号或空格分隔 · 矩阵每行独占一行</span><button type="button" className="button primary" onClick={solve}>运行算法 <span>→</span></button></div></div>
            {error && <div className="error" role="alert">{error}</div>}
            {result ? <div className="result-panel" aria-live="polite"><div className="result-header"><span className="success-dot" /><span>计算完成</span><h4>{result.summary}</h4></div>{result.metrics.length > 0 && <div className="metrics">{result.metrics.map(metric => <div className="metric" key={metric.label}><span>{metric.label}</span><strong>{metric.value}</strong></div>)}</div>}{result.table && <div className="table-wrap"><h5>{result.table.heading}</h5><table><thead><tr>{result.table.columns.map((column, index) => <th key={index}>{column}</th>)}</tr></thead><tbody>{result.table.rows.map((row, i) => <tr key={i}>{row.map((cell, j) => <td key={j}>{cell}</td>)}</tr>)}</tbody></table></div>}<div className="steps"><h5>计算过程 / 模型说明</h5><ol>{result.steps.map((step, i) => <li key={i}>{step}</li>)}</ol></div></div> : <div className="empty-result"><span>✳</span><strong>结果将在这里出现</strong><p>运行算法后，查看最优解、关键指标和计算步骤。</p></div>}
          </div></div>
      </section>

      <section className="about section" id="about"><span className="overline">03 / ABOUT THE PROJECT</span><div><h2>写给曾经的课堂，<br />也写给现在的你。</h2><p>这个项目最初诞生于学习运筹学时：希望把繁琐的手算过程变成更直观的练习。新版沿用这份初心，用 React、Next.js 和 TypeScript 重建界面与计算逻辑，让它更容易运行、维护和继续扩展。</p><a href="https://github.com/Xingdong-Li/OperationalResearch" target="_blank" rel="noreferrer">查看 2020 年的原项目 ↗</a></div></section>
    </main>
    <footer><span>OR / 运筹学实验室</span><span>从一道题，走向更好的决策。</span><a href="#top">返回顶部 ↑</a></footer>
  </div>;
}
