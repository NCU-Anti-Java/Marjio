package io.github.antijava.marjio.network;

import io.github.antijava.marjio.common.input.Status;

import java.io.IOException;

/**
 * Created by Date on 2015/12/28.
 */
public class Packer {

    public static String pack(StatusData statusData) throws IOException {

        return StatusData__JsonHelper.serializeToJson(statusData);
    }

    public static StatusData unpack(String string) throws IOException {

        return StatusData__JsonHelper.parseFromJson(string);
    }

    public byte[] pack(Status status) throws IOException {

        return null;
    }

    public Status unpack2(byte[] bytes) throws IOException {

        return null;
    }
}
