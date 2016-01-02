package io.github.antijava.marjio.network;

import com.esotericsoftware.kryonet.*;
import com.esotericsoftware.jsonbeans.JsonWriter;

import io.github.antijava.marjio.common.input.Request;
import io.github.antijava.marjio.common.network.RequestData;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;


public class NetworkTest {
    private Client mClient;
    private Server mServer;

    @Before
    public void setUp() throws Exception {
        mClient = new Client(16384, 8192, new JsonSerialization());
        mServer = new Server(16384, 8192, new JsonSerialization());
    }

    @After
    public void tearDown() throws Exception {

    }


    @Test
    public void testSendRequest() throws Exception {
        final int TCP_Port = 15566;
        final int UDP_Port = 15566;

        final UUID clientId = UUID.randomUUID();

        mServer.start();
        mServer.bind(TCP_Port, UDP_Port);
        mServer.addListener(new Listener() {

            @Override
            public void received (Connection connection, Object object) {
                if (object instanceof RequestData) {
                    Request request = Packer.DataToRequest((RequestData)object);

                    if (request.getType() == Request.Types.ClientWannaJoinRoom) {
                        Request response = new Request(clientId, Request.Types.ClientCanJoinRoom);
                        connection.sendTCP(Packer.RequestToData(response));
                        return;
                    }
                }
                Assert.assertTrue(false);
            }

        });



        mClient.start();
        mClient.connect(5000, "127.0.0.1", TCP_Port, UDP_Port);
        mClient.addListener(new Listener() {

            @Override
            public void received (Connection connection, Object object) {
                if (object instanceof RequestData) {
                    Request response = Packer.DataToRequest((RequestData)object);

                    if (response.getType() == Request.Types.ClientCanJoinRoom) {
                        Assert.assertTrue(true);
                        return;
                    }
                }
                Assert.assertTrue(false);
            }
        });

        Request request = new Request(clientId, Request.Types.ClientWannaJoinRoom);
        mClient.sendTCP(Packer.RequestToData(request));
    }

}