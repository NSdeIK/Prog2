package com.ns_deik.ns_client.gameroom.room_create;

public interface GRSInterface
{
    public void MsgSend(String msg);
    public boolean CheckReady();
    public void Close();
}
