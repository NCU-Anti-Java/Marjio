package io.github.antijava.marjio.common;

import io.github.antijava.marjio.common.input.Event;
import io.github.antijava.marjio.common.input.Key;
import io.github.antijava.marjio.common.input.Status;

import java.util.List;

/**
 * Created by fntsr on 2015/12/23.
 */
public interface IInput {
    /**
     * Input polling.
     */
    void update();


    boolean isPressing(Key key);
    boolean isPressed(Key key);
    boolean isReleased(Key key);
    boolean isTrigger(Key key);

    /**
     * with key pressed for a while then key is repeat.
     * */
    boolean isRepeat(Key key);

    List<Status> getStatuses();

    void triggerEvent(Event evt);
}
