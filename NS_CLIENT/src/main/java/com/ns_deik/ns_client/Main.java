package com.ns_deik.ns_client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private Scene HomePageScene;

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader HomepageFXML = new FXMLLoader(Main.class.getResource("homepage_fxml.fxml"));
        HomePageScene = new Scene(HomepageFXML.load(), 300,480);
        stage.getIcons().add(new Image("icon_new.png"));
        stage.setTitle("[NS_DEIK] - Kezdőlap");
        stage.setScene(HomePageScene);
        stage.setResizable(false);
        stage.show();
    }



    public static void main(String[] args) {
        launch();
    }
}




















