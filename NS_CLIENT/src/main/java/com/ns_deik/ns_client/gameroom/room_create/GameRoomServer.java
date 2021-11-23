package com.ns_deik.ns_client.gameroom.room_create;

import com.ns_deik.ns_client.gameroom.Data;
import com.ns_deik.ns_client.gameroom.DataType;
import com.ns_deik.ns_client.gameroom.User;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalTime;
import java.util.ArrayList;


public class GameRoomServer implements GRSInterface {

    //Variables
        //Strings
        private String name;
        private String roomcode;


        //Integer
        private final static int PORT = 6667;
        private int min_players = 2;
        private int max_players = 8;


        //Controllers
        private GameRoomCreateController controller;


        //ArrayList
        private ArrayList<User> players;
        private ArrayList<ObjectOutputStream> writers;


        //Thread ...
        private ServerListen serverlisten;


        //Constructor [ client will be the server ]
        public GameRoomServer(GameRoomCreateController controller, String name, String room)
        {
            this.controller = controller;
            this.name = name;
            this.roomcode = room;

            this.players = new ArrayList<User>();
            User p = new User(name);
            p.set_ready(true);
            this.players.add(p);

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


        //Get
        public boolean CheckReady()
        {
            return false;
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

        private String getReadyPlayers()
        {
            String list = "";
            for(int i = 0; i < this.players.size();++i)
            {
                User p = this.players.get(i);
                list += p.getname();
                list += (i == this.players.size() - 1 ? "" : ";" );
            }
            return list;
        }


        //Server listening accept [Thread] => ClientHandler class
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

            public void CloseServer()
            {
                try
                {
                    this.srvsock.close();
                }catch(IOException IOE)
                {
                    System.out.println("//CLOSE SERVER ERROR//");
                    IOE.printStackTrace();
                }
            }

        }


        //Client handling [Thread] [TODO - Fix socket problem...]
        private class ClientHandler extends Thread
        {
            //Network
            private Socket socket;
            private InputStream in;
            private ObjectInputStream oin;
            private OutputStream out;
            private ObjectOutputStream oout;


            //Constructor
            public ClientHandler(Socket socket)
            {
                this.socket = socket;
            }


            //Thread run
            @Override
            public void run()
            {
                try
                {
                    // Network [Receive/Send]
                    this.in = this.socket.getInputStream();
                    this.oin = new ObjectInputStream(this.in);
                    this.out = this.socket.getOutputStream();
                    this.oout = new ObjectOutputStream(this.out);

                    // Socket connected [if success] => while true
                    while(this.socket.isConnected())
                    {
                        // Incoming msg from client
                        Data incomingmsg = (Data) this.oin.readObject();

                        // Incoming msg is null => drop packet | If not null => Reading packet(s)
                        if(incomingmsg != null)
                        {
                            // Packet type?
                            switch(incomingmsg.getDataType())
                            {
                                // Client connecting to server
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
                                    //System.out.println("Utána: "+players.size());
                                    break;
                                }

                                // Client disconnecting...
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

                                // Client sent message...
                                case CHAT:
                                {
                                    controller.room_input(incomingmsg);
                                    broadcastmsg(incomingmsg);
                                    break;

                                }

                                // Client/Player moves on the gameboard...
                                case PLAYER_MOVEMENT:
                                {
                                    double x = incomingmsg.getX();
                                    double y = incomingmsg.getY();
                                    controller.GameBoard_PlayerMovement(x,y, incomingmsg.getName());
                                    broadcastmsg(incomingmsg);
                                    break;
                                }

                                // Players ready...[Waiting status]
                                case WAITING_PLAYERS_READY:
                                {
                                    controller.GameBoard_WaitingPlayersReady();
                                    break;
                                }

                                // Player ready - Room
                                case READY:
                                {
                                    for (User u: players)
                                    {
                                        if(u.getname().equals(incomingmsg.getName()))
                                        {
                                            u.set_ready(Boolean.parseBoolean(incomingmsg.getContent()));
                                            break;
                                        }
                                    }

                                    controller.updateReady(incomingmsg.getName(), Boolean.parseBoolean(incomingmsg.getContent()));

                                    broadcastmsg(incomingmsg);
                                    break;
                                }

                                case POINTS:
                                {
                                    controller.Points(incomingmsg.getName(), incomingmsg.getValue());
                                    break;
                                }

                                // I dont know what type? - Do nothing, or feedback
                                default:
                                {
                                    break;
                                }

                            }
                        }
                    }

                }//TODO [Handling error problems...]
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


        //Send all players [NETWORK PACKET]
        private void sendMSG(Data data)
        {
            for(int i = 1; i < this.players.size(); i++)
            {
                try
                {
                    this.writers.get(i).writeObject(data);
                }catch (IOException IOE)
                {
                    IOE.printStackTrace();
                }
            }
        }


        //NETWORK PACKETS
            //BROADCAST
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
                            IOE.getMessage();
                        }
                    }
                }
            }

            //CHAT
            public void MsgSend (String msg)
            {
                Data data = new Data(DataType.CHAT, this.name, msg);
                this.sendMSG(data);
                this.controller.room_input(data);
            }

            //GAMEBOARD
                //NEW GUI - BOARD GRID
                public void gameboardguinew(String [][] matrix)
                {
                    Data data = new Data(DataType.GAMEBOARD_GUI_NEW, name, matrix);
                    broadcastmsg(data);
                }

                //
                public void gameboardgui(ArrayList<String> players)
                {
                    Data data = new Data(DataType.GAMEBOARD_GUI, name, players);
                    broadcastmsg(data);
                }

                //START GAME
                public void gameboardready(String[][] matrix)
                {
                    Data data = new Data(DataType.START_GAME, name, matrix);
                    broadcastmsg(data);
                }

                //PLAYER MOVEMENT
                public void gameplayermovement(double x, double y)
                {
                    Data data = new Data(DataType.PLAYER_MOVEMENT, name, x,y);
                    broadcastmsg(data);
                }

                //WAITING PLAYERS
                public void waitingplayers()
                {
                    Data data = new Data(DataType.WAITING_PLAYERS,name,"");
                    broadcastmsg(data);
                }

                //WAITING PLAYERS READY
                public void waitingplayersready()
                {
                    Data data = new Data(DataType.WAITING_PLAYERS_READY,name,"");
                    broadcastmsg(data);
                }

                //GAME TIMER
                public void GameTimerMSG(LocalTime time)
                {
                    Data data = new Data(DataType.GAME_TIMER, name, time);
                    this.sendMSG(data);
                }

                //If server down
                public void Close()
                {
                    Data data = new Data(DataType.DISCONNECT, name, roomcode + " kódú szoba bezárult...["+name+"] kilépett!");
                    for(int i=1; i < this.players.size(); i++)
                    {
                        data.setName(this.players.get(i).getname());
                        try{
                            this.writers.get(i).writeObject(data);
                        }catch(IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    this.serverlisten.CloseServer();
                }

}
