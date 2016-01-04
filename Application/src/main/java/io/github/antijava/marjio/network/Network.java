package io.github.antijava.marjio.network;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import io.github.antijava.marjio.common.IApplication;
import io.github.antijava.marjio.common.IClient;
import io.github.antijava.marjio.common.IServer;
import io.github.antijava.marjio.common.network.ClientInfo;
import io.github.antijava.marjio.common.network.Packable;
import io.github.antijava.marjio.constant.Constant;

import java.io.IOException;
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
    private boolean mIsServerFlag;
    private UUID mMyId;

    // Server Fields
    private Server mServer;
    private List<ClientInfo> mClientList;
    private HashMap<UUID, Connection> mConnectionMap;

    // Client Fields
    private Client mClient;
    private boolean mConnectedFlag;

    public Network(IApplication application) {
        mApplication = application;
        mRunningFlag = false;
        mConnectedFlag = true;
        mServer = new Server(NET_WRITE_BUFFER_SIZE, NET_OBJECT_BUFFER_SIZE);
        mClient = new Client(NET_WRITE_BUFFER_SIZE, NET_OBJECT_BUFFER_SIZE);
        mConnectionMap = new HashMap<>();
        mClientList = new ArrayList<>();

        mServer.getKryo().register(byte[].class);
        mClient.getKryo().register(byte[].class);
    }

    // region ServerSide(Host)
    @Override
    public void start() throws IOException {
        if (mRunningFlag) {
            throw new UnsupportedOperationException();
        }

        mServer.bind(NET_TCP_PORT, NET_UDP_PORT);
        mServer.start();
        mApplication.getLogger().info("Server started.");

        mServer.addListener(new ServerReceiver(mApplication, mConnectionMap, mClientList));
        mRunningFlag = true;
        mIsServerFlag = true;
        mMyId = UUID.randomUUID();
    }

    @Override
    public void send(Packable packableObj, UUID clientID) {
        if (!mRunningFlag) {
            throw new UnsupportedOperationException();
        }

        Connection connection = mConnectionMap.get(clientID);
        connection.sendUDP(Packer.PackabletoByteArray(packableObj));
    }

    @Override
    public void sendTCP(Packable packableObj) {
        if (!mRunningFlag) {
            throw new UnsupportedOperationException();
        }

        mApplication.getLogger().info("Client send message");
        mClient.sendTCP(Packer.PackabletoByteArray(packableObj));
    }

    @Override
    public void broadcast(Packable packableObj) {
        if (!mRunningFlag) {
            throw new UnsupportedOperationException();
        }

        mServer.sendToAllUDP(Packer.PackabletoByteArray(packableObj));
    }

    @Override
    public void broadcastTCP(Packable packableObject) {
        if (!mRunningFlag) {
            throw new UnsupportedOperationException();
        }

        mServer.sendToAllTCP(Packer.PackabletoByteArray(packableObject));
    }

    @Override
    public List<ClientInfo> getClients() {
        if (!mRunningFlag) {
            throw new UnsupportedOperationException();
        }

        return mClientList;
    }
    // endregion ServerSide(Host)

    // region ClientSide(OtherPlayer)
    @Override
    public void start(InetAddress hostAddress) throws IOException {
        if (mRunningFlag) {
            throw new UnsupportedOperationException();
        }

        // If it unable to connect server, will throw IOException.
        // Also not keep executing other statements
        mClient.start();
        mApplication.getLogger().info("Client started.");

        mClient.addListener(new ClientReceiver(mApplication));
        mClient.connect(NET_TIMEOUT, hostAddress, NET_TCP_PORT, NET_UDP_PORT);
        mRunningFlag = true;
        mIsServerFlag = false;
    }

    @Override
    public void send(Packable packableObj) {
        if (!mRunningFlag) {
            throw new UnsupportedOperationException();
        }

        mClient.sendUDP(Packer.PackabletoByteArray(packableObj));
    }

    @Override
    public void sendTCP(Packable packableObj, UUID clientID) {
        if (!mRunningFlag) {
            throw new UnsupportedOperationException();
        }

        Connection connection = mConnectionMap.get(clientID);
        connection.sendTCP(Packer.PackabletoByteArray(packableObj));
    }

    @Override
    public boolean isConnected() {
        if (!mRunningFlag) {
            throw new UnsupportedOperationException();
        }

        return mConnectedFlag;
    }
    // endregion ClientSide(OtherPlayer)

    // region BothSide
    @Override
    public void stop() {
        if (!mRunningFlag) {
            throw new UnsupportedOperationException();
        }

        mRunningFlag = false;

        if (mIsServerFlag) {
            mServer.stop();
            mConnectionMap.clear();
            mClientList.clear();
            mApplication.getLogger().info("Server stopped.");
        } else {
            mClient.stop();
            mApplication.getLogger().info("Client stopped.");
        }
    }

    @Override
    public boolean isRunning() {
        return mRunningFlag;
    }

    @Override
    public void setMyId(UUID mMyId) {
        this.mMyId = mMyId;
    }

    @Override
    public UUID getMyId() {
        return mMyId;
    }
    // endregion BothSide
}
