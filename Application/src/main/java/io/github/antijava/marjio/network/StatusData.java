package io.github.antijava.marjio.network;

import com.instagram.common.json.annotation.JsonField;
import com.instagram.common.json.annotation.JsonType;
import io.github.antijava.marjio.common.input.Event;

import java.util.UUID;

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

    public UUID uuid;

    /*
     * DO NOT use this field directly, Set value to `uuid`.
     * When pack/unpack will fill `_uuid`/`uuid` field
     */
    @JsonField(fieldName = "uuid")
    public String _uuid;

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


    public StatusData PreparePack(){

        if(uuid != null)
            _uuid = uuid.toString();
        return this;
    }

    public StatusData AfterUnpack(){

        if(_uuid != null)
            uuid = UUID.fromString(_uuid);
        return this;
    }
}
