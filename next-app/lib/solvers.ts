export type SolverId = "simplex" | "assignment" | "allocation" | "prim" | "dijkstra" | "floyd" | "queue" | "inventory";

export type SolveResult = {
  summary: string;
  metrics: { label: string; value: string }[];
  steps: string[];
  table?: { heading: string; columns: string[]; rows: string[][] };
};

const fmt = (value: number) => !Number.isFinite(value) ? "∞" :
  value !== 0 && Math.abs(value) < 0.0001 ? Number(value.toPrecision(8)).toString() : Number(value.toFixed(6)).toString();

function finiteValue(value: number): number {
  if (!Number.isFinite(value) || Math.abs(value) > Number.MAX_SAFE_INTEGER)
    throw new Error("数值超出可靠计算范围，请缩小输入量级。");
  return value;
}

function checkValues(values: number[]) {
  values.forEach(finiteValue);
}

function checkGraph(matrix: number[][]) {
  for (const value of matrix.flat()) {
    if (value !== Infinity && !Number.isFinite(value)) throw new Error("边权必须是有效数字或无边标记 x。");
    if (Number.isFinite(value)) finiteValue(value);
  }
}

export function parseMatrix(source: string, missing = false): number[][] {
  const rows = source.trim().split(/\r?\n/).map(line => line.trim()).filter(Boolean);
  if (!rows.length) throw new Error("请先输入矩阵。");
  const matrix = rows.map((line, i) => line.split(/[\s,，;；]+/).map((raw, j) => {
    if (missing && /^(x|-|∞|inf|infinity)$/i.test(raw)) return Infinity;
    const value = Number(raw);
    if (!raw || !Number.isFinite(value)) throw new Error(`第 ${i + 1} 行第 ${j + 1} 列不是有效数字。`);
    return finiteValue(value);
  }));
  const width = matrix[0].length;
  if (matrix.some(row => row.length !== width)) throw new Error("矩阵各行列数必须相同。");
  return matrix;
}

function square(matrix: number[][]) {
  if (!matrix.length || !matrix[0]?.length) throw new Error("请先输入非空矩阵。");
  if (matrix.length !== matrix[0].length) throw new Error("请输入方阵。");
  if (matrix.some(row => row.length !== matrix.length)) throw new Error("矩阵各行列数必须相同。");
  if (matrix.length > 15) throw new Error("交互演示的矩阵规模最多为 15 × 15。");
}

export function dijkstra(matrix: number[][], start: number): SolveResult {
  square(matrix);
  checkGraph(matrix);
  const n = matrix.length;
  if (!Number.isInteger(start) || start < 0 || start >= n) throw new Error("起点超出节点范围。");
  if (matrix.flat().some(v => v < 0)) throw new Error("Dijkstra 不适用于负权边；请使用 Floyd。 ");
  const distance = Array(n).fill(Infinity) as number[];
  const previous = Array(n).fill(-1) as number[];
  const visited = Array(n).fill(false) as boolean[];
  const steps: string[] = [];
  distance[start] = 0;
  for (let k = 0; k < n; k++) {
    let u = -1;
    for (let i = 0; i < n; i++) if (!visited[i] && (u === -1 || distance[i] < distance[u])) u = i;
    if (u === -1 || !Number.isFinite(distance[u])) break;
    visited[u] = true;
    const updates: string[] = [];
    for (let v = 0; v < n; v++) {
      if (!visited[v] && Number.isFinite(matrix[u][v]) && finiteValue(distance[u] + matrix[u][v]) < distance[v]) {
        distance[v] = distance[u] + matrix[u][v];
        previous[v] = u;
        updates.push(`${v + 1}→${fmt(distance[v])}`);
      }
    }
    steps.push(`确定节点 ${u + 1}，距离 ${fmt(distance[u])}${updates.length ? `；更新 ${updates.join("、")}` : ""}`);
  }
  const rows = distance.map((d, i) => {
    const path: number[] = [];
    let p = i;
    while (p !== -1 && path.length <= n) { path.unshift(p + 1); p = previous[p]; }
    return [String(i + 1), fmt(d), Number.isFinite(d) ? path.join(" → ") : "不可达"];
  });
  return { summary: `从节点 ${start + 1} 出发的最短路径`, metrics: [{ label: "可达节点", value: `${distance.filter(Number.isFinite).length} / ${n}` }], steps, table: { heading: "路径结果", columns: ["终点", "距离", "路径"], rows } };
}

export function floyd(matrix: number[][]): SolveResult {
  square(matrix);
  checkGraph(matrix);
  const n = matrix.length;
  const d = matrix.map((row, i) => row.map((v, j) => i === j ? Math.min(0, v) : v));
  const steps: string[] = [];
  for (let k = 0; k < n; k++) {
    let updates = 0;
    for (let i = 0; i < n; i++) for (let j = 0; j < n; j++) {
      if (Number.isFinite(d[i][k]) && Number.isFinite(d[k][j]) && finiteValue(d[i][k] + d[k][j]) < d[i][j]) {
        d[i][j] = d[i][k] + d[k][j];
        updates++;
      }
    }
    if (d.some((row, i) => row[i] < 0)) throw new Error("图中存在负权环，最短距离没有定义。 ");
    steps.push(`以节点 ${k + 1} 为中转点，改进 ${updates} 条路径。`);
  }
  return { summary: "所有节点对的最短距离", metrics: [{ label: "节点数", value: String(n) }], steps, table: { heading: "距离矩阵", columns: ["起点 \\ 终点", ...Array.from({ length: n }, (_, i) => String(i + 1))], rows: d.map((row, i) => [String(i + 1), ...row.map(fmt)]) } };
}

export function prim(matrix: number[][], start: number): SolveResult {
  square(matrix);
  checkGraph(matrix);
  const n = matrix.length;
  if (!Number.isInteger(start) || start < 0 || start >= n) throw new Error("起点超出节点范围。 ");
  for (let i = 0; i < n; i++) for (let j = 0; j < n; j++) {
    if (matrix[i][j] !== matrix[j][i]) throw new Error("Prim 需要对称的无向图邻接矩阵。 ");
  }
  const used = Array(n).fill(false) as boolean[];
  used[start] = true;
  let total = 0;
  const steps: string[] = [];
  for (let count = 1; count < n; count++) {
    let best = Infinity, from = -1, to = -1;
    for (let i = 0; i < n; i++) if (used[i]) for (let j = 0; j < n; j++) {
      if (!used[j] && matrix[i][j] < best) { best = matrix[i][j]; from = i; to = j; }
    }
    if (to === -1) throw new Error("图不连通，无法生成覆盖所有节点的最小生成树。 ");
    used[to] = true;
    total = finiteValue(total + best);
    steps.push(`${from + 1} → ${to + 1}，权重 ${fmt(best)}`);
  }
  return { summary: "最小生成树", metrics: [{ label: "总权重", value: fmt(total) }, { label: "边数", value: String(n - 1) }], steps };
}

export function assignment(cost: number[][]): SolveResult {
  square(cost);
  const n = cost.length;
  if (cost.flat().some(v => !Number.isFinite(v))) throw new Error("指派成本必须全部为有限数字。 ");
  checkValues(cost.flat());
  const size = 1 << n;
  const dp = Array(size).fill(Infinity) as number[];
  const parent = Array(size).fill(-1) as number[];
  dp[0] = 0;
  for (let mask = 0; mask < size; mask++) {
    if (!Number.isFinite(dp[mask])) continue;
    const row = mask.toString(2).replace(/0/g, "").length;
    if (row === n) continue;
    for (let col = 0; col < n; col++) if (!(mask & (1 << col))) {
      const next = mask | (1 << col);
      const value = finiteValue(dp[mask] + cost[row][col]);
      if (value < dp[next]) { dp[next] = value; parent[next] = col; }
    }
  }
  const pairs: string[] = [];
  let mask = size - 1;
  for (let row = n - 1; row >= 0; row--) {
    const col = parent[mask];
    pairs.unshift(`任务 ${row + 1} → 人员 ${col + 1}，成本 ${fmt(cost[row][col])}`);
    mask ^= 1 << col;
  }
  return { summary: "最低成本指派方案", metrics: [{ label: "总成本", value: fmt(dp[size - 1]) }], steps: pairs };
}

export function allocation(profit: number[][]): SolveResult {
  if (!profit.length || !profit[0]?.length) throw new Error("请先输入非空收益矩阵。");
  const n = profit[0].length - 1;
  if (n < 0 || n > 30 || profit.some(row => row.length !== n + 1)) throw new Error("每行需依次给出分配 0 到 N 台设备的收益（N 最多 30）。 ");
  if (profit.flat().some(v => !Number.isFinite(v))) throw new Error("收益必须为有限数字。 ");
  checkValues(profit.flat());
  const dp = Array.from({ length: profit.length + 1 }, () => Array(n + 1).fill(-Infinity) as number[]);
  const choice = Array.from({ length: profit.length + 1 }, () => Array(n + 1).fill(0) as number[]);
  dp[0][0] = 0;
  for (let i = 1; i <= profit.length; i++) for (let total = 0; total <= n; total++) for (let give = 0; give <= total; give++) {
    const value = dp[i - 1][total - give] === -Infinity ? -Infinity : finiteValue(dp[i - 1][total - give] + profit[i - 1][give]);
    if (value > dp[i][total]) { dp[i][total] = value; choice[i][total] = give; }
  }
  const steps: string[] = [];
  let remaining = n;
  for (let i = profit.length; i >= 1; i--) {
    const give = choice[i][remaining];
    steps.unshift(`项目 ${i} 分配 ${give} 台，收益 ${fmt(profit[i - 1][give])}`);
    remaining -= give;
  }
  return { summary: `分配 ${n} 台设备的最优方案`, metrics: [{ label: "最大收益", value: fmt(dp[profit.length][n]) }], steps };
}

export function simplex(c: number[], a: number[][], b: number[]): SolveResult {
  if (!c.length || c.length > 15 || a.length > 30 || a.length !== b.length || !a.length || a.some(row => row.length !== c.length)) throw new Error("目标系数、约束矩阵和右端值维度不一致（最多 15 个变量、30 条约束）。 ");
  if ([...c, ...a.flat(), ...b].some(v => !Number.isFinite(v)) || b.some(v => v < 0)) throw new Error("请输入有限数字，且右端值 b 必须非负。 ");
  checkValues([...c, ...a.flat(), ...b]);
  const m = a.length, n = c.length;
  const t = Array.from({ length: m + 1 }, () => Array(n + m + 1).fill(0) as number[]);
  for (let i = 0; i < m; i++) { for (let j = 0; j < n; j++) t[i][j] = a[i][j]; t[i][n + i] = 1; t[i][n + m] = b[i]; }
  for (let j = 0; j < n; j++) t[m][j] = -c[j];
  const basis = Array.from({ length: m }, (_, i) => n + i);
  const steps: string[] = [];
  for (let iteration = 0; iteration < 1000; iteration++) {
    let entering = -1;
    // Bland's rule prevents cycling on degenerate tableaux.
    for (let j = 0; j < n + m; j++) if (t[m][j] < 0) { entering = j; break; }
    if (entering === -1) {
      const x = Array(n).fill(0) as number[];
      basis.forEach((col, i) => { if (col < n) x[col] = t[i][n + m]; });
      return { summary: "线性规划最优解", metrics: [{ label: "最大目标值", value: fmt(t[m][n + m]) }], steps: [...steps, "所有检验数已非负，达到最优。"], table: { heading: "决策变量", columns: ["变量", "取值"], rows: x.map((v, i) => [`x${i + 1}`, fmt(v)]) } };
    }
    let leaving = -1, ratio = Infinity;
    for (let i = 0; i < m; i++) if (t[i][entering] > 0) {
      const r = t[i][n + m] / t[i][entering];
      if (r < ratio || (r === ratio && (leaving === -1 || basis[i] < basis[leaving]))) { ratio = r; leaving = i; }
    }
    if (leaving === -1) throw new Error("目标函数无界。 ");
    const pivot = t[leaving][entering];
    for (let j = 0; j <= n + m; j++) t[leaving][j] = finiteValue(t[leaving][j] / pivot);
    for (let i = 0; i <= m; i++) if (i !== leaving) {
      const factor = t[i][entering];
      for (let j = 0; j <= n + m; j++) t[i][j] = finiteValue(t[i][j] - factor * t[leaving][j]);
    }
    steps.push(`第 ${iteration + 1} 次迭代：x${entering + 1} 入基，原基变量 x${basis[leaving] + 1} 出基。`);
    basis[leaving] = entering;
  }
  throw new Error("迭代次数达到上限，请检查输入的数值稳定性。 ");
}

export function queue(lambda: number, mu: number): SolveResult {
  if (!Number.isFinite(lambda) || !Number.isFinite(mu)) throw new Error("到达率和服务率必须是有限数字。");
  if (!(lambda > 0) || !(mu > 0) || lambda >= mu) throw new Error("M/M/1 模型要求 0 < 到达率 λ < 服务率 μ。 ");
  checkValues([lambda, mu]);
  const gap = mu - lambda;
  const rho = lambda / mu;
  const l = finiteValue(lambda / gap);
  const lq = finiteValue(rho * l);
  const w = finiteValue(1 / gap);
  const wq = finiteValue(rho * w);
  return { summary: "M/M/1 排队系统稳态指标", metrics: [
    { label: "利用率 ρ", value: fmt(rho) }, { label: "系统平均人数 L", value: fmt(l) },
    { label: "队列平均人数 Lq", value: fmt(lq) },
    { label: "系统平均时间 W", value: fmt(w) },
    { label: "排队平均时间 Wq", value: fmt(wq) },
    { label: "系统为空概率 P₀", value: fmt(gap / mu) },
  ], steps: ["假设顾客到达服从泊松过程，服务时间服从指数分布，单服务台且系统容量无限。", "时间单位取决于输入的到达率和服务率单位。"] };
}

export function inventory(demand: number, setup: number, holding: number): SolveResult {
  if (![demand, setup, holding].every(Number.isFinite)) throw new Error("输入必须是有限数字。");
  if (!(demand > 0) || !(setup > 0) || !(holding > 0)) throw new Error("年需求量、每次订货成本和单位年持有成本都必须大于 0。 ");
  checkValues([demand, setup, holding]);
  const q = finiteValue(Math.sqrt(2 * demand * setup / holding));
  const orders = finiteValue(demand / q);
  const cycle = finiteValue(q / demand);
  const cost = finiteValue(demand * setup / q + holding * q / 2);
  return { summary: "基本经济订货批量（EOQ）", metrics: [
    { label: "最优订货量 Q*", value: fmt(q) }, { label: "每年订货次数", value: fmt(orders) },
    { label: "订货周期（年）", value: fmt(cycle) }, { label: "年相关成本", value: fmt(cost) },
  ], steps: ["假设需求稳定、补货瞬时到达、没有缺货或数量折扣。", "年相关成本 = 年订货成本 + 年持有成本，不含采购金额。"] };
}
