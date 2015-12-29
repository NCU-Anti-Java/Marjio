package io.github.antijava.marjio.network;

import io.github.antijava.marjio.common.IApplication;
import io.github.antijava.marjio.common.IClient;
import io.github.antijava.marjio.common.input.Status;

import java.net.InetAddress;
import java.util.UUID;

/**
 * Created by fntsr on 2015/12/27.
 */
public class Client extends Connector implements IClient {
    private InetAddress mHostAddress;
    private boolean mConnectedFlag;
    private UUID mClientId;

    /**
     * Constructor
     * @param application IApplication Instance
     * @param sender Network output
     * @param receiver Network input
     */
    public Client(IApplication application, Sender sender, Receiver receiver) {
        super(application, sender, receiver);
        mConnectedFlag = false;
        mClientId = UUID.randomUUID();
    }

    @Override
    public void start(InetAddress hostAddress) throws InterruptedException, UnsupportedOperationException {
        mHostAddress = hostAddress;
        super.start();
    }

    @Override
    protected void onReceive(byte[] data, InetAddress address) throws Exception {
        mConnectedFlag = true;
        // TODO: Decode and triggerEvent()
    }

    @Override
    public void send(Status status) throws Exception {
        // TODO: Add mClientId to data
        byte[] data = Packer.pack(status);
        mSender.send(mHostAddress, data);
    }

    @Override
    public boolean isConnected() {
        return mConnectedFlag;
    }
}
