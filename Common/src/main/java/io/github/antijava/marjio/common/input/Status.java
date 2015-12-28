package io.github.antijava.marjio.common.input;

/**
 * Created by firejox on 2015/12/25.
 */
public abstract class Status {
    public abstract Type getType();
    public abstract Object getData();

    public enum Type {
        ServerMessage,
        ServerVerification,
        ClientMessage
    }
}
