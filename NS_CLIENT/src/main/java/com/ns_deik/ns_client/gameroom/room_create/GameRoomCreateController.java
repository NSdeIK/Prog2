package com.ns_deik.ns_client.gameroom.room_create;

import com.ns_deik.ns_client.Main;
import com.ns_deik.ns_client.gameroom.Data;
import com.ns_deik.ns_client.gameroom.GameBoardMain;
import com.ns_deik.ns_client.gameroom.User;
import com.ns_deik.ns_client.lobby.GameLobbyController;
import com.ns_deik.ns_client.mainServer.MainServer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

public class GameRoomCreateController implements Initializable {


    //Variables
        //Strings
        private String name = "";                 //Name
        private String room_code_string = "";     //Room code
        private String status;                    //Status [Server | Client]

        //Integer
        private int connectedPlayers;             //Connected players size
        private static final int max_players = 8; //Max players size

        //Char array
        private char code[] = {'0','1','2','3','4','5','6','7','8','9',
                'A','B','C','D','E','F','G','H','I','J',
                'K','L','M','N','O','P','Q','R','S','U',
                'V','W','X','Y','Z'};

        //ArrayList
        private ArrayList<User> players = new ArrayList<User>(); //Store players
        private ArrayList<Label> listNames;                   //Store players name [Label]
        private ArrayList<Label> listReady = new ArrayList<Label>();

        //Controllers
        private GameBoardMain gameboard;
        private MainServer main_server;

        //Interface
        private GRSInterface server;

        //Other
        private Stage stage;

        //FXML
        @FXML private ListView<HBox> ListViewPlayers; //Listview players - (hbox [Label => name])
        @FXML private TextArea room_chat;             //Room chat area
        @FXML private AnchorPane AnchorPane;          //Main scene
        @FXML private TextField room_chat_input;      //Room chat input (send msg)
        @FXML private TextField room_code;            //Room code [What this room code, who will joining the room?]
        @FXML private Button return_lobby,game_start; //Button - Return lobby[back lobby] | Game start [if all players are ready]
        @FXML private Label error_text;               //If ready failed or something... [room creator feedback]

        //Constructor (Server | Name | Stage | Status)
        public GameRoomCreateController(MainServer main_server, String name, Stage stage, String status)
        {
            this.main_server = main_server;
            this.name = name;
            this.stage = stage;
            this.status = status;
        }


        //Methods
            //Will generate a random char/code
            public char randomCode()
    {
        return code[(int) Math.floor(Math.random() *35)];
    }

            //Room code generation [max => 5 length code, Appendtext => room_code (string variable)]
            public void room_generator()
            {
                int code_length = 5;
                for(int i=0; i < code_length; ++i)
                {
                    room_code.appendText(String.valueOf(randomCode()));
                }
            }

            //Player handling
                //Add player
                    //[ArrayList (listnames) --> setText (name)]
                    //[Listview (index) --> visible true => will be visible in the list]
                    //[connectedplayers will increase]
                public void addPlayer(User p)
                {
                    Platform.runLater(()->
                    {
                        this.listNames.get(this.connectedPlayers).setText(p.getname());
                        this.ListViewPlayers.getItems().get(this.connectedPlayers).setVisible(true);
                        this.connectedPlayers++;
                    });
                }

                //Remove player
                    //Looking in the list [who this name]
                    //[ArrayList (listnames) --> setText (empty)]
                    //[Listview (index) --> visible false => remove visibility]
                    //[connectedplayers will decrease]
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

           //Room player ready
                public void updateReady(String name, boolean ready)
                {
                    for(int i=0; i < ListViewPlayers.getItems().size(); i++)
                    {
                        if(name.equals(listNames.get(i).getText()))
                        {
                            listReady.get(i).setStyle(ready ? "-fx-background-color: green;" : "-fx-background-color: red;");
                        }
                    }
                }
           //Room chat
                //AppendText => Room Chat Textarea [TEXT]
                public void room_input(String msg)
           {
               this.room_chat.appendText("\n" + msg);
           }

                //Network packet => Received from other players (msg)
                public void room_input(Data data)
                {
                    this.room_input(data.getName() + ": " + data.getContent());
                }

                //Network packet => Sending to all players (msg)
                public void send_chat_msg()
                {
                    String msg = this.room_chat_input.getText();
                    if(!msg.isEmpty() && !msg.isBlank())
                    {
                        this.server.MsgSend(msg);
                    }
                    this.room_chat_input.setText("");
                }


            //Gameboard
                //Wait for the object to load
                final CountDownLatch latch = new CountDownLatch(1);

                //Gameboard - Starting...
                private void GameBoard_Ready()
                {
                    try
                    {
                        FXMLLoader loader = new FXMLLoader(Main.class.getResource("gameboard.fxml"));
                        gameboard = new GameBoardMain(name,"server",server,listNames, connectedPlayers, main_server);
                        loader.setController(gameboard);
                        Scene loadscene = new Scene(loader.load(),1024,768);
                        gameboard.setscene(loadscene);
                        stage.setScene(loadscene);
                        stage.show();

                        latch.countDown();
                        gameboard.focus();

                    } catch (IOException IOE)
                    {
                        IOE.printStackTrace();
                    }
                }

                //Gameboard - Players movement set x,y [name]
                public void GameBoard_PlayerMovement(double x, double y, String name)
                {
                    try{
                        latch.await();
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                gameboard.setPlayerMovement(x,y,name);
                            }
                        });

                    }catch(InterruptedException IE)
                    {
                        ;
                    }
                }

                //Gameboard - Waiting for other players
                public void GameBoard_WaitingPlayersReady()
                {
                    gameboard.WaitingPlayersReady();
                }

                //Player points
                public void Points(String name, int value)
                {
                    gameboard.Points(name, value);
                }


    public void initialize(URL url, ResourceBundle rb)
    {
        //Main server change status => "server"
            main_server.setStatus("server");

        //Loading gui => no error
            error_text.setVisible(false);

        //New ArrayList...
            this.listNames = new ArrayList<Label>();

        //GUI ... [ 0-8 players [HBOX - Label (empty)] => listview items ] | Label => listNames (ArrayList)
            for(int i=0; i < max_players; ++i)
            {
                HBox hbox = new HBox();
                hbox.setSpacing(10);
                hbox.setVisible(false);
                //Name
                Label l = new Label("");
                l.setPrefWidth(200);
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

            connectedPlayers = 0;

        //Generate the code and send the room code to the server & Sets up "room creator" gui...
        if(status.equals("rc_server"))
        {
            room_generator();
            room_code_string = room_code.getText();
            this.main_server.RoomCreate(room_code_string);
            this.server = new GameRoomServer(this, this.name, room_code_string);
            this.listNames.get(0).setText(this.name);
            this.ListViewPlayers.getItems().get(0).setVisible(true);

            this.connectedPlayers = 1;
        }

        //Room chat send text
        room_chat_input.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if(keyEvent.getCode().equals(KeyCode.ENTER))
                {
                    send_chat_msg();
                }
            }
        });

        //Game Start button pressed... [GameBoard ready method]
        game_start.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                GameBoard_Ready();
            }
        });

        //Return lobby [TODO - Fix socket problem]
        return_lobby.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try{
                    server.Close();
                    server = null;
                    Stage stage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
                    FXMLLoader GameLobbyFXML = new FXMLLoader(Main.class.getResource("gamelobby.fxml"));
                    GameLobbyController gamelobby = new GameLobbyController(name,stage, main_server);
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

}
