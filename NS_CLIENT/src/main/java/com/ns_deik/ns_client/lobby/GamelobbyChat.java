package com.ns_deik.ns_client.lobby;

import javafx.application.Platform;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class GamelobbyChat implements Runnable
{
    private Socket socket;
    private GameLobbyController client;
    private DataInputStream in = null;
    private DataOutputStream out = null;

    GamelobbyChat(Socket socket, GameLobbyController client)
    {
        this.socket = socket;
        this.client = client;
    }

    @Override
    public void run()
    {
        client.gamelobbytextarea.appendText("[SERVER]: Üdvözöllek a lobby chaten! \\^o^/\n");
            while(true)
            {
                try {
                    in = new DataInputStream(socket.getInputStream());
                    String msg = in.readUTF();
                    System.out.println(msg);
                    Platform.runLater(() -> {
                        client.gamelobbytextarea.appendText(msg + "");});

                }catch(IOException IOE)
                {
                    ;
                }
            }

    }
}
