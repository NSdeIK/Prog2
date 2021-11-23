package com.ns_deik.ns_client.gameroom.room_join;

import com.ns_deik.ns_client.gameroom.GameBoardGame;
import com.ns_deik.ns_client.gameroom.GameBoardMain;

public interface GRJInterface
{
    public void MsgSend(String msg);
    public void CheckReady(boolean ready);
    public void Close();
    public void gameplayermovement(double x, double y);
    public void WaitingPlayersReady();
    public void setGameBoard(GameBoardGame game);
    public void sendReady(boolean ready);
    public void sendPoints(int value);
}
