package org.iad;

import java.util.ArrayList;
import java.util.List;

public class KVector {
    private final List<Double[]> assignPoints;
    private Double[] point;
    private Double[] previousPoint;
    private Double error;

    public Double getError() {
        return error;
    }

    public void setError(Double error) {
        this.error = error;
    }

    public KVector(double[] point) {
        this.point = new Double[2];
        assignPoints = new ArrayList<>();
        this.point[0] = point[0];
        this.point[1] = point[1];
        previousPoint = new Double[]{0.0, 0.0};
    }

    private void setPoint(Double[] point) {
        previousPoint = this.point;
        this.point = point;
    }

    public Double[] getPoint() {
        return point;
    }

    public List<Double[]> getAssignPoints() {
        return assignPoints;
    }

    public void calcNewCentroid(){
        Double[] avg = new Double[]{0.0, 0.0};

        for(Double[] point: assignPoints){
            avg[0] += point[0];
            avg[1] += point[1];
        }
        avg[0] /= assignPoints.size();
        avg[1] /= assignPoints.size();

        setPoint(avg);
    }

    public Double[] diffPoints(){
        Double[] diff = new Double[2];
        diff[0] = Math.abs(previousPoint[0] - point[0]);
        diff[1] = Math.abs(previousPoint[1] - point[1]);
        return diff;
    }

}
