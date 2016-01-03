package io.github.antijava.marjio.common.input;

import io.github.antijava.marjio.common.network.Packable;

import java.util.List;
import java.util.UUID;

/**
 * Created by Date on 2016/1/3.
 */
public class SyncList implements Packable{

    UUID mId;
    Object mData;

    public SyncList(Object data) {
        mData = data;
    }

    @Override
    public UUID getClientID() {
        return mId;
    }

    @Override
    public void setClientID(UUID id) {
        mId = id;
    }

    public Object getData() {
        return mData;
    }
}
