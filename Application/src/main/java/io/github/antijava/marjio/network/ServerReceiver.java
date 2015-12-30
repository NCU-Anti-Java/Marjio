package io.github.antijava.marjio.network;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import io.github.antijava.marjio.common.IApplication;
import io.github.antijava.marjio.common.input.Event;
import io.github.antijava.marjio.common.input.Status;
import io.github.antijava.marjio.common.network.ClientInfo;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by fntsr on 2015/12/30.
 */
public class ServerReceiver extends Listener{
    private HashMap<UUID, Connection> mConnectionMap;
    private IApplication mApplication;
    private List<ClientInfo> mClientList;

    public ServerReceiver(IApplication application, HashMap<UUID, Connection> map, List<ClientInfo> list) {
        mApplication = application;
        mConnectionMap = map;
        mClientList = list;
    }

    @Override
    public void received (Connection connection, Object object) {
        if (object instanceof Status) {
            Status status = (Status) object;

            if (!mConnectionMap.containsValue(connection)) {
                UUID uuid = UUID.randomUUID();
                mConnectionMap.put(uuid, connection);
                mClientList.add(new ClientInfo(uuid));
            }

            Event event = Packer.toEvent(status, Event.Type.NetWorkClient);
            mApplication.getInput().triggerEvent(event);
        }
    }
}
