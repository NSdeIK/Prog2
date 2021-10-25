package com.ns_deik.ns_client.gameroom.room_join;

import com.ns_deik.ns_client.gameroom.Data;
import com.ns_deik.ns_client.gameroom.DataType;
import com.ns_deik.ns_client.gameroom.User;
import javafx.scene.Group;
import javafx.scene.layout.GridPane;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GameRoomJoin implements GRJInterface
{
    private GameRoomJoinController controller;
    private ClientListen clientlisten;

    public void setcontroller(GameRoomJoinController controller)
    {
        this.controller = controller;
    }

    private String name;

    private InputStream in;
    private ObjectInputStream oin;
    private OutputStream out;
    private ObjectOutputStream oout;

    public GameRoomJoin(GameRoomJoinController controller, String room, String name)
    {
        this.controller = controller;
        this.name = name;
        this.clientlisten = new ClientListen("localhost",6667);
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
                this.socket = new Socket("localhost",6667);

                out = this.socket.getOutputStream();
                oout = new ObjectOutputStream(out);

                in = socket.getInputStream();
                oin = new ObjectInputStream(in);

                Data msg = new Data(DataType.CONNECT, name, "");
                oout.writeObject(msg);

                while(this.socket.isConnected())
                {
                    Data incomingmsg = (Data) oin.readObject();
                    System.out.println(incomingmsg.getPlayers());
                    if(incomingmsg.getDataType() != null)
                    {
                        switch (incomingmsg.getDataType())
                        {
                            case CONNECT_SUCCESS:
                            {
                                controller.room_client();
                                controller.updatePlayersList(PlayersList(incomingmsg.getContent()));
                                break;
                            }
                            case PLAYER_JOIN:
                            {
                                controller.room_input_text("Server: [" + incomingmsg.getName() + "] játékos csatlakozott a szobához!");
                                controller.addPlayer(new User(incomingmsg.getName()));
                                break;
                            }
                            case DISCONNECT:
                            {
                                if(incomingmsg.getName().equals(name))
                                {
                                    //...
                                    break;
                                }
                                else
                                {
                                    controller.room_input_text("Server: [" + incomingmsg.getName() + "] játékos lelépett!");
                                    controller.removePlayer(incomingmsg.getName());
                                    break;
                                }
                            }
                            case CHAT:
                            {
                                controller.room_input_text(incomingmsg);
                                break;
                            }
                            case START_GAME:
                            {
                                char[][] matrix = incomingmsg.getMatrix();
                                controller.GameBoard_Ready(matrix);
                                break;
                            }
                            case GAMEBOARD_GUI:
                            {
                                controller.GameBoard_Players(incomingmsg.getPlayers());
                                break;
                            }
                            case PLAYER_MOVEMENT:
                            {
                                double x = incomingmsg.getX();
                                double y = incomingmsg.getY();
                                controller.GameBoard_PlayerMovement(x,y, incomingmsg.getName());
                            }

                            default:
                            {
                                //System.out.println("... " + incomingmsg.toString());
                                break;
                            }
                        }
                    }
                }


            }catch(IOException IOE)
            {
                IOE.printStackTrace();
                controller.connect_failed();
            }catch(ClassNotFoundException CNFE)
            {
                CNFE.printStackTrace();
            }
        }
    }


    @Override
    public void MsgSend(String msg)
    {
        Data data = new Data(DataType.CHAT, this.name, msg );
        this.sendMSG(data);
        this.controller.room_input_text(data);
    }
    @Override
    public void gameplayermovement(double x, double y)
    {
        Data data = new Data(DataType.PLAYER_MOVEMENT, this.name, x,y);
        this.sendMSG(data);
    }

    @Override
    public void CheckReady(boolean ready)
    {
        ;
    }

    @Override
    public void Close()
    {
        Data msg = new Data(DataType.DISCONNECT, this.name, "");
        this.sendMSG(msg);
    }

    private List<User> PlayersList(String s)
    {
        List<User> list = new ArrayList<User>();

        String[] sTmp = s.split(";");
        for(int i=0; i< sTmp.length;i++)
        {
            String[] sName = sTmp[i].split(",");
            User u = new User(sName[0]);
            u.set_ready(Boolean.parseBoolean(sName[1]));
            list.add(u);
        }
        return list;
    }

    private void sendMSG(Data msg)
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
