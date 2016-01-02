package io.github.antijava.marjio.common.input;

import io.github.antijava.marjio.common.network.Packable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.UUID;

/**
 * Created by firejox on 2015/12/25.
 */
public class Status implements Packable, Externalizable {
    private Types mType;
    private Object mObject;
    private UUID mId;


    public Status(SceneObjectStatus obj, Types type) {
        mType = type;
        mObject = obj;
        mId = obj.uuid;
    }

    @Override
    public UUID getClientID() {
        return mId;
    }

    @Override
    public void setClientID(UUID id) {
        mId = id;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(mType.name());
        out.writeObject(mObject);
        out.writeObject(mId);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        mType = Types.valueOf(in.readUTF());
        mObject = in.readObject();
        mId = (UUID) in.readObject();
    }

    public enum Types {
        ServerMessage,
        ServerVerification,
        ClientMessage;
    }

    public Types getType() {
        return mType;
    }

    public Object getData() {
        return mObject;
    }
}
