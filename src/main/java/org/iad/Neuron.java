package org.iad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Neuron {
    private final List<Double[]> cluster;
    private Double[] weights;
    private Double[] previousWeights;
    private double potential;
    private double neighbourhoodRadius;
    private double learningRate;
    private double error;

    public Neuron(double neighbourhoodRadius, double learningRate) {
        cluster = new ArrayList<>();
        weights = new Double[]{Math.random(), Math.random()};
        previousWeights = new Double[]{0.0, 0.0};
        potential = 1d;
        this.neighbourhoodRadius = neighbourhoodRadius;
        this.learningRate = learningRate;
    }

    public Double[] getWeights() {
        return weights;
    }

    public List<Double[]> getCluster() {
        return cluster;
    }

    public void setWeights(Double[] weights) {
        previousWeights = this.weights;
        this.weights = weights;
    }

    public double getPotential() {
        return potential;
    }

    public void setPotential(double potential) {
        this.potential = potential;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        sb.append("Współrzędne środka klastra [X,Y]: \n").append(Arrays.toString(weights)).append("\n");
        sb.append("Błąd kwantyzacji: ").append(error).append("\n");
        sb.append("Współrzędne punktów należących do klastra [X,Y]: \n");
        for(Double[] point: cluster){
            sb.append(Arrays.toString(point)).append("\n");
        }
        sb.append('\n');
        return sb.toString();
    }

    public double getNeighbourhoodRadius() {
        return neighbourhoodRadius;
    }

    public void setNeighbourhoodRadius(double neighbourhoodRadius) {
        this.neighbourhoodRadius = neighbourhoodRadius;
    }

    public double getLearningRate() {
        return learningRate;
    }

    public double getError() {
        return error;
    }

    public void setError(double error) {
        this.error = error;
    }

    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }
}
