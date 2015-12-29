package io.github.antijava.marjio.scene.sceneObject;

import java.util.UUID;

/**
 * Created by firejox on 2015/12/28.
 */
public class StatusData {
    public final int Player = 0;
    public final int Block  = 1;
    public final int Item   = 2;


    public boolean query;

    public UUID uuid;

    public int id;
    public int type;
    public int action_id;
    public int time_counter;
    public int st_x;
    public int st_y;

}
