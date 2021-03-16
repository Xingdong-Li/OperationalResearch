package Test;

import Model.Fraction;
import Model.Matrix;
import SimplexMethod.SimplexMethodSolver;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class TestMatrix {
    Random random = new Random();
    Stream<Integer> stream = Stream.generate(()->random.nextInt(10));
    @Test
    public void testMatrix(){
        ArrayList<Fraction> data = new ArrayList<>();
        stream.limit(16).forEach(e->data.add(new Fraction(e)));
        int size = (int) Math.sqrt(data.size());
        Matrix matrix = new Matrix(data,size,size);
        System.out.println(data);
//        matrix.set(2,2,0);
        System.out.println(matrix);
//        System.out.println(matrix.add(matrix));
//        System.out.println(matrix.subtract(matrix));
//        System.out.println(matrix.multiply(matrix));
//        System.out.println(matrix.multiply(2));
//        System.out.println(matrix.calculateSingleResult(2, matrix, 2));
        matrix.equalSet(1,2,1);
        matrix.equalSet(2,2,0);
        matrix.equalSet(3,2,0);
        matrix.equalSet(4,2,0);
        matrix.equalSet(1,2,0);
        
        System.out.println(matrix);
    }
    
    @Test
    public void testSetAndSwitch(){
        List<Fraction> fractionList = new ArrayList<>();
        fractionList.add(new Fraction(1,5));
        fractionList.add(new Fraction(2,7));
        fractionList.add(new Fraction(4,11));
        fractionList.add(new Fraction(9,2));
        fractionList.add(new Fraction(8,-3));
        fractionList.add(new Fraction(-5,2));
        Matrix matrix1 = new Matrix(fractionList,2,3);
        Matrix matrix2 = new Matrix(fractionList,3,2);
        matrix1.set(1,2,0);
        System.out.println(matrix1);
//        System.out.println(matrix2);
        matrix1.equalSet(1,2,1);
        System.out.println(matrix1);
    }
    
    @Test
    public void testOperation(){
        List<Fraction> fractionList = new ArrayList<>();
        fractionList.add(new Fraction(1));
        fractionList.add(new Fraction(1));
        fractionList.add(new Fraction(1));
        fractionList.add(new Fraction(1));
        fractionList.add(new Fraction(1));
        fractionList.add(new Fraction(1));
        Matrix matrix1 = new Matrix(fractionList,2,3);
        Matrix matrix2 = new Matrix(fractionList,3,2);
        matrix1.set(1,2,0);
        matrix2.set(2,1,0);
        System.out.println(matrix1);
        System.out.println(matrix2);
        matrix1.equalSet(1,2,5);
        matrix2.equalSet(2,1,5);
        System.out.println(matrix1);
        System.out.println(matrix2);
    }
    
    @Test
    public void testSimplexMethod(){
        ArrayList<Fraction> fractions = new ArrayList<>();
        fractions.add(new Fraction(2));fractions.add(new Fraction(2));
        fractions.add(new Fraction(-1));fractions.add(new Fraction(2));
        fractions.add(new Fraction(4));fractions.add(new Fraction(1));
        fractions.add(new Fraction(0));fractions.add(new Fraction(4));
        ArrayList<Fraction> Cb = new ArrayList<>();
        Cb.add(new Fraction(6));Cb.add(new Fraction(-2));Cb.add(new Fraction(1));
        SimplexMethodSolver solver = new SimplexMethodSolver(3,fractions,Cb);
        System.out.println(solver.getMatrix());
        solver.start();
    }

    @Test
    public void testSimplexMethod2(){
        ArrayList<Fraction> fractions = new ArrayList<>();
        fractions.add(new Fraction(8));fractions.add(new Fraction(1));
        fractions.add(new Fraction(2));fractions.add(new Fraction(16));
        fractions.add(new Fraction(4));fractions.add(new Fraction(0));
        fractions.add(new Fraction(12));fractions.add(new Fraction(0));
        fractions.add(new Fraction(4));
        ArrayList<Fraction> Cb = new ArrayList<>();
        Cb.add(new Fraction(2));Cb.add(new Fraction(3));
        SimplexMethodSolver solver = new SimplexMethodSolver(2,fractions,Cb);
        Matrix matrix = solver.getMatrix();
        solver.start();
    }

    /*
     * 4 7
     * 60 20 40 30
     * 600 4 2 1 2
     * 1000 6 2 1 2
     * 400 2 1 3 2
     * 100 1 0 0 0
     * 200 0 1 0 0
     * 50 0 0 1 0
     * 100 0 0 0 1
     */

    @Test
    public void testSimplexMethod3(){
        ArrayList<Fraction> fractions = new ArrayList<>();
        fractions.add(new Fraction(600));
        fractions.add(new Fraction(4));fractions.add(new Fraction(2));
        fractions.add(new Fraction(1));fractions.add(new Fraction(2));

        fractions.add(new Fraction(1000));
        fractions.add(new Fraction(6));fractions.add(new Fraction(2));
        fractions.add(new Fraction(1));fractions.add(new Fraction(2));

        fractions.add(new Fraction(400));
        fractions.add(new Fraction(2));fractions.add(new Fraction(1));
        fractions.add(new Fraction(3));fractions.add(new Fraction(2));

        fractions.add(new Fraction(100));
        fractions.add(new Fraction(1));fractions.add(new Fraction(0));
        fractions.add(new Fraction(0));fractions.add(new Fraction(0));

        fractions.add(new Fraction(200));
        fractions.add(new Fraction(0));fractions.add(new Fraction(1));
        fractions.add(new Fraction(0));fractions.add(new Fraction(0));

        fractions.add(new Fraction(50));
        fractions.add(new Fraction(0));fractions.add(new Fraction(0));
        fractions.add(new Fraction(1));fractions.add(new Fraction(0));

        fractions.add(new Fraction(100));
        fractions.add(new Fraction(0));fractions.add(new Fraction(0));
        fractions.add(new Fraction(0));fractions.add(new Fraction(1));
        ArrayList<Fraction> Cb = new ArrayList<>();
        Cb.add(new Fraction(60));Cb.add(new Fraction(20));
        Cb.add(new Fraction(40));Cb.add(new Fraction(30));
        SimplexMethodSolver solver = new SimplexMethodSolver(4,fractions,Cb);
        solver.start();
    }
    
    @Test
    public void insert(){
        List<Integer> list = new ArrayList<>();
        list.add(1);list.add(2);list.add(3);list.add(4);list.add(5);list.add(6);
        list.remove(1);
        System.out.println(list);
        list.add(1,2);
        System.out.println(list);
    }
}
