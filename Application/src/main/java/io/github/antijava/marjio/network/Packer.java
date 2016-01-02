package io.github.antijava.marjio.network;

import io.github.antijava.marjio.common.input.Event;
import io.github.antijava.marjio.common.input.Request;
import io.github.antijava.marjio.common.input.Status;
import io.github.antijava.marjio.common.network.Packable;
import io.github.antijava.marjio.common.network.RequestData;

import java.io.IOException;
import java.util.UUID;

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

    public static RequestData RequestToData(Request request) {
        RequestData data = new RequestData();
        data.uuid = request.getClientID().toString();
        data.type = request.getType().name();
        return data;
    }

    public static Request DataToRequest(RequestData data) {
        UUID uuid = UUID.fromString(data.uuid);
        Request.Types type = Request.Types.valueOf(data.type);

        return new Request(uuid, type);
    }

}
