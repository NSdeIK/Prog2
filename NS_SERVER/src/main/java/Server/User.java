package Server;

import java.net.InetAddress;

public class User {
        private String name;
        private boolean check_ready;
        private InetAddress ip;
        private String room = "";

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

        public String getIP()
        {
            return ip.getHostAddress();
        }

        public void setIP(InetAddress ip)
        {
            this.ip = ip;
        }

        public String getRoom() { return room; }

        public void setRoom(String room) { this.room = room; }
}
