package com.ns_deik.ns_client.gameroom.room_create;

import com.ns_deik.ns_client.gameroom.Data;
import com.ns_deik.ns_client.gameroom.User;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class GameRoomCreateController implements Initializable {

    private GRSInterface server;
    private Socket socket;
    private String name = "";
    private String room_code_string = "";
    private String state;
    private Stage stage;
    private ArrayList<Socket> players = new ArrayList<Socket>();

    private int connectedPlayers;

    private static final int max_players = 8;

    @FXML
    private ListView<HBox> ListViewPlayers;
    private ArrayList<Label> listNames;

    @FXML
    private TextArea room_chat;

    @FXML
    private TextField room_chat_input;

    @FXML
    private TextField room_code;

    char code[] = {'0','1','2','3','4','5','6','7','8','9',
                   'A','B','C','D','E','F','G','H','I','J',
                   'K','L','M','N','O','P','Q','R','S','U',
                   'V','W','X','Y','Z'};

    public char randomCode()
    {
        return code[(int) Math.floor(Math.random() *35)];
    }

    public void room_generator()
    {
        int code_length = 5;
        for(int i=0; i < code_length; ++i)
        {
            room_code.appendText(String.valueOf(randomCode()));
        }
    }

    public GameRoomCreateController(Socket socket, String name, Stage stage, String state)
    {
        this.socket = socket;
        this.name = name;
        this.stage = stage;
        this.state = state;
    }

    public void addPlayer(User p)
    {
        Platform.runLater(()->
        {
            this.listNames.get(this.connectedPlayers).setText(p.getname());
            this.ListViewPlayers.getItems().get(this.connectedPlayers).setVisible(true);
            this.connectedPlayers++;
        });
    }

    public void removePlayer(String playername)
    {
        Platform.runLater(() ->
        {
            boolean find = false;
            for(int i = 1; i <this.connectedPlayers; ++i)
            {
                if(find)
                {
                    this.listNames.get(i-1).setText(this.listNames.get(i).getText());
                }
                if(this.listNames.get(i).getText().equals(playername))
                {
                    find = true;
                }
            }

            this.ListViewPlayers.getItems().get(this.connectedPlayers-1).setVisible(false);
            this.listNames.get(this.connectedPlayers-1).setText("");
            this.connectedPlayers--;
        });
    }

    public void room_input(String msg)
    {
        if(this.room_chat.getText().isEmpty())
        {
            this.room_chat.setText(msg);
        }
        else
        {
            this.room_chat.appendText("\n" + msg);
        }
    }

    public void room_input(Data data)
    {
        this.room_input(data.getName() + ": " + data.getContent());
    }

    public void send_chat_msg()
    {
        String msg = this.room_chat_input.getText();
        if(!msg.isEmpty())
        {
            this.server.MsgSend(msg);
        }
        this.room_chat_input.setText("");
    }


    public void initialize(URL url, ResourceBundle rb)
    {
        this.listNames = new ArrayList<Label>();

        for(int i=0; i < max_players; ++i)
        {
            HBox hbox = new HBox();
            hbox.setSpacing(10);
            Label l = new Label("");
            hbox.getChildren().add(l);
            this.listNames.add(l);

            this.ListViewPlayers.getItems().add(hbox);
        }

        connectedPlayers = 0;

        if(state == "rc_server")
        {
            room_generator();
            room_code_string = room_code.getText();
            this.server = new GameRoomServer(this, this.name, room_code_string);

            this.listNames.get(0).setText(this.name);
            this.ListViewPlayers.getItems().get(0).setVisible(true);

            this.connectedPlayers = 1;
        }

        this.server = new GameRoomServer(this, this.name, room_code_string);


        room_chat_input.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if(keyEvent.getCode().equals(KeyCode.ENTER))
                {
                    send_chat_msg();
                }
            }
        });
    }

}
