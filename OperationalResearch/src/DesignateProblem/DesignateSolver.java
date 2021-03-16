package DesignateProblem;

import java.util.*;

/**
 * @author  Li Xingdong
 * @since 2020.11.2
 */
public class DesignateSolver {
    private int time;
    private final List<Integer> data;
    private final List<int[][]> intermediary;
    private final List<Object[][]> intermediaryTicks;
    private int[][] spends;
    private Set<Integer> rowTick;
    private Set<Integer> columnTick;
    private int length;
    int n;//维度
    private Integer answer;
    public DesignateSolver(List<Integer> data) throws Exception{
        this.data=data;
        this.n = (int) Math.sqrt(data.size());
        if (n == 0 || n * n < data.size()) {
            throw new IllegalArgumentException("width != height");
        }
        spends = new int[n + 1][n + 1];
        length = spends.length;
        int k = 0;
        for (int i = 1; i < length; i++) {
            for (int j = 1; j < length; j++) {
                Integer spend = data.get(k++);
                if (spend<0){
                    throw new IllegalArgumentException("The value of spend must not be negative!");
                }
                spends[i][j] = spend;
            }
        }
        for (int i = 1; i < length; i++) {//行列标号
            spends[0][i] = i;
            spends[i][0] = i;
        }
        intermediary=new ArrayList<>();
        intermediaryTicks=new ArrayList<>();
        rowTick=new HashSet<>();
        columnTick = new HashSet<>();
    }

    public void firstStep() {
        int min;
        for (int i = 1; i < length; i++) {//row
            min = Integer.MAX_VALUE;
            for (int j = 1; j < length; j++) {
                min = Math.min(min, spends[i][j]);
            }
            for (int j = 1; j < length; j++) {
                spends[i][j] -= min;
            }
        }
        for (int j = 1; j < length; j++) {//column
            min = Integer.MAX_VALUE;
            for (int i = 1; i < length; i++) {
                min = Math.min(min, spends[i][j]);
            }
            for (int i = 1; i < length; i++) {
                spends[i][j] -= min;
            }
        }
    }

    public void start() {
        firstStep();
        transform();
        System.out.println("The answer is: "+getAnswer());
    }

    /**
     * 对矩阵开始就行变换，消去0
     */
    public void transform() {
        int[][] copy;
        do {
            copy = getCopy();
            int row = getReplaceIndexOfRow();
            if (row != -1) {
                setBracketScanningRow(row);
            }
            int column = getReplaceIndexOfColumn();
            if (column != -1) {
                setBracketScanningColumn(column);
            }
        }while (!isStable(copy));
//        show();
        intermediary.add(getCopy());//保存过程
        while (!tick()){
            reshape();
            firstStep();
            transform();
        }
    }

    public int[][] getSpends() {
        return spends;
    }

    public int getN() {
        return n;
    }

    public List<int[][]> getIntermediary() {
        return intermediary;
    }

    public Set<Integer> getRowTick() {
        return rowTick;
    }

    public Set<Integer> getColumnTick() {
        return columnTick;
    }

    public List<Object[][]> getIntermediaryTicks() {
        return intermediaryTicks;
    }

    /**
     * 遍历每一行：
     * 1.若发现只有一个0元素，则打括号（设置为-1）,并将其同列的其它0元素划去（设置为-2）,函数返回-1
     * 2.若发现每一行都超过1个0元素，则返回0元素最少的那一行的坐标
     *
     * @return -1 or 坐标,参考1，2
     */
    private int getReplaceIndexOfRow() {
        int index = -1;//initial is -1,if there is no 0 in metric, no need to change
        int lastCount = Integer.MAX_VALUE;
        boolean hasSlash = false;
        for (int i = 1; i < length; i++) {
            int count = 0;
            int column = 1;
            for (int j = 1; j < length; j++) {
                if (spends[i][j] == 0) {
                    column = j;
                    count++;
                }
            }
            if (count == 1) {//若只有一个，则直接打括号
                spends[i][column] = -1;
                slashZero(1, column, 2);//划去该元素所在列其它的0
                hasSlash=true;
            }
            if (!hasSlash){
                if (count!=0 && count < lastCount) {//如果是每列多0的情况，需要找到0最少的一行
                    index = i;
                    lastCount=count;
                }
            }
        }
        return hasSlash ? -1 : index;
    }

    /**
     * 当每一行0元素都大于1时，对最少0元素的那一行做打括号的操作，然后划去该元素所在行和列的其它0
     *
     * @param row
     */
    private void setBracketScanningRow(int row) {
        int column = 1;
        int length = spends[row].length;
        for (int i = 1; i < length; i++) {
            if (spends[row][i] == 0) {
                column = i;//记录下该行第一个0元素的列坐标
                spends[row][column] = -1;//给第一个0打括号
                break;
            }
        }
        slashZero(column + 1, row, 1);//划掉该元素所在行其它的0
        slashZero(1, column, 2);//划掉该元素所在列其它的0
    }

    private int getReplaceIndexOfColumn() {
        int index = -1;//initial is 1
        int lastCount = Integer.MAX_VALUE;
        boolean hasSlash = false;
        for (int j = 1; j < length; j++) {
            int count = 0;
            int row = 1;
            for (int i = 1; i < length; i++) {
                if (spends[i][j] == 0) {
                    row = i;
                    count++;
                }
            }
            if (count == 1) {//若只有一个，则直接打括号
                spends[row][j] = -1;
                slashZero(1, row, 1);//划去该元素所在行其它的0
                hasSlash=true;
            }
            if (!hasSlash){
                if (count!=0 && count < lastCount) {//如果是每列多0的情况，需要找到0最少的一列
                    index = j;
                    lastCount = count;
                }
            }
            
        }
        return hasSlash ? -1 : index;
    }

    /**
     * 当每一列0元素都大于1时，对最少0元素的那一列做打括号的操作，然后划去该元素所在行和列的其它0
     *
     * @param column
     */
    private void setBracketScanningColumn(int column) {
        int row = 1;
        int length = spends[column].length;
        for (int i = 1; i < length; i++) {
            if (spends[i][column] == 0) {
                row = i;
                spends[i][column] = -1;//给第一个0打括号
                break;
            }
        }
        slashZero(row + 1, column, 2);//划掉该元素所在列其它的0
        slashZero(1, row, 1);//划掉该元素所在行其它的0
    }

    /**
     * 划去0（设置为-2）
     *
     * @param start 开始遍历位置
     * @param index 括号元素所在的位置
     * @param axis  规定axis=1时，index表示的是行坐标，axis=2表示的是列坐标
     */
    private void slashZero(int start, int index, int axis) {
        if (axis != 1 && axis != 2) throw new IllegalArgumentException("Illegal axis index!");
        if (axis == 1) {
            for (int i = start; i < length; i++) {
                if (spends[index][i] == 0) {
                    spends[index][i] = -2;//划掉该括号所在的行的其它的0;
                }
            }
        } else {
            for (int i = start; i < length; i++) {
                if (spends[i][index] == 0) {
                    spends[i][index] = -2;//划掉该括号所在的行的其它的0;
                }
            }
        }

    }

    /**
     * 若有标号的0元素少于length个，则打勾开始,检查0元素的时候因为不想多写一个循环，就顺便开始打勾，保存在一个局部set里
     * 1: 给没有括号的行✔
     */
    private boolean tick(){
        Set<Integer> newRowTick = new HashSet<>();
        int kickNum=0;
        for (int i = 1; i <length; i++) {//第一步: 给没有括号的行✔（同时检查打括号0元素数量）
            boolean hasBracketZero = false;
            for (int j = 1; j < length; j++) {
                if (spends[i][j]==-1){
                    kickNum++;
                    hasBracketZero=true;
                    break;
                }
            }
            if (!hasBracketZero){
                newRowTick.add(i);
            }
        }
        if (kickNum==length-1){//若符合条件直接返回
            return true;
        }
        rowTick=newRowTick;//否则记下打勾行
        columnTick.clear();//同时清空打勾列
        int rowTickSize;
        int columnTickSize;
        do {
            rowTickSize=rowTick.size();
            columnTickSize=columnTick.size();
            tick2And3();
        }while (rowTick.size()!=rowTickSize || columnTickSize!=columnTick.size());
        if (checkDeadEnd()){
            throw new RuntimeException("Dead end!");
        }
        intermediaryTicks.add(new Object[][]{rowTick.toArray(),columnTick.toArray()});
//        System.out.println("===");
//        System.out.println(rowTick);
//        System.out.println(columnTick);
        return false;
    }
    
    private boolean checkDeadEnd(){
        return rowTick.size()==n && columnTick.size()==n;
    }

    /**
     * ✔的第二第三步
     * 2：对✔行上所有的零元素打✔
     * 3：对✔列上有括号的行打✔
     */
    private void tick2And3(){
        for (Integer row : rowTick) {
            for (int j = 1; j < length; j++) {
                if (spends[row][j]==-1 || spends[row][j]==-2){
                    columnTick.add(j);
                }
            }
        }
        for (Integer column : columnTick) {
            for (int i = 1; i < length; i++) {
                if (spends[i][column]==-1){
                    rowTick.add(i);
                }
            }
        }
    }

    /**
     * 重新生成一个新的含0矩阵，继续transform
     */
    private void reshape(){
        Set<int[]> noLine = new HashSet<>();
        Set<int[]> intersect = new HashSet<>();
        int min=Integer.MAX_VALUE;
        for (int i = 1; i < length; i++) {
            for (int j = 1; j < spends[i].length; j++) {
                if (spends[i][j]==-1 || spends[i][j]==-2){//reset
                    spends[i][j]=0;
                }
                if (!rowTick.contains(i)){
                    if (columnTick.contains(j)){
                        intersect.add(new int[]{i,j});
                    }
                }else {
                    if (!columnTick.contains(j)){
                        min=Math.min(min,spends[i][j]);
                        noLine.add(new int[]{i,j});
                    }
                }
            }
        }
        int finalMin = min;
        noLine.forEach(e-> spends[e[0]][e[1]]-= finalMin);
        intersect.forEach(e-> spends[e[0]][e[1]]+= finalMin);
    }
    
    public int[][] getCopy(){
        int[][] data=new int[length][length];
        for (int i = 0; i < data.length; i++) {
            data[i]=Arrays.copyOf(spends[i],length);
        }
        return data;
    }
    
    private boolean isStable(int[][] data){
        for (int i = 0; i < data.length; i++) {
            if (!Arrays.equals(data[i],spends[i]))
                return false;
        }
        return true;
    }

    public int getAnswer(){
        if (answer==null){
            answer=0;
            for (int i = 1; i < spends.length; i++) {
                for (int j = 1; j < spends[i].length; j++) {
                    if (spends[i][j]==-1){
                        answer+=data.get((i-1)*(length-1)+j-1);
                    }
                }
            }
        }
        return answer;
    }
    public void show() {
        System.out.println(++time+" -----------------------------");
        for (int[] spend : spends) {
            System.out.println(Arrays.toString(spend));
        }
    }
}
