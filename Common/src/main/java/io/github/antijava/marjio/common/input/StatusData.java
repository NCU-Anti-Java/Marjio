package io.github.antijava.marjio.common.input;

import java.util.UUID;

/**
 * Created by Date on 2015/12/29.
 */
public class StatusData {

    /*
     * Player 0
     * Block  1
     * Item   2
     */
    public static final int Player = 0;
    public static final int Block  = 1;
    public static final int Item   = 2;

    public UUID uuid;
    public int type;
    public int id;
    public int action_id;
    public int time_counter;
    public int st_x;
    public int st_y;
    public int x;
    public int y;
    public double vx;
    public double vy;
    public double ax;
    public double ay;
    public boolean query;
}