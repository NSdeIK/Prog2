package com.ns_deik.ns_client.gameroom;

import com.ns_deik.ns_client.gameroom.room_create.GRSInterface;
import com.ns_deik.ns_client.gameroom.room_join.GRJInterface;
import com.ns_deik.ns_client.mainServer.MainServer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class GameBoardMain
{
    //Strings
    private String name;                                        // Player name
    private String status;                                      // Player status [client | server]
    private String gridText;                                    // Get generated table value

    //Network [Interface]
    private GRSInterface server;                                // Room creator => own interface
    private GRJInterface client;                                // Client => own interface

    //Gameboard variables
    private Scene scene;                                        // Main scene
    private Group grp = new Group();                            // Own player group [first]
    private Group clgrp = new Group();                          // Other players group [2-8]

    //Controllers
    private GameBoardGame gamemain = new GameBoardGame();       // New gameboard...
    private GameBoardMain controller = this;                    // Set this gameboard controller
    private MainServer mainserver;                              // Set mainserver

    //Main scene
    @FXML private Pane PaneFXML;                                // Main pane

    //Waiting
    private StackPane waiting = new StackPane();                // If waiting status => set waiting pane

    //Players settings
    private int connectedPlayers;                               // How many connected players?
    private int ready=0;                                        // How many are ready?

    //Players arraylists
    private ArrayList<Label> players;                           //Players label [name]
    private ArrayList<Group> p_gui_group = new ArrayList<Group>();  //Label + image => group
    private ArrayList<String> players_name = new ArrayList();   //Players name

    //Players points
    HashMap<String,Integer> map=new HashMap<String,Integer>();
    private int points = 0;

    //Gameboard generator code + GRID cols/rows
    private ArrayList randomChar = new ArrayList();             //Storing random char
    private String[][] matrix = new String[4][5];                   //2D array [4x5]
    private GridPane grid;                                      //Grid table
    private StackPane gridpane;                                 //Grid pane

    //Movement [true -> yes, false -> no]
    private boolean movement = false;

    //Images
    private Image pyramid = new Image(getClass().getResourceAsStream("/sand_pyramid.jpg"));
    private Image sand = new Image(getClass().getResourceAsStream("/sand_texture.jpg"));
    private Image character = new Image(getClass().getResourceAsStream("/char.png"));
    private Image hourglass = new Image(getClass().getResourceAsStream("/hourglass.gif"));

    //Timer variables
    private StackPane stp = new StackPane();                                    //Timer pane + Game
    private Label timer = new Label();                                          //Timer label (10,9,8...)
    private static final Integer starttime = 5;                                 //Start time (normal = 15s | fast = 10s?)
    private IntegerProperty timersec = new SimpleIntegerProperty(starttime);
    private Timeline timeline;

    //Game rounds
    private int rounds = 0;                                                     //Game rounds

    //Game code [fields]
    private String[] code =
            {"ho","csa","sza","ke","lap", "so", "kas", "sa", "es", "ol",
             "fa", "ja", "aka", "si", "pa" ,"ti" ,"ta", "st" ,"ki", "kel", "hi" ,"ek",
             "ok" ,"is", "el" ,"gu" ,"ga" ,"csa" ,"sza", "pe" ,"os" ,"ri" ,"bi", "lem",
             "ni" ,"ala", "eg", "szer" ,"lo" ,"tal" ,"vo", "ap" ,"tek" ,"ed" ,"la" ,"te",
             "em" ,"in", "je", "ba", "ze" ,"ko", "et" ,"zi", "al" ,"me", "ot", "po", "tor",
             "mu" ,"to", "szok", "or" ,"at" ,"ka" ,"bo" ,"re" ,"ma" ,"ag", "sze", "ar", "as",
             "mo", "ve", "go", "csa", "sza" ,"fo", "ro" ,"am", "ak", "ha", "li", "er", "len",
             "ge", "esz", "ki", "ab", "zo", "pi", "do", "en", "aj" ,"mi", "an", "it" ,"zu", "il",
             "na", "va" ,"ca", "kar", "da" ,"bu" ,"di" ,"za" ,"be"};


    //Controllers
        public GameBoardMain() {}

        //Room creator
        public GameBoardMain(String name, String status, GRSInterface server,ArrayList players, int connectedPlayers, MainServer mainserver)
        {
            this.name = name;
            this.status = status;
            this.server = server;
            this.players = players;
            this.connectedPlayers = connectedPlayers;
            this.mainserver  = mainserver;
            mainserver.setGameBoard(gamemain);
        }

        //Client
        public GameBoardMain(String name, String status, GRJInterface client, String[][] matrix, MainServer mainserver)
        {
            this.name = name;
            this.status = status;
            this.client = client;
            this.matrix = matrix;
            this.mainserver  = mainserver;
            mainserver.setGameBoard(gamemain);
            client.setGameBoard(gamemain);
        }


    //Methods
        //Set
        public void focus()
    {
        PaneFXML.requestFocus();
    }
        public void setscene(Scene scene)
        {
            this.scene = scene;
        }
        public void setMatrix(String[][] matrix) { for(int i=0; i < 4; i++){for(int j=0; j<5; j++) { this.matrix[i][j] = matrix[i][j];}} }
        public void setPlayers(ArrayList list)
        {
            this.players_name = list;
        }
        public void setRounds(int rounds) { this.rounds = rounds; }
        public void setMovement(boolean movement) { this.movement = movement;}

        //Get
        public String[][] getMatrix()
        {
            return matrix;
        }
        public Scene getscene()
        {
            return scene;
        }
        public String getname()
        {
            return this.name;
        }
        private String randomChar() {
            return code[(int) Math.floor(Math.random() * 108)];
        }
        public int getRounds() { return rounds; }


    //Timer
        private void timer()
        {
            //GUI
                //Pane
                    PaneFXML.getChildren().remove(stp);
                    stp.setPrefSize(600,750);
                    stp.setStyle("-fx-opacity: 0.6;");
                    stp.setLayoutX(10);
                    stp.setLayoutY(10);
                    VBox vbox = new VBox();

                //TIMER GUI
                    timer.setFont(Font.font("Comic Sans MS",225));
                    vbox.getChildren().add(timer);
                    vbox.setAlignment(Pos.CENTER);
                    stp.getChildren().add(vbox);
                    PaneFXML.getChildren().add(stp);

                //TIMER binding
                    timer.textProperty().bind(timersec.asString());
                    if(timeline != null)
                    {
                        timeline.stop();
                    }
                    timersec.set(starttime);
                    timeline = new Timeline();
                    timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(starttime+1),new KeyValue(timersec,0)));
                    timeline.playFromStart();

                //TIMER IS FINISHED
                    timeline.setOnFinished(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                //Clear pane
                                    stp.getChildren().clear();
                                    stp.setStyle("-fx-opacity: 1;");
                                 //Clear grid
                                    grid.setVisible(false);

                                    for(Group p : p_gui_group)
                                    {
                                        p.setVisible(false);
                                    }

                                //Set movement false [dont move players]
                                    movement = false;

                                //If room creator [set interface (own)]
                                    if(status.equals("server"))
                                    {
                                        gamemain.setclientserver(server);
                                    }

                                //Gameboard main set...
                                    gamemain.setgamegui(stp, gridText, controller, mainserver,status);
                            }});}});
        }


   //Board generator [ If not contains + 'code' | If contains + '-' (empty) ]
       private void randomchargenerator() {
           for (int i = 0; i < matrix.length; ++i) {
               for (int j = 0; j < matrix[i].length; ++j) {
                   String ch = randomChar();
                   if (!randomChar.contains(ch)) {
                       randomChar.add(ch);
                       matrix[i][j] = ch;
                   } else {
                       randomChar.add("-");
                       matrix[i][j] = "-";
                   }
               }
           }

       }


    //Gameboard Settings
        public void GameBoardGrid()
        {
            //GUI - GRID
                PaneFXML.getChildren().remove(grid);
                grid = new GridPane();
                PaneFXML.setStyle("-fx-background-image: url('room_background.jpg');-fx-background-repeat: stretch;-fx-background-position: center center; ");
                grid.setLayoutX(10);
                grid.setLayoutY(10);

                //SERVER GUI (Room creator)
                    if(status.equals("server"))
                    {
                        randomChar.clear();
                        randomchargenerator();
                        for(int i=0;i<4;++i)
                        {
                            for(int j=0;j<5;++j)
                            {
                                gridpane = new StackPane();
                                Label egypt_text = new Label();
                                DropShadow shadow = new DropShadow();
                                egypt_text.setFont(Font.loadFont(getClass().getResourceAsStream("/egyptian.ttf"), 50));
                                egypt_text.setEffect(shadow);
                                if(matrix[i][j] != "-")
                                {
                                    egypt_text.setText(String.valueOf(matrix[i][j]));
                                    ImageView water_view = new ImageView(sand);
                                    water_view.setFitWidth(150);
                                    water_view.setFitHeight(150);
                                    egypt_text.setTextFill(Color.WHITE);
                                    gridpane.getChildren().addAll(water_view,egypt_text);
                                }
                                else
                                {
                                    ImageView lava_view = new ImageView(pyramid);
                                    lava_view.setFitWidth(150);
                                    lava_view.setFitHeight(150);
                                    gridpane.getChildren().add(lava_view);
                                }
                                gridpane.setMaxSize(150, 150);
                                grid.add(gridpane, i, j);
                            }
                        }
                        grid.setAlignment(Pos.CENTER);
                        PaneFXML.getChildren().add(grid);

                        if(rounds == 0)
                        {
                            this.server.gameboardready(getMatrix());
                        }
                        else if(rounds >= 1 && !(rounds == 0) )
                        {
                            this.server.gameboardguinew(getMatrix());
                            for(Group g : p_gui_group)
                            {
                                g.toFront();
                                g.setVisible(true);
                            }
                            grp.toFront();

                            ready=0;
                            WaitingPlayers();
                            WaitingPlayersReady();
                        }
                        else
                        {
                            ;
                        }

                    }
                //CLIENT GUI
                    else if(status.equals("client"))
                    {
                        for(int i=0;i<4;++i)
                        {
                            for(int j=0;j<5;++j)
                            {
                                gridpane = new StackPane();
                                Label egypt_text = new Label();
                                if(!(matrix[i][j].equals("-")))
                                {
                                    egypt_text.setText(String.valueOf(matrix[i][j]));
                                    ImageView water_view = new ImageView(sand);
                                    water_view.setFitWidth(150);
                                    water_view.setFitHeight(150);
                                    egypt_text.setTextFill(Color.WHITE);
                                    gridpane.getChildren().addAll(water_view,egypt_text);
                                }
                                else
                                {
                                    ImageView lava_view = new ImageView(pyramid);
                                    lava_view.setFitWidth(150);
                                    lava_view.setFitHeight(150);
                                    gridpane.getChildren().add(lava_view);
                                }

                                gridpane.setMaxSize(150, 150);
                                grid.add(gridpane, i, j);

                                DropShadow shadow = new DropShadow();
                                egypt_text.setFont(Font.loadFont(getClass().getResourceAsStream("/egyptian.ttf"), 50));
                                egypt_text.setEffect(shadow);
                            }

                        }

                        PaneFXML.getChildren().add(grid);

                        if(rounds >= 1)
                        {

                            for(Group g : p_gui_group)
                            {
                                g.setVisible(true);
                                g.toFront();
                            }
                            grp.toFront();

                            this.client.WaitingPlayersReady();
                        }
                    }
                //?
                    else
                    {;}

        }


    //Gameboard players (GUI + Arraylist)
        public void GameBoardPlayers()
        {

            if(status.equals("server"))
            {
                Label lname = new Label(name);
                StackPane pane = new StackPane();
                lname.setTranslateY(-45);
                lname.setFont(Font.font("Verdana",32));
                ImageView character_set = new ImageView(character);
                character_set.setFitWidth(50);
                character_set.setFitHeight(50);

                pane.setAlignment(Pos.CENTER);
                pane.getChildren().addAll(lname,character_set);

                grp.setTranslateX(50);
                grp.setTranslateY(50);
                grp.getChildren().addAll(pane);
                PaneFXML.getChildren().add(grp);
                p_gui_group.add(grp);

                for(int i=0; i < connectedPlayers; i++)
                {
                    if(players.get(i).getText().equals(this.name))
                    {
                            if(!players_name.contains(this.name))
                            {
                                players_name.add(this.name);
                            }
                    }else
                    {
                        ImageView cl_character_set = new ImageView(character);
                        Label clientnames = new Label(players.get(i).getText());
                        StackPane cpane = new StackPane();
                        clientnames.setTranslateY(-45);
                        clientnames.setFont(Font.font("Verdana",32));
                        cl_character_set.setFitWidth(50);
                        cl_character_set.setFitHeight(50);

                        cpane.setAlignment(Pos.CENTER);
                        cpane.getChildren().addAll(clientnames, cl_character_set);
                        clgrp = new Group();
                        clgrp.getChildren().add(cpane);
                        clgrp.setTranslateX(i*50);
                        clgrp.setTranslateY(i*50);
                        PaneFXML.getChildren().add(clgrp);
                        p_gui_group.add(clgrp);
                        if(!(players_name.contains(players.get(i).getText())))
                        {
                            players_name.add(players.get(i).getText());
                        }

                    }
                }
                this.server.gameboardgui(players_name);
                grp.toFront();
                WaitingPlayers();
                WaitingPlayersReady();
            }
            else if(status.equals("client"))
            {
                for(int i=0; i < players_name.size();i++)
                {
                    String getname = (String) players_name.get(i);
                    if(getname.equals(this.name))
                    {
                        Label lname = new Label(this.name);
                        StackPane pane = new StackPane();
                        lname.setTranslateY(-45);
                        lname.setFont(Font.font("Verdana",32));
                        ImageView character_set = new ImageView(character);
                        character_set.setFitWidth(50);
                        character_set.setFitHeight(50);

                        pane.setAlignment(Pos.CENTER);
                        pane.getChildren().addAll(lname,character_set);

                        grp.setTranslateX(50);
                        grp.setTranslateY(50);
                        grp.getChildren().addAll(pane);
                        PaneFXML.getChildren().add(grp);
                        p_gui_group.add(grp);
                    }
                    else
                    {
                        ImageView cl_character_set = new ImageView(character);
                        Group clgrp = new Group();
                        Label clientnames = new Label(getname);
                        StackPane cpane = new StackPane();
                        clientnames.setTranslateY(-45);
                        clientnames.setFont(Font.font("Verdana",32));
                        cl_character_set.setFitWidth(50);
                        cl_character_set.setFitHeight(50);

                        cpane.setAlignment(Pos.CENTER);
                        cpane.getChildren().addAll(clientnames, cl_character_set);
                        clgrp = new Group();
                        clgrp.getChildren().add(cpane);
                        clgrp.setTranslateX(50);
                        clgrp.setTranslateY(50);
                        PaneFXML.getChildren().add(clgrp);
                        p_gui_group.add(clgrp);
                    }
                }
                grp.toFront();
                this.client.WaitingPlayersReady();
            }
            else
            {
                ;
            }
        }


    //Waiting
        //Set gui
        public void WaitingPlayers()
        {

            if(status.equals("server"))
            {
                VBox vbox = new VBox();

                waiting = new StackPane();
                waiting.setPrefSize(1024,768);
                waiting.setStyle("-fx-background-color: gray;-fx-opacity: 0.96;");

                Label waitingtext = new Label("Várakozás a többi játékosokra...");
                waitingtext.setFont(Font.font("Comic Sans MS",32));
                waitingtext.setTextFill(Color.WHITE);

                ImageView hourglass_view = new ImageView(hourglass);
                vbox.getChildren().addAll(waitingtext,hourglass_view);
                vbox.setAlignment(Pos.CENTER);

                waiting.getChildren().add(vbox);
                waiting.setAlignment(Pos.CENTER);

                PaneFXML.getChildren().add(waiting);
                this.server.waitingplayers();
            }
            else if(status.equals("client"))
            {
                VBox vbox = new VBox();

                waiting = new StackPane();
                waiting.setPrefSize(1024,768);
                waiting.setStyle("-fx-background-color: gray;-fx-opacity: 0.96;");

                Label waitingtext = new Label("Várakozás a többi játékosokra...");
                waitingtext.setFont(Font.font("Comic Sans MS",32));
                waitingtext.setTextFill(Color.WHITE);

                ImageView hourglass_view = new ImageView(hourglass);
                vbox.getChildren().addAll(waitingtext,hourglass_view);
                vbox.setAlignment(Pos.CENTER);

                waiting.getChildren().add(vbox);
                waiting.setAlignment(Pos.CENTER);

                PaneFXML.getChildren().add(waiting);
            }
            else
            {
                ;
            }

        }
        //Remove gui
        private void removeWaitingPlayersGUI()
        {
            waiting.setVisible(false);
        }

        //Ready => Game start...
        public void WaitingPlayersReady()
        {
            Thread t = new Thread()
            {
                public void run()
                {
                    while(true) {
                        if (status.equals("server")) {
                            ready++;
                            if (ready == players_name.size()) {
                                server.waitingplayersready();
                                removeWaitingPlayersGUI();
                                movement = true;
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (rounds != 4) {
                                            timer();
                                        }
                                    }
                                });
                            }
                            break;
                        }
                        try
                        {
                            Thread.sleep(100);
                        }catch (InterruptedException i)
                        {
                            //
                        }
                    }
                }
            };
            t.start();

            if(status.equals("client"))
            {
                removeWaitingPlayersGUI();
                movement = true;
                rounds++;
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (rounds != 4) {
                            timer();
                        }
                    }
                });

            }
        }


    //Movement [TODO - Get grid ...]
        //Set player moves
        public void setPlayerMovement(double x, double y, String p_name)
        {
            for(int i=0; i < players_name.size();i++)
            {
                String getname = (String) players_name.get(i);
                if(getname.equals(p_name))
                {
                    p_gui_group.get(i).setTranslateX(x);
                    p_gui_group.get(i).setTranslateY(y);
                }
            }
        }

        //Check grid [x,y]
        private int checkgridcellX(double x)
        {
            if(x < 85)
            {
                return 0;
            }
            else if(x < 235)
            {
                return 1;
            }
            else if(x < 385 )
            {
                return 2;
            }
            else
            {
                return 3;
            }
        }

        private int checkgridcellY(double y)
        {
            if(y < 135)
            {
                return 0;
            }
            else if(y < 285 )
            {
                return 1;
            }
            else if(y < 435)
            {
                return 2;
            }
            else if(y < 585)
            {
                return 3;
            }
            else
            {
                return 4;
            }
        }


    //Movement - KeyEvent
       @FXML public void WindowEvent(KeyEvent event)
        {
            double x = grp.getTranslateX();
            double y = grp.getTranslateY();

                for(Node node : grid.getChildren())
                {
                    Integer columnIndex = GridPane.getColumnIndex(node);
                    Integer rowIndex = GridPane.getRowIndex(node);

                    if (columnIndex == null)
                    {
                        columnIndex = 0;
                    }

                    if (rowIndex == null) {
                        rowIndex = 0;
                    }
                    if(columnIndex == checkgridcellX(x) && rowIndex == checkgridcellY(y))
                    {
                            if(node instanceof StackPane)
                            {
                                if(((StackPane)node).getChildren().size() > 1) {
                                    gridText = ((Label) ((StackPane) node).getChildren().get(1)).getText();
                                    //System.out.println("--> " + ((Label) ((StackPane) node).getChildren().get(1)).getText());
                                    //slabel.setText(((Label) ((StackPane) node).getChildren().get(1)).getText());
                                }
                                else {
                                    gridText = "-";
                                }
                            }
                    }
                }

                if(movement)
                {
                    if(event.getCode() == KeyCode.W) {
                        grp.setTranslateY(y - 10);

                        if(status.equals("client"))
                        {
                            this.client.gameplayermovement(x,y-10);
                        }
                        else if(status.equals("server"))
                        {
                            this.server.gameplayermovement(x,y-10);
                        }
                        else{;}
                    }
                    else if(event.getCode() == KeyCode.S)
                    {
                        grp.setTranslateY(y+10);
                        if(status.equals("client"))
                        {
                            this.client.gameplayermovement(x,y+10);
                        }
                        else if(status.equals("server"))
                        {
                            this.server.gameplayermovement(x,y+10);
                        }
                        else{;}
                    }
                    else if(event.getCode() == KeyCode.A)
                    {
                        grp.setTranslateX(x-10);
                        if(status.equals("client"))
                        {
                            this.client.gameplayermovement(x-10,y);
                        }
                        else if(status.equals("server"))
                        {
                            this.server.gameplayermovement(x-10,y);
                        }
                        else{;}
                    }
                    else if(event.getCode() == KeyCode.D)
                    {
                        grp.setTranslateX(x+10);
                        if(status.equals("client"))
                        {
                            this.client.gameplayermovement(x+10,y);
                        }
                        else if(status.equals("server"))
                        {
                            this.server.gameplayermovement(x+10,y);
                        }
                        else{;}
                    }
                    else
                    {
                        ;
                    }
                }
        }

        public void Points(String name, int value)
        {
            if(status.equals("server"))
            {
                points+=value;
                map.put(name,points);
            }
            else
            {
                client.sendPoints(value);
            }
            for(Map.Entry m : map.entrySet()){
                System.out.println(m.getKey()+" "+m.getValue());
            }
        }

    //Initialize
    public void initialize()
    {
        GameBoardGrid();
        GameBoardPlayers();
    }
}
