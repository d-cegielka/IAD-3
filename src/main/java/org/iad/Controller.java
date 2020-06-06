package org.iad;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Controller {
    public TextField numOfVectorsTF;
    public TextField upperRangeTF;
    public TextField lowerRangeTF;
    public TextField precisionTF;
    public Button KMeansRunBtn;
    public TextField numOfNeuronTF;
    public TextField numOfEpochTF;
    public TextField neighbourhoodRadiusTF;
    public TextField learningRateTF;
    public ChoiceBox<String> typeNeighbourhoodCB;
    public Button runAllBtn;
    public Button KohonenRunBtn;
    File lastFile;
    List<Double[]> entryPoints;

    @FXML
    void initialize(){
        typeNeighbourhoodCB.getItems().addAll("gaussowskie", "prostokątne");
        typeNeighbourhoodCB.getSelectionModel().selectFirst();
    }


    List<Double[]> readDataFromFile(String path){
        List<Double[]> data = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
            String line;
            while((line = reader.readLine())!=null ){
                data.add(Arrays.stream(line.split(",")).map(Double::valueOf).toArray(Double[]::new));
            }
            reader.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public void readData() {
        entryPoints = readDataFromFile(openFile("Wybierz plik z danymi").getPath());
        if(entryPoints.size() > 0) {
            KMeansRunBtn.setDisable(false);
            KohonenRunBtn.setDisable(false);
            runAllBtn.setDisable(false);
        }
    }

    public void kMeansRun() {
        final double lowerRange = Double.parseDouble(lowerRangeTF.getText());
        final double upperRange = Double.parseDouble(upperRangeTF.getText());
        final int numOfVectors = Integer.parseInt(numOfVectorsTF.getText());
        final double precision = Double.parseDouble(precisionTF.getText());
        Thread kMeansThread = new Thread(kMeansTask(lowerRange,upperRange,precision,numOfVectors));
        kMeansThread.start();
        /*KMeans kMeans = new KMeans(Integer.parseInt(numOfVectorsTF.getText()),
                lowerRange,upperRange, Double.parseDouble(precisionTF.getText()), entryPoints);
        drawKMeansDiagram(kMeans);
        File saveKMeansRaport = new File("kMeans_report.txt");
        try {
            Files.writeString(Paths.get(saveKMeansRaport.getPath()),kMeans.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    private Task<?> kMeansTask(final double lowerRange, final double upperRange, final double precision, final int numOfVector) {
        return new Task<>() {
            @Override
            protected Object call() {
                KMeans kMeans = new KMeans(numOfVector, lowerRange, upperRange, precision, entryPoints);
                Platform.runLater(() -> {
                    drawKMeansDiagram(kMeans);
                    File saveKMeansRaport = new File("kMeans_report.txt");
                    try {
                        StringBuilder report = new StringBuilder("Parametry grupowania: \n");
                        report.append("Ilość wektorów K: ").append(numOfVector).append("\n");
                        report.append("Zakres losowania współrzędnych: [").append(lowerRange).append(":").append(upperRange).append("]").append("\n");
                        report.append("Dokładność: ").append(precision).append("\n\n");
                        report.append(kMeans.toString());
                        Files.writeString(Paths.get(saveKMeansRaport.getPath()), report);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                return true;
            }
        };
    }

    public void kohonenRun() {
        int numOfNeuron = Integer.parseInt(numOfNeuronTF.getText());
        double neighbourhoodRadius = Double.parseDouble(neighbourhoodRadiusTF.getText());
        boolean isGaussianNeighbourhood = typeNeighbourhoodCB.getSelectionModel().getSelectedItem().contains("gauss");
        double learningRate = Double.parseDouble(learningRateTF.getText());
        int numOfEpoch = Integer.parseInt(numOfEpochTF.getText());
        Thread kohonenThread = new Thread(kohonenTask(numOfNeuron,learningRate,neighbourhoodRadius,numOfEpoch,isGaussianNeighbourhood,false));
        kohonenThread.start();
    }

    private Task<?> kohonenTask(final int numOfNeuron, final double learningRate, final double neighbourhoodRadius,
                                final int numOfEpoch, final boolean isGaussianNeighbourhood, final boolean normalizeInput){
        return new Task<>() {
            @Override
            protected Object call() {
                Kohonen kohonen = new Kohonen(numOfNeuron, learningRate, neighbourhoodRadius);
                kohonen.learning(numOfEpoch, isGaussianNeighbourhood, entryPoints, normalizeInput);
                Platform.runLater(() -> {
                    drawKohonenDiagram(kohonen);
                    File saveKohonenRaport = new File("kohonen_report.txt");
                    try {
                        StringBuilder report = new StringBuilder("Parametry nauki: \n");
                        report.append("Ilość neuronów: ").append(numOfNeuron).append("\n");
                        report.append("Promień sąsiedztwa: ").append(neighbourhoodRadius).append("\n");
                        report.append("Typ sąsiedztwa: ").append(isGaussianNeighbourhood ? "gaussowskie" : "prostokątne").append("\n");
                        report.append("Współczynnik nauki: ").append(learningRate).append("\n");
                        report.append("Liczba epok: ").append(numOfEpoch).append("\n\n");
                        report.append(kohonen.toString());
                        Files.writeString(Paths.get(saveKohonenRaport.getPath()), report);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                return true;
            }
        };
    }


    public void runAll() {
        final double lowerRange = Double.parseDouble(lowerRangeTF.getText());
        final double upperRange = Double.parseDouble(upperRangeTF.getText());
        final int numOfVectors = Integer.parseInt(numOfVectorsTF.getText());
        final double precision = Double.parseDouble(precisionTF.getText());
        int numOfNeuron = Integer.parseInt(numOfNeuronTF.getText());
        double neighbourhoodRadius = Double.parseDouble(neighbourhoodRadiusTF.getText());
        boolean isGaussianNeighbourhood = typeNeighbourhoodCB.getSelectionModel().getSelectedItem().contains("gauss");
        double learningRate = Double.parseDouble(learningRateTF.getText());
        int numOfEpoch = Integer.parseInt(numOfEpochTF.getText());
        Thread kMeansThread = new Thread(kMeansTask(lowerRange,upperRange,precision,numOfVectors));
        Thread kohonenThread = new Thread(kohonenTask(numOfNeuron,learningRate,neighbourhoodRadius,numOfEpoch,isGaussianNeighbourhood,false));
        kMeansThread.start();
        kohonenThread.start();
    }

    /**
     * Wybór pliku do odczytu
     * @param info komunikat jaki plik wybieramy
     * @return wybrany plik do odczytu
     */
    @FXML
    private File openFile(String info) {
        final FileChooser fileChooser = new FileChooser();
        if (lastFile != null) {
            fileChooser.setInitialDirectory(lastFile.getParentFile());
        }
        fileChooser.setTitle(info);
        File getFile = fileChooser.showOpenDialog(new Stage());
        lastFile = getFile;
        return getFile;
    }

    /**
     * Zapis wykresu do pliku png
     */
    public void saveChart(ScatterChart<Number,Number> chart, String path) {
        try {
            File save = new File(path);
            chart.setAnimated(false);
            WritableImage image = chart.snapshot(null, null);
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", save);
            chart.setAnimated(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void drawKMeansDiagram(KMeans kMeans){
        Stage diagramStage = new Stage();
        diagramStage.setTitle("Diagram");
        final double lowerRange = Double.parseDouble(lowerRangeTF.getText());
        final double upperRange = Double.parseDouble(upperRangeTF.getText());
        final NumberAxis xAxis = new NumberAxis(lowerRange * 2.5, upperRange * 2.5, (upperRange - lowerRange) / 10);
        final NumberAxis yAxis = new NumberAxis(lowerRange * 2.5, upperRange * 2.5, (upperRange - lowerRange) / 10);
        final ScatterChart<Number,Number> sc = new
                ScatterChart<>(xAxis, yAxis);
        sc.setAnimated(true);
        xAxis.setLabel("X");
        yAxis.setLabel("Y");
        sc.setTitle("Algorytm K-Średnich");

        XYChart.Series<Number,Number> centroids = new XYChart.Series<>();
        centroids.setName("Centroidy");
        for(int i=0; i<kMeans.getVectorList().size(); i++){
            Double[] centroid = kMeans.getVectorList().get(i).getCentroid();
            centroids.getData().add(new XYChart.Data<>(centroid[0], centroid[1]));
            sc.getData().add(createSeries(kMeans.getVectorList().get(i).getCluster(), String.valueOf(i)));
        }
        sc.getData().add(centroids);

        Scene scene  = new Scene(sc, 1000, 750);
        diagramStage.setScene(scene);
        diagramStage.show();
        saveChart(sc,"KMeans_diagram.png");
    }

    private void drawKohonenDiagram(Kohonen kohonen){
        Stage diagramStage = new Stage();
        final double lowerRange = Double.parseDouble(lowerRangeTF.getText());
        final double upperRange = Double.parseDouble(upperRangeTF.getText());
        diagramStage.setTitle("Diagram");
        final NumberAxis xAxis = new NumberAxis(lowerRange * 2.5, upperRange * 2.5, (upperRange - lowerRange) / 10);
        final NumberAxis yAxis = new NumberAxis(lowerRange * 2.5, upperRange * 2.5, (upperRange - lowerRange) / 10);
        final ScatterChart<Number,Number> sc = new
                ScatterChart<>(xAxis, yAxis);
        sc.setAnimated(true);
        xAxis.setLabel("X");
        yAxis.setLabel("Y");
        sc.setTitle("Algorytm Kohonena");

        XYChart.Series<Number,Number> centroids = new XYChart.Series<>();
        centroids.setName("Centroidy");
        for (int i = 0; i < kohonen.getListOfNeurons().size(); i++) {
            Double[] centroid = kohonen.getListOfNeurons().get(i).getWeights();
            centroids.getData().add(new XYChart.Data<>(centroid[0], centroid[1]));
            sc.getData().add(createSeries(kohonen.getListOfNeurons().get(i).getCluster(), String.valueOf(i)));
        }
        sc.getData().add(centroids);

        Scene scene  = new Scene(sc, 1000, 750);
        diagramStage.setScene(scene);
        diagramStage.show();
        saveChart(sc,"Kohonen_diagram.png");
    }

    private XYChart.Series<Number, Number> createSeries(List<Double[]> points, String numCluster){
        XYChart.Series<Number, Number> cluster = new XYChart.Series<>();
        cluster.setName("Klaster " + numCluster);
        for(Double[] point: points){
            cluster.getData().add(new XYChart.Data<>(point[0],point[1]));
        }
        return cluster;
    }

}

