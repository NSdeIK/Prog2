package com.ns_deik.ns_client.homepage;

import com.ns_deik.ns_client.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.*;
import java.util.Optional;

public class HomepageController
{

    //JavaFX elements
        private Stage stage;
        private Scene scene;
        private Parent root;

    //Window size [W,H]
        private int width = 1024;
        private int height = 768;


    //If "Game" button
    @FXML
    protected void Game(ActionEvent event) throws IOException
    {
        //Window size default value [choosed]
        int choosed = 0;


        //Storing choosed value [File reading => ...]
        try (BufferedReader FileReader = new BufferedReader(new FileReader(new File("usersettings.txt")));)
        {
            String line;
            while((line = FileReader.readLine()) != null)
            {
                choosed = Integer.parseInt(line);
            }
        }


        //Choosed value switch [manual]
        switch(choosed)
        {
            case 0:
                width = 1024;
                height = 768;
                break;
            case 1:
                width = 1280;
                height = 1024;
                break;
            case 2:
                width = 1366;
                height = 768;
                break;
            case 3:
                width = 1600;
                height = 900;
                break;
            case 4:
                width = 1920;
                height = 1080;
                break;
            case 5:
                width = 2560;
                height = 1440;
                break;
            default:
                width = 1024;
                height = 768;
                break;
        }


        //FXML load
        FXMLLoader GameFXML = new FXMLLoader(Main.class.getResource("game.fxml"));

        //Stage set
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();

        //Window size disabled [ manual size - manual elements x y coords ]
            //GameController gameController = new GameController(width,height,stage);

        //Controller set [1024x768 window size - default]
        GameController gameController = new GameController(stage);
        GameFXML.setController(gameController);

        //Scene disabled with window size [ ... ]
            //Scene scene = new Scene(GameFXML.load(),width,height);

        //Scene set [1024x768 window size - default]
        Scene scene = new Scene(GameFXML.load(),1024,768);

        //Stage settings
        stage.setScene(scene);
        stage.setTitle("[NS-DEIK] Játék");
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.show();

    }


    //If "Settings" button
    @FXML
    public void Settings(ActionEvent event) throws IOException
    {
        //Check file is exist [yes - do nothing, no - create file]
            File UserSettings = new File("usersettings.txt");
            if(UserSettings.exists())
            {
                ;
            }
            else
            {
                FileWriter newUserSettings = new FileWriter("usersettings.txt");
                newUserSettings.write("0"); //Default value
                newUserSettings.close();
            }

        //Settings fxml | Stage | Scene | other settings
        FXMLLoader SettingsFxml = new FXMLLoader(Main.class.getResource("settings.fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(SettingsFxml.load(),300,480);
        stage.setScene(scene);
        stage.setTitle("[NS-DEIK] Beállítások");
        stage.show();
    }


    //If "Exit" button
    @FXML
    protected void Exit()
    {
        //New alert warning --> [WARNING type]
        Alert warning = new Alert(Alert.AlertType.WARNING);

        //Set title
        warning.setTitle("Figyelmeztetés");

        //Header text
        warning.setHeaderText("Biztosan akarsz kilépni?");

        //Two buttons [yes, no, "ok"] - Adding 2 two buttons
        ButtonType yesButton = new ButtonType("Igen", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("Nem", ButtonBar.ButtonData.NO);
        warning.getButtonTypes().addAll(yesButton,noButton);

        // "ok" button disabled...
        warning.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);

        //If yes => Exit window | If no => Do nothing
        Optional<ButtonType> result = warning.showAndWait();
        if(result.get() == yesButton)
        {
            System.exit(0);
        }
    }


}
