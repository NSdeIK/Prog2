package com.ns_deik.ns_client.gameroom;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.ArrayList;

public class Data implements Serializable
{

    //Variables
        //Strings
        private String name;
        private String content;

        //Value
        private int value;

        //Double
        private double x,y;

        //DataType
        private DataType dataType;

        //Packet serialversion
        private static final long serialVersionUID = 1L;

        //Gameboard matrix grid
        private String[][] matrix = new String[4][5];

        //ArrayList
        private ArrayList players = new ArrayList();

        //LocalTime - Timezone
        private LocalTime gametimer;


    //Constructors
        //Empty
        public Data()
        {}

        //Default - String type
        public Data(String type, String name, String content)
        {
            this.dataType = DataType.valueOf(type);
            this.name = name;
            this.content = content;
        }

        //Default - Datatype type
        public Data(DataType type, String name, String content)
        {
            this.dataType = type;
            this.name = name;
            this.content = content;
        }

        //Server -> Client (default + matrix)
        public Data(DataType type, String name, String [][] matrix)
        {
            this.dataType = type;
            this.name = name;
            for(int i=0; i<4; i++)
            {
                for(int j=0; j<5; j++)
                {
                    this.matrix[i][j] = matrix[i][j];
                }
            }
        }

        //Server -> Client | Client -> Server (default, player x,y movements)
        public Data(DataType type, String name, double x, double y)
        {
            this.dataType = type;
            this.name = name;
            this.x = x;
            this.y = y;
        }

        //Server -> Client (default + players)
        public Data(DataType type, String name, ArrayList players)
        {
            this.dataType = type;
            this.name = name;
            this.players = players;
        }

        //Server -> Client (default + localtime)
        public Data(DataType type, String name, LocalTime gametimer)
        {
            this.dataType = type;
            this.name = name;
            this.gametimer = gametimer;
        }

        //Points...
        public Data(DataType type, String name, int value)
        {
            this.dataType = type;
            this.name = name;
            this.value = value;
        }

        //Get
        public String getName()
        {
            return name;
        }
        public String getContent()
    {
        return content;
    }
        public int getValue() { return value; }
        public double getX() { return x; }
        public double getY() { return y; }
        public String[][] getMatrix()
    {
        return matrix;
    }
        public ArrayList getPlayers() { return players; }
        public DataType getDataType()
        {
            return dataType;
        }
        public LocalTime getGametimer() { return gametimer; }
        public String CheckConsole()
    {
        return this.name + "( " + this.dataType.toString() + " ):" + this.content;
    }


        //Set
        public void setName(String name)
        {
            this.name = name;
        }
        public void SetContent(String content)
    {
        this.content = content;
    }
        public void setDataType(DataType type)
        {
            this.dataType = type;
        }
        public void setMatrix(String[][] matrix)
        {
            this.matrix = matrix;
        }
        public void setGrp(ArrayList players) { this.players = players; }
        public void setGametimer(LocalTime gametimer) { this.gametimer = gametimer; }

}
