package Dijkstra;

import Model.Graph;

import java.util.ArrayList;
import java.util.List;

public class Dijkstra {
    private Graph graph;
    private VisitedVertex vv;
    private int start;
    //----------show
    private List<String> visitList;
    private String[] info;

    public Dijkstra(Graph graph,int start) {
        if (graph == null) throw new NullPointerException("Graph cannot be null!");
        this.graph = graph;
        int length = graph.getPoints().length;
        if(start<0 || start>= length) throw new IllegalArgumentException("Not include this point!");
        vv = new VisitedVertex(length,start);
        this.start=start;
        visitList = new ArrayList<>();
        info = new String[length];
    }

    //迪杰斯特拉算法实现
    public void start() {
        for (int j = 0; j < graph.getPoints().length; j++) {
            visitList.add(graph.getPoints()[start]);
            update(start);//更新start到周围顶点的距离和前驱顶点
            start = vv.nextPoint();// 选择并返回新的访问顶点
            if (start==-1) break;
        }
        save();
    }


    //更新index下标顶点到周围顶点的距离和周围顶点的前驱顶点,
    private void update(int index) {
        double len = 0;
        //根据遍历我们的邻接矩阵的  matrix[index]行
        double[][] weights = graph.getWeights();
        for (int j = 0; j < weights[index].length; j++) {
            // len 含义是 : 出发顶点到index顶点的距离 + 从index顶点到j顶点的距离的和 
            len = vv.dis[index] + weights[index][j];
            // 如果j顶点没有被访问过，并且 len 小于出发顶点到j顶点的距离，就需要更新
            if (!vv.visited[j] && len < vv.dis[j]) {
                //更新j顶点的前驱为index顶点
                vv.pre[j]=index;
                //更新出发顶点到j顶点的距离
                vv.dis[j]=len;
            }
        }
    }


    //显示最后的结果
    //即将三个数组的情况输出
    public void save() {
        for (int i = 0;i< vv.dis.length;i++) {
            String detail=graph.getPoints()[i];
            double distance = vv.dis[i];
            detail+=distance != Integer.MAX_VALUE ? "(" + distance + ")" : "(Infinity)";
            info[i]=detail;
        }
    }

    public List<String> getVisitList() {
        return visitList;
    }

    public String[] getInfo() {
        return info;
    }
}



