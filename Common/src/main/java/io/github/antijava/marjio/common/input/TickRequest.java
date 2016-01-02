package io.github.antijava.marjio.common.input;

import io.github.antijava.marjio.common.network.Packable;

import java.util.UUID;

/**
 * Created by freyr on 2016/1/2.
 */
public class TickRequest implements Packable {

    private UUID mId;
    private int mStartTime;
    private int mReceiveTime;

    public TickRequest(int st) {
        mStartTime = st;
    }

    @Override
    public UUID getClientID() {
        return mId;
    }

    @Override
    public void setClientID(UUID id) {
        mId = id;
    }

    public int getStartTime() {
        return mStartTime;
    }

    public void setStartTime(int time) {
        mStartTime = time;
    }

    public int getReceiveTime() {
        return mReceiveTime;
    }

    public void setReceiveTime(int time) {
        mReceiveTime = time;
    }

    public int getTimeOffset(int time) {
        return (mStartTime - time) / 2;
    }

    public int getNewTime(int time) {
        return mReceiveTime + getTimeOffset(time);
    }
}
