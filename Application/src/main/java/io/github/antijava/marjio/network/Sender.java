package io.github.antijava.marjio.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by fntsr on 2015/12/27.
 */
public class Sender {
    public DatagramSocket mClientSocket;

    public Sender() throws Exception
    {
        mClientSocket = new DatagramSocket();
    }

    public void send(InetAddress address, byte[] data ) throws Exception
    {
        DatagramPacket sendPacket = new DatagramPacket(data, data.length, address, 9876);
        mClientSocket.send(sendPacket);
    }
}
