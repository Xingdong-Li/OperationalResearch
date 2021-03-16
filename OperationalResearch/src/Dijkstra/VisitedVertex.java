package Dijkstra;

import java.util.Arrays;

// 已访问顶点集合
public class VisitedVertex {
    // 记录各个顶点是否访问过 1表示访问过,0未访问,会动态更新
    public boolean[] visited;
    // 每个下标对应的值为前一个顶点下标, 会动态更新
    public int[] pre;
    // 记录出发顶点到其他所有顶点的距离,比如G为出发顶点，就会记录G到其它顶点的距离，会动态更新，求的最短距离就会存放到dis
    public double[] dis;

    /**
     * @param length :表示顶点的个数
     * @param index: 出发顶点对应的下标, 比如G顶点，下标就是6
     */
    public VisitedVertex(int length, int index) {
        this.visited = new boolean[length];
        this.pre = new int[length];
        this.dis = new double[length];
        //初始化 dis数组
        Arrays.fill(dis, Integer.MAX_VALUE);
        this.visited[index] = true; //设置出发顶点被访问过
        this.dis[index] = 0;//设置出发顶点的访问距离为0
    }

    /**
     * 继续选择并返回新的访问顶点， 比如这里的G 完后，就是 A点作为新的访问顶点(注意不是出发顶点)
     *
     * @return
     */
    public int nextPoint() {
        double min = Integer.MAX_VALUE;
        int index = -1;
        for (int i = 0; i < visited.length; i++) {
            if (!visited[i] && dis[i] < min) {
                min = dis[i];
                index = i;
            }
        }
        //更新 index 顶点被访问过
        if (index!=-1) visited[index] = true;
        return index;
    }
}
