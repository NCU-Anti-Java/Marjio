package io.github.antijava.marjio.network;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import io.github.antijava.marjio.common.input.Status;
import io.github.antijava.marjio.common.input.StatusData;

import java.util.UUID;

/**
 * Created by freyr on 2015/12/29.
 */
public class PackerTest {
    Status st;
    StatusData data;

    @Before
    public void setUp() throws Exception {
        data = new StatusData();
        data.uuid = UUID.randomUUID();
        st = new Status(data, Status.Type.ClientMessage);
    }

    @After
    public void tearDown() throws Exception {

    }
    @Test
    public void testToEvent() throws Exception {

    }
}