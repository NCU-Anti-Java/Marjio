package io.github.antijava.marjio.network;

import com.instagram.common.json.annotation.JsonField;
import com.instagram.common.json.annotation.JsonType;

/**
 * Created by Date on 2015/12/29.
 */
@JsonType
public class Status {

    public static final int ServerMessage = 0;
    public static final int ServerVerification = 1;
    public static final int ClientMessage = 2;

    /*
     * ServerMessage: 0
     * ServerVerification: 1
     * ClientMessage :2
     */
    @JsonField(fieldName = "type")
    int type;
    @JsonField(fieldName = "statusData")
    StatusData statusData;

    public Status(){

    }

    public Status(StatusData statusData, int type){
        this.statusData = statusData;
        this.type = type;
    }

    public Object getData() {

        return statusData;
    }

    public int getType() {

        return this.type;
    }

    public Status PreparePack() {

        statusData.PreparePack();
        return this;
    }

    public Status AfterUnpack() {

        statusData.AfterUnpack();
        return this;
    }
}
