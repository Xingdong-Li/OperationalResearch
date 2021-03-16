package Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/**
 * @author  Li Xingdong, Qi HangJiang
 * @since 2020.11.4
 */
public final class Fraction implements Comparable<Fraction>, Serializable {
    private final int numerator;
    private final int denominator;

    public Fraction() {
        numerator = 0;
        denominator = 1;
    }

    public Fraction(int value) {
        numerator = value;
        denominator = 1;
    }

    public Fraction(int numerator, int denominator) {
        if (denominator == 0) throw new IllegalArgumentException("Denominator cannot be 0!");
        int pub = gcd(numerator, denominator);
        numerator = numerator / pub;
        denominator = denominator / pub;
        if (denominator < 0) {
            denominator = -denominator;
            numerator = -numerator;
        }
        this.numerator=numerator;
        this.denominator=denominator;
    }

    boolean isInt() {
        return numerator % denominator == 0;
    }

    public Fraction multiply(Fraction num) {
        return new Fraction(numerator * num.numerator, denominator * num.denominator);
    }

    public Fraction divide(Fraction num) {
        return new Fraction(numerator * num.denominator, denominator * num.numerator);
    }

    public Fraction add(Fraction num) {
        if (denominator == num.denominator)
            return new Fraction(numerator + num.numerator, denominator);
        else {
            return new Fraction(numerator * num.denominator + denominator * num.numerator, denominator * num.denominator);
        }
    }

    public Fraction subtract(Fraction num) {
        return new Fraction(numerator * num.denominator - denominator * num.numerator, denominator * num.denominator);
    }
    
    public Fraction copy(){
        return new Fraction(numerator,denominator);
    }

    public Fraction reciprocalInverse(){
        if (numerator==0){
            return new Fraction(numerator,denominator);
        }
        return new Fraction(denominator,numerator);
    }

    public double toDouble() {
        return (double) numerator / denominator;
    }
    
    public boolean isPositive(){
        return numerator>0;
    }
    
    public boolean isZero(){
        return numerator==0;
    }
    
    

    public String toString() {
        if (denominator==1){
            return String.valueOf(numerator);
        }
        return numerator + "/" + denominator;
    }

    @Override
    public int compareTo(Fraction num) {
        double res = this.toDouble()-num.toDouble();
        if (res < 0) return -1;
        else if (res > 0) return 1;
        else return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fraction fraction = (Fraction) o;
        return numerator == fraction.numerator &&
                denominator == fraction.denominator;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numerator, denominator);
    }

    public static int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    public static Fraction[] fractionalizeAllAsArray(int[] array) {
        Fraction[] fractions = new Fraction[array.length];
        for (int i = 0; i < array.length; i++) {
            fractions[i] = new Fraction(array[i]);
        }
        return fractions;
    }

    public static List<Fraction> fractionalizeAllAsList(int[] array) {
        List<Fraction> fractionList = new ArrayList<>();
        for (int value : array) {
            fractionList.add(new Fraction(value));
        }
        return fractionList;
    }

    public int getNumerator() {
        return numerator;
    }

    public int getDenominator() {
        return denominator;
    }
}
