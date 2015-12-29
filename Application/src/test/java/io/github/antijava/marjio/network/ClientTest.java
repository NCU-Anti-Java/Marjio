package io.github.antijava.marjio.network;
import java.net.InetAddress;

import io.github.antijava.marjio.common.input.Status;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.*;

import io.github.antijava.marjio.common.IApplication;


/**
 * Created by fntsr on 2015/12/28.
 */
public class ClientTest {
    private Client mClient;
    private InetAddress mAddress;
    private Receiver mReceiver;
    private Sender mSender;
    private IApplication mApplication;

    @org.junit.Before
    public void setUp() throws Exception {
        // Mock
        mApplication = mock(IApplication.class);
        mSender = mock(Sender.class);
        mReceiver = mock(Receiver.class);

        // Initialize
        mClient = spy(new Client(mApplication, mSender, mReceiver));
        mAddress = InetAddress.getByName("127.0.0.1");
    }

    @org.junit.After
    public void tearDown() throws Exception {
        if (mClient.isRunning()) {
            mClient.stop();
        }
    }

    /**
     * 測試是否能收到封包
     * @throws Exception
     */
    @org.junit.Test
    public void testOnReceive() throws Exception {
        byte[] data = "SOMETHING".getBytes();
        when(mReceiver.recieve()).thenReturn(data);
        when(mReceiver.getData()).thenReturn(data);
        when(mReceiver.getSourceAddress()).thenReturn(mAddress);

        mClient.start(mAddress);

        verify(mClient, atLeastOnce()).onReceive(data, mAddress);
    }

    /**
     * 測試是否能發送封包
     * @throws Exception
     */
    // TODO: Refactor it
//    @org.junit.Test
//    public void testSend() throws Exception {
//        byte[] data = "Serialize Data".getBytes();
//        Status status = mock(Status.class);
//
//        when(mPacker.pack(status)).thenReturn(data);
//
//        mClient = spy(new Client(mApplication, mSender, mReceiver));
//        mClient.start(mAddress);
//        mClient.send(status);
//
//        verify(mSender, times(1)).send(mAddress, data);
//    }

    /**
     * 測試連線成功是否能偵測
     * @throws Exception
     */
    @org.junit.Test
    public void testIsConnected() throws Exception {
        byte[] data = "SOMETHING".getBytes();
        when(mReceiver.recieve()).thenReturn(data);

        mClient.start(mAddress);
        Thread.sleep(1000);

        Assert.assertTrue(mClient.isConnected());
    }

    /**
     * 測試啟動功能
     * @throws Exception
     */
    @Test
    public void testStart() throws Exception {
        mClient.start(mAddress);
        Thread.sleep(1000);

        Assert.assertTrue(mClient.isRunning());
    }

    /**
     * 測試在啟用狀態時，再次啟用是否會拋出意外
     * @throws Exception
     */
    @Test(expected=Exception.class)
    public void testStartTwice() throws Exception {
        mClient.start(mAddress);
        Thread.sleep(1000);
        mClient.start(mAddress);
    }

    /**
     * 測試停用功能
     * @throws Exception
     */
    @Test
    public void testStop() throws Exception {
        mClient.start(mAddress);
        Thread.sleep(1000);
        mClient.stop();
        Thread.sleep(1000);

        Assert.assertFalse(mClient.isRunning());
    }

    /**
     * 測試在停用狀態時，再次停用是否會拋出意外
     * @throws Exception
     */
    @Test(expected=Exception.class)
    public void testStopTwice() throws Exception {
        mClient.start(mAddress);
        Thread.sleep(1000);
        mClient.stop();
        Thread.sleep(1000);
        mClient.stop();
    }
}