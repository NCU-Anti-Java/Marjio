package io.github.antijava.marjio.common.input;

/**
 * Created by firejox on 2015/12/25.
 */
public class Status {
    Type type;
    Object obj;

    public Status(Object obj, Type type) {
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
        ServerMessage(0),
        ServerVerification(1),
        ClientMessage(2);

        private final int value;

        private Type(int i) {
            value = i;
        }

        public int getValue() { return value; }

        static public Type TypeOfInt(int value) {
            for (final Type type : Type.values())
                if (value == type.value)
                    return type;

            return null;
        }
    }
}
