package com.ns_deik.ns_client.mainServer;

import Server.*;
import com.ns_deik.ns_client.gameroom.Data;
import com.ns_deik.ns_client.gameroom.User;
import com.ns_deik.ns_client.lobby.GameLobbyController;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class MainServer implements SInterface {

    private boolean network = false;
    private GameLobbyController controller;
    private ClientListen clientlisten;
    private ArrayList<User> players;

    private String name;

    private InputStream in;
    private ObjectInputStream oin;
    private OutputStream out;
    private ObjectOutputStream oout;

    public MainServer(GameLobbyController controller,String name)
    {
        this.controller = controller;
        this.name = name;
        this.clientlisten = new ClientListen("192.168.1.114",6666);
        this.clientlisten.start();
    }

    public MainServer(){};

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

                System.out.println(name);

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
                                controller.username_setLabel();
                                //System.out.println("Siker!");
                                break;
                            }
                            case LOBBY_CHAT:
                            {
                                controller.lobby_input_text(incomingmsg);
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
                                    controller.lobby_input_text("Server: [" + incomingmsg.getName() + "] játékos lelépett!");
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
            controller.lobby_input_text("[HIBA] Elnézést kérjük! A szerver nem elérhető!");
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
