package io.github.antijava.marjio.network;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.github.antijava.marjio.common.input.Status;

import java.util.UUID;

/**
 * Created by freyr on 2015/12/29.
 */
public class PackerTest {
    Status st;

    @Before
    public void setUp() throws Exception {

        st = new Status(Status.DataTypes.Player);
        st.setClientID(UUID.randomUUID());
    }

    @After
    public void tearDown() throws Exception {

    }
    @Test
    public void testToEvent() throws Exception {

    }
}