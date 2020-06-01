package org.iad;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


class KMeansTest {

    KMeans kMeans;
    List<Double[]> entryPoints = new ArrayList<>();
    @BeforeEach
    void setUp() {
        entryPoints.add(new Double[]{-2.2581347770510782,-1.2535452405830094});
        entryPoints.add(new Double[]{-0.0022859666781399612, -3.015756689059613});
        entryPoints.add(new Double[]{2.790852557961781, -2.1463803829337214});
        entryPoints.add(new Double[]{3.3753906740755877, 1.0722255742307056});
        entryPoints.add(new Double[]{0.658327633260735, 3.883331337539439});
        kMeans = new KMeans(5, -5.5, 5.5, 0.5, entryPoints);
    }

    @Test
    void getVectorList() {
        for(KVector k:kMeans.getVectorList()){
            System.out.println(Arrays.toString(k.getPoint()));
            System.out.println("Przypisane: ");
            for(int i =0; i< k.getAssignPoints().size();i++){
                System.out.println(Arrays.toString(k.getAssignPoints().get(i)));
            }
            System.out.println("\n\n\n");
        }
    }
}