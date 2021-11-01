package com.ns_deik.ns_client.gameroom.room_join;

import com.ns_deik.ns_client.Main;
import com.ns_deik.ns_client.gameroom.Data;
import com.ns_deik.ns_client.gameroom.GameBoardMain;
import com.ns_deik.ns_client.gameroom.User;
import com.ns_deik.ns_client.lobby.GameLobbyController;
import com.ns_deik.ns_client.mainServer.MainServer;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

public class GameRoomJoinController implements Initializable {

    private Stage stage;
    private String name;
    private String state;
    private GameBoardMain gameboard;
    GameRoomJoin client;
    MainServer server;
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
    Button return_lobby;
    @FXML
    AnchorPane join_pane;

    @FXML
    private ListView<HBox> ListViewPlayersC;

    private ArrayList<Label> listNames;

    private int connectedPlayers;

    public GameRoomJoinController(MainServer server, String name, Stage stage, String state)
    {
        this.server = server;
        this.name = name;
        this.stage = stage;
        this.state = state;

    }

    final CountDownLatch latch = new CountDownLatch(1);

    public void GameBoard_Ready(char[][] matrix)
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try
                {
                    FXMLLoader loader = new FXMLLoader(Main.class.getResource("gameboard.fxml"));
                    gameboard = new GameBoardMain(name,"client",client,matrix);
                    loader.setController(gameboard);

                    Scene loadscene = new Scene(loader.load(),1024,768);
                    gameboard.setscene(loadscene);
                    stage.setScene(loadscene);
                    stage.show();

                    latch.countDown();
                    gameboard.focus();
                }catch(IOException IOE)
                {
                    ;
                }
            }
        });

    }

    public void GameBoard_Players(ArrayList playerslist)
    {
        try {
            latch.await();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    gameboard.setPlayers(playerslist);
                    gameboard.GameBoardPlayers();
                }
            });
        }catch(InterruptedException IE)
        {
            ;
        }
    }

    public void GameBoard_WaitingPlayers()
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                gameboard.WaitingPlayers();
            }
        });
    }

    public void GameBoard_WaitingPlayersReady()
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                gameboard.WaitingPlayersReady();
            }
        });
    }

    public void GameBoard_PlayerMovement(double x, double y, String p_name)
    {
        gameboard.setPlayerMovement(x,y, p_name);
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

        return_lobby.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try{

                    Stage stage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
                    FXMLLoader GameLobbyFXML = new FXMLLoader(Main.class.getResource("gamelobby.fxml"));
                    GameLobbyController gamelobby = new GameLobbyController(name,stage, server);
                    GameLobbyFXML.setController(gamelobby);

                    Scene scene = new Scene(GameLobbyFXML.load(),1024,768);

                    stage.setScene(scene);
                    stage.setTitle("[NS-DEIK] Játék - Lobby");
                    stage.show();
                }catch (IOException IOE)
                {
                    ;
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
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                stage.setTitle("[NS-DEIK] Játék - [" + room + "]");
                stage.centerOnScreen();
                stage.setResizable(false);
                stage.show();
                players_gui();
            }
        });

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
        if(this.client != null)
        {
            this.client.Close();
        }

    }

    public void connect_failed()
    {
        try
        {
            FXMLLoader GameLobbyFXML = new FXMLLoader(Main.class.getResource("gamelobby.fxml"));
            GameLobbyController gamelobby = new GameLobbyController(name,stage, server);
            GameLobbyFXML.setController(gamelobby);
            Scene scene = new Scene(GameLobbyFXML.load(),1024,768);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    stage.setScene(scene);
                    stage.setTitle("[NS-DEIK] Játék - Lobby");
                    stage.show();
                    gamelobby.lobby_input_text("[HIBA] Sikertelen szoba csatlakozása!");
                }
            });

        }catch(IOException IOE)
        {
            ;
        }

    }


}
