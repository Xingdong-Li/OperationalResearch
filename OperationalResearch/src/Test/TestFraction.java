package Test;

import Model.Fraction;
import org.junit.Test;

public class TestFraction {
    @Test
    public void testFraction(){
        Fraction fraction = new Fraction(6, 10);
        Fraction fraction2 = new Fraction(5, 10);
        Fraction fraction3 = new Fraction(4, 10);
        Fraction fraction4 = new Fraction(2, 5);
        System.out.println(fraction);
        System.out.println(fraction2);
        System.out.println(fraction.compareTo(fraction2));
        System.out.println(fraction.subtract(fraction2));
        System.out.println(fraction.divide(fraction2));
        System.out.println(fraction.add(fraction3));
        System.out.println(fraction3.subtract(fraction4));
        System.out.println(Fraction.gcd(0, -1) + " " + Fraction.gcd(-15, 12));
        System.out.println("你好");
    }
}
