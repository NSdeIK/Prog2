package com.ns_deik.ns_client.gameroom.room_create;

import java.util.ArrayList;

public interface GRSInterface
{
    public void MsgSend(String msg);
    public boolean CheckReady();
    public void Close();
    public void gameboardready(char[][] matrix);
    public void gameboardgui(ArrayList<String> players);
    public void gameplayermovement(double x, double y);
    public void waitingplayers();
    public void waitingplayersready();
}
