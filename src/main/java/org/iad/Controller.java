package org.iad;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.DoublePredicate;

import javafx.fxml.FXML;

public class Controller {


    List<Double[]> readData(String path){
        List<Double[]> data = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
            String line;
            while((line = reader.readLine())!=null ){
                data.add(Arrays.stream(reader.readLine().split(",")).map(Double::valueOf).toArray(Double[]::new));
            }
            reader.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
