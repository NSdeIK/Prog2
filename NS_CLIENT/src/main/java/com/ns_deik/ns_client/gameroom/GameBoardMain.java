package com.ns_deik.ns_client.gameroom;

import com.ns_deik.ns_client.gameroom.room_create.GRSInterface;
import com.ns_deik.ns_client.gameroom.room_join.GRJInterface;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;

public class GameBoardMain
{
    private GRSInterface server;
    private GRJInterface client;
    private String name;
    private String status;
    private Scene scene;
    private Group grp = new Group();

    private int connectedPlayers;

    private ArrayList<Label> players;
    private ArrayList<Group> p_gui_group = new ArrayList<Group>();
    private ArrayList<String> players_name = new ArrayList();

    @FXML
    private Pane PaneFXML;

    private char[][] matrix = new char[4][5];
    private ArrayList randomChar = new ArrayList();

    private Image lava = new Image(getClass().getResourceAsStream("/water_gray.gif"));
    private Image water = new Image(getClass().getResourceAsStream("/water.gif"));
    private Image character = new Image(getClass().getResourceAsStream("/char.png"));



    char code[] = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'U',
            'V', 'W', 'X', 'Y', 'Z'};

    private char randomChar() {
        return code[(int) Math.floor(Math.random() * 25)];
    }

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

    private void GameBoardGrid()
    {

        if(status == "server")
        {
            GridPane grid = new GridPane();
            ColumnConstraints col = new ColumnConstraints();
            RowConstraints row = new RowConstraints();
            randomChar.clear();

            for (int i = 0; i < 4; i++) {
                col = new ColumnConstraints();
                col.setPercentWidth(100);
                row = new RowConstraints();
                row.setPercentHeight(100);
                grid.getColumnConstraints().add(col);
                grid.getRowConstraints().add(row);
                if (i == 3) {
                    grid.getRowConstraints().add(row);
                }
            }

            for (int i = 0; i < grid.getColumnConstraints().size(); i++) {
                for (int j = 0; j < grid.getRowConstraints().size(); j++) {
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


            System.out.println(randomChar);

            for(int i=0;i<4;++i)
            {
                for(int j=0;j<5;++j)
                {
                    StackPane gridpane = new StackPane();
                    Label egypt_text = new Label();
                    if(matrix[i][j] != '-')
                    {
                        egypt_text.setText(String.valueOf(matrix[i][j]));
                        ImageView water_view = new ImageView(water);
                        water_view.setFitWidth(170);
                        water_view.setFitHeight(154);
                        egypt_text.setTextFill(Color.WHITE);
                        gridpane.getChildren().addAll(water_view,egypt_text);
                    }
                    else
                    {
                        ImageView lava_view = new ImageView(lava);
                        lava_view.setFitWidth(170);
                        lava_view.setFitHeight(154);
                        gridpane.getChildren().add(lava_view);
                    }


                    gridpane.setMaxSize(170, 120);
                    grid.add(gridpane, i, j);

                    egypt_text.setFont(Font.loadFont(getClass().getResourceAsStream("/egyptian.ttf"), 72));
                }

            }

            PaneFXML.getChildren().add(grid);
            this.server.gameboardready(getMatrix());
        }
        else if(status == "client")
        {
            GridPane grid = new GridPane();
            ColumnConstraints col = new ColumnConstraints();
            RowConstraints row = new RowConstraints();

            for (int i = 0; i < 4; i++) {
                col = new ColumnConstraints();
                col.setPercentWidth(100);
                row = new RowConstraints();
                row.setPercentHeight(100);
                grid.getColumnConstraints().add(col);
                grid.getRowConstraints().add(row);
                if (i == 3) {
                    grid.getRowConstraints().add(row);
                }
            }

            for(int i=0;i<4;++i)
            {
                for(int j=0;j<5;++j)
                {
                    StackPane gridpane = new StackPane();
                    Label egypt_text = new Label();
                    if(matrix[i][j] != '-')
                    {
                        egypt_text.setText(String.valueOf(matrix[i][j]));
                        ImageView water_view = new ImageView(water);
                        water_view.setFitWidth(170);
                        water_view.setFitHeight(154);
                        egypt_text.setTextFill(Color.WHITE);
                        gridpane.getChildren().addAll(water_view,egypt_text);
                    }
                    else
                    {
                        ImageView lava_view = new ImageView(lava);
                        lava_view.setFitWidth(170);
                        lava_view.setFitHeight(154);
                        gridpane.getChildren().add(lava_view);
                    }


                    gridpane.setMaxSize(170, 120);
                    grid.add(gridpane, i, j);

                    egypt_text.setFont(Font.loadFont(getClass().getResourceAsStream("/egyptian.ttf"), 72));
                }

            }

            PaneFXML.getChildren().add(grid);

        }else
        {
            ;
        }
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
                    System.out.println("Kliens: " + players.get(i).getText());
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
        }
        else
        {
            ;
        }
    }

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
       /* if(status.equals("client"))
        {

        }
        else if(status.equals("server"))
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
        }*/
    }


   @FXML public void WindowEvent(KeyEvent event)
    {
            double x = grp.getTranslateX();
            double y = grp.getTranslateY();
            if(event.getCode() == KeyCode.W)
            {
                grp.setTranslateY(y-10);
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

    public void initialize()
    {
        GameBoardGrid();
        GameBoardPlayers();
    }
}
