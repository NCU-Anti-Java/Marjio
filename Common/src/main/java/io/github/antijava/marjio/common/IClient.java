package io.github.antijava.marjio.common;

import io.github.antijava.marjio.common.input.Status;

import java.net.InetAddress;

/**
 * Created by fntsr on 2015/12/23.
 */
public interface IClient {
    /**
     * Start connecting to a Server
     *
     * @param hostAddress Server address
     */
    void start(InetAddress hostAddress) throws Exception;

    /**
     * Stop the connection.
     */
    void stop() throws Exception;

    /**
     * Send message to Server.
     *
     * @param status information package
     */
    void send(Status status) throws Exception;

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
