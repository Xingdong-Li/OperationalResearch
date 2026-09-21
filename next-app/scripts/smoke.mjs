import assert from "node:assert/strict";
import fs from "node:fs";
import vm from "node:vm";
import ts from "typescript";

const source = fs.readFileSync("lib/solvers.ts", "utf8");
const compiled = ts.transpileModule(source, { compilerOptions: { module: ts.ModuleKind.CommonJS, target: ts.ScriptTarget.ES2020 } }).outputText;
const context = { exports: {} };
vm.runInNewContext(compiled, context);
const s = context.exports;

assert.equal(s.simplex([3, 5], [[1, 0], [0, 2], [3, 2]], [4, 12, 18]).metrics[0].value, "36");
assert.equal(s.assignment([[9, 2, 7], [6, 4, 3], [5, 8, 1]]).metrics[0].value, "9");
assert.equal(s.allocation([[0, 3, 5, 6], [0, 4, 5, 8], [0, 2, 5, 7]]).metrics[0].value, "9");
assert.equal(s.prim([[0, 2, Infinity, 6], [2, 0, 3, 8], [Infinity, 3, 0, 1], [6, 8, 1, 0]], 0).metrics[0].value, "6");
assert.equal(s.dijkstra([[0, 4, 1, Infinity], [Infinity, 0, Infinity, 1], [Infinity, 2, 0, 5], [Infinity, 0, Infinity, 0]], 0).table.rows[3][1], "4");
assert.equal(s.floyd([[0, 3, Infinity, 7], [Infinity, 0, 2, Infinity], [Infinity, Infinity, 0, 1], [4, Infinity, Infinity, 0]]).table.rows[0][4], "6");
assert.equal(s.queue(3, 5).metrics[1].value, "1.5");
assert.equal(s.inventory(1200, 50, 2).metrics[0].value, "244.948974");
assert.throws(() => s.dijkstra([[0, -1], [Infinity, 0]], 0), /负权/);
assert.throws(() => s.floyd([[0, -2], [-1, 0]]), /负权环/);
assert.throws(() => s.prim([[0, 1], [2, 0]], 0), /对称/);
assert.throws(() => s.queue(5, 5), /到达率/);
console.log("12 algorithm checks passed");
