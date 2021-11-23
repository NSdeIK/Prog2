package com.ns_deik.ns_client.gameroom;

import java.net.InetAddress;

public class User
{

    //Variables
        private String name;
        private boolean check_ready;
        private InetAddress ip;

    //Constructor
        //Only name
        public User(String name)
        {
            this.name = name;
            this.check_ready = false;
        }

        //Name & Ip
        public User(String name, InetAddress ip)
        {
            this.name = name;
            this.check_ready = false;
            this.ip = ip;
        }

    //Get
        //Return, what is player is name?
        public String getname()
    {
        return name;
    }

        //Return, what is player is ready status?
        public boolean is_ready_check()
    {
        return check_ready;
    }

        //Return, what is the player is ip?
        public InetAddress getIP()
        {
            return ip;
        }

    //Set
        //Set ready status [true/false]?
        public void set_ready(boolean ready_check)
    {
        this.check_ready = ready_check;
    }

        //Set player ip [ xxx.xxx.xxx.xxx format (ipv4) | .... (ipv6)?]
        public void setIP(InetAddress ip)
        {
            this.ip = ip;
        }

}
