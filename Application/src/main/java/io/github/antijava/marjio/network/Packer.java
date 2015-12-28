package io.github.antijava.marjio.network;

import com.bluelinelabs.logansquare.LoganSquare;
import io.github.antijava.marjio.common.input.Status;

import java.io.IOException;

/**
 * Created by Date on 2015/12/28.
 */
public class Packer {

    public static byte[] pack(Status status) throws IOException {

        return LoganSquare.serialize(status).getBytes();
    }

    public static Status unpack(byte[] bytes) throws IOException {

        return LoganSquare.parse(new String(bytes), Status.class);
    }
}
