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
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
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

    //Strings
    private String name;
    private String original_name;
    private String state;
    private String room;

    //Integer
    private int connectedPlayers;

    //Controllers
    private GameBoardMain gameboard;
    private GameRoomJoin client;
    private MainServer server;

    //GUI
    private Stage stage;

    //ArrayList
    private ArrayList<Label> listNames;
    private ArrayList<Label> listReady = new ArrayList<Label>();

    //FXML
    @FXML TextField room_input;
    @FXML TextField room_chat_input;
    @FXML TextArea room_chat;
    @FXML VBox JoinedRoom;
    @FXML VBox JoinRoom;
    @FXML Button return_lobby;
    @FXML AnchorPane join_pane;
    @FXML private Button ready_button;
    @FXML private ListView<HBox> ListViewPlayers;

    //Constructor
    public GameRoomJoinController(MainServer server, String name, Stage stage, String state)
    {
        this.server = server;
        this.name = name;
        this.original_name = name;
        this.stage = stage;
        this.state = state;

    }


    //Methods
        //Ready
        final CountDownLatch latch = new CountDownLatch(1);
        public void GameBoard_Ready(String[][] matrix)
        {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try
                    {
                        FXMLLoader loader = new FXMLLoader(Main.class.getResource("gameboard.fxml"));
                        gameboard = new GameBoardMain(name,"client",client,matrix, server);
                        loader.setController(gameboard);
                        Scene loadscene = new Scene(loader.load(),1024,768);
                        gameboard.setscene(loadscene);
                        stage.setScene(loadscene);
                        stage.show();
                        latch.countDown();
                        gameboard.focus();
                    }catch(IOException IOE)
                    {
                        IOE.getMessage();
                    }
                }
            });

        }

        //Players list...
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

        //Waiting players...
        public void GameBoard_WaitingPlayers()
        {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    gameboard.WaitingPlayers();
                }
            });
        }

        //Ready [Waiting]
        public void GameBoard_WaitingPlayersReady()
        {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    gameboard.WaitingPlayersReady();
                }
            });
        }

        //Player moving...
        public void GameBoard_PlayerMovement(double x, double y, String p_name)
        {
            gameboard.setPlayerMovement(x,y, p_name);
        }

        //New gui gameboard table...
        public void GameBoard_NEW(String[][] matrix)
        {
            gameboard.setMatrix(matrix);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    gameboard.GameBoardGrid();
                }
            });

        }

        //If join room
        public void joinroom(String ip)
        {
            this.client = new GameRoomJoin(this,room, name, ip);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    ready_button.setText("Nem állok készen!");
                    ready_button.setStyle("-fx-background-color: red;");
                }
            });

        }

        //Room client gui
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

        //Players gui add [TODO - fix]
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
                hbox.setVisible(false);
                //Name
                    Label l = new Label("");
                    l.setFont(new Font("Verdana",18));
                    hbox.setAlignment(Pos.CENTER);
                    hbox.getChildren().add(l);
                    this.listNames.add(l);
                //Ready
                    l = new Label("");
                    l.setPrefSize(10,10);
                    l.setStyle(i == 0 ? "-fx-background-color:green;" : "-fx-background-color:red");
                    l.setVisible(true);
                    hbox.getChildren().add(l);
                    this.listReady.add(l);

                this.ListViewPlayers.getItems().add(hbox);
            }
        }

        //Player add
        public void addPlayer(User p)
        {
            Platform.runLater(() ->
            {
                this.listNames.get(this.connectedPlayers).setText(p.getname());
                this.ListViewPlayers.getItems().get(this.connectedPlayers).setVisible(true);
                this.connectedPlayers++;
            });
        }

        //Player remove
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

                this.ListViewPlayers.getItems().get(this.connectedPlayers-1).setVisible(false);
                this.listNames.get(this.connectedPlayers-1).setText("");
                this.connectedPlayers--;
            });
        }

        //Update player list
        public void updatePlayersList(List<User> players)
        {
            Platform.runLater(()->
            {
                    for(int i=0; i < players.size(); ++i)
                    {
                        User p = players.get(i);
                        this.listNames.get(i).setText(p.getname());
                        this.ListViewPlayers.getItems().get(i).setVisible(true);
                        this.listReady.get(i).setStyle(p.is_ready_check() ? "-fx-background-color: green" : "-fx-background-color: red");

                    }
                    this.connectedPlayers = players.size();
            });
        }

        //Room input txt
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

        //Room chat => send mg
        public void send_chat_msg()
        {
            String msg = this.room_chat_input.getText();
            if(!msg.isEmpty())
            {
                this.client.MsgSend(msg);
            }
            this.room_chat_input.setText("");
        }

        //Disconnect...
        public void CloseConnection()
        {
            if(this.client != null)
            {
                this.client.Close();
            }

        }

        //Connect fail... [TODO - fix]
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

    public void return_lobby(String msg)
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
                    gamelobby.lobby_input_text("[ÜZENET]: " + msg);
                }
            });

        }catch(IOException IOE)
        {
            ;
        }
    }

        //Player Ready
        public void updateReady(String name, boolean ready)
        {
            for (int i=0; i < this.ListViewPlayers.getItems().size(); i++)
            {
                if(name.equals(this.listNames.get(i).getText()))
                {
                    this.listReady.get(i).setStyle(ready ? "-fx-background-color: green;" : "-fx-background-color: red;");
                    break;
                }
            }
        }


    //Initialize...
    public void initialize(URL url, ResourceBundle rs)
    {
        //Status set [Mainserver => "client"]
            server.setStatus("client");

        //FXML panel visibility
            JoinRoom.setVisible(true);
            JoinedRoom.setVisible(false);

        //Set controller
            server.setJoinController(this);

        //Room join input
            room_input.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if(keyEvent.getCode().equals(KeyCode.ENTER))
                {
                    room = room_input.getText();
                    if(!room.isEmpty())
                    {
                        server.RoomJoin(room);
                    }
                }}});

        //Close window
            stage.setOnCloseRequest(new EventHandler<WindowEvent>()
            {
                @Override
                public void handle(WindowEvent event)
                {
                    CloseConnection();
                }
            });

        //Room chat input
            room_chat_input.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if(keyEvent.getCode().equals(KeyCode.ENTER))
                {
                    send_chat_msg();
                }
            }
        });

        //Return lobby [TODO - fix problem...]
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

        //Ready button
            ready_button.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if(ready_button.getText().equals("Nem állok készen!"))
                    {
                        ready_button.setText("Készenállok!");
                        ready_button.setStyle("-fx-background-color: green;");
                        client.sendReady(true);
                        updateReady(original_name,true);
                    }
                    else
                    {
                        ready_button.setText("Nem állok készen!");
                        ready_button.setStyle("-fx-background-color: red;");
                        client.sendReady(false);
                        updateReady(original_name,false);
                    }

                }
            });
    }

}
