package com.ns_deik.ns_client.gameroom;

import com.ns_deik.ns_client.gameroom.room_create.GRSInterface;
import com.ns_deik.ns_client.gameroom.room_join.GRJInterface;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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

public class GameBoardMain
{
    //Gameboard variables
    private GRSInterface server;
    private GRJInterface client;
    private String name;
    private String status;
    private Scene scene;
    private Group grp = new Group();

    //Main scene
    @FXML
    private Pane PaneFXML;

    //Waiting
    private StackPane waiting;

    //Check x,y, grid get value
    private Pane teszt;
    private Label xlabel;
    private Label ylabel;
    private Label slabel;

    //Players settings
    private int connectedPlayers;
    private ArrayList<Label> players;
    private ArrayList<Group> p_gui_group = new ArrayList<Group>();
    private ArrayList<String> players_name = new ArrayList();
    private int ready=0;

    //Gameboard generator code + GRID cols/rows
    private ArrayList randomChar = new ArrayList();
    private char[][] matrix = new char[4][5];
    private GridPane grid;
    private StackPane gridpane;

    //Movement [true -> yes, false -> no]
    private boolean movement = false;

    //Images
    private Image lava = new Image(getClass().getResourceAsStream("/water_gray.gif"));
    private Image water = new Image(getClass().getResourceAsStream("/water.gif"));
    private Image character = new Image(getClass().getResourceAsStream("/char.png"));
    private Image hourglass = new Image(getClass().getResourceAsStream("/hourglass.gif"));
    private Image gameboardicon = new Image(getClass().getResourceAsStream("/icon.png"));

    //Timer variables
    private Label timer = new Label();
    private static final Integer starttime = 15;
    private IntegerProperty timersec = new SimpleIntegerProperty(starttime);
    private Timeline timeline;

    private char code[] = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'U',
            'V', 'W', 'X', 'Y', 'Z'};

    public GameBoardMain() {}

    public GameBoardMain(String name, String status, GRSInterface server,ArrayList players, int connectedPlayers)
    {
        this.name = name;
        this.status = status;
        this.server = server;
        this.players = players;
        this.connectedPlayers = connectedPlayers;
    }

    public GameBoardMain(String name,String status,GRJInterface client, char[][] matrix)
    {
        this.name = name;
        this.status = status;
        this.client = client;
        this.matrix = matrix;
    }

    //Methods
    public void focus()
    {
        PaneFXML.requestFocus();
    }
    public void setscene(Scene scene)
    {
        this.scene = scene;
    }
    public char[][] getMatrix()
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
    public void setPlayers(ArrayList list)
    {
        this.players_name = list;
    }
    private char randomChar() {
        return code[(int) Math.floor(Math.random() * 25)];
    }

    //Timer
    private void timer()
    {
        StackPane stp = new StackPane();
        stp.setPrefSize(600,750);
        stp.setStyle("-fx-opacity: 0.6;");
        stp.setLayoutX(10);
        stp.setLayoutY(10);
        VBox vbox = new VBox();

        timer.setFont(Font.font("Comic Sans MS",225));
        vbox.getChildren().add(timer);
        vbox.setAlignment(Pos.CENTER);
        stp.getChildren().add(vbox);
        PaneFXML.getChildren().add(stp);


        timer.textProperty().bind(timersec.asString());
        if(timeline != null)
        {
            timeline.stop();
        }
        timersec.set(starttime);
        timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(starttime+1),new KeyValue(timersec,0)));
        timeline.playFromStart();
        timeline.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        vbox.setVisible(false);
                        movement = false;
                        stp.getChildren().add(new ImageView(gameboardicon));
                        stp.setStyle("-fx-background-color: whitesmoke;-fx-opacity: 0.7;");
                    }
                });
            }
        });
    }

   //Board generator
   private void randomchargenerator() {
       for (int i = 0; i < matrix.length; ++i) {
           for (int j = 0; j < matrix[i].length; ++j) {
               char ch = randomChar();
               if (!randomChar.contains(ch)) {
                   randomChar.add(ch);
                   matrix[i][j] = ch;
               } else {
                   randomChar.add('-');
                   matrix[i][j] = '-';
               }
           }
       }
   }

    //Gameboard Settings
    private void GameBoardGrid()
    {
        grid = new GridPane();
        PaneFXML.setStyle("-fx-background-color: red");
        grid.setLayoutX(10);
        grid.setLayoutY(10);
        if(status == "server")
        {
            randomChar.clear();
            randomchargenerator();
            System.out.println(randomChar);

            for(int i=0;i<4;++i)
            {
                for(int j=0;j<5;++j)
                {
                    gridpane = new StackPane();
                    Label egypt_text = new Label();
                    egypt_text.setFont(Font.loadFont(getClass().getResourceAsStream("/egyptian.ttf"), 72));
                    if(matrix[i][j] != '-')
                    {
                        egypt_text.setText(String.valueOf(matrix[i][j]));
                        ImageView water_view = new ImageView(water);
                        water_view.setFitWidth(150);
                        water_view.setFitHeight(150);
                        egypt_text.setTextFill(Color.WHITE);
                        gridpane.getChildren().addAll(water_view,egypt_text);
                    }
                    else
                    {
                        ImageView lava_view = new ImageView(lava);
                        lava_view.setFitWidth(150);
                        lava_view.setFitHeight(150);
                        gridpane.getChildren().add(lava_view);
                    }
                    gridpane.setMaxSize(150, 150);
                    grid.add(gridpane, i, j);
                }
            }
            System.out.println(matrix);
            for (int k=0; k<4; k++ )
            {
                for(int l=0;l<5; l++)
                {
                    System.out.println(matrix[k][l]);
                }
            }
            grid.setAlignment(Pos.CENTER);
            PaneFXML.getChildren().add(grid);

            this.server.gameboardready(getMatrix());
        }
        else if(status == "client")
        {
            for(int i=0;i<4;++i)
            {
                for(int j=0;j<5;++j)
                {
                    gridpane = new StackPane();
                    Label egypt_text = new Label();
                    if(matrix[i][j] != '-')
                    {
                        egypt_text.setText(String.valueOf(matrix[i][j]));
                        ImageView water_view = new ImageView(water);
                        water_view.setFitWidth(150);
                        water_view.setFitHeight(150);
                        egypt_text.setTextFill(Color.WHITE);
                        gridpane.getChildren().addAll(water_view,egypt_text);
                    }
                    else
                    {
                        ImageView lava_view = new ImageView(lava);
                        lava_view.setFitWidth(150);
                        lava_view.setFitHeight(150);
                        gridpane.getChildren().add(lava_view);
                    }

                    gridpane.setMaxSize(150, 150);
                    grid.add(gridpane, i, j);

                    egypt_text.setFont(Font.loadFont(getClass().getResourceAsStream("/egyptian.ttf"), 72));
                }

            }

            PaneFXML.getChildren().add(grid);

        }else
        {
            ;
        }

        //Test
        /*
        xlabel = new Label();
        ylabel = new Label();
        ylabel.setTranslateY(15);
        slabel = new Label();
        slabel.setTranslateY(35);
        teszt = new Pane();
        teszt.setPrefSize(50,50);
        teszt.setStyle("-fx-background-color: green");
        teszt.setTranslateX(700);
        teszt.setTranslateY(100);
        teszt.getChildren().addAll(xlabel,ylabel,slabel);*/

        //PaneFXML.getChildren().add(teszt);
    }
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

            grp.getChildren().addAll(pane);
            PaneFXML.getChildren().add(grp);
            p_gui_group.add(grp);

            for(int i=0; i < connectedPlayers; i++)
            {
                if(players.get(i).getText().equals(this.name))
                {
                    players_name.add(this.name);
                }else
                {
                    ImageView cl_character_set = new ImageView(character);
                    Group clgrp = new Group();
                    Label clientnames = new Label(players.get(i).getText());
                    StackPane cpane = new StackPane();
                    clientnames.setTranslateY(-45);
                    clientnames.setFont(Font.font("Verdana",32));
                    cl_character_set.setFitWidth(50);
                    cl_character_set.setFitHeight(50);

                    cpane.setAlignment(Pos.CENTER);
                    cpane.getChildren().addAll(clientnames, cl_character_set);
                    clgrp.getChildren().add(cpane);
                    clgrp.setTranslateX(300);
                    clgrp.setTranslateY(300);
                    PaneFXML.getChildren().add(clgrp);
                    p_gui_group.add(clgrp);
                    players_name.add(players.get(i).getText());
                }
            }
            this.server.gameboardgui(players_name);
            WaitingPlayers();
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
                    clgrp.getChildren().add(cpane);
                    clgrp.setTranslateX(300);
                    clgrp.setTranslateY(300);
                    PaneFXML.getChildren().add(clgrp);
                    p_gui_group.add(clgrp);
                }
            }
            this.client.WaitingPlayersReady();
        }
        else
        {
            ;
        }
    }

    //Waiting
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
    private void removeWaitingPlayersGUI()
    {
        waiting.setVisible(false);
    }
    public void WaitingPlayersReady()
    {
        if(status.equals("server"))
        {
            ready++;
            if(ready == players_name.size())
            {
                this.server.waitingplayersready();
            }
            removeWaitingPlayersGUI();
            movement = true;
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    timer();
                }
            });

        }else if(status.equals("client"))
        {
            removeWaitingPlayersGUI();
            movement = true;
            timer();
        }
        else
        {
            ;
        }

    }

    //Movement
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

        /*
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
                                System.out.println("--> " + ((Label) ((StackPane) node).getChildren().get(1)).getText());
                                slabel.setText(((Label) ((StackPane) node).getChildren().get(1)).getText());
                            }
                            else {
                                System.out.println("---> -");
                                slabel.setText("-");
                            }
                        }
                }
            }*/

            if(movement)
            {
                if(event.getCode() == KeyCode.W) {
                    grp.setTranslateY(y - 10);
                    //ylabel.setText(String.valueOf(y-10));

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
                    //ylabel.setText(String.valueOf(y+10));
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
                    //xlabel.setText(String.valueOf(x-10));
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
                    //xlabel.setText(String.valueOf(x+10));
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
    public void initialize()
    {
        GameBoardGrid();
        GameBoardPlayers();
    }
}
