package Server;

import java.io.Serializable;
import java.net.InetAddress;

public class MainData implements Serializable
{
    private static final long serialVersionUID = 1L;
    private DataType dataType;
    private String name;
    private String room;
    private String content;
    private InetAddress ip;


    public MainData()
    {
    }

    public MainData(String type, String name, String content)
    {
        this.dataType = DataType.valueOf(type);
        this.name = name;
        this.content = content;
    }

    public MainData(DataType type, String name, String content)
    {
        this.dataType = type;
        this.name = name;
        this.content = content;
    }

    public DataType getDataType()
    {
    return dataType;
    }

    public void setDataType(DataType type)
    {
        this.dataType = type;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getRoom()
    {
        return room;
    }

    public void setRoom(String content)
    {
        this.room = room;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
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
