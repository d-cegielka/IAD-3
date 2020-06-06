package org.iad;

import de.alsclo.voronoi.Voronoi;
import de.alsclo.voronoi.graph.Graph;
import de.alsclo.voronoi.graph.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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
            System.out.println(Arrays.toString(k.getCentroid()));
            System.out.println("Przypisane: ");
            for(int i = 0; i< k.getCluster().size(); i++){
                System.out.println(Arrays.toString(k.getCluster().get(i)));
            }
            System.out.println("\n\n\n");
        }
        List<Point> points = new ArrayList<>();
        for(KVector sk:kMeans.getVectorList()){
            points.add(new Point(sk.getCentroid()[0],sk.getCentroid()[1]));
        }
        Voronoi voronoi = new Voronoi(points);
        //voronoi.applyBoundingBox(-1,12,1200,720);
        Graph graph = voronoi.getGraph();
        System.out.print(graph.toString());




    }
}