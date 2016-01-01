package io.github.antijava.marjio.common;

import io.github.antijava.marjio.common.input.*;

import java.util.List;

/**
 * Created by fntsr on 2015/12/23.
 */
public interface IInput {
    int key_start_ticks = 24;
    int key_repeat_ticks = 6;

    /**
     * Input polling.
     */
    void update();


    boolean isPressing(Key key);
    boolean isPressed(Key key);
    boolean isReleased(Key key);
    boolean isTrigger(Key key);

    boolean isKeyUp(Key key);
    boolean isKeyDown(Key Key);

    /**
     * with key pressed for a while then key is repeat.
     * */
    boolean isRepeat(Key key);

    List<Status> getStatuses();
    List<Request> getRequest();
    List<TickRequest> getTickRequest();

    void triggerEvent(Event evt);
}
