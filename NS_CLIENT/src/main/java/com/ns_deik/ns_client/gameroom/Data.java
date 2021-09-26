package com.ns_deik.ns_client.gameroom;


import java.io.Serializable;

public class Data implements Serializable
{
    private DataType dataType;
    private String name;
    private String content;

    public Data()
    {}

    public Data(String type, String name, String content)
    {
        this.dataType = DataType.valueOf(type);
        this.name = name;
        this.content = content;
    }

    public Data(DataType type, String name, String content)
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

    public String getContent()
    {
        return content;
    }

    public void SetContent(String content)
    {
        this.content = content;
    }

    public String CheckConsole()
    {
        return this.name + "( " + this.dataType.toString() + " ):" + this.content;
    }

}
