package io.github.antijava.marjio.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.List;

/**
 * Created by fntsr on 2015/12/27.
 */
public class Receiver {

    private DatagramSocket mServerSocket;
    private byte[] mData;
    private List<DatagramSocket> mSockets;

    public byte[] recieve() throws IOException {
        final int PORT = 9876;
        final int SIZE = 1024;

        byte[] receiveData = new byte[SIZE];


        DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);
        mServerSocket = new DatagramSocket(PORT);
        mServerSocket.receive(packet);
        mData = packet.getData();
        DatagramSocket socket = mServerSocket;
//        socket.get
        return mData;
    }

    public DatagramSocket getSocket() {
        return mServerSocket;
    }

    public byte[] getData() {
        return mData;
    }
}
