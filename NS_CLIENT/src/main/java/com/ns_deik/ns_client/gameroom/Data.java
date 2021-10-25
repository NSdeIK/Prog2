package com.ns_deik.ns_client.gameroom;

import java.io.Serializable;
import java.util.ArrayList;

public class Data implements Serializable
{
    private static final long serialVersionUID = 1L;
    private DataType dataType;
    private String name;
    private String content;
    private char[][] matrix;
    private ArrayList players = new ArrayList();
    private double x,y;

    public Data()
    {}

    public Data(String type, String name, String content)
    {
        this.dataType = DataType.valueOf(type);
        this.name = name;
        this.content = content;
    }

    public Data(DataType type, String name, char [][] matrix)
    {
        this.dataType = type;
        this.name = name;
        this.matrix = matrix;
    }

    public Data(DataType type, String name, double x, double y)
    {
        this.dataType = type;
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public Data(DataType type, String name, ArrayList players)
    {
        this.dataType = type;
        this.name = name;
        this.players = players;
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

    public char[][] getMatrix()
    {
        return matrix;
    }

    public double getX() { return x; }
    public double getY() { return y; }

    public void setMatrix(char[][] matrix)
    {
        this.matrix = matrix;
    }

    public ArrayList getPlayers() { return players; }

    public void setGrp(ArrayList players) { this.players = players; }

    public String CheckConsole()
    {
        return this.name + "( " + this.dataType.toString() + " ):" + this.content;
    }

}
