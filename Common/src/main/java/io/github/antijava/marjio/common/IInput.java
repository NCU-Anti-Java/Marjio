package io.github.antijava.marjio.common;

import io.github.antijava.marjio.common.input.*;
import io.github.antijava.marjio.common.network.Packable;

import java.lang.annotation.Inherited;
import java.util.List;
import java.util.function.Supplier;

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

    /**
     * with key pressed for a while then key is repeat.
     * */
    boolean isRepeat(Key key);

    List<Status> getStatuses();
    List<Request> getRequest();



    @NetWorkData({Status.class, Request.class})
    List<? extends Packable> getNetWorkData(Class c);

    void triggerEvent(Event evt);
}
