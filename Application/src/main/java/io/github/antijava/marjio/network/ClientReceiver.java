package io.github.antijava.marjio.network;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import io.github.antijava.marjio.common.IApplication;
import io.github.antijava.marjio.common.input.Event;
import io.github.antijava.marjio.common.input.Status;

/**
 * Created by fntsr on 2015/12/30.
 */
public class ClientReceiver extends Listener {
    private IApplication mApplication;

    public ClientReceiver(IApplication application) {
        mApplication = application;
    }

    @Override
    public void received (Connection connection, Object object) {
        if (object instanceof Status) {
            Status status = (Status) object;
            Event event = Packer.toEvent(status, Event.Type.NetWorkClient);
            mApplication.getInput().triggerEvent(event);
        }
    }
}
