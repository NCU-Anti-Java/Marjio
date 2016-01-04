package io.github.antijava.marjio.common;

import io.github.antijava.marjio.common.input.Status;
import io.github.antijava.marjio.common.network.Packable;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

/**
 * Created by fntsr on 2015/12/23.
 */
public interface IClient {
    /**
     * Start connecting to a Server
     *
     * @param hostAddress Server address
     */
    void start(InetAddress hostAddress) throws IOException;

    /**
     * Stop the connection.
     */
    void stop();

    /**
     * Send message to Server.
     *
     * @param packableObject information package
     */
    void send(Packable packableObject);

    /**
     * Send message to Server.
     *
     * @param packableObject information package
     */
    void sendTCP(Packable packableObject);

    /**
     * Specific if this client is trying to connect or is connected.
     *
     * @return if client is running
     */
    boolean isRunning();

    /**
     * Specific if the connecting is connected.
     *
     * @return if client connected to server
     */
    boolean isConnected();

    void setMyId(UUID mMyId);
    UUID getMyId();
}
