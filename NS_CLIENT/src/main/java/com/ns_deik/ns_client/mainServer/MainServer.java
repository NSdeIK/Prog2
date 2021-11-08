package com.ns_deik.ns_client.mainServer;

import Server.*;
import com.ns_deik.ns_client.gameroom.Data;
import com.ns_deik.ns_client.gameroom.User;
import com.ns_deik.ns_client.gameroom.room_join.GameRoomJoinController;
import com.ns_deik.ns_client.homepage.GameController;
import com.ns_deik.ns_client.lobby.GameLobbyController;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class MainServer implements SInterface {

    private boolean network = false;
    private GameLobbyController lobbycontroller;
    private GameController gamecontroller;
    private GameRoomJoinController gameroomjoincontroller;
    private ClientListen clientlisten;
    private ArrayList<User> players;

    private String name;

    private InputStream in;
    private ObjectInputStream oin;
    private OutputStream out;
    private ObjectOutputStream oout;

    private String status = "";

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
                                //controller.username_setLabel();
                                //System.out.println("Siker!");
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
                                    //...
                                }
                                else
                                {
                                    lobbycontroller.lobby_input_text("Server: [" + incomingmsg.getName() + "] játékos lelépett!");
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
                                System.out.println("Rossz a jelszó!");
                                break;
                            }
                            case ROOM_JOIN:
                            {
                                if(status.equals("client"))
                                {
                                    gameroomjoincontroller.joinroom(incomingmsg.getContent());
                                    break;
                                }
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
                System.out.println(this.socket);
                network = false;
                ;
            }catch(ClassNotFoundException CNFE)
            {
            }
        }
    }

    public void setJoinController(GameRoomJoinController GRJC)
    {
        this.gameroomjoincontroller = GRJC;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

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

    public void RoomCreate(String roomkey)
    {
        MainData data = new MainData(DataType.ROOM_CREATE, name, roomkey);
        this.sendMSG(data);
    }

    public void RoomJoin(String roomkey)
    {
        MainData data = new MainData(DataType.ROOM_JOIN, name, roomkey);
        this.sendMSG(data);
    }

    @Override
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
}
