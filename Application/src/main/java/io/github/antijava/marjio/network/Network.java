package io.github.antijava.marjio.network;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import io.github.antijava.marjio.common.IApplication;
import io.github.antijava.marjio.common.IClient;
import io.github.antijava.marjio.common.IServer;
import io.github.antijava.marjio.common.input.Status;
import io.github.antijava.marjio.common.network.ClientInfo;
import io.github.antijava.marjio.constant.Constant;

import java.io.IOException;
import java.lang.annotation.Inherited;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Network Module
 * Created by fntsr on 2015/12/30.
 */
public class Network implements IClient, IServer, Constant {

    // Common Fields
    private IApplication mApplication;
    private boolean mRunningFlag;
    private boolean mConnectedFlag;
    private Server mServer;
    private Client mClient;

    // Server Fields
    private List<ClientInfo> mClientList;
    private HashMap<UUID, Connection> mConnectionMap;

    public Network(IApplication application) {
        mApplication = application;
        mRunningFlag = false;
        mConnectedFlag = true;
        mServer = new Server();
        mClient = new Client();
        mConnectionMap = new HashMap<>();
        mClientList = new ArrayList<>();

        mServer.getKryo().register(Status.class);
        mClient.getKryo().register(Status.class);
    }

    @Override
    public void start() throws InterruptedException, UnsupportedOperationException, IOException {
        if (mRunningFlag) {
            throw new UnsupportedOperationException();
        }
        mRunningFlag = true;
        mServer.start();
        mServer.bind(NET_TCP_PORT, NET_UDP_PORT);
        mServer.addListener(new ServerReceiver(mApplication, mConnectionMap, mClientList));

    }

    @Override
    public void start(InetAddress hostAddress) throws InterruptedException, UnsupportedOperationException, IOException {
        if (mRunningFlag) {
            throw new UnsupportedOperationException();
        }
        mRunningFlag = true;

        mServer.start();
        mServer.bind(NET_TCP_PORT, NET_UDP_PORT);
        mServer.addListener(new ClientReceiver(mApplication));

        mClient = new Client();
        mClient.start();
        mClient.connect(NET_TIMEOUT, hostAddress, NET_TCP_PORT, NET_UDP_PORT);
    }

    @Override
    public void stop() throws InterruptedException, UnsupportedOperationException {
        mRunningFlag = false;
        mServer.stop();
        if (mClient != null) {
            mClient.stop();
        }
    }

    @Override
    public boolean isRunning() {
        return mRunningFlag;
    }

    @Override
    public boolean isConnected() {
        return mConnectedFlag;
    }

    @Override
    public void send(Status status) throws Exception {
        mClient.sendUDP(status);
    }

    @Override
    public void send(Status status, UUID clientID) throws Exception {
        Connection connection = mConnectionMap.get(clientID);
        connection.sendUDP(status);
    }

    @Override
    public void broadcast(Status status) throws Exception {
        mServer.sendToAllUDP(status);
    }

    @Override
    public List<ClientInfo> getClients() {
        return mClientList;
    }
}
