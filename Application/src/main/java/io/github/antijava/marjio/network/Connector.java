package io.github.antijava.marjio.network;

import io.github.antijava.marjio.common.IApplication;

import java.net.InetAddress;

/**
 * Created by fntsr on 2015/12/27.
 */
public abstract class Connector implements Runnable{
    protected IApplication mApplication;
    protected Sender mSender;
    protected Receiver mReceiver;
    protected boolean mRunningFlag;
    protected Thread mServerThread;


    public Connector(IApplication application, Sender sender, Receiver receiver) {
        mApplication = application;
        mSender = sender;
        mReceiver = receiver;
    }

    public void start() throws InterruptedException, UnsupportedOperationException {
        if (mRunningFlag) {
            // TODO: 改成比較合適且狹義的 Exception
            throw new UnsupportedOperationException();
        }
        mRunningFlag = true;
        mServerThread = new Thread(this);
        mServerThread.start();
    }

    public void stop() throws InterruptedException, UnsupportedOperationException {
        if (!mRunningFlag) {
            // TODO: 改成比較合適且狹義的 Exception
            throw new UnsupportedOperationException();
        }
        mRunningFlag = false;
        mServerThread.join();
    }

    public void run()
    {
        try
        {
            while (mRunningFlag)
            {
                mReceiver.recieve();
                byte[] data = mReceiver.getData();
                InetAddress address = mReceiver.getSourceAddress();
                onReceive(data, address);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public boolean isRunning() {
        return mRunningFlag;
    }

    protected abstract void onReceive(byte[] data, InetAddress address) throws Exception;
}
