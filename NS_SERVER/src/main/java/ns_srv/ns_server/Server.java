package ns_srv.ns_server;

import Server.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server extends Application implements ServerInterface {

    private Scene scene;

    private final static int port = 6666;


    private String name;

    private ServerSocket srvsock;
    private Socket socket;

    private ArrayList<User> players;
    private ArrayList<ObjectOutputStream> writers;

    private ServerListen serverListen;


    private boolean isClosed = false;

    @FXML
    Label status_label;

    @FXML
    Label clients_size;


    private boolean isClosed()
    {
        return this.isClosed;
    }

    @Override
    public void start(Stage stage) throws IOException
    {
        FXMLLoader Server_FXML = new FXMLLoader(Server.class.getResource("Server.fxml"));
        scene = new Scene(Server_FXML.load(), 400,480);
        stage.setTitle("[NS_DEIK] - Server");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    @FXML
    protected void button_on() {
        try
        {
        this.players = new ArrayList<User>();
        this.writers = new ArrayList<ObjectOutputStream>();

        this.serverListen = new ServerListen(port);
        this.serverListen.start();

        }catch(IOException IOE)
        {
            if(IOE.getMessage().contains("Address already in use"))
            {
                System.out.println("Már használatban van a port!");
            }
        }
        }

    private class ServerListen extends Thread
    {
        private ServerSocket srvsock;

        public ServerListen(int port) throws IOException
        {
            this.srvsock = new ServerSocket(port);
            System.out.println("A szerver elindult...");
            status_label.setText("On");
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
                System.out.println(this.getId()+": " +SE.getMessage());
            }catch(IOException IOE)
            {
                IOE.printStackTrace();
            }finally {
                try{
                    this.srvsock.close();
                }catch(IOException IOE)
                {
                    ;
                }
            }
        }

        public void Close() throws IOException {
            this.srvsock.close();
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
            System.out.println("Jelenleg valaki csatlakozott!");
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
                this.oout = new ObjectOutputStream(this.out);

                while(this.socket.isConnected())
                {
                    MainData incomingmsg = (MainData) this.oin.readObject();
                    if(incomingmsg != null)
                    {
                        System.out.println(incomingmsg.getName() +": " +incomingmsg.getDataType());
                        switch(incomingmsg.getDataType())
                        {
                            case CONNECT:
                            {
                                MainData msgreply = new MainData();
                                User p = new User(incomingmsg.getName(),this.socket.getInetAddress());
                                players.add(p);
                                writers.add(this.oout);

                                msgreply.setDataType(DataType.CONNECT_SUCCESS);
                                msgreply.setName(incomingmsg.getName());

                                this.oout.writeObject(msgreply);
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        clients_size.setText(String.valueOf(players.size()));
                                    }
                                });
                                System.out.println("Játékos száma: [" + players.size()+"]");
                                break;
                            }
                            case LOBBY_CHAT:
                            {
                                System.out.println(incomingmsg.getName() + ": " + incomingmsg.getContent());
                                broadcastmsg(incomingmsg);
                                break;
                            }
                            case DISCONNECT:
                            {
                                broadcastmsg(incomingmsg);

                                for(int i=0; i < players.size(); ++i)
                                {
                                    if(players.get(i).getname().equals(incomingmsg.getName()))
                                    {
                                        System.out.println("Egy játékos lelépett!\nElőtte: " + players.size());
                                        players.remove(i);
                                        writers.remove(i);
                                        Platform.runLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                clients_size.setText(String.valueOf(players.size()));
                                            }
                                        });
                                        System.out.println("Utána: " + players.size());
                                        break;
                                    }
                                }
                                socket.close();
                                break;
                            }

                            case PING:
                            {
                                break;
                            }
                            default:
                            {
                                break;
                            }
                        }
                    }
                }
            }catch(SocketException SE)
            {
                System.out.println("1." + SE.getMessage());
            }catch(IOException IOE)
            {
                System.out.println("2." + IOE.getMessage());
            }catch(ClassNotFoundException CNFE)
            {
                CNFE.printStackTrace();
            }
        }


    }

    public void broadcastmsg(MainData data)
    {
        for(int i=0; i < this.players.size();++i)
        {
            if(!data.getName().equals(this.players.get(i).getname()))
            {
                System.out.println(i + ": " + this.players.get(i).getname());
                try
                {
                    this.writers.get(i).writeObject(data);
                }catch(IOException IOE)
                {
                    ;
                }
            }
        }
    }

    @FXML
    protected void button_off() throws IOException
    {
        serverListen.Close();
        this.isClosed = true;
        status_label.setText("Off");
        players.clear();

        System.out.println("Sikeresen lekapcsolódott az összes kliens!");
        clients_size.setText(String.valueOf(players.size()));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
