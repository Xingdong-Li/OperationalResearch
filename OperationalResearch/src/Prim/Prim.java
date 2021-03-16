package Prim;

import Model.Graph;

import java.util.ArrayList;
import java.util.List;
/**
 * @author  Li Xingdong
 * @since 2020.12.6
 */
public class Prim {
    private Graph graph;
    private List<String> mediums;
    private double totalWeight;

    public Prim(Graph graph) {
        if (graph == null) throw new NullPointerException("Graph cannot be null!");
        this.graph = graph;
        mediums = new ArrayList<>();
    }

    public void prim(int v) {
        int vertexNum = graph.getPoints().length;
        double[][] weight = graph.getWeights();
        String[] points = graph.getPoints();
        boolean[] visited = new boolean[vertexNum];
        visited[v] = true;
        int v1 = -1, v2 = -1;
        double minWeight = Integer.MAX_VALUE;
        totalWeight = 0;
        for (int i = 0; i < vertexNum - 1; i++) {//n-1轮，2个点不用找了
            boolean accessible = false;
            for (int j = 0; j < vertexNum; j++) {//被访问过的
                for (int k = 0; k < vertexNum; k++) {//还没被访问的
                    double value = weight[j][k]==-1 ? Integer.MAX_VALUE : weight[j][k];
                    if (visited[j] && !visited[k] && value < minWeight) {
                        accessible=true;
                        minWeight = weight[j][k];
                        v1 = j;
                        v2 = k;
                    }
                }
            }
            if (!accessible) {
                totalWeight=-1;
                return;
            }
            mediums.add("From <span class=\"badge badge-pill badge-light\" style=\"font-size: medium\">" + points[v1]
                    + "</span> to <span class=\"badge badge-pill badge-light\" style=\"font-size: medium\">" + points[v2]
                    + "</span>, weight: <span class=\"badge badge-pill badge-light\" style=\"font-size: medium\">" + minWeight +"</span>"); 
            totalWeight += minWeight;
            visited[v2] = true;
            minWeight = Integer.MAX_VALUE;
        }
    }

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public List<String> getMediums() {
        return mediums;
    }

    public void setMediums(List<String> mediums) {
        this.mediums = mediums;
    }

    public double getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(double totalWeight) {
        this.totalWeight = totalWeight;
    }
}
