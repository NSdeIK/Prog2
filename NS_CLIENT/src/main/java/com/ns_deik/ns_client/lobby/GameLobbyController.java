package com.ns_deik.ns_client.lobby;

import com.ns_deik.ns_client.Main;
import com.ns_deik.ns_client.gameroom.room_create.GameRoomCreateController;
import com.ns_deik.ns_client.gameroom.room_join.GameRoomJoinController;
import com.ns_deik.ns_client.mainServer.ConnectToServer;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class GameLobbyController implements Initializable
{
    //Client
    private int width,height;
    private Stage stage;
    private Scene scene;

    //Networking
    private ConnectToServer cts;
    private DataInputStream in = null;
    private DataOutputStream out = null;

    //Username
    private String name;

    //FXML
    @FXML
    private StackPane pane;
    @FXML
    private Label name_label;
    @FXML
    public TextArea gamelobbytextarea;
    @FXML
    private TextField gamelobby_input;
    @FXML
    private Button room_create,room_join;

    public GameLobbyController(int width, int height, Stage stage,ConnectToServer cts, String name)
    {
        this.width = width;
        this.height = height;
        this.stage = stage;
        this.cts = cts;
        this.name = name;
    };

    public void username_setLabel()
    {
        name_label.setText(name);
    }

    public void lobby_chat()
    {
        GamelobbyChat lobby_task = new GamelobbyChat(this.cts.getSocket(), this);
        Thread th = new Thread(lobby_task);
        th.start();
    }

   public void initialize(URL url, ResourceBundle rb)
   {
       username_setLabel();
       lobby_chat(); //WARNING - Thread

       //Chat -->Enter chat...
       gamelobby_input.setOnKeyPressed(new EventHandler<KeyEvent>() {
           @Override
           public void handle(KeyEvent keyEvent) {
               try
               {
                   out = new DataOutputStream(cts.getSocket().getOutputStream());
                    if(keyEvent.getCode().equals(KeyCode.ENTER))
                    {
                        String msg = gamelobby_input.getText();
                        if(msg.length() == 0)
                        {
                            ;
                        }
                        else
                        {
                            out.writeUTF("["+name+"]: " + msg +"\n");
                            out.flush();
                        }
                        gamelobby_input.clear();
                    }
               }
               catch(IOException IOE)
               {
                   System.err.println(IOE);
               }
           }
       });

       //Room create
       room_create.setOnMousePressed(new EventHandler<MouseEvent>() {
           @Override
           public void handle(MouseEvent event) {
               try {
               FXMLLoader GameRoomCreate = new FXMLLoader(Main.class.getResource("gameroomcreate.fxml"));

               GameRoomCreateController roomcreatectrl = new GameRoomCreateController(cts.getSocket(), name, stage, "rc_server");
               GameRoomCreate.setController(roomcreatectrl);

               scene = new Scene(GameRoomCreate.load(),1024,768);
               stage.setScene(scene);
               stage.setTitle("[NS-DEIK] Játék - Szoba készítés");
               stage.centerOnScreen();
               stage.setResizable(false);
               stage.show();

               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
       });

       room_join.setOnMousePressed(new EventHandler<MouseEvent>() {
           @Override
           public void handle(MouseEvent event) {
               try
               {
                   FXMLLoader GameRoomJoin = new FXMLLoader(Main.class.getResource("gameroomjoin.fxml"));

                   GameRoomJoinController roomjoinctrl = new GameRoomJoinController(cts.getSocket(), name, stage, "rj_client");
                   GameRoomJoin.setController(roomjoinctrl);

                   scene = new Scene(GameRoomJoin.load(),1024,768);
                   stage.setScene(scene);
                   stage.setTitle("[NS-DEIK] Játék - Szoba készítés");
                   stage.centerOnScreen();
                   stage.setResizable(false);
                   stage.show();
               }catch(IOException e)
               {
                   e.printStackTrace();
               }

           }
       });
   }
}
