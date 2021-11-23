package com.ns_deik.ns_client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private Scene HomePageScene; //Storing scene

    @Override
    public void start(Stage stage) throws IOException {

        //FXML load file
        FXMLLoader HomepageFXML = new FXMLLoader(Main.class.getResource("homepage_fxml.fxml"));

        //Set HomePageScene variable
        HomePageScene = new Scene(HomepageFXML.load(), 300,480);

        //Stage
            //icon
            stage.getIcons().add(new Image("icon_new.png"));

            //title
            stage.setTitle("[NS_DEIK] - Kezdőlap");

            //set scene
            stage.setScene(HomePageScene);

            //resizeable? [true | false => false]
            stage.setResizable(false);

            //show scene
            stage.show();
    }



    public static void main(String[] args) {
        launch();
    }
}




















