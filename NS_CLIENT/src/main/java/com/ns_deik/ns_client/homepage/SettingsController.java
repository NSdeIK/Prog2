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
    @FXML

    private Stage stage;
    private Scene scene;
    private Parent root;

    private String[] window_size = {"1024x768","1280x1024","1366x768","1600x900","1920x1080","2560x1440"};
    private ObservableList<String> windows_size_list = FXCollections.observableArrayList(window_size);

    @FXML
    private ChoiceBox<String> windows_size = new ChoiceBox<>(windows_size_list);

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

    @FXML
    public void windows_size_selected() throws IOException
    {
        int indexnew = windows_size.getSelectionModel().getSelectedIndex();
        System.out.println(indexnew);
        File UserSettings = new File("usersettings.txt");
        if(UserSettings.exists())
        {
            FileWriter newUserSettings = new FileWriter("usersettings.txt");
            newUserSettings.write(String.valueOf(indexnew));
            newUserSettings.flush();
            newUserSettings.close();
        }
        else
        {
            ;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rs) {
        int index = 0;
        try ( BufferedReader FileReader = new BufferedReader(new FileReader(new File("usersettings.txt")));)
        {
            String line;
            while((line = FileReader.readLine()) != null)
            {
                index = Integer.parseInt(line);
                System.out.println(line);
            }
        }
        catch(IOException io)
        {
            io.printStackTrace();
        }

        windows_size.setItems(windows_size_list);
        windows_size.getSelectionModel().select(index);
    }
}
