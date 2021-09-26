package com.ns_deik.ns_client.homepage;

import com.ns_deik.ns_client.mainServer.ConnectToServer;
import com.ns_deik.ns_client.lobby.GameLobbyController;
import com.ns_deik.ns_client.Main;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

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
    private ConnectToServer conn_to_srv;
    private InetAddress ip = null;

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
    HBox hbox;
    @FXML
    TextField username_input;
    @FXML
    StackPane stackPane;


    GameController(int width,int height, Stage stage)
    {
        this.width = width;
        this.height = height;
        this.stage = stage;
    };

    private void server_status_img_settings()
    {
        green_status.setImage(green_img);
        green_status.setFitWidth(12);
        green_status.setFitHeight(12);
        green_status.setTranslateX(12);
        green_status.setTranslateY(6);

        red_status.setImage(red_img);
        red_status.setFitWidth(12);
        red_status.setFitHeight(12);
        red_status.setTranslateX(12);
        red_status.setTranslateY(6);
    }

    private void server_on_to_off()
    {
        if(hbox.getChildren().contains(green_status))
        {
            hbox.getChildren().remove(green_status);
        }
        if(!hbox.getChildren().contains(red_status))
        {
            hbox.getChildren().add(red_status);
        }
        username_input.setDisable(true);
    }
    private void server_off_to_on()
    {
        if(!hbox.getChildren().contains(green_status))
        {
            hbox.getChildren().add(green_status);
        }
        if(hbox.getChildren().contains(red_status))
        {
            hbox.getChildren().remove(red_status);
        }
        username_input.setDisable(false);
    }

    private void client_off()
    {
        stage.setOnCloseRequest(new EventHandler<WindowEvent>()
        {
            @Override
            public void handle(WindowEvent event)
            {
                client_closed = true;
                try {
                    conn_to_srv.closed_client(true);
                    Platform.exit();
                    System.exit(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void network_getbyname()
    {
        try {
            ip = InetAddress.getByName("192.168.1.114");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private void server_status_check()
    {
        if(client_closed != true)
        {
            conn_to_srv = new ConnectToServer(ip, 6666);
            server_status_img_settings();

            //WARNING - Thread!!!
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    while(true)
                    {
                        if(conn_to_srv.check_server_status() == true)
                        {
                            Platform.runLater(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    server_off_to_on();
                                }
                            });
                        }
                        else
                        {
                            Platform.runLater(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    server_on_to_off();
                                }
                            });
                        }
                        try {
                            Thread.sleep(4000); // 10 sec delay
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

        }
    }

    public void initialize(URL url, ResourceBundle rs)
    {
        client_off();
        network_getbyname();
        server_status_check();

        username_input.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if(keyEvent.getCode().equals(KeyCode.ENTER))
                {
                    try
                    {
                        name = username_input.getText();
                        System.out.println(name);

                        FXMLLoader GameLobbyFXML = new FXMLLoader(Main.class.getResource("gamelobby.fxml"));
                        GameLobbyController gamelobby = new GameLobbyController(width,height,stage,conn_to_srv,name);
                        GameLobbyFXML.setController(gamelobby);

                        scene = new Scene(GameLobbyFXML.load(),width,height);
                        stage.setScene(scene);
                        stage.setTitle("[NS-DEIK] Játék - Lobby");
                        stage.show();
                    }
                    catch(IOException IO)
                    {
                        ;
                    }
                }
            }
        });

    }

}


