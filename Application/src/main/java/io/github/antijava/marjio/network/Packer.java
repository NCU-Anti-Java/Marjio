package io.github.antijava.marjio.network;

import io.github.antijava.marjio.common.input.Event;
import io.github.antijava.marjio.common.input.Status;

import java.io.IOException;

/**
 * Created by Date on 2015/12/28.
 */
public class Packer {
    /*
     * @Param statusData
     * @Param type:
     *             tag Type.NetworkServer:
     *                 when Server recv packet from Client, and Server need pass Event to Input Module
     *             tag Type.NetworkClient:
     *                 when Client recv packet from Server, and need pass Event to Input Module
     */
    public static Event toEvent(Status status, Event.Type type) {

        return new Event(status, type);
    }

}
