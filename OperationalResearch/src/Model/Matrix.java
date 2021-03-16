package Model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author  Li Xingdong
 * @since 2020.11.5
 */
public final class Matrix implements Serializable {
    public static final int OPERATION_ADD = 1;
    public static final int OPERATION_SUB = 2;
    public static final int OPERATION_MUL = 3;
    private final Fraction[][] data;
    public final int row;
    public final int column;

    public Matrix(List<Fraction> data, int row, int column) {
        if (data == null || data.size() == 0 || data.size() != row * column)
            throw new IllegalArgumentException("Wrong arguments!");
        this.data = new Fraction[row][column];
        this.row = row;
        this.column = column;
        int k = 0;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                Fraction fraction = data.get(k++);
                if (fraction == null) throw new NullPointerException("Datum cannot be null!");
                this.data[i][j] = fraction;
            }
        }
    }

    private Matrix(int row, int column) {
        if (row == 0 || column == 0) throw new IllegalArgumentException("Wrong arguments!");
        this.row = row;
        this.column = column;
        data = new Fraction[row][column];
    }

    /**
     * 矩阵加法运算
     *
     * @param matrix 要相加的矩阵
     * @return 新的矩阵
     */
    public Matrix add(Matrix matrix) {
        if (illegalOperation(this, matrix, OPERATION_ADD))
            throw new IllegalArgumentException("This two matrices cannot do addition!");
        Matrix newMatrix = new Matrix(row, column);
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                newMatrix.data[i][j] = this.data[i][j].add(matrix.data[i][j]);
            }
        }
        return newMatrix;
    }

    /**
     * 矩阵减法运算
     *
     * @param matrix 要减去的矩阵
     * @return 新的矩阵
     */
    public Matrix subtract(Matrix matrix) {
        if (illegalOperation(this, matrix, OPERATION_SUB))
            throw new IllegalArgumentException("This two matrices cannot do subtraction!");
        Matrix newMatrix = new Matrix(row, column);
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                newMatrix.data[i][j] = this.data[i][j].subtract(matrix.data[i][j]);
            }
        }
        return newMatrix;
    }

    /**
     * 矩阵乘法运算matrix 矩阵与矩阵相乘
     *
     * @param matrix 矩阵matrix
     * @return 新的矩阵
     */
    public Matrix multiply(Matrix matrix) {
        if (illegalOperation(this, matrix, OPERATION_MUL))
            throw new IllegalArgumentException("This two matrices cannot do multiplication!");
        Matrix newMatrix = new Matrix(row, matrix.column);
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < matrix.column; j++) {
                newMatrix.data[i][j] = calculateSingleResult(i, matrix, j);
            }
        }
        return newMatrix;
    }

    /**
     * 矩阵乘法运算b 矩阵的数乘
     *
     * @param fraction 数
     * @return 新的矩阵
     */
    public Matrix multiply(Fraction fraction) {
        Matrix matrix = new Matrix(row, column);
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                matrix.data[i][j] = data[i][j].multiply(fraction);
            }
        }
        return matrix;
    }

    public Matrix multiply(int value) {
        return multiply(new Fraction(value));
    }

    public Matrix T() {
        Matrix newMatrix = new Matrix(column, row);
        for (int i = 0; i < column; i++) {
            for (int j = 0; j < row; j++) {
                newMatrix.data[i][j] = this.data[j][i];
            }
        }
        return newMatrix;
    }

    /**
     * 矩阵乘法a中result每个元素的单一运算
     *
     * @param matrix     矩阵
     * @param thisRow    这个矩阵参与单一运算的行标
     * @param thatColumn 这个矩阵参与单一运算的列标
     * @return 运算结果
     */
    public Fraction calculateSingleResult(int thisRow, Matrix matrix, int thatColumn) {
        Fraction result = new Fraction();
        for (int i = 0; i < column; i++) {
            result = result.add(this.data[thisRow][i].multiply(matrix.data[i][thatColumn]));
        }
        return result;
    }

    /**
     * 判断矩阵是否可以进行合法运算
     *
     * @param m1   矩阵a
     * @param m2   矩阵b
     * @param type 判断运算类型，是加法，减法，还是乘法运算
     * @return legal true 运算合法; false 运算不合法
     */
    public static boolean illegalOperation(Matrix m1, Matrix m2, int type) {
        boolean illegal = false;
        if (type == OPERATION_ADD || type == OPERATION_SUB) {//加减的阶数需要一样
            if (m1.row != m2.row || m1.column != m2.column) {
                illegal = true;
            }
        } else if (type == OPERATION_MUL) {//2*3 X 3*2
            if (m1.column != m2.row) {
                illegal = true;
            }
        }
        return illegal;
    }

    /**
     * 改变矩阵元素的值
     *
     * @param row    行号
     * @param column 列号
     * @param value  值
     */
    public void set(int row, int column, Fraction value) {
        checkRowIndex(row);
        checkColumnIndex(column);
        if (value == null) throw new NullPointerException("Datum cannot be null!");
        row -= 1;
        column -= 1;
        data[row][column] = value;
    }

    public void set(int row, int column, int value) {
        set(row, column, new Fraction(value));
    }

    public void equalSet(int row, int column, Fraction value) {
        checkRowIndex(row);
        checkColumnIndex(column);
        row -= 1;
        column -= 1;
        Fraction that = data[row][column];
        if (that.compareTo(value)==0){
            return;
        }
        
        if (!that.isZero()) {
            if (value.isZero()){
                int rowIndex = -1;
                for (int i = 0; i < this.row; i++) {
                    if (i!=row){
                        if (!data[i][column].isZero()) {
                            rowIndex = i;
                            break;
                        }
                    }
                }
                if (rowIndex == -1)
                    throw new IllegalArgumentException("This matrix cannot be equal set to " + value + " in (" + row + ", " + column + ")");
                Fraction[] minus = obtainCopyRow(rowIndex, data[rowIndex][column]);
                Fraction copy = data[row][column].copy();
                for (int i = 0; i < this.column; i++) {
                    minus[i] = minus[i].multiply(copy);
                }
                for (int i = 0; i < this.column; i++) {
                    data[row][i] = data[row][i].subtract(minus[i]);
                }
            }else {//都不为0
                Fraction proportion = that.divide(value);
                scaleRow(row,proportion);
            }
        } else {//原值为0
            int rowIndex = -1;
            for (int i = 0; i < this.row; i++) {
                if (!data[i][column].isZero()) {
                    rowIndex = i;
                    break;
                }
            }
            if (rowIndex == -1)
                throw new IllegalArgumentException("This matrix cannot be equal set to " + value + " in (" + row + ", " + column + ")");
            Fraction copy = data[rowIndex][column].copy();
            Fraction[] fractions = obtainCopyRow(rowIndex, copy);
            Fraction[] plus = new Fraction[this.column];
            for (int i = 0; i < this.column; i++) {
                plus[i] = fractions[i].multiply(value);
            }
            for (int i = 0; i < this.column; i++) {
                data[row][i] = data[row][i].add(plus[i]);
            }
        }
    }

    public void equalSet(int row, int column, int value) {
        equalSet(row, column, new Fraction(value));
    }

    public void scale(Fraction size, boolean expand) {
        size = expand ? size : size.reciprocalInverse();
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                data[i][j] = data[i][j].multiply(size);
            }
        }
    }
    
    private void scaleRow(int row, Fraction size, boolean expand){
        size = expand ? size : size.reciprocalInverse();
        for (int i = 0; i < data[row].length; i++) {
            data[row][i] = data[row][i].multiply(size);
        }
    }

    private void scaleRow(int row, Fraction size){
        scaleRow(row,size,false);
    }

    private void scaleRow(int row, int size, boolean expand){
        scaleRow(row,new Fraction(size),expand);
    }
    
    private Fraction[] obtainCopyRow(int row, Fraction size, boolean expand){
        size = expand ? size : size.reciprocalInverse();
        Fraction[] fractions = new Fraction[this.column];
        for (int i=0;i<this.column;i++){
            fractions[i] = data[row][i].copy().multiply(size);
        }
        return fractions;
    }
    
    public Matrix copy(){
        List<Fraction> copy = new ArrayList<>(row*column);
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                copy.add(data[i][j].copy());
            }
        }
        return new Matrix(copy,row,column);
    }

    public Fraction[][] getData() {
        return data;
    }

    private Fraction[] obtainCopyRow(int row, int size){
        return obtainCopyRow(row,new Fraction(size),false);
    }

    private Fraction[] obtainCopyRow(int row, Fraction size){
        return obtainCopyRow(row,size,false);
    }

    private void scaleRow(int row, int size){
        scaleRow(row,new Fraction(size));
    }
    public void scale(Fraction size) {
        scale(size, false);
    }

    public void scale(int size, boolean expand) {
        scale(new Fraction(size), expand);
    }

    public void scale(int size) {
        scale(new Fraction(size));
    }


    /**
     * 对矩阵某一行设置值
     * 若开启严格模式，则必须满足条件：被设置行的长度=rowList长度
     *
     * @param row     行标
     * @param rowList 数值列表
     * @param strict  是否开启严格模式
     */
    public void setRow(int row, List<Fraction> rowList, boolean strict) {
        checkRowIndex(row);
        if (strict) {
            if (column != rowList.size())
                throw new IllegalArgumentException("The rowList size and column size are different!");
        }
        int min = Math.min(column, rowList.size());
        row -= 1;
        for (int i = 0; i < min; i++) {
            data[row][i] = rowList.get(i);
        }
    }

    /**
     * 对矩阵某一行设置值， 严格模式为false
     *
     * @param row     行标
     * @param rowList 数值列表
     */
    public void setRow(int row, List<Fraction> rowList) {
        setRow(row, rowList, false);
    }

    /**
     * 对矩阵某一行设置值
     * 若开启严格模式，则必须满足条件：被设置行的长度=rowList长度
     *
     * @param column     行标
     * @param columnList 数值列表
     * @param strict     是否开启严格模式
     */
    public void setColumn(int column, List<Fraction> columnList, boolean strict) {
        checkColumnIndex(column);
        if (strict) {
            if (this.row != columnList.size())
                throw new IllegalArgumentException("The columnList size and column size are different!");
        }
        int min = Math.min(this.row, columnList.size());
        column -= 1;
        for (int i = 0; i < min; i++) {
            data[i][column] = columnList.get(i);
        }
    }

    /**
     * 对矩阵某一行设置值， 严格模式为false
     *
     * @param column     行标
     * @param columnList 数值列表
     */
    public void setColumn(int column, List<Fraction> columnList) {
        setColumn(column, columnList, false);
    }

    /**
     * Switch two rows
     *
     * @param index1 a row
     * @param index2 another row
     */
    public void switchRow(int index1, int index2) {
        checkRowIndex(index1, index2);
        index1 -= 1;
        index2 -= 1;
        Fraction[] temp = data[index1];
        data[index1] = data[index2];
        data[index2] = temp;
    }

    /**
     * Switch two columns
     *
     * @param index1 a column
     * @param index2 another column
     */
    public void switchColumn(int index1, int index2) {
        checkColumnIndex(index1, index2);
        index1 -= 1;
        index2 -= 1;
        for (int i = 0; i < row; i++) {
            Fraction temp = data[i][index1];
            data[i][index1] = data[i][index2];
            data[i][index2] = temp;
        }
    }

    public Fraction get(int index1, int index2) {
        checkRowIndex(index1);
        checkColumnIndex(index2);
        index1 -= 1;
        index2 -= 1;
        return data[index1][index2];
    }

    private void checkRowIndex(int... rows) {
        for (int row : rows) {
            if (row - 1 < 0) throw new IllegalArgumentException("Index starts from 1!");
            if (row > this.row) throw new IllegalArgumentException("Row index out of bound, which is " + this.row);
        }
    }

    private void checkColumnIndex(int... columns) {
        for (int column : columns) {
            if (column - 1 < 0) throw new IllegalArgumentException("Index starts from 1!");
            if (column > this.column)
                throw new IllegalArgumentException("Row index out of bound, which is " + this.column);
        }
    }

    @Override
    public String toString() {
        StringBuilder matrix = new StringBuilder("Matrix{\n");
        for (Fraction[] datum : data) {
            matrix.append("\t").append(Arrays.toString(datum)).append("\n");
        }
        matrix.append('}');
        return matrix.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Matrix matrix = (Matrix) o;
        if (row != matrix.row || column != matrix.column) return false;
        for (int i = 0; i < data.length; i++) {
            if (!Arrays.equals(data[i], matrix.data[i])) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(row, column);
        int dataHashCodes = 0;
        for (Fraction[] datum : data) {
            dataHashCodes += Arrays.hashCode(datum);
        }
        result = 31 * result + dataHashCodes;
        return result;
    }
}
