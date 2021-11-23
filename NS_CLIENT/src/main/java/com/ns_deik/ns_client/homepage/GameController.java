package com.ns_deik.ns_client.homepage;

import com.ns_deik.ns_client.lobby.GameLobbyController;
import com.ns_deik.ns_client.Main;
import com.ns_deik.ns_client.mainServer.MainServer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.net.URL;
import java.util.ResourceBundle;


public class GameController implements Initializable
{

    //Constructor
    GameController(Stage stage)
    {
        this.stage = stage;
    }

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

    //FXML
    @FXML VBox login_vbox;
    @FXML VBox register_vbox;
    @FXML VBox reconnecting;
    @FXML Button jumplogin;
    @FXML Button userlogin;
    @FXML Button jumpregister;
    @FXML Button userregister;
    @FXML TextField reg_user_input;
    @FXML PasswordField reg_password_input;
    @FXML TextField login_user_input;
    @FXML PasswordField login_password_input;
    @FXML Text error_login;

    public void initialize(URL url, ResourceBundle rs)
    {
        //Connect server
        client = new MainServer(this,name);

        //Style

        //User create
        userregister.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                    //Set name value
                    name = reg_user_input.getText();

                    //Set password value
                    String password = reg_password_input.getText();

                    //Check two value is empty?
                    if(name.isEmpty() || password.isEmpty())
                    {
                        System.out.println("Warning... user/password missing?");
                        // TODO - gui warning
                    }
                    else
                    {
                        client.RegisterMSG(name, password);
                    }


            }
        });

        //User login
        userlogin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //Set name
                name = login_user_input.getText();

                //Set password
                String password = login_password_input.getText();

                //Check two value is empty?
                if(name.isEmpty() || password.isEmpty())
                {
                    error_login.setText("Rossz felhasználónév, vagy jelszó...");
                }
                else
                {
                    client.LoginMSG(name, password);
                }
            }
        });

        //Jump to register/login panel
        jumpregister.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                jumpRegisterPanel();
            }
        });

        jumplogin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
               jumpLoginPanel();
            }
        });

    }


    //Jump to login/register/lobby panel

    public void jumpLoginPanel()
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                login_vbox.setVisible(true);
                register_vbox.setVisible(false);
            }
        });
    }

    public void jumpRegisterPanel()
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                login_vbox.setVisible(false);
                register_vbox.setVisible(true);
            }
        });
    }

    public void jumpLobbyPanel()
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try
                {
                    //Set name
                    name = login_user_input.getText();

                    //Server change "" to original name
                    client.setName(name);

                    //FXML
                    FXMLLoader GameLobbyFXML = new FXMLLoader(Main.class.getResource("gamelobby.fxml"));

                    //Controller
                    GameLobbyController gamelobby = new GameLobbyController(name,stage, client);
                    GameLobbyFXML.setController(gamelobby);

                    //Settings...
                    scene = new Scene(GameLobbyFXML.load(),1024,768);
                    stage.setScene(scene);
                    stage.setTitle("[NS-DEIK] Játék - Lobby");
                    stage.show();

                }catch (IOException e)
                {


                }
            }
        });

    }

    //If server is down
    public void reconnect() {
        login_vbox.setVisible(false);
        reconnecting.setVisible(true);
        client.reconnect();
    }

    public void reconnect_success()
    {
        login_vbox.setVisible(true);
        reconnecting.setVisible(false);
    }

    //Bad login
    public void bad_login(String error)
    {
        error_login.setText(error);
    }
}


