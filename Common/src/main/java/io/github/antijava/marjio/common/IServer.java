package io.github.antijava.marjio.common;

import io.github.antijava.marjio.common.input.Status;
import io.github.antijava.marjio.common.network.ClientInfo;
import io.github.antijava.marjio.common.network.Packable;

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
    void start() throws IOException;

    /**
     * Stop listening.
     */
    void stop();

    /**
     * Broadcast message to all connected clients.
     *
     * @param packableObject information package
     */
    void broadcast(Packable packableObject);

    /**
     * Broadcast message to all connected clients.
     *
     * @param packableObject information package
     */
    void broadcastTCP(Packable packableObject);

    /**
     * Send message to specific client
     *
     * @param packableObject information package
     * @param clientID client's id
     */
    void send(Packable packableObject, UUID clientID);

    /**
     * Send message to specific client
     *
     * @param packableObject information package
     * @param clientID client's id
     */
    void sendTCP(Packable packableObject, UUID clientID);

    /**
     * Return all connected clients' information.
     *
     * @return IP 清單，暫定是 String List
     */
    List<ClientInfo> getClients();

    /**
     * Specifics if is listening.
     *
     * @return  if is listening
     */
    boolean isRunning();

    void setMyId(UUID mMyId);
    UUID getMyId();
}
