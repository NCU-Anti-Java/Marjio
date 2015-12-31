package io.github.antijava.marjio.network;

import io.github.antijava.marjio.common.input.Event;
import io.github.antijava.marjio.common.input.Status;
import io.github.antijava.marjio.common.network.Packable;

import java.io.IOException;

/**
 * Created by Date on 2015/12/28.
 */
public class Packer {
    /*
     * @Param statusData
     * @Param type:
     *             tag Types.NetworkServer:
     *                 when Server recv packet from Client, and Server need pass Event to Input Module
     *             tag Types.NetworkClient:
     *                 when Client recv packet from Server, and need pass Event to Input Module
     */
    public static Event toEvent(Packable packableObject , Event.Type type) {

        return new Event(packableObject, type);
    }

}
