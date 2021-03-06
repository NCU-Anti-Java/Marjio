package io.github.antijava.marjio.common.input;

import io.github.antijava.marjio.common.network.Packable;

import java.util.Collections;
import java.util.EnumSet;
import java.util.UUID;

/**
 * Created by firejox on 2015/12/25.
 */
public class Status implements Packable {
    public Types mType;

    public UUID mId;



    public Status(UUID id) {
        mId = id;
    }

    @Override
    public UUID getClientID() {
        return mId;
    }

    @Override
    public void setClientID(UUID id) {
        mId = id;
    }

    public Types getType() {
        return mType;
    }

    public void setType(final Types type) {
        mType = type;
    }


    public enum Types {
        ServerMessage,
        ServerVerification,
        ClientMessage;
    }


}
