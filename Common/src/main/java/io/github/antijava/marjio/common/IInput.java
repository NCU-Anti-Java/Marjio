package io.github.antijava.marjio.common;

import io.github.antijava.marjio.common.input.*;
import io.github.antijava.marjio.common.network.Packable;

import java.lang.annotation.Inherited;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by fntsr on 2015/12/23.
 */
public interface IInput extends IKeyInput {
    int key_start_ticks = 24;
    int key_repeat_ticks = 6;

    /**
     * Input polling.
     */
    void update();

    boolean isTrigger(Key key);

    List<Status> getStatuses();
    List<Request> getRequest();
    List<TickRequest> getTickRequest();

    List<SyncList> getSyncList();
    List<GameSet> getGameSet();


    @NetWorkData({Status.class, Request.class, SyncList.class, TickRequest.class, GameSet.class})
    List<? extends Packable> getNetWorkData(Class c);

    void triggerEvent(Event evt);

}
