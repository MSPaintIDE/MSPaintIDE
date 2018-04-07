package com.uddernetworks.mspaint.main;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Test extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("MS Paint IDE");
        primaryStage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("ms-paint-logo.png")));


        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Test.fxml"));
        Scene scene = new Scene(root);

        primaryStage.setScene(scene);

        primaryStage.show();


        TextArea node = (TextArea) scene.lookup("#output");
        node.setText("Text here");

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < 100; i++) {
            builder.append("Line #" + i).append("\n");
        }

        node.setText(builder.toString());
    }

    @FXML
    private void handleButtonClick(ActionEvent event) {
        System.out.println("event = " + event);
    }
}
