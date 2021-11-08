package com.ns_deik.ns_client.lobby;

import Server.MainData;
import com.ns_deik.ns_client.Main;
import com.ns_deik.ns_client.gameroom.room_create.GameRoomCreateController;
import com.ns_deik.ns_client.gameroom.room_join.GameRoomJoinController;
import com.ns_deik.ns_client.mainServer.MainServer;
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
import javafx.stage.WindowEvent;

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
    private DataInputStream in = null;
    private DataOutputStream out = null;
    private MainServer client;

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
    private Button room_create,room_join,exit_button;


    public GameLobbyController(String name,Stage stage ,MainServer client){
        this.name = name;
        this.stage = stage;
        this.client = client;
    };

    public void username_setLabel()
    {
        name_label.setText(name);
    }

    public void send_chat_msg(String msg)
    {
        if(!msg.isEmpty())
        {
            this.client.MsgSend(msg);
        }
        gamelobbytextarea.appendText(name + ": "+ msg +"\n");
    }

    public void lobby_input_text(String text)
    {
            this.gamelobbytextarea.appendText(text + "\n");
    }

    public void lobby_input_text(MainData data)
    {
        this.lobby_input_text("\n" + data.getName() + ": " + data.getContent());
    }

    private void exit()
    {
        this.client.Exit();
    }

   public void initialize(URL url, ResourceBundle rb)
   {
       username_setLabel();

       gamelobby_input.setOnKeyPressed(new EventHandler<KeyEvent>() {
           @Override
           public void handle(KeyEvent keyEvent) {

               if(keyEvent.getCode().equals(KeyCode.ENTER))
               {
                   String msg = gamelobby_input.getText();
                   if(msg.length() == 0)
                   {
                       ;
                   }
                   else
                   {
                       send_chat_msg(msg);
                   }
                   gamelobby_input.clear();
               }
           }
       });

       //Room create
       room_create.setOnMousePressed(new EventHandler<MouseEvent>() {
           @Override
           public void handle(MouseEvent event) {
               try {
               FXMLLoader GameRoomCreate = new FXMLLoader(Main.class.getResource("gameroomcreate.fxml"));

               GameRoomCreateController roomcreatectrl = new GameRoomCreateController(client, name, stage, "rc_server");
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

                   GameRoomJoinController roomjoinctrl = new GameRoomJoinController(client, name, stage, "rj_client");
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

       stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
           @Override
           public void handle(WindowEvent windowEvent) {
               exit();
               System.exit(0);
           }
       });

       exit_button.setOnMousePressed(new EventHandler<MouseEvent>() {
           @Override
           public void handle(MouseEvent mouseEvent) {
               exit();
               System.exit(0);
           }
       });
   }
}
