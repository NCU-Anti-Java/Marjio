package io.github.antijava.marjio.common.input;

/**
 * Created by firejox on 2015/12/25.
 */
public class Status {
    Type type;
    Object obj;

    Status(Object obj, Type type) {
        this.type = type;
        this.obj = obj;
    }

    public Type getType() {
        return type;
    }

    public Object getData() {
        return obj;
    }

    public enum Type {
        ServerMessage,
        ServerVerification,
        ClientMessage
    }
}
