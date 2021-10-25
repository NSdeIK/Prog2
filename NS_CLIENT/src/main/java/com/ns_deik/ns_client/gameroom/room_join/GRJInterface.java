package com.ns_deik.ns_client.gameroom.room_join;

public interface GRJInterface
{
    public void MsgSend(String msg);
    public void CheckReady(boolean ready);
    public void Close();
    public void gameplayermovement(double x, double y);
}
