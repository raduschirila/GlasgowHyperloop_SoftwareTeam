package main.app;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller implements Initializable {

    @FXML
    private LineChart<Integer, Integer> lineChart1;
    @FXML
    private LineChart<Integer, Integer> lineChart2;
    @FXML
    private Button emergencyStopButton;
    @FXML
    private CheckBox allowStopCheckbox;

    private XYChart.Series series1 = new XYChart.Series<Integer, Integer>();
    private XYChart.Series series2 = new XYChart.Series<Integer, Integer>();

    @FXML
    private void handleAllowStopCheckbox(ActionEvent event) {
        if (allowStopCheckbox.isSelected()) {
            emergencyStopButton.setDisable(false);
        } else {
            emergencyStopButton.setDisable(true);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Thread socketListener = new Thread(new SocketListener());
        socketListener.start();

        series1.setName("Speed");
        series2.setName("Acceleration");

        lineChart1.getData().add(series1);
        lineChart1.setAnimated(false);
        lineChart2.getData().add(series2);
        lineChart2.setAnimated(false);
    }

    public class SocketListener implements Runnable {
        @Override
        public void run() {
            try {
                String hostName = "localhost";
                int portNumber = 5000;

                try (
                        ServerSocket serverSock = new ServerSocket(portNumber);
                        // Socket kkSocket = new Socket(hostName, portNumber);
                ) {
                    while (true) {
                        Socket client = serverSock.accept();
                        String clientAddress = client.getInetAddress().getHostAddress();
                        System.out.println("\r\nNew connection from " + clientAddress + ":" + client.getPort());

                        DataInputStream in = new DataInputStream(new BufferedInputStream(client.getInputStream()));

                        int time = 0;
                        int count;
                        byte[] buffer = new byte[4096]; // or 4096, or more
                        while ((count = in.read(buffer)) > 0)
                        {
                            ByteArrayOutputStream bufferStream = new ByteArrayOutputStream();
                            bufferStream.write(buffer, 0, count);

                            try {
                                // big-endian by default
                                ByteBuffer wrapped = ByteBuffer.wrap(buffer);
                                int timeStamp = wrapped.getInt();
                                int speed = wrapped.getInt();
                                int acc = wrapped.getInt();

                                System.out.println("Hyperloop POD: time (" + timeStamp + "), speed (" + speed + "), acc (" + acc + ");");

                                Platform.runLater(new Runnable(){
                                    @Override public void run() {
                                        series1.getData().add(new XYChart.Data<>(timeStamp, speed));
                                        series2.getData().add(new XYChart.Data<>(timeStamp, acc));
                                    }
                                });

                                ++time;

                            } catch (NumberFormatException e) {
                            }
                        }

                        System.out.println("Client stopped transmitting!");
                    }


                } catch (UnknownHostException e) {
                    System.err.println("Don't know about host " + hostName);
                    System.exit(1);
                } catch (IOException e) {
                    System.err.println("Couldn't get I/O for the connection to " +
                            hostName);
                    System.exit(1);
                }

                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
