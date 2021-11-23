package com.ns_deik.ns_client.mainServer;

import Server.*;
import com.ns_deik.ns_client.gameroom.GameBoardGame;
import com.ns_deik.ns_client.gameroom.User;
import com.ns_deik.ns_client.gameroom.room_join.GameRoomJoinController;
import com.ns_deik.ns_client.homepage.GameController;
import com.ns_deik.ns_client.lobby.GameLobbyController;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class MainServer implements SInterface {

    //Variables

        //Networks - Socket [Receive/Send]
        private boolean network = false;
        private InputStream in;
        private ObjectInputStream oin;
        private OutputStream out;
        private ObjectOutputStream oout;
        private ClientListen clientlisten;

        //Others
        private String name;
        private String status = "";

        //ArrayList - Players [User - get name | get ip | ...]
        private ArrayList<User> players;

        //Controllers
        private GameLobbyController lobbycontroller;
        private GameController gamecontroller;
        private GameRoomJoinController gameroomjoincontroller;
        private GameBoardGame game;

        //Method reconnect
        public boolean getnetwork() { return network; }
        public void reconnect()
        {
            this.clientlisten = new ClientListen("localhost",6666);
            this.clientlisten.start();
        }

    //Constructors
    public MainServer(){}
    public MainServer(GameController controller, String name){
        this.gamecontroller = controller;
        this.name = name;
        this.clientlisten = new ClientListen("localhost",6666);
        this.clientlisten.start();
    };
    public MainServer(GameLobbyController controller,String name)
    {
        this.lobbycontroller = controller;
        this.name = name;
        this.clientlisten = new ClientListen("localhost",6666);
        this.clientlisten.start();
    }


    // [Server socket || Network packets handling]
    private class ClientListen extends Thread
    {
        private Socket socket;
        private String address;
        private int port;

        public ClientListen(String address, int port)
        {
            this.address = address;
            this.port = port;
        }

        @Override
        public void run()
        {
            try
            {
                this.socket = new Socket(address,port);
                if(this.socket != null)
                {
                    network = true;
                }

                out = this.socket.getOutputStream();
                oout = new ObjectOutputStream(out);

                in = socket.getInputStream();
                oin = new ObjectInputStream(in);

                MainData msg = new MainData("CONNECT", name, "");
                oout.writeObject(msg);
                while(this.socket.isConnected())
                {
                    MainData incomingmsg = (MainData) oin.readObject();
                    System.out.println(incomingmsg.getDataType());
                    if(incomingmsg != null)
                    {
                        switch (incomingmsg.getDataType())
                        {
                            case CONNECT_SUCCESS:
                            {
                                gamecontroller.reconnect_success();
                                break;
                            }
                            case LOBBY_CHAT:
                            {
                                lobbycontroller.lobby_input_text(incomingmsg);
                                break;
                            }
                            case DISCONNECT:
                            {

                                if(incomingmsg.getName().equals(name))
                                {
                                    break;
                                }
                                else
                                {
                                    lobbycontroller.lobby_input_text("Server: [" + incomingmsg.getName() + "] játékos lelépett!");
                                    break;
                                }
                            }
                            case REGISTER_USER_SUCCESS:
                            {
                                System.out.println("Sikeres fiók létrehozása!");
                                gamecontroller.jumpLoginPanel();
                                break;
                            }
                            case REGISTER_USER_FAIL:
                            {
                                System.out.println("Nem sikerült létrehozni!");
                                break;
                            }
                            case LOGIN_USER_SUCCESS:
                            {
                                gamecontroller.jumpLobbyPanel();
                                break;
                            }
                            case LOGIN_USER_FAIL:
                            {
                                if(incomingmsg.getContent().equals("Error#1"))
                                {
                                    gamecontroller.bad_login("Adatbázis hiba...");
                                    break;
                                }
                                break;
                            }
                            case ROOM_JOIN:
                            {
                                if(status.equals("client"))
                                {
                                    gameroomjoincontroller.joinroom(incomingmsg.getContent());
                                    break;
                                }
                                break;
                            }
                            case WORD_SUCCESS:
                            {
                                game.addlist(incomingmsg.getContent());
                                break;
                            }
                            default:
                            {
                                System.out.println("... " + incomingmsg.toString());
                                break;
                            }
                        }
                    }
                }
            }catch(IOException IOE)
            {
                gamecontroller.reconnect();
                network = false;
                ;
            }catch(ClassNotFoundException CNFE)
            {
            }
        }
    }


    //Methods [Sets]
    public void setName(String name)
    {
        this.name = name;
    }
    public void setStatus(String status)
    {
        this.status = status;
    } // Set status [client || server]
    public void setJoinController(GameRoomJoinController GRJC)
    {
        this.gameroomjoincontroller = GRJC;
    }
    public void setGameBoard(GameBoardGame game)
    {
        this.game = game;
    }
    public void setLobbyController(GameLobbyController lobby) { this.lobbycontroller = lobby; }


    //Register & Login [Network package]
    public void RegisterMSG(String name, String password)
    {
        MainData data = new MainData(DataType.REGISTER_USER, name, password);
        this.sendMSG(data);
    }
    public void LoginMSG(String name, String password)
    {
        MainData data = new MainData(DataType.LOGIN_USER, name, password);
        this.sendMSG(data);
    }


    //Lobby Chat
    public void MsgSend (String msg)
    {
        if(this.oout != null)
        {
            MainData data = new MainData(DataType.LOBBY_CHAT, this.name, msg);
            this.sendMSG(data);
        }
        else
        {
            lobbycontroller.lobby_input_text("[HIBA] Elnézést kérjük! A szerver nem elérhető!");
        }

    }


    //Room creating... [New roomkey & get ip]
    public void RoomCreate(String roomkey)
    {
        MainData data = new MainData(DataType.ROOM_CREATE, name, roomkey);
        this.sendMSG(data);
    }


    //Room joining [Need roomkey]
    public void RoomJoin(String roomkey)
    {
        MainData data = new MainData(DataType.ROOM_JOIN, name, roomkey);
        this.sendMSG(data);
    }


    //Gameboard - Word checking [Network packet]
    public void GameMSG(String word,int type, String character)
    {
        MainData data = new MainData(DataType.WORD_CHECK, name, word, type, character);
        this.sendMSG(data);
    }


    //Sending data [Network packet]
    private void sendMSG(MainData msg)
    {
        try
        {
            this.oout.writeObject(msg);
        }
        catch(IOException IOE)
        {
            ;
        }
    }


    //Disconnect
    public void Exit()
    {
        if(this.oout != null)
        {
            MainData data = new MainData(DataType.DISCONNECT,this.name,"");
            this.sendMSG(data);
        }else
        {
            ;
        }

    }


}
