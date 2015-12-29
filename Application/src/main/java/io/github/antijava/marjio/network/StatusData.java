package io.github.antijava.marjio.network;

import com.instagram.common.json.annotation.JsonField;
import com.instagram.common.json.annotation.JsonType;
import io.github.antijava.marjio.common.input.Event;

/**
 * Created by Date on 2015/12/29.
 */
@JsonType
public class StatusData {

    /*
     * Player 0
     * Block  1
     * Item   2
     */
    public static final int Player = 0;
    public static final int Block  = 1;
    public static final int Item   = 2;

    @JsonField(fieldName = "type")
    public int type;

    @JsonField(fieldName = "id")
    public int id;
    @JsonField(fieldName = "action_id")
    public int action_id;
    @JsonField(fieldName = "timer_counter")
    public int time_counter;
    @JsonField(fieldName = "st_x")
    public int st_x;
    @JsonField(fieldName = "st_y")
    public int st_y;

    public Event packToEvent(Event.Type type){

        return new Event(this, type);
    }
}
