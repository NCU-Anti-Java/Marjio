package io.github.antijava.marjio.common.input;

import io.github.antijava.marjio.common.network.Packable;

/**
 * Created by fntsr on 2015/12/31.
 */
public class Request implements Packable {
    private Types mType;

    public Request(Types type) {
        mType = type;
    }

    public Types getType() {
        return mType;
    }

    public enum Types {
        ClientWannaJoinRoom,
        ClientCanJoinRoom;
    }


}
