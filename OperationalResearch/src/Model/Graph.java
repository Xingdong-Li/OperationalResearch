package Model;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author Li Xingdong
 * @since 2020.12.6
 */
public class Graph implements Serializable, Cloneable {
    private String[] points;
    private double[][] weights;

    public Graph() {
    }

    public Graph(String[] points, double[] weights) {
        int vertexNum = points.length;
        this.points = points;
        int index = 0;
        this.weights = new double[vertexNum][vertexNum];
        for (double[] weight : this.weights) {
            for (int i = 0; i < weight.length; i++) {
                weight[i] = weights[index] == -1 ? Integer.MAX_VALUE : weights[index];
                ++index;
            }
        }
    }

    public String[] getPoints() {
        return points;
    }

    public void setPoints(String[] points) {
        this.points = points;
    }

    public double[][] getWeights() {
        return weights;
    }

    public void setWeights(double[][] weights) {
        this.weights = weights;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(weights.length * weights.length);
        for (double[] weight : weights) {
            builder.append(Arrays.toString(weight)).append("\n");
        }
        return "Graph{\n" +
                "points=" + Arrays.toString(points) + "\n" +
                "weights=\n" + builder.toString()+
                '}';
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Graph clone = (Graph) super.clone();
        String[] newPoints = Arrays.copyOf(this.points, this.points.length);
        double[][] newWeights = new double[this.weights.length][];
        for (int i = 0; i < this.weights.length; i++) {
            newWeights[i] = Arrays.copyOf(this.weights[i],this.weights[i].length);
        }
        clone.setPoints(newPoints);
        clone.setWeights(newWeights);
        return clone;
    }
}
