package com.ns_deik.ns_client.gameroom.room_create;


import com.ns_deik.ns_client.gameroom.Data;
import com.ns_deik.ns_client.gameroom.DataType;
import com.ns_deik.ns_client.gameroom.User;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;


public class GameRoomServer implements GRSInterface {

    private final static int PORT = 6667;
    private int min_players = 2;
    private int max_players = 8;

    private GameRoomCreateController controller;
    private String name;
    private String roomcode;

    private ServerListen serverlisten;

    private ArrayList<User> players;
    private ArrayList<ObjectOutputStream> writers;

    public GameRoomServer(GameRoomCreateController controller, String name, String room)
    {
        this.controller = controller;
        this.name = name;
        this.roomcode = room;

        this.players = new ArrayList<User>();
        User player = new User(name);
        player.set_ready(true);
        this.players.add(player);

        this.writers = new ArrayList<ObjectOutputStream>();
        this.writers.add(null);

        try
        {
            this.serverlisten = new ServerListen(PORT);
            this.serverlisten.start();
        }
        catch(IOException IOE)
        {
            ;
        }
    }

    private class ServerListen extends Thread
    {
        private ServerSocket srvsock;

        public ServerListen(int port) throws IOException
        {
            this.srvsock = new ServerSocket(port);
            System.out.println("Client server started");
        }

        @Override
        public void run()
        {
            try
            {
                while(true)
                {
                    new ClientHandler(this.srvsock.accept()).start();
                }
            }catch(SocketException SE)
            {
                ;
            }
            catch(IOException IOE)
            {
                ;
            }finally {
                try
                {
                    this.srvsock.close();
                }catch(IOException IOE)
                {
                    ;
                }
            }
        }

    }

    private class ClientHandler extends Thread
    {
        private Socket socket;

        private InputStream in;
        private ObjectInputStream oin;
        private OutputStream out;
        private ObjectOutputStream oout;

        public ClientHandler(Socket socket)
        {
            System.out.println("Server --> valaki csatlakozott...");
            this.socket = socket;
        }

        @Override
        public void run()
        {
            try
            {
                this.in = this.socket.getInputStream();
                this.oin = new ObjectInputStream(this.in);
                this.out = this.socket.getOutputStream();
                this.oout = new ObjectOutputStream(out);
                while(this.socket.isConnected())
                {
                    Data incomingmsg = (Data) this.oin.readObject();
                    if(incomingmsg != null)
                    {
                        switch(incomingmsg.getDataType())
                        {
                            case CONNECT:
                            {
                                Data msgreply = new Data();
                                User p = new User(incomingmsg.getName(), this.socket.getInetAddress());
                                players.add(p);
                                writers.add(this.oout);
                                controller.addPlayer(p);

                                msgreply.setDataType(DataType.PLAYER_JOIN);
                                msgreply.setName(incomingmsg.getName());
                                broadcastmsg(msgreply);

                                msgreply.setDataType(DataType.CONNECT_SUCCESS);
                                msgreply.setName(name);
                                msgreply.SetContent(getPlayerList());

                                controller.room_input("Szerver: [" +incomingmsg.getName() + "] játékos csatlakozott a szobához!");

                                this.oout.writeObject(msgreply);
                                break;
                            }
                            case DISCONNECT:
                            {
                                controller.room_input("Szerver: [" +incomingmsg.getName() +"] játékos lelépett!");
                                broadcastmsg(incomingmsg);
                                controller.removePlayer(incomingmsg.getName());

                                for(int i=1; i < players.size(); ++i)
                                {
                                    if(players.get(i).getname().equals(incomingmsg.getName()))
                                    {
                                        players.remove(i);
                                        writers.remove(i);
                                        break;
                                    }
                                }

                                socket.close();
                                break;
                            }
                            case CHAT:
                            {
                                controller.room_input(incomingmsg);
                                broadcastmsg(incomingmsg);
                                break;

                            }
                            default:
                            {
                                System.out.println("....");
                                break;
                            }

                        }
                    }
                }

            }
            catch(SocketException SE)
            {
                ;
            }catch(IOException IOE)
            {
                ;
            }catch(ClassNotFoundException CNFE)
            {
                ;
            }

        }
    }

    private void sendMSG(Data data)
    {
        for(int i = 1; i < this.players.size(); ++i)
        {
            try
            {
                this.writers.get(i).writeObject(data);
            }catch (IOException IOE)
            {
                ;
            }
        }
    }

    @Override
    public void MsgSend (String msg)
    {
        Data data = new Data(DataType.CHAT, this.name, msg);
        this.sendMSG(data);
        this.controller.room_input(data);
    }

    public boolean CheckReady()
    {
        return false;
    }

    public void Close()
    {
        ;
    }

    private void broadcastmsg(Data data)
    {
        for(int i=1; i < this.players.size(); i++)
        {
            if(!data.getName().equals(this.players.get(i).getname()))
            {
                try
                {
                    this.writers.get(i).writeObject(data);
                }
                catch(IOException IOE)
                {
                    ;
                }
            }
        }
    }

    private String getPlayerList()
    {
        String list = "";
        for(int i = 0; i < this.players.size();++i)
        {
            User p = this.players.get(i);
            list += p.getname() + "," + p.is_ready_check();
            list += (i == this.players.size() - 1 ? "" : ";" );
        }
        return list;
    }

}