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
    private Stage stage;
    private Scene scene;
    private Parent root;

    private int width = 1024;
    private int height = 768;



    @FXML
    protected void Game(ActionEvent event) throws IOException
    {
        int choosed = 0;

        try (BufferedReader FileReader = new BufferedReader(new FileReader(new File("usersettings.txt")));)
        {
            String line;
            while((line = FileReader.readLine()) != null)
            {
                choosed = Integer.parseInt(line);
            }
        }

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


        FXMLLoader GameFXML = new FXMLLoader(Main.class.getResource("game.fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        //GameController gameController = new GameController(width,height,stage);
        GameController gameController = new GameController();
        GameFXML.setController(gameController);

        //Scene scene = new Scene(GameFXML.load(),width,height);  //Soon...
        Scene scene = new Scene(GameFXML.load(),1024,768);

        stage.setScene(scene);
        stage.setTitle("[NS-DEIK] Játék");
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.show();

    }

    @FXML
    public void Settings(ActionEvent event) throws IOException
    {

            File UserSettings = new File("usersettings.txt");
            if(UserSettings.exists())
            {
                ;
            }
            else
            {
                FileWriter newUserSettings = new FileWriter("usersettings.txt");
                newUserSettings.write("0");
                newUserSettings.close();
            }

        FXMLLoader SettingsFxml = new FXMLLoader(Main.class.getResource("settings.fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(SettingsFxml.load(),300,480);
        stage.setScene(scene);
        stage.setTitle("[NS-DEIK] Beállítások");
        stage.show();
    }

    @FXML
    protected void Exit()
    {
        Alert warning = new Alert(Alert.AlertType.WARNING);
        warning.setTitle("Figyelmeztetés");
        warning.setHeaderText("Biztosan akarsz kilépni?");
        ButtonType yesButton = new ButtonType("Igen", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("Nem", ButtonBar.ButtonData.NO);
        warning.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
        warning.getButtonTypes().addAll(yesButton,noButton);

        Optional<ButtonType> result = warning.showAndWait();
        if(result.get() == yesButton)
        {
            System.exit(0);
        }
    }


}
