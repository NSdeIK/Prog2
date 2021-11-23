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
    //Client [Width & Height | Stage | Scene]
        private int width,height;
        private Stage stage;
        private Scene scene;


    //Networking
        private DataInputStream in = null;
        private DataOutputStream out = null;
        private MainServer server;


    //Username
        private String name;


    //FXML
        @FXML private StackPane pane;
        @FXML private Label name_label;
        @FXML public TextArea gamelobbytextarea;
        @FXML private TextField gamelobby_input;
        @FXML private Button room_create,room_join,exit_button;


    //Constructor [Set => name | stage | server]
    public GameLobbyController(String name,Stage stage ,MainServer client){
        this.name = name;
        this.stage = stage;
        this.server = client;
        client.setLobbyController(this);
    };


    //Set methods
        //Username
        public void username_setLabel()
    {
        name_label.setText(name);
    }


    //Lobby Chat [ Send | AppendText ]

        //Send msg [if not empty] => Broadcast all players & AppendText [text]
        public void send_chat_msg(String msg)
        {
            if(!msg.isEmpty())
            {
                this.server.MsgSend(msg);
            }
            gamelobbytextarea.appendText(name + ": "+ msg +"\n");
        }

        //Receive msg from other player => AppendText [text]
        public void lobby_input_text(String text)
    {
            this.gamelobbytextarea.appendText(text + "\n");
    }
            //Receive msg [Network packet]
            public void lobby_input_text(MainData data)
            {
                this.lobby_input_text(data.getName() + ": " + data.getContent());
            }


    //If disconnecting => send server msg [ DISCONNECT ]
    private void exit()
    {
        this.server.Exit();
    }


   public void initialize(URL url, ResourceBundle rb)
   {

       //The login is successful [Profile name set]
            username_setLabel();


       //If keyboard is detected
            //Lobby Chat input...
            gamelobby_input.setOnKeyPressed(new EventHandler<KeyEvent>() {
           @Override
           public void handle(KeyEvent keyEvent) {

               if(keyEvent.getCode().equals(KeyCode.ENTER))
               {
                   //Get text
                   String msg = gamelobby_input.getText();

                   //If msg is empty, do nothing
                   if(msg.length() == 0)
                   {
                       ;
                   }
                   else
                   {
                       //Send chat void [ msg => packet => server => broadcast => other players]
                       send_chat_msg(msg);
                   }

                   //Input reset [clear all text]
                   gamelobby_input.clear();
               }
           }
       });

            //Room create
            room_create.setOnMousePressed(new EventHandler<MouseEvent>() {
           @Override
           public void handle(MouseEvent event) {
               try {

               //Room create FXML
               FXMLLoader GameRoomCreate = new FXMLLoader(Main.class.getResource("gameroomcreate.fxml"));

               //Room create controller [new | set]
               GameRoomCreateController roomcreatectrl = new GameRoomCreateController(server, name, stage, "rc_server");
               GameRoomCreate.setController(roomcreatectrl);

               //Scene [set, title, window center, resizable, show]
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

            //Room join
            room_join.setOnMousePressed(new EventHandler<MouseEvent>() {
           @Override
           public void handle(MouseEvent event) {
               try
               {
                   //Room join FXML
                   FXMLLoader GameRoomJoin = new FXMLLoader(Main.class.getResource("gameroomjoin.fxml"));

                   //Room join controller [new | set]
                   GameRoomJoinController roomjoinctrl = new GameRoomJoinController(server, name, stage, "rj_client");
                   GameRoomJoin.setController(roomjoinctrl);

                   //Scene [set, title, window center, resizable, show]
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


       //If windows closing...
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
