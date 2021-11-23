package com.ns_deik.ns_client.gameroom;

import com.ns_deik.ns_client.gameroom.room_create.GRSInterface;
import com.ns_deik.ns_client.mainServer.MainServer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;

public class GameBoardGame
{
    //Strings
    private String GridText;
    private String status = "";

    //ArrayList
    private ArrayList<Integer> words_points = new ArrayList<>();

    //Controllers
    private GameBoardMain controller;
    private MainServer mainserver;
    private GRSInterface clientserver;

    //GUI
    private Label Word_Card = new Label();
    private TextField Word_Card_Input = new TextField();
    private ListView<String> words_list = new ListView();
    private StackPane sp;

    //Dice
    private DiceGenerator dice = new DiceGenerator();

    //LocalTime
    private LocalTime plustime;


    //Methods
        //Set
        public void setPlustime(LocalTime plustime) {
        this.plustime = plustime;
    }

        public void setgamegui(StackPane sp, String GridText, GameBoardMain controller, MainServer mainserver, String status) {
        this.sp = sp;
        this.GridText = GridText;
        this.controller = controller;
        this.mainserver = mainserver;
        this.status = status;

        Word_Card.setAlignment(Pos.CENTER);
        Word_Card.setTranslateY(-170);
        Word_Card.setFont(Font.font("Comic Sans MS",170));

        Word_Card_Input.setAlignment(Pos.CENTER);
        Word_Card_Input.setTranslateY(50);
        Word_Card_Input.setPrefSize(450,100);
        Word_Card_Input.setFont(Font.font("Comic Sans MS",48));

        words_points.clear();

        words_list.getItems().clear();
        words_list.setTranslateX(450);
        words_list.setMaxWidth(100);
        words_list.setMaxHeight(300);
        words_list.setStyle("-fx-background-color: transparent;");



        sp.getChildren().add(words_list);

        dice.DiceRoll();

        switch (dice.getDiceRoll())
        {
            case 1:
                Word_Card.setText("..."+ GridText);
                break;
            case 2:
                Word_Card.setText(GridText+"...");
                break;
            case 3:
                Word_Card.setText("..."+GridText+"...");
                break;
        }
        sp.getChildren().addAll(Word_Card,Word_Card_Input);

        if(status.equals("server"))
        {
            plustime = getplusLocalTime();
            clientserver.GameTimerMSG(plustime);
            gamewhile();

        }else if(status.equals("client"))
        {
            Thread t = new Thread()
            {
                public void run()
                {
                    while(true)
                    {
                        if(plustime != null)
                        {
                            gamewhile();
                            break;
                        }
                    }
                    try {
                        sleep(250);
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                    }
                }
            };
            t.start();
        }
        else
        {
            ;
        }

    }

        public void setclientserver(GRSInterface clientserver)
    {
        this.clientserver = clientserver;
    }

        //Get
        public LocalTime getLocalTime() {
            OffsetDateTime offsetDateTime = OffsetDateTime.now();
            LocalTime localTime=offsetDateTime.toLocalTime();
            return  localTime;
        }       //Original time
        public LocalTime getplusLocalTime() {
        OffsetDateTime offsetDateTime = OffsetDateTime.now();
        LocalTime localTime=offsetDateTime.toLocalTime();
        return  localTime.plusSeconds(10);
    }   //Plus time [TODO - RANDOM TIME 45-60s]


    //Game while...
        private void gamewhile()
        {
            Thread t = new Thread()
            {
                public void run()
                {
                    try
                    {
                        while(true)
                        {
                            //Set time
                            LocalTime originaltime = getLocalTime();

                            //Compare value (example: plus time => 14:50:05 | original time => 14:50:00 | value = 5)
                            int value = plustime.compareTo(originaltime);
                            //If value high than 0 [game]
                            //If value low than 0 [end game]
                            if(value > 0)
                            {
                                //Word input => get value => Main server => check word (good or bad)?
                                Word_Card_Input.setOnAction(new EventHandler<ActionEvent>() {
                                    @Override
                                    public void handle(ActionEvent actionEvent) {
                                        String text = Word_Card_Input.getText();
                                        Word_Card_Input.clear();
                                        if(!text.isEmpty())
                                        {
                                            mainserver.GameMSG(text,dice.getDiceRoll(),GridText);
                                        }
                                    }
                                });
                            }
                            else
                            {
                                //Game is ended

                                //Set plustime = null
                                    plustime = null;

                                //Adding up points
                                    int points_sum = words_points.stream().mapToInt(Integer::intValue).sum();



                                // Set rounds [rounds+1]
                                    controller.setRounds(controller.getRounds()+1);


                                // GUI UPDATE - Back table (new grid...)
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(status.equals("server"))
                                            {
                                                sp.getChildren().clear();
                                                controller.Points(controller.getname(), points_sum);
                                                controller.GameBoardGrid();
                                                controller.focus();
                                            }
                                            else if(status.equals("client"))
                                            {
                                                sp.getChildren().clear();
                                                controller.Points(controller.getname(), points_sum);
                                                controller.focus();
                                            }
                                            else
                                            {
                                                ;
                                            }

                                        }
                                    });

                                // Set players moves (true - can moves)
                                controller.setMovement(true);

                                //While ended
                                break;
                            }
                            //Thread sleep - 0.1 sec
                            Thread.sleep(100);
                        }

                    }catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            };

            t.start();
        }


    //GUI update (words_list)
        public void addlist(String word)
        {
            Platform.runLater(new Runnable() {
                @Override
                public void run()
                {
                    if(words_list.getItems().contains(word))
                    {
                        words_list.setCellFactory(lv -> new ListCell<String>()
                        {
                            @Override
                            protected void updateItem(String item, boolean empty)
                            {
                                super.updateItem(item, empty);
                                if(empty)
                                {
                                    ;
                                }
                                else
                                {
                                    setText(item);
                                    if(item.equals(word))
                                    {
                                        setStyle("-fx-background-color: red;");
                                    }
                                }
                            }
                        });
                    }
                    else
                    {
                        int points = word.length() * 10;
                        words_points.add(points);
                        words_list.getItems().add(word);

                        words_list.setCellFactory(lv -> new ListCell<String>()
                        {
                            @Override
                            protected void updateItem(String item, boolean empty)
                            {
                                super.updateItem(item, empty);
                                if(empty)
                                {
                                    ;
                                }
                                else
                                {
                                    setText(item);
                                    if(item.equals(word))
                                    {
                                        setStyle("-fx-background-color: green;");
                                    }
                                }
                            }
                        });
                    }
                }

            });
        }
}
