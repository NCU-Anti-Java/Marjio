package io.github.antijava.marjio.common.network;

import java.util.UUID;

/**
 * Created by fntsr on 2016/1/2.
 */
public class RequestData implements Packable {
    public String uuid;
    public String type;

    @Override
    public UUID getClientID() {
        return null;
    }

    @Override
    public void setClientID(UUID id) {

    }
}
