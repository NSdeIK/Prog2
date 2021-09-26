package ns_srv.ns_server;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Clients_Lobby implements Runnable
{
    Socket socket;
    Server server;
    DataInputStream in;
    DataOutputStream out;

    Clients_Lobby(Socket socket, Server server)
    {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run()
    {
        try
        {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            while(true)
            {
                String msg = in.readUTF();
                server.broadcast(msg);
            }

        }catch(IOException IOE)
        {
            ;
        }
    }

    public void Send_MSG_Client(String msg)
    {
        try
        {
            out.writeUTF(msg);
            out.flush();
        }catch(IOException IOE)
        {
            ;
        }
    }
}
