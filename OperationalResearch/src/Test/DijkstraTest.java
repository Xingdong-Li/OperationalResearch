package Test;

import Dijkstra.Dijkstra;
import Model.Graph;

public class DijkstraTest {
    public static void main(String[] args) {
        String[] vertex = {"A", "B", "C", "D", "E", "F", "G"};
        //邻接矩阵
        final int N = -1;// 表示不可以连接
        double[] weights = {
                N, 5, 7, N, N, N, 2,
                5, N, N, 9, N, N, 3,
                7, N, N, N, 8, N, N,
                N, 9, N, N, N, 4, N,
                N, N, 8, N, N, 5, 4,
                N, N, N, 4, 5, N, 6,
                2, 3, N, N, 4, 6, N};
        //创建 Graph对象
        Graph graph = new Graph(vertex, weights);
        //测试迪杰斯特拉算法
        Dijkstra dijkstra = new Dijkstra(graph,2);

        dijkstra.start();//C
    }
}
