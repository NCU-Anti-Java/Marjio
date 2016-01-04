package io.github.antijava.marjio.common.input;


/**
 * Created by freyr on 2016/1/2.
 */
public interface IKeyInput {

    boolean isPressing(Key key);

    boolean isPressed(Key key);

    boolean isReleased(Key key);

    /**
     * with key pressed for a while then key is repeat.
     * */
    boolean isRepeat(Key key);

    boolean isKeyUp(Key key);

    boolean isKeyDown(Key key);

}
