package io.github.antijava.marjio.common.input;


import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

/**
 * Created by Date on 2015/12/29.
 */
public class StatusData implements IKeyInput {

    /*
     * Player 0
     * Block  1
     * Item   2
     */
    public static final int Player = 0;
    public static final int Block  = 1;
    public static final int Item   = 2;

    public UUID uuid;
    public int type;
    public int id;
    public int action_id;
    public int time_counter;
    public int st_x;
    public int st_y;
    public int x;
    public int y;
    public double vx;
    public double vy;
    public double ax;
    public double ay;
    public boolean query;

    public EnumSet<Key> pressed = EnumSet.noneOf(Key.class);
    public EnumSet<Key> pressing = EnumSet.noneOf(Key.class);
    public EnumSet<Key> released = EnumSet.noneOf(Key.class);
    public EnumSet<Key> repeat = EnumSet.noneOf(Key.class);


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


}