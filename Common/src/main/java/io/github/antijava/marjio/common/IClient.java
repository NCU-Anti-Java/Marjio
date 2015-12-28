package io.github.antijava.marjio.common;

import java.net.InetAddress;

/**
 * Created by fntsr on 2015/12/23.
 */
public interface IClient {
    /**
     * Start connecting to a Server
     *
     * @param hostIP Server address
     */
    void start(InetAddress hostAddress);

    /**
     * Stop the connection.
     */
    void stop() throws InterruptedException;

    /**
     * Send message to Server.
     *
     * @param packableObject packable object
     */
    void send(Packable packableObject) throws Exception;

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
}
