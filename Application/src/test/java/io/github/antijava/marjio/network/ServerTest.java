package io.github.antijava.marjio.network;

import io.github.antijava.marjio.common.IApplication;
import io.github.antijava.marjio.common.input.Status;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.atLeastOnce;

/**
 * Created by fntsr on 2015/12/29.
 */
public class ServerTest {
    private Server mServer;
    private Receiver mReceiver;
    private Sender mSender;
    private Packer mPacker;
    private IApplication mApplication;

    @Before
    public void setUp() throws Exception {
        // Mock
        mApplication = mock(IApplication.class);
        mSender = mock(Sender.class);
        mReceiver = mock(Receiver.class);
        mPacker = mock(Packer.class);

        // Initialize
        mServer = spy(new Server(mApplication, mSender, mReceiver));
    }

    /**
     * 每項測試結束後，執行的方法。主要用來關閉 Server
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        if (mServer.isRunning()) {
            mServer.stop();
        }
    }

    /**
     * 測試接收訊息是否正常
     * @throws Exception
     */
    @Test
    public void testOnReceive() throws Exception {
        byte[] data = "SOMETHING".getBytes();
        InetAddress address = InetAddress.getByName("192.168.1.1");

        // stub
        when(mReceiver.recieve()).thenReturn(data);
        when(mReceiver.getData()).thenReturn(data);
        when(mReceiver.getSourceAddress()).thenReturn(address);

        // Test Begin
        mServer.start();
        Thread.sleep(1000);

        // Assert
        verify(mServer, atLeastOnce()).onReceive(data, address);
    }

    /**
     * 測試是否能正常紀錄 Client 清單
     * @throws Exception
     */
    @Test
    public void testGetClients() throws Exception {
        byte[] data = "Serialize Data".getBytes();
        List<InetAddress> clients = new ArrayList<>();
        clients.add(InetAddress.getByName("192.168.1.1"));
        clients.add(InetAddress.getByName("192.168.1.2"));
        clients.add(InetAddress.getByName("192.168.1.3"));
        clients.add(InetAddress.getByName("192.168.1.4"));
        clients.add(InetAddress.getByName("192.168.1.5"));

        // stub
        when(mReceiver.getData()).thenReturn(data);
        when(mReceiver.getSourceAddress())
                .thenReturn(clients.get(0))
                .thenReturn(clients.get(1))
                .thenReturn(clients.get(2))
                .thenReturn(clients.get(3))
                .thenReturn(clients.get(4));

        // Test Begin
        mServer.start();
        Thread.sleep(1000);

        // Assert
        List<InetAddress> actual = mServer.getClients();
        Assert.assertEquals(actual.size(), clients.size());
        Assert.assertTrue(actual.containsAll(clients));
        Assert.assertTrue(clients.containsAll(actual));
    }

    // TODO: Refactor it
//    /**
//     * 測試發送功能是否正常
//     * @throws Exception
//     */
//    @Test
//    public void testSend() throws Exception {
//        byte[] data = "Serialize Data".getBytes();
//        InetAddress address = InetAddress.getByName("192.168.1.1");
//        Status status = mock(Status.class);
//
//        // stub & spy
//        when(mPacker.pack(status)).thenReturn(data);
//        mServer = spy(new Server(mApplication, mSender, mReceiver, mPacker));
//
//        // Test Begin
//        mServer.start();
//        Thread.sleep(1000);
//        mServer.send(status, address);
//
//        // Assert
//        verify(mSender, times(1)).send(address, data);
//    }
//
//    /**
//     * 測試廣播功能是否正常
//     * @throws Exception
//     */
//    @Test
//    public void testBroadcast() throws Exception {
//        byte[] data = "Serialize Data".getBytes();
//        Status status = mock(Status.class);
//        List<InetAddress> clients = new ArrayList<>();
//        clients.add(InetAddress.getByName("192.168.1.1"));
//        clients.add(InetAddress.getByName("192.168.1.2"));
//        clients.add(InetAddress.getByName("192.168.1.3"));
//
//        // stub
//        when(mReceiver.getData())
//                .thenReturn(data)
//                .thenReturn(data)
//                .thenReturn(data)
//                .thenReturn(null);
//        when(mReceiver.getSourceAddress())
//                .thenReturn(clients.get(0))
//                .thenReturn(clients.get(1))
//                .thenReturn(clients.get(2))
//                .thenReturn(null);
//
//        // Test Begin
//        mServer.start();
//        Thread.sleep(1000);
//        mServer.broadcast(status);
//
//        // Assert
//        verify(mServer, times(1)).send(status, clients.get(0));
//        verify(mServer, times(1)).send(status, clients.get(1));
//        verify(mServer, times(1)).send(status, clients.get(2));
//    }

    /**
     * 測試啟動功能
     * @throws Exception
     */
    @Test
    public void testStart() throws Exception {
        mServer.start();
        Thread.sleep(1000);

        Assert.assertTrue(mServer.isRunning());
    }

    /**
     * 測試在啟用狀態時，再次啟用是否會拋出意外
     * @throws Exception
     */
    @Test(expected=Exception.class)
    public void testStartTwice() throws Exception {
        mServer.start();
        Thread.sleep(1000);
        mServer.start();
    }

    /**
     * 測試停用功能
     * @throws Exception
     */
    @Test
    public void testStop() throws Exception {
        mServer.start();
        Thread.sleep(1000);
        mServer.stop();
        Thread.sleep(1000);

        Assert.assertFalse(mServer.isRunning());
    }

    /**
     * 測試在停用狀態時，再次停用是否會拋出意外
     * @throws Exception
     */
    @Test(expected=Exception.class)
    public void testStopTwice() throws Exception {
        mServer.start();
        Thread.sleep(1000);
        mServer.stop();
        Thread.sleep(1000);
        mServer.stop();
    }
}
