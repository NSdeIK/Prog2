package com.ns_deik.ns_client.homepage;

import com.lambdaworks.crypto.SCryptUtil;
import com.ns_deik.ns_client.lobby.GameLobbyController;
import com.ns_deik.ns_client.Main;
import com.ns_deik.ns_client.mainServer.MainServer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.w3c.dom.Text;

import java.io.*;
import java.net.*;
import java.net.URL;
import java.util.ResourceBundle;


public class GameController implements Initializable
{

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
    @FXML
    VBox login_vbox;
    @FXML
    VBox register_vbox;

    @FXML
    Button jumplogin;

    @FXML
    Button userlogin;



    @FXML
    Button jumpregister;
    @FXML
    Button userregister;
    @FXML
    TextField reg_user_input;
    @FXML
    PasswordField reg_password_input;
    @FXML
    TextField login_user_input;
    @FXML
    PasswordField login_password_input;

    @FXML
    TextField username_input;



    public void initialize(URL url, ResourceBundle rs)
    {
        client = new MainServer(this,name);
        /*username_input.setOnKeyPressed(new EventHandler<KeyEvent>() {
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
        });*/

        userregister.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                    name = reg_user_input.getText();
                    String password = reg_password_input.getText();
                    if(name.isEmpty() || password.isEmpty())
                    {
                        System.out.println("Warning... user/password missing?");

                    }
                    else
                    {
                        client.RegisterMSG(name, password);
                        System.out.println(name + "-" + password);
                    }


            }
        });

        userlogin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                name = login_user_input.getText();
                String password = login_password_input.getText();
                if(name.isEmpty() || password.isEmpty())
                {
                    System.out.println("Warning... user/password missing?");

                }
                else
                {
                    client.LoginMSG(name, password);
                }
            }
        });

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
                    name = login_user_input.getText();
                    client.setName(name);
                    //Stage stage = (Stage)((Node)jumplogin.getSource()).getScene().getWindow();
                    FXMLLoader GameLobbyFXML = new FXMLLoader(Main.class.getResource("gamelobby.fxml"));
                    //GameLobbyController gamelobby = new GameLobbyController(width,height,stage,conn_to_srv,name);
                    GameLobbyController gamelobby = new GameLobbyController(name,stage, client);
                    GameLobbyFXML.setController(gamelobby);
                    //scene = new Scene(GameLobbyFXML.load(),width,height);
                    scene = new Scene(GameLobbyFXML.load(),1024,768);

                    stage.setScene(scene);
                    stage.setTitle("[NS-DEIK] Játék - Lobby");
                    stage.show();
                }catch (IOException e)
                {
                    ;
                }
            }
        });

    }
}


