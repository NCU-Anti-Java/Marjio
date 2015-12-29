package io.github.antijava.marjio.network;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Date on 2015/12/29.
 */
public class PackerTest {

    private final int ID = 5;
    private final int ACTION_ID = 10;
    private final int TIMER_COUNTER = Integer.MAX_VALUE;
    private final int ST_X = 100;
    private final int ST_Y = 1000;

    public StatusData gen(int type){

        StatusData statusData = new StatusData();

        statusData.type = type;
        statusData.id = ID;
        statusData.action_id = ACTION_ID;
        statusData.time_counter = TIMER_COUNTER;
        statusData.st_x = ST_X;
        statusData.st_y = ST_Y;

        return statusData;
    }

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testPack() throws Exception {

        StatusData player = gen(StatusData.Player);
        StatusData block = gen(StatusData.Block);
        StatusData item = gen(StatusData.Item);

        StatusData player2 = gen(StatusData.Player);
        StatusData block2 = gen(StatusData.Block);
        StatusData item2 = gen(StatusData.Item);

        // same data, but different instance need same result JSON
        String playerJSON = Packer.pack(player);
        String player2JSON = Packer.pack(player2);
        assertEquals(playerJSON, player2JSON);

        String blockJSON = Packer.pack(block);
        String blockJSON2 = Packer.pack(block2);
        assertEquals(blockJSON, blockJSON2);

        String itemJSON = Packer.pack(item);
        String item2JSON = Packer.pack(item2);
        assertEquals(itemJSON, item2JSON);

        // different instance & data, need different result JSON
        assertNotEquals(playerJSON, itemJSON);
        assertNotEquals(playerJSON, blockJSON);
        assertNotEquals(itemJSON, blockJSON);

    }

    @Test
    public void testUnpack() throws Exception {

        String playerJSON
                = "{\"timer_counter\":2147483647,\"action_id\":10,\"id\":5,\"type\":0,\"st_x\":100,\"st_y\":1000}";
        String blockJSON
                = "{\"timer_counter\":2147483647,\"action_id\":10,\"id\":5,\"type\":1,\"st_x\":100,\"st_y\":1000}";
        String itemJSON
                = "{\"timer_counter\":2147483647,\"action_id\":10,\"id\":5,\"type\":2,\"st_x\":100,\"st_y\":1000}";

        StatusData player = Packer.unpack(playerJSON);
        assertEquals(player.type, StatusData.Player);
        assertEquals(player.id, ID);
        assertEquals(player.action_id, ACTION_ID);
        assertEquals(player.time_counter, TIMER_COUNTER);
        assertEquals(player.st_x, ST_X);
        assertEquals(player.st_y, ST_Y);

        StatusData block = Packer.unpack(blockJSON);
        assertEquals(block.type, StatusData.Block);
        assertEquals(block.id, ID);
        assertEquals(block.action_id, ACTION_ID);
        assertEquals(block.time_counter, TIMER_COUNTER);
        assertEquals(block.st_x, ST_X);
        assertEquals(block.st_y, ST_Y);

        StatusData item = Packer.unpack(itemJSON);
        assertEquals(item.type, StatusData.Item);
        assertEquals(item.id, ID);
        assertEquals(item.action_id, ACTION_ID);
        assertEquals(item.time_counter, TIMER_COUNTER);
        assertEquals(item.st_x, ST_X);
        assertEquals(item.st_y, ST_Y);


        String player2JSON
                = "{\"type\":0,\"action_id\":10,\"timer_counter\":2147483647,\"id\":5,\"st_x\":100,\"st_y\":1000}";
        StatusData player2 = Packer.unpack(player2JSON);

        assertEquals(player.type, player2.type);
        assertEquals(player.id, player2.id);
        assertEquals(player.action_id, player2.action_id);
        assertEquals(player.time_counter, player2.time_counter);
        assertEquals(player.st_x, player2.st_x);
        assertEquals(player.st_y, player2.st_y);
    }
}