package Floyd;

import Model.Graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Floyd {
    private Graph graph;
    private double[][] distances;
    private String[][] pre;
    private List<String[][]> pres;
    private List<String[][]> mediums;
    public String errLog = "None";

    public Floyd(Graph graph) {
        if (graph == null) throw new NullPointerException("Graph cannot be null!");
        try {
            this.graph = (Graph) graph.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            throw new RuntimeException("Fail to clone graph!");
        }
        distances = graph.getWeights();
        int length = distances.length;
        pre = new String[length][length];
        for (int i = 0; i < pre.length; i++) {
            Arrays.fill(pre[i],graph.getPoints()[i]);
        }
        pres = new ArrayList<>();
        mediums = new ArrayList<>();
    }

    public void start() {
        double[][] weights = graph.getWeights();
        int length = weights.length;
        for (int k = 0; k < length; k++) {
            for (int i = 0; i < length; i++) {
                for (int j = 0; j < length; j++) {
                    if (distances[i][k]!=Integer.MAX_VALUE && distances[k][j]!=Integer.MAX_VALUE){
                        double detour = distances[i][k] + distances[k][j];
                        if (distances[i][j] > detour) {
                            if (i==j && detour !=0) {
                                String[] points = graph.getPoints();
                                errLog = "点 "+points[i]+" 经过点 "+points[k]+" 到达点 "+points[j]+",距离由 "+distances[i][j]
                                        +" 变为 "+distances[i][k] +" + "+ distances[k][j]+" = "+ detour
                                        +",图中至少存在一个负周期，故不可能有最短距离";
                                return;
                            }
                            distances[i][j] = detour;
                            pre[i][j]=pre[k][j];
                        }
                    }
                }
            }
            saveMedium();
        }

    }
    
    public void saveMedium(){
        String[][] pre = new String[this.pre.length][];
        String[][] distances = new String[this.distances.length][this.distances.length];
        for (int i = 0; i < this.pre.length; i++) {
            pre[i]=Arrays.copyOf(this.pre[i],this.pre[i].length);
        }
        for (int i = 0; i < this.distances.length; i++) {
            for (int j = 0; j < this.distances.length; j++) {
                distances[i][j] = this.distances[i][j] == Integer.MAX_VALUE ? "Infinity" :  String.valueOf(this.distances[i][j]);
            }
        }
        pres.add(pre);
        mediums.add(distances);
    }

    public void show(){
        for (int i = 0; i < pres.size(); i++) {
            String[][] strings = pres.get(i);
            String[][] medium = mediums.get(i);
            System.out.println("前驱:");
            for (String[] string : strings) {
                System.out.println(Arrays.toString(string));
            }
            System.out.println("距离:");
            for (String[] doubles : medium) {
                System.out.println(Arrays.toString(doubles));
            }
            System.out.println("-------------------------------");
        }
    }

    public List<String[][]> getMediums() {
        return mediums;
    }

    public double[][] getDistances() {
        return distances;
    }

    public List<String[][]> getPres() {
        return pres;
    }
}
