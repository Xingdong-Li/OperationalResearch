package Test;

import DesignateProblem.DesignateSolver;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Stream;

public class TestDesignateSolver {
    Random random = new Random();
    Stream<Integer> stream = Stream.generate(()->random.nextInt(100));
    @Test
    public void testRandomly() throws Exception {
        ArrayList<Integer> data = new ArrayList<>();
        stream.limit(16).forEach(data::add);
        DesignateSolver designateSolver = new DesignateSolver(data);
        designateSolver.start();
    }
    
    @Test
    public void test1() throws Exception {
        ArrayList<Integer> data = new ArrayList<>();
        data.add(4);data.add(1);data.add(8);data.add(2);
        data.add(9);data.add(8);data.add(4);data.add(7);
        data.add(8);data.add(4);data.add(6);data.add(3);
        data.add(6);data.add(5);data.add(7);data.add(2);
        DesignateSolver designateSolver = new DesignateSolver(data);
        designateSolver.start();
    }
    
    @Test
    public void test2() throws Exception {
        ArrayList<Integer> data = new ArrayList<>();
        data.add(12);data.add(7);data.add(9);data.add(7);data.add(9);
        data.add(8);data.add(9);data.add(6);data.add(6);data.add(6);
        data.add(7);data.add(17);data.add(12);data.add(14);data.add(9);
        data.add(15);data.add(14);data.add(6);data.add(6);data.add(10);
        data.add(4);data.add(10);data.add(7);data.add(10);data.add(9);
        DesignateSolver designateSolver = new DesignateSolver(data);
        designateSolver.start();
    }

    @Test
    public void test3() throws Exception {
        ArrayList<Integer> data = new ArrayList<>();
        data.add(3);data.add(8);data.add(2);data.add(10);data.add(3);
        data.add(8);data.add(7);data.add(2);data.add(9);data.add(7);
        data.add(6);data.add(4);data.add(2);data.add(7);data.add(5);
        data.add(8);data.add(4);data.add(2);data.add(3);data.add(5);
        data.add(9);data.add(10);data.add(6);data.add(9);data.add(10);
        DesignateSolver designateSolver = new DesignateSolver(data);
        designateSolver.start();
    }

    @Test
    public void test4() throws Exception {
        ArrayList<Integer> data = new ArrayList<>();
        data.add(15);data.add(18);data.add(21);data.add(24);
        data.add(19);data.add(23);data.add(22);data.add(18);
        data.add(26);data.add(17);data.add(16);data.add(19);
        data.add(19);data.add(21);data.add(23);data.add(17);
        DesignateSolver designateSolver = new DesignateSolver(data);
        designateSolver.firstStep();
        designateSolver.show();
    }

    @Test
    public void testZero() throws Exception {
        ArrayList<Integer> data = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            data.add(0);
        }
        DesignateSolver designateSolver = new DesignateSolver(data);
        designateSolver.start();
    }

    @Test
    public void testOne() throws Exception {
        ArrayList<Integer> data = new ArrayList<>();
        data.add(16);
        DesignateSolver designateSolver = new DesignateSolver(data);
        designateSolver.start();
    }

    @Test
    public void testFour() throws Exception {
        ArrayList<Integer> data = new ArrayList<>();
        data.add(16);data.add(8);data.add(12);data.add(14);
        DesignateSolver designateSolver = new DesignateSolver(data);
        designateSolver.start();
    }
    @Test
    public void testCopy() throws Exception {
        ArrayList<Integer> data = new ArrayList<>();
        stream.limit(16).forEach(data::add);
        DesignateSolver designateSolver = new DesignateSolver(data);
        designateSolver.show();
        int[][] copy = designateSolver.getCopy();
        for (int i = 1;i < copy.length; i++) {
            Arrays.fill(copy[i], 0);
        }
        designateSolver.show();
    }
}
