package ns_srv.ns_server;

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
import java.util.List;

public class Server extends Application {

    private Scene scene;

    private final static int port = 6666;

    private ServerSocket srvsock;
    private Socket socket;

    private final ArrayList<Socket> clients = new ArrayList<>();
    private final List<Clients_Lobby> clientslist = new ArrayList<Clients_Lobby>();

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
    protected void button_on() throws IOException {

        srvsock = new ServerSocket(port);
        System.out.println("A szerver elindult 6666-os porttal!");
        status_label.setText("On");
        new Thread(() ->
        {
            while (!isClosed())
            {
            socket = null;
            try {
                socket = this.srvsock.accept();
                System.out.println("Kliens csatlakozott a szerverre: " + socket.getRemoteSocketAddress());

                Clients_Lobby cl = new Clients_Lobby(socket, this);
                clients.add(this.socket);
                clientslist.add(cl);

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        clients_size.setText(String.valueOf(clients.size()));
                    }
                });

                Thread t2 = new Thread(cl);
                t2.start();

            } catch (IOException IOE)
                {
                if (isClosed())
                    {
                        System.out.println("A szerver leállt!");
                        break;
                    }
                }
             }
        }).start();

        }


    public void broadcast(String msg)
    {
        System.out.println(msg);
        for(Clients_Lobby cl : this.clientslist)
        {
            cl.Send_MSG_Client(msg);
        }
    }

    class Clients extends Thread
    {
        final Socket s;
        final DataInputStream fromClient;
        final DataOutputStream toClient;

        Clients(Socket s,DataOutputStream p,DataInputStream b)
        {
            this.s = s;
            this.toClient = p;
            this.fromClient = b;
            clients.add(s);
            System.out.println(s);
        }

        @Override
        public void run()
        {
            String fromclient_msg;
            while(true)
            {
                try
                {
                    if(this.s.isConnected()) {
                        fromclient_msg = fromClient.readUTF();
                        System.out.println(fromclient_msg);
                        if (fromclient_msg.equals("Closed_client")) {
                            System.out.println("A kliens " + this.s + "kilépett");
                            clients.remove(this.s);

                            Platform.runLater(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    clients_size.setText(String.valueOf(clients.size()));
                                }
                            });

                            toClient.writeUTF("Rendben!");

                            this.s.close();
                            this.toClient.close();
                            this.fromClient.close();

                            break;
                        }
                    }
                }
                catch(IOException IOE)
                {
                    //IOE.printStackTrace();
                }
            }
            try
            {
                this.toClient.close();
                this.fromClient.close();
            }
            catch(IOException IOE)
            {
                IOE.printStackTrace();
            }
        }

    }

    @FXML
    protected void button_off() throws IOException
    {
        this.isClosed = true;
        srvsock.close();
        status_label.setText("Off");

        for(Socket socket : clients)
        {
            socket.close();
        }

        clientslist.clear();
        clients.clear();

        System.out.println("Sikeresen lekapcsolódott az összes kliens!");
        clients_size.setText(String.valueOf(clients.size()));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
