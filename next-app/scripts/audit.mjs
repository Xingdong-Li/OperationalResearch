import assert from "node:assert/strict";
import fs from "node:fs";
import vm from "node:vm";
import ts from "typescript";

const source = fs.readFileSync("lib/solvers.ts", "utf8");
const compiled = ts.transpileModule(source, { compilerOptions: { module: ts.ModuleKind.CommonJS, target: ts.ScriptTarget.ES2020 } }).outputText;
const context = { exports: {} };
vm.runInNewContext(compiled, context);
const s = context.exports;
let seed = 20260920;
function random(max) { seed ^= seed << 13; seed ^= seed >>> 17; seed ^= seed << 5; return (seed >>> 0) % max; }
function near(actual, expected) { assert.ok(Math.abs(actual - expected) < 1e-5, `${actual} != ${expected}`); }
function metric(result) { return Number(result.metrics[0].value); }

function assignmentReference(cost, row = 0, used = 0) {
  if (row === cost.length) return 0;
  let best = Infinity;
  for (let col = 0; col < cost.length; col++) if (!(used & (1 << col)))
    best = Math.min(best, cost[row][col] + assignmentReference(cost, row + 1, used | (1 << col)));
  return best;
}

function allocationReference(profit, remaining, row = 0) {
  if (row === profit.length) return remaining === 0 ? 0 : -Infinity;
  let best = -Infinity;
  for (let give = 0; give <= remaining; give++)
    best = Math.max(best, profit[row][give] + allocationReference(profit, remaining - give, row + 1));
  return best;
}

function shortestReference(graph, start) {
  const dist = Array(graph.length).fill(Infinity);
  dist[start] = 0;
  for (let round = 1; round < graph.length; round++) for (let u = 0; u < graph.length; u++) for (let v = 0; v < graph.length; v++)
    dist[v] = Math.min(dist[v], dist[u] + graph[u][v]);
  return dist;
}

function mstReference(graph) {
  const edges = [];
  for (let i = 0; i < graph.length; i++) for (let j = i + 1; j < graph.length; j++) if (Number.isFinite(graph[i][j])) edges.push([graph[i][j], i, j]);
  edges.sort((a, b) => a[0] - b[0]);
  const parent = Array.from({ length: graph.length }, (_, i) => i);
  const root = i => parent[i] === i ? i : (parent[i] = root(parent[i]));
  let total = 0, count = 0;
  for (const [weight, i, j] of edges) if (root(i) !== root(j)) { parent[root(i)] = root(j); total += weight; count++; }
  assert.equal(count, graph.length - 1);
  return total;
}

function boundedLpReference(c, a, b) {
  const candidates = [[0, 0]];
  const lines = [...a, [1, 0], [0, 1]];
  const rhs = [...b, 0, 0];
  for (let i = 0; i < lines.length; i++) for (let j = i + 1; j < lines.length; j++) {
    const determinant = lines[i][0] * lines[j][1] - lines[i][1] * lines[j][0];
    if (Math.abs(determinant) < 1e-9) continue;
    const x = (rhs[i] * lines[j][1] - lines[i][1] * rhs[j]) / determinant;
    const y = (lines[i][0] * rhs[j] - rhs[i] * lines[j][0]) / determinant;
    if (x >= -1e-8 && y >= -1e-8 && a.every((row, k) => row[0] * x + row[1] * y <= b[k] + 1e-8)) candidates.push([x, y]);
  }
  return Math.max(...candidates.map(([x, y]) => c[0] * x + c[1] * y));
}

assert.equal(metric(s.assignment([[8, 4, 4], [2, 0, 1], [3, 1, 7]])), 7);
near(Number(s.dijkstra([[0, 3e9], [Infinity, 0]], 0).table.rows[1][1]), 3e9);
near(metric(s.prim([[0, 3e9], [3e9, 0]], 0)), 3e9);
assert.throws(() => s.dijkstra([[0, 2, 1], [Infinity, 0, -3], [Infinity, Infinity, 0]], 0), /负权/);
assert.throws(() => s.floyd([[-2]]), /负权环/);
assert.throws(() => s.simplex([1], [[1]], [-1]), /非负/);
near(metric(s.allocation([[0, -5]])), -5);
near(metric(s.allocation([[2], [3]])), 5);
assert.equal(metric(s.prim([[0]], 0)), 0);
assert.equal(metric(s.assignment([[7]])), 7);
assert.throws(() => s.simplex([1], [[-1]], [1]), /无界/);

for (let n = 2; n <= 6; n++) for (let trial = 0; trial < 100; trial++) {
  const costs = Array.from({ length: n }, () => Array.from({ length: n }, () => random(21) - 10));
  near(metric(s.assignment(costs)), assignmentReference(costs));
}

for (let trial = 0; trial < 300; trial++) {
  const n = 1 + random(7), projects = 1 + random(5);
  const profits = Array.from({ length: projects }, () => Array.from({ length: n + 1 }, () => random(21) - 10));
  near(metric(s.allocation(profits)), allocationReference(profits, n));
}

for (let trial = 0; trial < 300; trial++) {
  const n = 2 + random(6);
  const graph = Array.from({ length: n }, (_, i) => Array.from({ length: n }, (_, j) => i === j ? 0 : random(4) ? random(20) : Infinity));
  const expected = shortestReference(graph, 0);
  const rows = s.dijkstra(graph, 0).table.rows;
  expected.forEach((d, i) => assert.equal(rows[i][1], Number.isFinite(d) ? String(d) : "∞"));
}

for (let trial = 0; trial < 200; trial++) {
  const n = 2 + random(5);
  const graph = Array.from({ length: n }, (_, i) => Array.from({ length: n }, (_, j) => i === j ? 0 : Infinity));
  for (let i = 0; i < n; i++) for (let j = i + 1; j < n; j++) if (random(3)) graph[i][j] = random(15) - 5;
  const rows = s.floyd(graph).table.rows;
  for (let i = 0; i < n; i++) shortestReference(graph, i).forEach((d, j) => assert.equal(rows[i][j + 1], Number.isFinite(d) ? String(d) : "∞"));
}

for (let trial = 0; trial < 200; trial++) {
  const n = 2 + random(7);
  const graph = Array.from({ length: n }, (_, i) => Array.from({ length: n }, (_, j) => i === j ? 0 : Infinity));
  for (let i = 1; i < n; i++) { const weight = random(31) - 10; graph[i - 1][i] = weight; graph[i][i - 1] = weight; }
  for (let i = 0; i < n; i++) for (let j = i + 2; j < n; j++) if (random(3)) graph[i][j] = graph[j][i] = random(31) - 10;
  near(metric(s.prim(graph, 0)), mstReference(graph));
}

for (let trial = 0; trial < 300; trial++) {
  const c = [random(21) - 10, random(21) - 10];
  const a = [[1, 0], [0, 1], [random(5), random(5)]];
  const b = [1 + random(10), 1 + random(10), 1 + random(20)];
  near(metric(s.simplex(c, a, b)), boundedLpReference(c, a, b));
}

for (let trial = 0; trial < 200; trial++) {
  const c = [(random(25) - 12) / 4, (random(25) - 12) / 4];
  const a = [[1, 0], [0, 1], [(random(17) - 8) / 4, (random(17) - 8) / 4]];
  const b = [1 + random(10) / 2, 1 + random(10) / 2, random(20) / 2];
  near(metric(s.simplex(c, a, b)), boundedLpReference(c, a, b));
}

// A degenerate linear program that cycles under Dantzig's entering-variable rule.
near(metric(s.simplex([10, -57, -9, -24], [[0.5, -5.5, -2.5, 9], [0.5, -1.5, -0.5, 1], [1, 0, 0, 0]], [0, 0, 1])), 1);
near(metric(s.simplex([1], [[1e-10]], [1])), 1e10);
near(metric(s.simplex([1e-10], [[1]], [1])), 1e-10);

assert.throws(() => s.queue(1, Infinity), /有限/);
assert.throws(() => s.inventory(Infinity, 2, 3), /有限/);
assert.throws(() => s.assignment([[Number.MAX_VALUE, Number.MAX_VALUE], [Number.MAX_VALUE, Number.MAX_VALUE]]), /范围/);
assert.throws(() => s.dijkstra([[0, NaN], [Infinity, 0]], 0), /数字/);
for (let i = 0; i < 100; i++) {
  const lambda = (random(100) + 1) / 10, mu = lambda + (random(100) + 1) / 10;
  const result = s.queue(lambda, mu);
  near(Number(result.metrics[1].value), lambda * Number(result.metrics[3].value));
  near(Number(result.metrics[2].value), lambda * Number(result.metrics[4].value));
}
console.log("Algorithm audit passed: 500 assignments, 300 allocations, 300 Dijkstra graphs, 200 Floyd graphs, 200 MSTs, 500 bounded LPs, 100 queue invariants and boundary cases.");
