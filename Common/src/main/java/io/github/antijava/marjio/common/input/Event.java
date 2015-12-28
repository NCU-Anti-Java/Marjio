package io.github.antijava.marjio.common.input;

/**
 * Created by firejox on 2015/12/25.
 */
public class Event {
    Object obj;
    Type type;

    public Event(Object obj, Type type) {
        this.obj = obj;
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public Object getData() {
        return obj;
    }

    public enum Type {
        Keyboard,
        NetworkServer, //from client packet
        NetWorkClient //from server input
    }
}
