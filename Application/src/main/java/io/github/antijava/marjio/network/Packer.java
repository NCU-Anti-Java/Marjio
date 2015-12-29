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
    public static byte[] pack(io.github.antijava.marjio.common.input.Status status) throws IOException {

        return Status__JsonHelper.serializeToJson(new Status(status).PreparePack()).getBytes("UTF-8");
    }

    public static io.github.antijava.marjio.common.input.Status unpack(byte[] JSONstring) throws IOException {

        return Status__JsonHelper.parseFromJson(
                    new String(JSONstring)
        ).AfterUnpack();

    }

    /*
     * @Param statusData
     * @Param type:
     *             tag Type.NetworkServer:
     *                 when Server recv packet from Client, and Server need pass Event to Input Module
     *             tag Type.NetworkClient:
     *                 when Client recv packet from Server, and need pass Event to Input Module
     */
    public static Event toEvent(io.github.antijava.marjio.common.input.Status status, Event.Type type) {

        return new Event(status, type);
    }

}
