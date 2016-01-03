package io.github.antijava.marjio.common.input;

import io.github.antijava.marjio.common.network.Packable;

import java.util.Collections;
import java.util.EnumSet;
import java.util.UUID;

/**
 * Created by firejox on 2015/12/25.
 */
public class Status implements Packable, IKeyInput {
    public Types mType;

    UUID mId;

    DataTypes mDataType;

    public int tick;
    public int x;
    public int y;
    public double vx;
    public double vy;
    public double ax;
    public double ay;

    public boolean query;

    public int send_tick;
    public int recieve_tick;

    public EnumSet<Key> pressed = EnumSet.noneOf(Key.class);
    public EnumSet<Key> pressing = EnumSet.noneOf(Key.class);
    public EnumSet<Key> released = EnumSet.noneOf(Key.class);
    public EnumSet<Key> repeat = EnumSet.noneOf(Key.class);


    public Status(DataTypes type) {
        mDataType = type;
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

    public DataTypes getDataType() {
        return mDataType;
    }

    public boolean isValidKeySets() {

        return Collections.disjoint(pressed, pressing) &&
                Collections.disjoint(pressing, released) &&
                Collections.disjoint(released, pressed);
    }

    @Override
    public boolean isPressing(final Key key) {
        return pressed.contains(key);
    }

    @Override
    public boolean isPressed(final Key key) {
        return pressing.contains(key);
    }

    @Override
    public boolean isReleased(final Key key) {
        return released.contains(key);
    }

    @Override
    public boolean isRepeat(final Key key) {
        return repeat.contains(key);
    }

    @Override
    public boolean isKeyUp(final Key key) {
        return !pressed.contains(key) && !pressing.contains(key);
    }

    @Override
    public boolean isKeyDown(Key key) {
        return pressed.contains(key) ||  pressing.contains(key);
    }

    public enum Types {
        ServerMessage,
        ServerVerification,
        ClientMessage;
    }

    public enum DataTypes {
        Player,
        Block,
        Item;
    }
}
