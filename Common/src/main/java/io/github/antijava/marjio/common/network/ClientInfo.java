package io.github.antijava.marjio.common.network;

import java.net.InetAddress;
import java.util.UUID;

/**
 * Created by fntsr on 2015/12/28.
 */
public class ClientInfo {
    private UUID mClientID;
    private Object mTag;

    public ClientInfo(UUID uuid) {
        mClientID = uuid;
    }
    
    public UUID getClientID() {
        return mClientID;
    }

    public Object getTag() {
        return mTag;
    }

    public void setTag(Object tag) {
        mTag = tag;
    }
}