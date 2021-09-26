package com.ns_deik.ns_client.gameroom;

import java.net.InetAddress;

public class User
{
    private String name;
    private boolean check_ready;
    private InetAddress ip;

    public User(String name)
    {
        this.name = name;
        this.check_ready = false;
    }

    public User(String name, InetAddress ip)
    {
        this.name = name;
        this.check_ready = false;
        this.ip = ip;
    }

    public String getname()
    {
        return name;
    }

    public boolean is_ready_check()
    {
        return check_ready;
    }

    public void set_ready(boolean ready_check)
    {
        this.check_ready = ready_check;
    }

    public InetAddress getIP()
    {
        return ip;
    }

    public void setIP(InetAddress ip)
    {
        this.ip = ip;
    }
}
