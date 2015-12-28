package io.github.antijava.marjio.common.network;

import java.net.InetAddress;
import java.util.UUID;

/**
 * Created by fntsr on 2015/12/28.
 */
public class Player {
    private InetAddress mAddress;
    private UUID mClientID;
    private Object mTag;

    public Player(InetAddress address, UUID uuid) {
        mAddress = address;
        mClientID = uuid;
    }
    
    public UUID getClientID() {
        return mClientID;
    }

    public InetAddress getAddress() {
        return mAddress;
    }

    public Object getTag() {
        return mTag;
    }

    public void setTag(Object tag) {
        mTag = tag;
    }
}