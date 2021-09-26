package com.ns_deik.ns_client.gameroom.room_join;

import com.ns_deik.ns_client.gameroom.Data;
import com.ns_deik.ns_client.gameroom.DataType;
import com.ns_deik.ns_client.gameroom.User;
import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GameRoomJoin implements GRJInterface
{
    private GameRoomJoinController controller;
    private ClientListen clientlisten;

    private String name;

    private InputStream in;
    private ObjectInputStream oin;
    private OutputStream out;
    private ObjectOutputStream oout;

    public GameRoomJoin(GameRoomJoinController controller, String room, String name)
    {
        this.controller = controller;
        this.name = name;
        this.clientlisten = new ClientListen("192.168.1.89",6667);
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
                this.socket = new Socket("192.168.1.89",6667);

                out = this.socket.getOutputStream();
                oout = new ObjectOutputStream(out);
                in = this.socket.getInputStream();
                oin = new ObjectInputStream(in);

                Data msg = new Data(DataType.CONNECT, name, "");
                oout.writeObject(msg);

                while(this.socket.isConnected())
                {
                    Data incomingmsg = (Data) oin.readObject();
                    if(incomingmsg != null)
                    {
                        switch (incomingmsg.getDataType())
                        {
                            case CONNECT_SUCCESS:
                            {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        try
                                        {
                                            controller.room_client();
                                            //controller.resetList();
                                            controller.updatePlayersList(PlayersList(incomingmsg.getContent()));
                                        }catch(IOException IOE)
                                        {
                                            ;
                                        }

                                    }
                                });

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
                                }
                                else
                                {
                                    controller.room_input_text("Server: [" + incomingmsg.getName() + "] játékos lelépett!");
                                    controller.removePlayer(incomingmsg.getName());
                                }
                            }
                            case CHAT:
                            {
                                controller.room_input_text(incomingmsg);
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
                ;
            }catch(ClassNotFoundException CNFE)
            {
                ;
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
