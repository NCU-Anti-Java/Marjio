package io.github.antijava.marjio.network;

import io.github.antijava.marjio.common.input.Event;

import java.io.IOException;

/**
 * Created by Date on 2015/12/28.
 */
public class Packer {

    /*
     * StatusData__JsonHelper need to use `gradle clean && gradle classes` to auto gen it
     * It will locate to gen/main/java rather than src/main/java
     * application:clean & application:classes
     */
    public static String pack(StatusData statusData) throws IOException {

        return StatusData__JsonHelper.serializeToJson(statusData.PreparePack());
    }

    public static StatusData unpack(String JSONstring) throws IOException {

        return  StatusData__JsonHelper.parseFromJson(JSONstring).AfterUnpack();

    }

    /*
     * @Param statusData
     * @Param type:
     *             tag Type.NetworkServer:
     *                 when Server recv packet from Client, and Server need pass Event to Input Module
     *             tag Type.NetworkClient:
     *                 when Client recv packet from Server, and need pass Event to Input Module
     */
    public static Event toEvent(StatusData statusData, Event.Type type) {

        return new Event(statusData, type);
    }

}
