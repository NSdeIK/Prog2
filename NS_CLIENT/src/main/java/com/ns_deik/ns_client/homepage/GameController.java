package com.ns_deik.ns_client.homepage;

import com.ns_deik.ns_client.lobby.GameLobbyController;
import com.ns_deik.ns_client.Main;
import com.ns_deik.ns_client.mainServer.MainServer;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.net.URL;
import java.util.ResourceBundle;


public class GameController implements Initializable
{


    //Thread
    private Thread th;

    //Networking
    private static boolean network = false;
    private boolean client_closed = false;
    private InetAddress ip = null;
    private MainServer client;

    //Client
    private Stage stage;
    private Scene scene;
    private int width,height;

    //Username
    private String name;

    //Images
    private Image green_img = new Image("green_circle.png");
    private Image red_img = new Image("red_circle.png");
    private ImageView green_status = new ImageView();
    private ImageView red_status = new ImageView();

    //FXML
    @FXML
    HBox server_on;
    @FXML
    HBox server_off;
    @FXML
    TextField username_input;


    public void initialize(URL url, ResourceBundle rs)
    {
        username_input.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if(keyEvent.getCode().equals(KeyCode.ENTER))
                {
                    try
                    {
                        name = username_input.getText();
                        System.out.println(name);
                        Stage stage = (Stage)((Node)keyEvent.getSource()).getScene().getWindow();
                        FXMLLoader GameLobbyFXML = new FXMLLoader(Main.class.getResource("gamelobby.fxml"));
                        //GameLobbyController gamelobby = new GameLobbyController(width,height,stage,conn_to_srv,name);
                        GameLobbyController gamelobby = new GameLobbyController(name,stage);
                        GameLobbyFXML.setController(gamelobby);
                        //scene = new Scene(GameLobbyFXML.load(),width,height);
                        scene = new Scene(GameLobbyFXML.load(),1024,768);

                        stage.setScene(scene);
                        stage.setTitle("[NS-DEIK] Játék - Lobby");
                        stage.show();
                    }
                    catch(IOException IO)
                    {
                        IO.printStackTrace();
                    }
                }
            }
        });
    }

}


