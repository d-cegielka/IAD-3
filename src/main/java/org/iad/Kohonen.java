package org.iad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Kohonen {
    List<Neuron> listOfNeurons;
    final double minPotential;

    public Kohonen(final int numOfNeurons, final double learningRate, final double neighbourhoodRadius) {
        listOfNeurons = new ArrayList<>();
        minPotential = 0.75;
        for (int i = 0; i < numOfNeurons; i++) {
            listOfNeurons.add(new Neuron(neighbourhoodRadius,learningRate));
        }
    }

    public void learning(final int numOfEpoch, boolean isGaussianNeighbourhood, List<Double[]> inputVectors, final boolean normalizeInput){
        int winnerNeuronIndex;
        if(normalizeInput){
            List<Double[]> normalizedInputVectors = new ArrayList<>();
            for(Double[] inputVector:inputVectors){
                normalizedInputVectors.add(normalizeInput(inputVector));
            }
            inputVectors = normalizedInputVectors;
        }
        for (int i = 0; i < numOfEpoch; i++) {
            for(Double[] inputVector: inputVectors){
                winnerNeuronIndex = WTM(inputVector);
                if(isGaussianNeighbourhood){
                    adaptiveWeightsGaussian(winnerNeuronIndex,inputVector);
                } else {
                    adaptiveWeights(winnerNeuronIndex,inputVector);
                }
                /*if(listOfNeurons.get(winnerNeuronIndex).getPotential() >= minPotential){*/
                    double radius = listOfNeurons.get(winnerNeuronIndex).getNeighbourhoodRadius();
                    listOfNeurons.get(winnerNeuronIndex).setNeighbourhoodRadius(radius - radius * ((i + 1.0) / numOfEpoch));
                /*}*/
            }
        }
        calcError();
    }

    private int WTM(Double[] vector){
        int winnerNeuronIndex = 0;
        double minDistance = Double.MAX_VALUE;

        for (int i = 0; i <listOfNeurons.size(); i++) {
            Double distance = calcEuclideanDistance(vector,listOfNeurons.get(i).getWeights());
            listOfNeurons.get(i).getCluster().removeIf(s -> Arrays.equals(s, vector));
            if(minDistance > distance){
                minDistance = distance;
                winnerNeuronIndex = i;
            }
        }
        listOfNeurons.get(winnerNeuronIndex).getCluster().add(vector);
        return winnerNeuronIndex;
    }

    private void adaptiveWeightsGaussian(final int winnerIndex, Double[] vector){
        Neuron winnerNeuron = listOfNeurons.get(winnerIndex);
        double gaussianNeighbourhood;
        for (int i = 0; i < listOfNeurons.size(); i++) {
            Neuron adaptedNeuron = listOfNeurons.get(i);

            if(adaptedNeuron.getPotential() < minPotential){
                if(i == winnerIndex) {
                    adaptedNeuron.setPotential(adaptedNeuron.getPotential() - minPotential);
                } else {
                    adaptedNeuron.setPotential(adaptedNeuron.getPotential() + (1d / listOfNeurons.size()));
                }
                break;
            }

            if(i == winnerIndex){
                gaussianNeighbourhood = 1;
                adaptedNeuron.setPotential(adaptedNeuron.getPotential() - minPotential);
            } else {
                gaussianNeighbourhood = calcGaussianNeighbourhood(winnerNeuron,adaptedNeuron);
                adaptedNeuron.setPotential(adaptedNeuron.getPotential() + (1d / listOfNeurons.size()));
            }
            Double[] newWeights = new Double[2];
            newWeights[0] = adaptedNeuron.getWeights()[0] + adaptedNeuron.getLearningRate() * gaussianNeighbourhood * (vector[0] - adaptedNeuron.getWeights()[0]);
            newWeights[1] = adaptedNeuron.getWeights()[1] + adaptedNeuron.getLearningRate() * gaussianNeighbourhood * (vector[1] - adaptedNeuron.getWeights()[1]);
            adaptedNeuron.setWeights(newWeights);
        }
     }

    private void adaptiveWeights(final int winnerIndex, Double[] vector){
        Neuron winnerNeuron = listOfNeurons.get(winnerIndex);
        double distance;
        for (int i = 0; i < listOfNeurons.size(); i++) {
            Neuron adaptedNeuron = listOfNeurons.get(i);
            //adaptedNeuron.getCluster().removeIf(s -> Arrays.equals(s, vector));
            if(adaptedNeuron.getPotential() < minPotential){
                if(i == winnerIndex) {
                    adaptedNeuron.setPotential(adaptedNeuron.getPotential() - minPotential);
                } else {
                    adaptedNeuron.setPotential(adaptedNeuron.getPotential() + (1d / listOfNeurons.size()));
                }
                break;
            }

            distance = calcEuclideanDistance(vector,winnerNeuron.getWeights());
            Double[] newWeights = new Double[2];
            if(i == winnerIndex || distance <= winnerNeuron.getNeighbourhoodRadius()){
                newWeights[0] = adaptedNeuron.getWeights()[0] + adaptedNeuron.getLearningRate() * 1 * (vector[0] - adaptedNeuron.getWeights()[0]);
                newWeights[1] = adaptedNeuron.getWeights()[1] + adaptedNeuron.getLearningRate() * 1 * (vector[1] - adaptedNeuron.getWeights()[1]);
                adaptedNeuron.setWeights(newWeights);
                if(i == winnerIndex){
                    adaptedNeuron.setPotential(adaptedNeuron.getPotential() - minPotential);
                } else {
                    adaptedNeuron.setPotential(adaptedNeuron.getPotential() + (1d / listOfNeurons.size()));
                }
            }
        }
        //winnerNeuron.getCluster().add(vector);
    }

    public Double[] normalizeInput(Double[] vector) {
        double sum = 0d;
        Double[] output = new Double[vector.length];
        for(int i = 0; i < vector.length; i++) {
            sum += Math.pow(vector[i], 2);
        }
        sum = Math.sqrt(sum);
        for(int i = 0; i < output.length; i++) {
            output[i] = vector[i] / sum;
        }
        return output;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Średni błąd kwantyzacji: ").append(calcAvgError()).append("\n\n");
        for(Neuron neuron: listOfNeurons) {
            sb.append(neuron.toString());
        }
        sb.append("\n");
        return sb.toString();
    }

    public Double calcAvgError(){
        double avgError = 0d;
        for(Neuron neuron: listOfNeurons){
            avgError += neuron.getError();
        }
        avgError/= listOfNeurons.size();
        return avgError;
    }

    private void calcError(){
        for (Neuron neuron : listOfNeurons) {
            double error = 0d;
            if(neuron.getCluster().size() == 0) {
                neuron.setError(error);
                break;
            }
            for (Double[] point : neuron.getCluster()) {
                error += Math.pow(calcEuclideanDistance(neuron.getWeights(), point),2);
            }
            error /= neuron.getCluster().size();
            neuron.setError(error);
        }
    }

    private Double calcGaussianNeighbourhood(Neuron winnerNeuron, Neuron neuron){
        double distance = calcEuclideanDistance(neuron.getWeights(),
                winnerNeuron.getWeights());
        return Math.exp(-(distance * distance) / (2 * Math.pow(winnerNeuron.getNeighbourhoodRadius(), 2)));
    }

    private Double calcEuclideanDistance(Double[] point1, Double[] point2){
        return Math.sqrt((point1[0] - point2[0]) * (point1[0] - point2[0]) +
                (point1[1] - point2[1]) * (point1[1] - point2[1]));
    }

    public List<Neuron> getListOfNeurons() {
        return listOfNeurons;
    }
}
