package com.ns_deik.ns_client.gameroom.room_join;

import com.ns_deik.ns_client.gameroom.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GameRoomJoin implements GRJInterface
{

    //String
    private String name;

    //Controllers
    private GameBoardGame game;
    private GameRoomJoinController controller;

    //Networks
    private ClientListen clientlisten;
    private InputStream in;
    private ObjectInputStream oin;
    private OutputStream out;
    private ObjectOutputStream oout;

    //Constructors [ room_join controller | room code | name | ip ] (Joining the room with ip socket + fix port)
    public GameRoomJoin(GameRoomJoinController controller, String room, String name, String ip)
    {
        this.controller = controller;
        this.name = name;
        this.clientlisten = new ClientListen(ip,6667);
        this.clientlisten.start();
    }

    //Get
    private List<User> PlayersList(String s)
    {
        List<User> list = new ArrayList<>();              // New arraylist
        String[] sTmp = s.split(";");               // Example [string s] => [ test, true; test2, false ]
        for(int i=0; i< sTmp.length;i++)                  // Example [s.split] => [ test, true ]
        {
            String[] sName = sTmp[i].split(",");    // Example [sTmp[i] => [ test ]
            User u = new User(sName[0]);                  // User class => store name variable ( test )
            u.set_ready(Boolean.parseBoolean(sName[1]));  // User class => set ready status ( true | false )
            list.add(u);                                  // Arraylist add user
        }
        return list;                                      //Return list
    }

    //Set [Join/Gameboard controllers]
    public void setcontroller(GameRoomJoinController controller)
    {
        this.controller = controller;
    }
    public void setGameBoard(GameBoardGame game)
{
    this.game = game;
}

    //Network packets
        //SEND MSG TO SERVER
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

        //ROOM CHAT
        public void MsgSend(String msg)
        {
            Data data = new Data(DataType.CHAT, this.name, msg );
            this.sendMSG(data);
            this.controller.room_input_text(data);
        }

        //PLAYER MOVEMENT
        public void gameplayermovement(double x, double y)
        {
            Data data = new Data(DataType.PLAYER_MOVEMENT, this.name, x,y);
            this.sendMSG(data);
        }

        //WAITING PLAYERS READY
        public void WaitingPlayersReady()
        {
            Data data = new Data(DataType.WAITING_PLAYERS_READY, this.name, "");
            this.sendMSG(data);
        }

        //CHECK READY [TODO - Ready - Lobby]
        public void CheckReady(boolean ready)
        {
            ;
        }

        //DISCONNECT
        public void Close()
        {
            Data msg = new Data(DataType.DISCONNECT, this.name, "");
            this.sendMSG(msg);
        }

        //PLAYER READY/NOT READY
        public void sendReady(boolean ready)
        {
            Data data = new Data(DataType.READY, this.name, "" + ready);
            this.sendMSG(data);
        }

        //SEND POINTS
        public void sendPoints(int value)
        {
            Data data = new Data(DataType.POINTS, name, value);
            this.sendMSG(data);
        }


    //Network [Client-Server communication]
    private class ClientListen extends Thread
    {
        //Network variables
        private Socket socket;
        private String address;
        private int port;

        //Constructor
        public ClientListen(String address, int port)
        {
            this.address = address;
            this.port = port;
        }

        //Thread run
        @Override
        public void run()
        {
            try
            {
                //Socket connect to server (ip + fix port)
                this.socket = new Socket(address,6667);

                //Network communication
                out = this.socket.getOutputStream();
                oout = new ObjectOutputStream(out);
                in = socket.getInputStream();
                oin = new ObjectInputStream(in);

                //Connect to server => Network packet [CONNECT TYPE]
                Data msg = new Data(DataType.CONNECT, name, "");
                oout.writeObject(msg);

                //If success => while is connected [true]
                while(this.socket.isConnected())
                {
                    //Incoming msg from server (room creator)
                    Data incomingmsg = (Data) oin.readObject();

                    //Incoming msg != null
                    if(incomingmsg.getDataType() != null)
                    {
                        //WWhat is the type?
                        switch (incomingmsg.getDataType())
                        {
                            // Server replied that it was successful
                            case CONNECT_SUCCESS:
                            {
                                controller.room_client();
                                controller.updatePlayersList(PlayersList(incomingmsg.getContent()));
                                break;
                            }

                            // Server broadcasted other players is connected
                            case PLAYER_JOIN:
                            {
                                controller.room_input_text("Server: [" + incomingmsg.getName() + "] játékos csatlakozott a szobához!");
                                controller.addPlayer(new User(incomingmsg.getName()));
                                break;
                            }

                            // Server disconnected [TODO fix..]
                            case DISCONNECT:
                            {
                                if(incomingmsg.getName().equals(name))
                                {
                                    controller.return_lobby(incomingmsg.getContent());
                                    break;
                                }
                                else
                                {
                                    controller.room_input_text("Server: [" + incomingmsg.getName() + "] játékos lelépett!");
                                    controller.removePlayer(incomingmsg.getName());
                                    System.out.println("A szerver szoba bezárult!");
                                    break;
                                }
                            }

                            // Room chat
                            case CHAT:
                            {
                                controller.room_input_text(incomingmsg);
                                break;
                            }

                            // Game started
                            case START_GAME:
                            {
                                String[][] matrix = incomingmsg.getMatrix();
                                controller.GameBoard_Ready(matrix);
                                break;
                            }

                            // Game gui...
                            case GAMEBOARD_GUI:
                            {
                                controller.GameBoard_Players(incomingmsg.getPlayers());
                                break;
                            }

                            // New gameboard grid
                            case GAMEBOARD_GUI_NEW:
                            {
                                controller.GameBoard_NEW(incomingmsg.getMatrix());
                                break;
                            }

                            // Players moves
                            case PLAYER_MOVEMENT:
                            {
                                double x = incomingmsg.getX();
                                double y = incomingmsg.getY();
                                controller.GameBoard_PlayerMovement(x,y, incomingmsg.getName());
                                break;
                            }

                            // Waiting players status...
                            case WAITING_PLAYERS:
                            {
                                controller.GameBoard_WaitingPlayers();
                                break;
                            }

                            // Ready players [WAITING]
                            case WAITING_PLAYERS_READY:
                            {
                                controller.GameBoard_WaitingPlayersReady();
                                break;
                            }

                            // Game timer [Timezone]
                            case GAME_TIMER:
                            {
                                game.setPlustime(incomingmsg.getGametimer());
                                break;
                            }

                            case READY:
                            {
                                controller.updateReady(incomingmsg.getName(),Boolean.parseBoolean(incomingmsg.getContent()));
                                break;
                            }

                            // ??
                            default:
                            {
                                break;
                            }
                        }
                    }
                }
                //TODO [ Handling error... ]
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

}
