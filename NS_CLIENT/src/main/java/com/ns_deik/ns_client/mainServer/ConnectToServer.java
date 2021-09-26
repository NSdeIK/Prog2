package com.ns_deik.ns_client.mainServer;

import java.io.*;
import java.net.*;

public class ConnectToServer {
    private boolean status = false;
    private Socket clientsock;
    private Thread th;
    private DataOutputStream toServer = null;
    private InputStreamReader fromServer = null;

    private InetAddress host;
    private int port;

    public Socket getSocket()
    {
        return clientsock;
    }

    public boolean check_server_status()
    {
        return status;
    }

    public void closed_client(boolean close) throws IOException {
        try
        {
            if(close == true)
            {
                if(this.clientsock != null)
                {
                    toServer.writeUTF("Closed_client");
                    this.toServer.close();
                    this.fromServer.close();
                    this.clientsock.close();
                    System.exit(0);
                }
            }
        }
        catch(IOException IOE)
        {
            this.clientsock.close();
            //IOE.printStackTrace();
        }

    }

    public ConnectToServer(InetAddress host, int port)
    {
        this.host = host;
        this.port = port;
        //WARNING - THREAD
        th = new Thread() {
            @Override
            public void run()
            {
                System.out.println("Csatlakozás...");
                connect();
                while (true)
                {
                    if(clientsock != null)
                    {
                            //check();
                    }
                    try
                    {
                        if(clientsock == null)
                        {
                            connect();
                        }

                        Thread.sleep(4000);
                    }
                    catch (Exception e)
                    {
                        ;
                    }
                }
            }
        };
        th.start();
    }

    public void connect(){
        try
        {
            clientsock = new Socket(host, port);
            status = true;
            toServer = new DataOutputStream(clientsock.getOutputStream());
            fromServer = new InputStreamReader(clientsock.getInputStream());
        }
        catch (IOException IOE)
        {
            status = false;
            try
            {
                if(clientsock != null)
                {
                    clientsock.close();
                }

                System.out.println("Nem elérhető a szerver!");

            }catch(IOException IOE2)
            {
                IOE2.printStackTrace();
            }
        }
    }

    public void check() {
        try {
            clientsock.setSoTimeout(1);
            int response = fromServer.read();
            System.out.println(response);
            if (response == -1) {
                System.out.println("Újracsatlakozás...\n");
                try
                {
                    connect();
                }
                catch (Exception e)
                {
                    System.out.println("Hiba - Kód --> [CTS-CHECK]\n");
                }
            }
            clientsock.setSoTimeout(0);
        }
        catch (SocketException e)
        {
           //If error --> its good signal (server is online)
        }
        catch (IOException e)
        {
            //e.printStackTrace();
        }
    }
}
