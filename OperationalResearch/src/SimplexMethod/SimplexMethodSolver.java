package SimplexMethod;


import Model.Fraction;
import Model.Matrix;

import java.util.*;
/**
 * @author  Li Xingdong
 * @since 2020.11.6
 */
public class SimplexMethodSolver {
    private int variableNum;
    private int constraintNum;
    private Map<Integer, Fraction> checkNum;
    private Map<Integer, Fraction> tempCheck;
    private Map<Integer, Integer> Xb;
    private List<Matrix> matrixList;
    private List<Object[]> tempChecks;
    private List<Fraction> thetas;
    private List<Integer> indexes;//记录换出和换入变量的序号
    private List<Integer> Xbs;
    private List<Fraction> objs;
    private Matrix matrix;
    private Integer resultType = -1;//默认有最优解


    /**
     * max方程变量数目
     *
     * @param variableNum the number of variable
     * @param parameters  b + X1, X2, .etc
     * @param Cb          Cb
     */
    public SimplexMethodSolver(int variableNum, List<Fraction> parameters, List<Fraction> Cb) {
        this.variableNum = variableNum;
        this.constraintNum = parameters.size() / (variableNum + 1);
        int newVariableNum = variableNum + constraintNum;
        int total = (newVariableNum + 1) * constraintNum;
        List<Fraction> newParameters = new ArrayList<>(total);
        for (int i = 0; i < constraintNum; i++) {
            for (int j = 0; j < variableNum + 1; j++) {
                Fraction fraction = parameters.get(i * (variableNum + 1) + j);
                newParameters.add(fraction);
            }
            for (int j = 0; j < constraintNum; j++) {
                newParameters.add(new Fraction(i == j ? 1 : 0));
            }
        }
        Xb = new HashMap<>();
        int colIndex = 1;
        for (int i = variableNum + 1; i <= newVariableNum; i++) {
            Xb.put(colIndex++, i);
        }
        this.checkNum = new HashMap<>();
        this.tempCheck = new HashMap<>();
        this.matrixList = new ArrayList<>();
        this.tempChecks = new ArrayList<>();
        this.thetas = new ArrayList<>();
        this.indexes = new ArrayList<>();
        this.Xbs = new ArrayList<>();
        this.objs = new ArrayList<>();
        objs.add(new Fraction());
        for (int i = 1; i <= Cb.size(); i++) {
            checkNum.put(i, Cb.get(i - 1));
        }
        for (int i = Cb.size() + 1; i <= newVariableNum; i++) {
            checkNum.put(i, new Fraction());
        }
        int column = variableNum + constraintNum + 1;
        matrix = new Matrix(newParameters, constraintNum, column);
        matrixList.add(matrix.copy());
    }

    public void transform() {
        int inVariable = getInVariable();
        int outVariable;
        if (inVariable == -1) {
            return;
        } else if (inVariable == -2) {
            resultType = -2;
            return;
        }
        outVariable = getOutVariable(inVariable);
        if (outVariable == -3) {
            resultType = -3;
        } else {
            Xb.remove(outVariable);
            Xb.put(outVariable, inVariable);
            matrix.equalSet(outVariable, inVariable + 1, 1);
            for (int i = 1; i <= constraintNum; i++) {
                if (i != outVariable) {
                    matrix.equalSet(i, inVariable + 1, 0);
                }
            }
            matrixList.add(matrix.copy());
            Fraction obj = getObj();
            objs.add(obj);
            transform();
        }
    }

    public void start() {
        transform();
        for (int i = 1; i <= matrix.row; i++) {//补充最后一列Xb信息
            Xbs.add(Xb.get(i));
        }
    }

    /**
     * Get the variable which will be switched in next time
     * If it is positive, then we check if it can continue:
     * If the parameters in this column are all negative, then it means we cannot operate anymore,
     * because we get a no boundary solution
     * else continue;
     * If there is no positive one, but there is a nonbasic variable checkNum which value is 0, then
     * we get infinite best solutions;
     * If no nonbasic variable checkNum is 0 and all the checkNum <=0, we get the only one best solution
     *
     * @return index if checkNum is positive, -1 if it is the only one best and -2 if there are infinite best solutions
     */
    public int getInVariable() {
        int inIndex = 0;
        Fraction check = new Fraction(Integer.MIN_VALUE);
        for (int i = 1; i <= checkNum.size(); i++) {
            Fraction initialCheck = checkNum.get(i);
            for (int j = 1; j <= Xb.size(); j++) {
                Fraction cb = checkNum.get(Xb.get(j));
                Fraction xi = matrix.get(j, i + 1);
                initialCheck = initialCheck.subtract(cb.multiply(xi));
            }
            tempCheck.put(i, initialCheck);
            if (initialCheck.compareTo(check) > 0) {
                inIndex = i;
                check = initialCheck;
            }
        }
        tempChecks.add(tempCheck.values().toArray());
        if (check.isPositive()) {//有检验数大于0，继续迭代
            indexes.add(inIndex);
            return inIndex;
        }
        //检验数都<=0, inIndex必为正, 但是没有大于0的检验数了, inIndex充当标志位
        Set<Integer> nonbasic = new HashSet<>();//获取非基变量列表
        Set<Integer> keySet = checkNum.keySet();
        for (Integer xb : Xb.values()) {
            if (!keySet.contains(xb)) {
                nonbasic.add(xb);
            }
        }
        for (Integer checkNumIndex : nonbasic) {
            Fraction tempCheckNum = tempCheck.get(checkNumIndex);
            if (tempCheckNum.isZero()) {
                inIndex = -2;//infinite best solutions
                break;
            }
        }
        if (inIndex != -2) {//唯一最优解
            inIndex = -1;
        }
        //全是负数
        return inIndex;
    }

    /**
     * @param inIndex switch in variable
     * @return if no result, return -3, otherwise the row index
     */
    public int getOutVariable(int inIndex) {
        int XbIndex = -3;
        if (inIndex > 0) {//look for out index
            Fraction fraction = new Fraction(Integer.MAX_VALUE);
            for (int i = 1; i <= matrix.row; i++) {
                Fraction b = matrix.get(i, 1);
                Fraction Xi = matrix.get(i, inIndex + 1);//矩阵从1开始
                Xbs.add(Xb.get(i));
                if (Xi.isPositive()) {
                    Fraction theta = b.divide(Xi);
                    thetas.add(theta);
                    if (theta.compareTo(fraction) < 0) {
                        XbIndex = i;
                        fraction = theta;
                    }
                } else {
                    thetas.add(new Fraction());
                }
            }
            if (XbIndex != -3) {
                int outIndex = Xb.get(XbIndex);
                Xb.put(XbIndex, outIndex);//移入换入的非基变量
            }
            indexes.add(XbIndex - 1);
        }
        return XbIndex;
    }

    /**
     * Count each step's obj
     *
     * @return obj
     */
    public Fraction getObj() {
        Fraction obj = new Fraction();
        for (int i = 1; i <= constraintNum; i++) {
            obj = obj.add(matrix.get(i, 1).multiply(checkNum.get(Xb.get(i))));
        }
        return obj;
    }

    public Map<Integer, Fraction> getCheckNum() {
        return checkNum;
    }

    public List<Integer> getXbs() {
        return Xbs;
    }

    public List<Object[]> getTempChecks() {
        return tempChecks;
    }

    public List<Matrix> getMatrixList() {
        return matrixList;
    }

    public List<Fraction> getThetas() {
        return thetas;
    }

    public List<Integer> getIndexes() {
        return indexes;
    }

    public List<Fraction> getObjs() {
        return objs;
    }

    public Integer getResultType() {
        return resultType;
    }

    public int getVariableNum() {
        return variableNum;
    }

    public int getConstraintNum() {
        return constraintNum;
    }

    public Map<Integer, Integer> getXb() {
        return Xb;
    }

    public Matrix getMatrix() {
        return matrix;
    }
}
