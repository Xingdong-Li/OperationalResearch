package Model;

public class NegativeWeightGraph extends Graph {
    public NegativeWeightGraph(String[] points, double[] weights) {
        int vertexNum = points.length;
        super.setPoints(points);
        double[][] thisWeights = new double[vertexNum][vertexNum];
        int index = 0;
        for (int i = 0; i < thisWeights.length; i++) {
            for (int j = 0; j < thisWeights[i].length; j++) {
                if (i==j) thisWeights[i][j]=0;
                else thisWeights[i][j]= weights[index]==0 ? Integer.MAX_VALUE : weights[index];
                ++index;
            }
        }
        super.setWeights(thisWeights);
    }
}
