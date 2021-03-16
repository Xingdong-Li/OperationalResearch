package Test;

import Model.Graph;
import Prim.Prim;
import org.junit.Test;

import java.util.Arrays;

public class TestPrim {
    @Test
    public void testPrim() {
        String[] points = {"A", "B", "C", "D", "E", "F", "G"};
        double[] weights =
                {
                        -1, 5, 7, -1, -1, -1, 2,
                        5, -1, -1, 9, -1, -1, 3,
                        7, -1, -1, -1, 8, -1, -1,
                        -1, 9, -1, -1, -1, 4, -1,
                        -1, -1, 8, -1, -1, 5, 4,
                        -1, -1, -1, 4, 5, -1, 6,
                        2, 3, -1, -1, 4, 6, -1
                };
        Graph graph = new Graph(points, weights);
        Prim prim = new Prim(graph);
        prim.prim(0);
    }

    @Test
    public void testPrim2() {
        String[] points = {"A", "B", "C", "D", "E", "F"};
        double[] weights =
                {
                        -1, 10, 16, 11, 10, 17,
                        10, -1, 9.5, -1, -1, 19.5,
                        16, 9.5, -1, 7, -1, 12,
                        11, -1, 7, -1, 8, 7,
                        10, -1, -1, 8, -1, 9,
                        17, 19.5, 12, 7, 9, -1
                };
        Graph graph = new Graph(points, weights);
        Prim prim = new Prim(graph);
        prim.prim(0);
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(new String[]{"a","b","c"}));
    }
}
