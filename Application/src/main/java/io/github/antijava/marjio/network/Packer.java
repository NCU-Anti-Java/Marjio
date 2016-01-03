package io.github.antijava.marjio.network;

import io.github.antijava.marjio.common.input.Event;
import io.github.antijava.marjio.common.network.Packable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Date on 2015/12/28.
 */
public class Packer {
    /*
     * @Param statusData
     * @Param type:
     *             tag Types.NetworkServer:
     *                 when Server recv packet from Client, and Server need pass Event to Input Module
     *             tag Types.NetworkClient:
     *                 when Client recv packet from Server, and need pass Event to Input Module
     */
    public static Event toEvent(Packable packableObject , Event.Type type) {
        return new Event(packableObject, type);
    }

    public static Packable ByteArraytoPackable(byte[] arr) {
        Packable p = null;
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(arr);
            ObjectInputStream oin = new ObjectInputStream(in);

            p =  (Packable) oin.readObject();

            oin.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return p;
    }

    public static byte[] PackabletoByteArray(Packable p) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oout = new ObjectOutputStream(out);

            oout.writeObject(p);

            oout.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }

}
