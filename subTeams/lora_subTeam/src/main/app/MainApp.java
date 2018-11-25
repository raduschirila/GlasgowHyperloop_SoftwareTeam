package main.app;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import main.app.Controller;

public class MainApp extends Application {

    //private int xSeriesData = 0;
    //private XYChart.Series series1;
    private ConcurrentLinkedQueue<Number> dataQ1 = new ConcurrentLinkedQueue<Number>();

    @Override public void start(Stage stage) throws Exception {
        //URL url = new File("src/main/resources/FXMLGui.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(getClass().getResource("FXMLGui.fxml"));

        stage.setScene(new Scene(root));
        stage.setTitle("Hyperloop GUI");

        //init(stage);
        stage.show();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent we) {
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}