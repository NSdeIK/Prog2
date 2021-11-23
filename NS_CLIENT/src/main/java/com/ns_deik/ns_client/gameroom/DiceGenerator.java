package com.ns_deik.ns_client.gameroom;

import java.util.Random;

public class DiceGenerator
{

    //Variables
    private int gennum; //Generated num
    private int sides = 3; //Max 3 num value

    //Roll the dice
    public void DiceRoll()
    {
        //Random
        Random rnum = new Random();
        //1-2-3 one of the following
        gennum = rnum.nextInt(sides) + 1;
    }

    //Get generated num (1 | 2 | 3)
    public int getDiceRoll()
    {
        return gennum;
    }
}

