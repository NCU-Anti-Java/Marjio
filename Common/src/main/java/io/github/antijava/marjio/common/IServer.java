package io.github.antijava.marjio.common;

import java.net.InetAddress;
import java.util.List;

/**
 * Created by fntsr on 2015/12/23.
 */
public interface IServer {
    /**
     * Start to listen on socket.
     */
    void start();

    /**
     * Stop listening.
     */
    void stop() throws InterruptedException;

    /**
     * Broadcast message to all connected clients.
     *
     * @param packableObject packable object to be sent
     */
    void broadcast(Packable packableObject);

    /**
     * Send message to specific client
     *
     * @param packableObject packable object to be sent
     * @param address client's address which will received messages
     */
    void send(Packable packableObject, InetAddress address);

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
