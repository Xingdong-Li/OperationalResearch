package Test;

import Floyd.Floyd;
import Model.Graph;
import Model.NegativeWeightGraph;
import org.junit.Test;

public class TestFloyd {
    public static void main(String[] args) {
        String[] points = {"A", "B", "C", "D", "E", "F", "G", "H"};
        final int N = 0;// 表示不可以连接
        double[] weights =
                {
                        N, -1, -2, 3, N, N, N, N,
                        6, N, N, N, 2, N, N, N,
                        N, -3, N, -5, N, 1, N, N,
                        8, N, N, N, N, N, 2, N,
                        N, -1, N, N, N, N, N, N,
                        N, N, N, N, 1, N, 1, 7,
                        N, N, N, -1, N, N, N, N,
                        N, N, N, N, -3, N, -5, N
                };
        Graph graph = new NegativeWeightGraph(points, weights);
        Floyd floyd = new Floyd(graph);
        floyd.start();
        floyd.show();
    }

    @Test
    public void testFloyd() {
//        String[] points = {"A", "B", "C", "D", "E"};
        String[] points = {"0", "1", "2", "3", "4"};
        final int N = 0;// 表示不可以连接
        double[] weights =
                {
                        N, 3, 8, N, -4,
                        N, N, N, 1, 7,
                        N, 4, N, N, N,
                        2, N, -5, N, N,
                        N, N, N, 6, N,
                };
        Graph graph = new NegativeWeightGraph(points, weights);
        Floyd floyd = new Floyd(graph);
        floyd.start();
        floyd.show();
    }


}
