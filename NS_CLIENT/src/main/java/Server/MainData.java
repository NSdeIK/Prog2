package Server;

import java.io.Serializable;
import java.net.InetAddress;

public class MainData implements Serializable {

    //Variables
        //Strings
        private String name;
        private String room;
        private String content;
        private String character;

        //Integer
        private int num;

        //Networks & Packets
        private static final long serialVersionUID = 1L;
        private DataType dataType;
        private InetAddress ip;

    //Constructors

        //Default - Empty
        public MainData() {
        }

        // [String] => [DataType]
        public MainData(String type, String name, String content)
        {
            this.dataType = DataType.valueOf(type);
            this.name = name;
            this.content = content;
        }

        // Normal packet [type | name | content]
        public MainData(DataType type, String name, String content)
        {
            this.dataType = type;
            this.name = name;
            this.content = content;
        }

        // Word checking network packet [ type | name | content | word_num_type | char ]
        public MainData(DataType type, String name, String content, int num, String character)
        {
            this.dataType = type;
            this.name = name;
            this.content = content;
            this.num = num;
            this.character = character;
        }

    //Get methods
        //Strings
        public String getName()
        {
            return name;
        }
        public String getRoom()
        {
            return room;
        }
        public String getContent()
        {
            return content;
        }
        public String getString() { return character; }

        //Integer
        public int getNum() { return num; }

        //Networks
        public DataType getDataType()
        {
            return dataType;
        }
        public InetAddress getIP()
        {
            return ip;
        }

    //Set methods
        //Strings
        public void setName(String name)
    {
        this.name = name;
    }
        public void setRoom(String content)
    {
        this.room = room;
    }
        public void SetContent(String content)
    {
        this.content = content;
    }
        public void setString(String character) { this.character = character; }

        //Integer
        public void setNum(int num) { this.num = num; }

        //Networks
        public void setDataType(DataType type)
        {
            this.dataType = type;
        }
        public void setIP(InetAddress ip)
    {
        this.ip = ip;
    }

}
