package io.github.antijava.marjio.common;

import io.github.antijava.marjio.common.input.Status;

import java.net.InetAddress;
import java.util.List;

/**
 * Created by fntsr on 2015/12/23.
 */
public interface IServer {
    /**
     * Start to listen on socket.
     */
    void start() throws InterruptedException, UnsupportedOperationException;

    /**
     * Stop listening.
     */
    void stop() throws InterruptedException, UnsupportedOperationException;

    /**
     * Broadcast message to all connected clients.
     *
     * @param status information package
     */
    void broadcast(Status status);

    /**
     * Send message to specific client
     *
     * @param status information package
     * @param address client's address which will received messages
     */
    void send(Status status, InetAddress address);

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
