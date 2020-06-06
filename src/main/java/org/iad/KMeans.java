package org.iad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class KMeans {
    private final List<KVector> vectorList;

    public KMeans(final int numOfVectors, final double lowerRange, final double upperRange,
                  final double precision, final List<Double[]> entryPoints) {

        //Tworzenie listy wektorów z losowymi punktami
        vectorList = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < numOfVectors; i++) {
            double[] point = new double[2];
            point = random.doubles(point.length, lowerRange, upperRange).toArray();
            vectorList.add(new KVector(point));
        }
        double avgError = 0d;
        int i=0;

        while(true) {
            //Ustalanie przynależności punktów wejściowych do wektorów
            for(Double[] entryPoint:entryPoints){
                assignPoint(entryPoint);
            }

            //Obliczanie nowego punktu środkowego wektora jako średniej z przypisanych punktów
            for(KVector v:vectorList){
                if(v.getCluster().size() > 0){
                    v.calcNewCentroid();
                }
            }
            calcError();
            double newAvgError = calcAvgError();
            if (i > 0) {
                double breakCond = (avgError - newAvgError) / newAvgError;
                if(Double.isNaN(breakCond)) {
                    breakCond = 0d;
                }
                System.out.println(breakCond);
                if (breakCond <= precision) {
                    break;
                }
            }
            avgError = newAvgError;
            i++;
            /*int counter = vectorList.size() * 2;
            for(KVector v:vectorList){
                Double[] diff = v.diffPoints();
                if(diff[0] > precision){
                    System.out.println(diff[0]);
                    counter--;
                    break;
                }
                if(diff[1] > precision){
                    System.out.println(diff[1]);
                    counter--;
                    break;
                }
            }

            if(counter == vectorList.size()*2){
                calcError();
                break;
            }*/
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Średni błąd kwantyzacji: ").append(calcAvgError()).append("\n\n");
        for(KVector kVector: vectorList) {
            sb.append(kVector.toString());
        }
        sb.append("\n");
        return sb.toString();
    }

    public List<KVector> getVectorList() {
        return vectorList;
    }

    public double calcAvgError(){
        double avgError = 0d;
        for(KVector kVector: vectorList){
           avgError += kVector.getError();
        }
        avgError /= vectorList.size();
        return avgError;
    }

    private void assignPoint(final Double[] entryPoint){
        //Double minDistance = calcEuclideanDistance(entryPoint,vectorList.get(0).getPoint());
        int indexVector = 0;
        double minDistance = Double.MAX_VALUE;

        for (int i = 0; i < vectorList.size(); i++) {
            double distance = calcEuclideanDistance(entryPoint,vectorList.get(i).getCentroid());
            vectorList.get(i).getCluster().removeIf(s -> Arrays.equals(s, entryPoint));
            if(minDistance > distance){
                minDistance = distance;
                indexVector = i;
            }
        }
        vectorList.get(indexVector).getCluster().add(entryPoint);
    }

    private void calcError(){
        for (KVector kVector : vectorList) {
            double error = 0d;
            if(kVector.getCluster().size() == 0) {
                kVector.setError(error);
                break;
            }
            for (Double[] point : kVector.getCluster()) {
                error += Math.pow(calcEuclideanDistance(kVector.getCentroid(), point),2);
            }
            error /= kVector.getCluster().size();
            kVector.setError(error);
            //System.out.println(kVector.getError());
        }
        //System.out.println();
    }

    private double calcEuclideanDistance(Double[] point1, Double[] point2){
        return Math.sqrt((point1[0]-point2[0])*(point1[0]-point2[0]) +
                (point1[1]-point2[1])*(point1[1]-point2[1]));
    }
}
