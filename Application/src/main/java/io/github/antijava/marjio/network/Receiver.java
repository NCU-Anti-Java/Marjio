package io.github.antijava.marjio.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by fntsr on 2015/12/27.
 */
public class Receiver {

    private final int SIZE = 1024;
    private DatagramSocket mServerSocket;

    public Receiver() throws SocketException {
        mServerSocket = new DatagramSocket(9876);
    }

    public byte[] recieve() throws IOException {
        byte[] receiveData = new byte[SIZE];

        DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);
        mServerSocket.receive(packet);

        return packet.getData();
    }
}
