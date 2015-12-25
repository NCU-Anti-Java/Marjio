package io.github.antijava.marjio.common;

/**
 * Created by fntsr on 2015/12/23.
 */
public interface IInput {
    boolean isPressing(Key key);
    boolean isPressed(Key key);
    boolean isReleased(Key key);
    boolean isTrigger(Key key);
}
