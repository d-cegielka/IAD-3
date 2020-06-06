package org.iad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KVector {
    private final List<Double[]> cluster;
    private Double[] centroid;
    private Double[] previousCentroid;
    private double error;


    public KVector(double[] centroid) {
        this.centroid = new Double[2];
        cluster = new ArrayList<>();
        this.centroid[0] = centroid[0];
        this.centroid[1] = centroid[1];
        previousCentroid = new Double[]{0.0, 0.0};
    }

    public void calcNewCentroid(){
        Double[] avg = new Double[]{0.0, 0.0};

        for(Double[] point: cluster){
            avg[0] += point[0];
            avg[1] += point[1];
        }
        avg[0] /= cluster.size();
        avg[1] /= cluster.size();

        setCentroid(avg);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        sb.append("Współrzędne środka klastra [X,Y]: \n").append(Arrays.toString(centroid)).append("\n");
        sb.append("Błąd kwantyzacji: ").append(error).append("\n");
        sb.append("Współrzędne punktów należących do klastra [X,Y]: \n");
        for(Double[] point: cluster){
            sb.append(Arrays.toString(point)).append("\n");
        }
        sb.append('\n');
        return sb.toString();
    }

    public double getError() {
        return error;
    }

    public void setError(double error) {
        this.error = error;
    }

    private void setCentroid(Double[] centroid) {
        previousCentroid = this.centroid;
        this.centroid = centroid;
    }

    public Double[] getCentroid() {
        return centroid;
    }

    public List<Double[]> getCluster() {
        return cluster;
    }

    public Double[] diffPoints(){
        Double[] diff = new Double[2];
        diff[0] = Math.abs(previousCentroid[0] - centroid[0]);
        diff[1] = Math.abs(previousCentroid[1] - centroid[1]);
        return diff;
    }

}
