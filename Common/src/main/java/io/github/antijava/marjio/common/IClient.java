package io.github.antijava.marjio.common;

import java.net.InetAddress;

/**
 * Created by fntsr on 2015/12/23.
 */
public interface IClient {
    void start(InetAddress hostAddress);
    void stop();
}
