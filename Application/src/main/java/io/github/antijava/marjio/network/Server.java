package io.github.antijava.marjio.network;

import io.github.antijava.marjio.common.IApplication;
import io.github.antijava.marjio.common.IServer;
import io.github.antijava.marjio.common.input.Status;
import io.github.antijava.marjio.common.network.Player;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fntsr on 2015/12/27.
 */
public class Server extends Connector implements IServer {
    private List<InetAddress> mClients;
    private Packer mPacker;

    public Server(IApplication application, Sender sender, Receiver receiver, Packer packer) {
        super(application, sender, receiver);
        mClients = new ArrayList<>();
        mPacker = packer;
    }

    @Override
    public void broadcast(Status status) throws Exception {
        assert mClients.size() != 0;
        for (InetAddress client: mClients) {
            send(status, client);
        }
    }

    @Override
    public void send(Status status, InetAddress address) throws Exception {
        byte[] data = mPacker.pack(status);
        mSender.send(address, data);
    }

    @Override
    protected void onReceive(byte[] data, InetAddress address) {
        if (!mClients.contains(address)) {
            mClients.add(address);
        }
        // TODO: Decode and triggerEvent()
    }

    @Override
    public List<InetAddress> getClients() {
        return mClients;
    }
}
