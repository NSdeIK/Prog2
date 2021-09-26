package com.ns_deik.ns_client.gameroom.room_join;

import com.ns_deik.ns_client.gameroom.Data;
import com.ns_deik.ns_client.gameroom.DataType;
import com.ns_deik.ns_client.gameroom.User;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class GameRoomJoinController implements Initializable {

    private Stage stage;
    private Scene scene;
    private Socket socket;
    private String name;
    private String state;
    GameRoomJoin client;
    String room;

    @FXML
    TextField room_input;
    @FXML
    TextField room_chat_input;
    @FXML
    TextArea room_chat;
    @FXML
    VBox JoinedRoom;
    @FXML
    VBox JoinRoom;

    @FXML
    private ListView<HBox> ListViewPlayersC;

    private ArrayList<Label> listNames;

    private int connectedPlayers;

    public GameRoomJoinController(Socket socket, String name, Stage stage, String state)
    {
        this.socket = socket;
        this.name = name;
        this.stage = stage;
        this.state = state;

    }

    public void initialize(URL url, ResourceBundle rs)
    {
        JoinRoom.setVisible(true);
        JoinedRoom.setVisible(false);
        room_input.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if(keyEvent.getCode().equals(KeyCode.ENTER))
                {
                    room = room_input.getText();
                    joinroom();
                }
            }
        });

        stage.setOnCloseRequest(new EventHandler<WindowEvent>()
        {
            @Override
            public void handle(WindowEvent event)
            {
                CloseConnection();
            }
        });

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

    public void joinroom()
    {
        this.client = new GameRoomJoin(this,room, name);
    }

    public void room_client() throws IOException
    {
        stage.setTitle("[NS-DEIK] Játék - [" + room + "]");
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.show();
        players_gui();
    }

    public void players_gui()
    {
        JoinRoom.setVisible(false);
        JoinedRoom.setVisible(true);

        this.listNames = new ArrayList<Label>();
        for(int i = 0; i < 8; ++i)
        {
            HBox hbox = new HBox();
            hbox.setPrefSize(200,37);
            hbox.setSpacing(5);
            Label l = new Label("");
            l.setFont(new Font("Verdana",18));
            hbox.setAlignment(Pos.CENTER);
            hbox.getChildren().add(l);
            this.listNames.add(l);
            this.ListViewPlayersC.getItems().add(hbox);
        }
    }

    public void addPlayer(User p)
    {
        Platform.runLater(() ->
        {
            this.listNames.get(this.connectedPlayers).setText(p.getname());
            this.ListViewPlayersC.getItems().get(this.connectedPlayers-1).setVisible(true);
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
                    System.out.println(playername);
                    find = true;
                }
            }

            this.ListViewPlayersC.getItems().get(this.connectedPlayers-1).setVisible(false);
            this.listNames.get(this.connectedPlayers-1).setText("");
            this.connectedPlayers--;
        });
    }

    public void updatePlayersList(List<User> players)
    {
        Platform.runLater(()->
        {
            if(state == "rj_client")
            {
                for(int i=0; i < players.size(); ++i)
                {
                    User p = players.get(i);
                    this.listNames.get(i).setText(p.getname());
                }
                this.connectedPlayers = players.size();
            }
        });
    }

    public void room_input_text(String text)
    {
        if(this.room_chat.getText().isEmpty())
        {
            this.room_chat.setText(text);
        }
        else
        {
            this.room_chat.appendText("\n" + text);
        }
    }

    public void room_input_text(Data data)
    {
        this.room_input_text(data.getName() + ": " + data.getContent());
    }

    public void send_chat_msg()
    {
        String msg = this.room_chat_input.getText();
        if(!msg.isEmpty())
        {
            this.client.MsgSend(msg);
        }
        this.room_chat_input.setText("");
    }
    public void CloseConnection()
    {
        this.client.Close();
    }

}
