package DynamicProgramming;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * @author  Li Xingdong
 * @since 2020.11.12
 */
public class Dynamic {
    private int factoryNum;
    private int machineNum;
    private int[][] intermediary;
    private int[][] data;
    private Object[] middles;

    public Dynamic(int factoryNum, List<Integer> data){
        this(factoryNum,data,true);
    }

    public Dynamic(int factoryNum, List<Integer> data, boolean backward) {
        this.factoryNum = factoryNum;
        this.machineNum = data.size()/factoryNum;
        this.data = new int[factoryNum][machineNum];
        intermediary = new int[factoryNum+1][machineNum];
        middles = new Object[factoryNum];
        fill(data,backward);
    }
    
    private void fill(List<Integer> data, boolean backward){
        if (backward){
            for (int i = 0; i < factoryNum; i++) {
                for (int j = 0; j < machineNum; j++) {
                    this.data[i][j] = data.get(j*factoryNum+factoryNum-1-i);
                }
            }
        }else {
            for (int i = 0; i < factoryNum; i++) {
                for (int j = 0; j < machineNum; j++) {
                    this.data[i][j] = data.get(j*factoryNum+i);
                }
            }
        }
    }
    public void start(){
        for (int i = 0; i < factoryNum; i++) {//从第一个工厂开始
            List<int[]> middle = new ArrayList<>();
            for (int j = 0; j < machineNum; j++) {//工厂i有j台设备时
                for (int k = j; k >= 0; k--) {
                    intermediary[i+1][j]=Math.max(intermediary[i+1][j],data[i][j-k]+intermediary[i][k]);
//                    System.out.printf("%3d+%-3d",data[i][j-k],intermediary[i][k]);
                    middle.add(new int[]{data[i][j-k],intermediary[i][k]});
//                    if (k!=0) System.out.print(" | ");
                }
//                System.out.println(" --> Max="+intermediary[i+1][j]);
            }
            middles[i]=middle;
//            System.out.println("================");
        }
    }
        
    
    public void showData(){
        for (int[] datum : data) {
            System.out.println(Arrays.toString(datum));
        }
    }
    
    public void showInt(){
        for (int[] ints : intermediary) {
            System.out.println(Arrays.toString(ints));
        }
    }

    public int[][] getIntermediary() {
        return intermediary;
    }

    public Object[] getMiddles() {
        return middles;
    }

    public int getFactoryNum() {
        return factoryNum;
    }

    public int getMachineNum() {
        return machineNum;
    }
}
