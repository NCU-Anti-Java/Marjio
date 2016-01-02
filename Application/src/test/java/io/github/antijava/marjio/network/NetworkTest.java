package io.github.antijava.marjio.network;

import com.esotericsoftware.kryonet.*;

import io.github.antijava.marjio.common.input.Request;
import io.github.antijava.marjio.common.input.SceneObjectStatus;
import io.github.antijava.marjio.common.input.Status;
import io.github.antijava.marjio.common.network.RequestData;
import io.github.antijava.marjio.common.network.StatusData;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;


public class NetworkTest {
    private final int TCP_Port = 15566;
    private final int UDP_Port = 15566;
    private final UUID clientId = UUID.randomUUID();

    @Before
    public void setUp() throws Exception {


    }

    @After
    public void tearDown() throws Exception {

    }


    @Test
    public void testSendRequest() throws Exception {
        Client client = new Client(16384, 8192, new JsonSerialization());
        Server server = new Server(16384, 8192, new JsonSerialization());

        server.start();
        server.bind(TCP_Port, UDP_Port);
        server.addListener(new Listener() {

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



        client.start();
        client.connect(5000, "127.0.0.1", TCP_Port, UDP_Port);
        client.addListener(new Listener() {

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
        client.sendTCP(Packer.RequestToData(request));

        Thread.sleep(2000);

        client.stop();
        server.stop();
        client.close();
        server.close();
    }

    @Test
    public void testSendStatus() throws Exception {
        Client client = new Client(16384, 8192, new JsonSerialization());
        Server server = new Server(16384, 8192, new JsonSerialization());

        SceneObjectStatus info = new SceneObjectStatus();
        info.uuid = clientId;
        info.type = SceneObjectStatus.Types.Player;
        info.x = 0;
        info.y = 0;
        info.vx = 55;
        info.vy = 66;
        info.ax = 77;
        info.ay = 88;

        Status status = new Status(info, Status.Types.ClientMessage);

        server.start();
        server.bind(TCP_Port, UDP_Port);
        server.addListener(new Listener() {
            @Override
            public void received (Connection connection, Object object) {
                if (object instanceof StatusData) {
                    Status receiveStatus = Packer.DataToStatus((StatusData) object);

                    // TODO: More accurately compare with status.equals(anotherStatus);
                    if (receiveStatus.getClientID().toString().equals(clientId.toString())) {
                        Assert.assertTrue(true);
                        return;
                    }
                }
                Assert.assertTrue(false);
            }
        });


        client.start();
        client.connect(5000, "127.0.0.1", TCP_Port, UDP_Port);

        client.sendTCP(Packer.StatustToData(status));

        Thread.sleep(2000);

        client.stop();
        server.stop();
        client.close();
        server.close();
    }

}