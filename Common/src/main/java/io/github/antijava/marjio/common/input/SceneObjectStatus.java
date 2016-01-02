package io.github.antijava.marjio.common.input;

import java.util.UUID;

/**
 * Created by Date on 2015/12/29.
 */
public class SceneObjectStatus {
    public enum Types {
        Player,
        Block,
        Item
    }
    public UUID uuid;
    public Types type;
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