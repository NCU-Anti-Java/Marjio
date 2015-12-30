package io.github.antijava.marjio.network;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by Date on 2015/12/29.
 */
public class StatusDataTest {

    private StatusData statusData;
    private UUID uuid;

    @Before
    public void setUp() throws Exception {

        statusData = new StatusData();
        uuid = UUID.randomUUID();
    }

    @After
    public void tearDown() throws Exception {

        statusData = null;
        uuid = null;
    }

    @Test
    public void testPreparePack() throws Exception {

        statusData.uuid = uuid;
        statusData.PreparePack();
        assertEquals(statusData._uuid, uuid.toString());
    }

    @Test
    public void testAfterUnpack() throws Exception {

        statusData._uuid = uuid.toString();
        statusData.AfterUnpack();
        assertEquals(statusData.uuid, uuid);
    }
}