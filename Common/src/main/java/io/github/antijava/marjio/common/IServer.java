package io.github.antijava.marjio.common;

import io.github.antijava.marjio.common.input.Status;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.UUID;

/**
 * Created by fntsr on 2015/12/23.
 */
public interface IServer {
    /**
     * Start to listen on socket.
     */
    void start() throws InterruptedException, UnsupportedOperationException, IOException;

    /**
     * Stop listening.
     */
    void stop() throws InterruptedException, UnsupportedOperationException;

    /**
     * Broadcast message to all connected clients.
     *
     * @param status information package
     */
    void broadcast(Status status) throws Exception;

    /**
     * Send message to specific client
     *
     * @param status information package
     * @param clientID client's id
     */
    void send(Status status, UUID clientID) throws Exception;

    /**
     * Return all connected clients' information.
     *
     * @return IP 清單，暫定是 String List
     */
    List getClients();

    /**
     * Specifics if is listening.
     *
     * @return  if is listening
     */
    boolean isRunning();
}
