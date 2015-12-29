package io.github.antijava.marjio.network;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import io.github.antijava.marjio.common.input.Status;
import io.github.antijava.marjio.network.StatusData;

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
    public void testPack() throws Exception {
        System.out.print(new String(Packer.pack(st)));
    }

    @Test
    public void testUnpack() throws Exception {
        Status new_st = Packer.unpack(Packer.pack(st));
        StatusData n_data = (StatusData)new_st.getData();

        Assert.assertTrue(st.getType() == new_st.getType());
        Assert.assertTrue(n_data.uuid.equals(data.uuid));
        Assert.assertTrue(n_data.id == data.id);
    }

    @Test
    public void testToEvent() throws Exception {

    }
}