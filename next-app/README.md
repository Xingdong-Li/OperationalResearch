# 运筹学实验室 · Next.js 重写版

这是 [OperationalResearch 原项目](https://github.com/Xingdong-Li/OperationalResearch) 的独立新版。原项目使用 HTML、jQuery、Bootstrap 和 Java Servlet；新版使用 React 19、Next.js 16 App Router、TypeScript，算法直接在浏览器运行，无需 Java 容器或数据库。

## 运行

需要 Node.js 20.9+。在本目录执行：

```bash
npm ci
npm run dev
```

打开 `http://localhost:3000`。生产构建与静态导出：

```bash
npm run build
```

输出位于 `out/`，可部署至任意静态站点托管服务。

若要在本机运行构建后的静态页面，执行 `npm run start`（默认端口 3000，可用 `PORT` 环境变量修改）。

## 模块

| 原项目 | 新版实现 | 输入约束 |
| --- | --- | --- |
| 单纯形法 | 标准最大化线性规划，显示枢轴迭代 | `Ax ≤ b`、`x ≥ 0`、`b ≥ 0` |
| 指派问题 | 状态压缩动态规划，求最小总成本 | 方阵，最多 15 项 |
| 动态规划 | 多项目设备分配，求最大收益 | 每行给出分配 `0..N` 台的收益 |
| Prim | 最小生成树和选边过程 | 对称、连通图；允许负权边 |
| Dijkstra | 单源最短路径及路径重建 | 非负边权 |
| Floyd | 所有节点对最短距离，检测负权环 | 最多 15 个节点 |
| 排队论 | M/M/1 稳态指标计算 | `0 < λ < μ` |
| 存储论 | 基本 EOQ 模型计算 | 正数、稳定需求、瞬时补货 |

矩阵按行输入，列用逗号或空格分隔；图中无边可填 `x`。示例数据已预填在每个模块中。

## 项目结构

- `app/page.tsx`：页面与交互
- `app/globals.css`：响应式样式
- `lib/solvers.ts`：纯函数算法实现
- `scripts/smoke.mjs`：代表性样例及边界条件检查
- `scripts/audit.mjs`：与独立求解方法对拍的确定性随机测试
- `scripts/serve.mjs`：本地静态页面服务器

## 验证

```bash
npm test
npm run lint
npm run build
```

新版保留了原站八个主题的入口，交互模型与展示方式经过重新设计。尤其是排队论和存储论，原站以公式查询为主，这里增加了 M/M/1 与基本 EOQ 的计算器；它们不覆盖原站的全部公式分类。单纯形法当前支持标准的 `≤` 约束；如需支持等式、`≥`、负右端值或整数规划，可在 `lib/solvers.ts` 上继续扩展两阶段法或接入专业求解器。

计算使用 JavaScript 数值。输入或中间结果超过 `Number.MAX_SAFE_INTEGER` 的量级会拒绝计算，以避免把数值溢出误显示成正确结果。单纯形法使用浮点表和 Bland 选轴规则，适合交互教学；高精度或大规模求解仍建议使用专业优化库。
