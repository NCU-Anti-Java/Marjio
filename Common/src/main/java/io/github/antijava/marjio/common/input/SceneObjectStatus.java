package io.github.antijava.marjio.common.input;

import java.io.Serializable;
import java.util.Collections;
import java.util.EnumSet;
import java.util.UUID;

/**
 * Created by Date on 2015/12/29.
 */
public class SceneObjectStatus extends Status implements IKeyInput {

    public SceneObjectTypes mDataType;

    public int tick;
    public int x;
    public int y;
    public double vx;
    public double vxm;
    public double vy;
    public double vym;
    public double ax;
    public double ay;

    public String item_have;

    public boolean query;

    public int send_tick;
    public int recieve_tick;

    public EnumSet<Key> pressed = EnumSet.noneOf(Key.class);
    public EnumSet<Key> pressing = EnumSet.noneOf(Key.class);
    public EnumSet<Key> released = EnumSet.noneOf(Key.class);
    public EnumSet<Key> repeat = EnumSet.noneOf(Key.class);

    public enum SceneObjectTypes {
        Player,
        Block,
        Item;
    }


    public SceneObjectStatus(UUID id, SceneObjectTypes types) {
        super(id);
        mDataType = types;
    }

    public SceneObjectTypes getDataType() {
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
        return pressed.contains(key) || pressing.contains(key);
    }
}