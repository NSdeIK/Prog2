package com.ns_deik.ns_client.homepage;

import com.ns_deik.ns_client.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable
{
    //Windows size
        private String[] window_size = {"1024x768","1280x1024","1366x768","1600x900","1920x1080","2560x1440"};
        private ObservableList<String> windows_size_list = FXCollections.observableArrayList(window_size);

    //FXML
        @FXML private Stage stage;
        @FXML private ChoiceBox<String> windows_size = new ChoiceBox<>(windows_size_list);

    //Return homepage scene
    @FXML
    public void homepage_return(ActionEvent event) throws IOException
    {
        FXMLLoader homepage_return = new FXMLLoader(Main.class.getResource("homepage_fxml.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene_return = new Scene(homepage_return.load(),300,480);
        HomepageController homepageController = homepage_return.<HomepageController>getController();
        stage.setTitle("[NS-DEIK] Kezdőlap");
        stage.setScene(scene_return);
        stage.show();
    }


    //If windows size is selected
    @FXML
    public void windows_size_selected() throws IOException
    {
        //Choosed item => get item index
        int indexnew = windows_size.getSelectionModel().getSelectedIndex();

        //File create
        File UserSettings = new File("usersettings.txt");

        //Check file is exists
        if(UserSettings.exists())
        {
            //File writer is on
            FileWriter newUserSettings = new FileWriter("usersettings.txt");

            //Write [int to string => index value]
            newUserSettings.write(String.valueOf(indexnew));

            //Flush / Close
            newUserSettings.flush();
            newUserSettings.close();
        }
        else
        {
            ;
        }
    }

    //Initialize => if empty => new file => if selected window size [save file]
    //           => if not empty => read file => choosed window size show [ChoiceBox]
    @Override
    public void initialize(URL url, ResourceBundle rs) {
        //Storing index value
        int index = 0;

        //File reading
        try ( BufferedReader FileReader = new BufferedReader(new FileReader(new File("usersettings.txt")));)
        {
            //String line
            String line;

            //File reading != null (Line variable)
            while((line = FileReader.readLine()) != null)
            {
                //Converting string to int
                index = Integer.parseInt(line);
            }
        }
        catch(IOException io)
        {
            io.printStackTrace();
        }

        //Set items with obserable list
        windows_size.setItems(windows_size_list);

        //Set choosed item [choosed || default (0)]
        windows_size.getSelectionModel().select(index);
    }
}
