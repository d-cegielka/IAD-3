package org.iad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class KMeans {
    private List<KVector> vectorList;

    public KMeans(final int numOfVectors, final double lowerRange, final double upperRange,
                  final double precision, final List<Double[]> entryPoints) {

        //Tworzenie listy wektorów z losowymi punktami
        vectorList = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < numOfVectors; i++) {
            double[] point = new double[2];
            point = random.doubles(point.length,lowerRange,upperRange).toArray();
            vectorList.add(new KVector(point));
        }


        for (int i = 0; i < 50; i++) {
            //Ustalanie przynależności punktów wejściowych do wektorów
            for(Double[] entryPoint:entryPoints){
                assignPoint(entryPoint);
            }

            //Obliczanie nowego punktu środkowego wektora jako średniej z przypisanych punktów
            for(KVector v:vectorList){
                if(v.getAssignPoints().size() > 0){
                    v.calcNewCentroid();
                }
            }

            int counter = vectorList.size() * 2;
            for(KVector v:vectorList){
                Double[] diff = v.diffPoints();
                if(diff[0] > precision){
                    counter--;
                    break;
                }
                if(diff[1] > precision){
                    counter--;
                    break;
                }
            }
            if(counter == vectorList.size()*2){
                calcError();
            }
        }
    }

    public List<KVector> getVectorList() {
        return vectorList;
    }


    private void assignPoint(final Double[] entryPoint){
        //Double minDistance = calcEuclideanDistance(entryPoint,vectorList.get(0).getPoint());
        int indexVector = 0;
        double minDistance = Double.MAX_VALUE;

        for (int i = 0; i < vectorList.size(); i++) {
            Double distance = calcEuclideanDistance(entryPoint,vectorList.get(i).getPoint());
            if(minDistance > distance){
                minDistance = distance;
                indexVector = i;
                vectorList.get(i).getAssignPoints().removeIf(s -> Arrays.equals(s, entryPoint));
            }
        }
        vectorList.get(indexVector).getAssignPoints().add(entryPoint);
    }

    private void calcError(){
        for (KVector kVector : vectorList) {
            double error = 0;
            for (Double[] point : kVector.getAssignPoints()) {
                error += calcEuclideanDistance(kVector.getPoint(), point);
            }
            kVector.setError(error / kVector.getAssignPoints().size());
        }
    }

    private Double calcEuclideanDistance(Double[] point1, Double[] point2){
        return Math.sqrt((point1[0]-point2[0])*(point1[0]-point2[0]) +
                (point1[1]-point2[1])*(point1[1]-point2[1]));
    }
}
