package io.github.antijava.marjio.common.input;

import io.github.antijava.marjio.common.network.Packable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Date on 2016/1/3.
 */
public class SyncList implements Packable {

    UUID mId;
    /**
     *  If you want to change back to Object,
     *   please see Status.
     **/
    ArrayList mData;

    public SyncList() {}

    public SyncList(ArrayList data) {
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

    public ArrayList getData() {
        return mData;
    }

}
