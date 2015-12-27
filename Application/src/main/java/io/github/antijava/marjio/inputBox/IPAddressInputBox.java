package io.github.antijava.marjio.inputBox;

import io.github.antijava.marjio.common.IApplication;
import io.github.antijava.marjio.common.IInput;
import io.github.antijava.marjio.common.Key;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Zheng-Yuan on 12/26/2015.
 */
public class IPAddressInputBox extends InputTextBox {
    private final static int MAX_LENGTH = 15;
    private final static List<Key> VALID_INPUT = new ArrayList<>(Arrays.asList(
            Key.LEFT, Key.RIGHT,
            Key.BACK_SPACE, Key.DELETE, Key.PERIOD,
            Key.NUMPAD0, Key.DIGIT0,
            Key.NUMPAD1, Key.DIGIT1,
            Key.NUMPAD2, Key.DIGIT2,
            Key.NUMPAD3, Key.DIGIT3,
            Key.NUMPAD4, Key.DIGIT4,
            Key.NUMPAD5, Key.DIGIT5,
            Key.NUMPAD6, Key.DIGIT6,
            Key.NUMPAD7, Key.DIGIT7,
            Key.NUMPAD8, Key.DIGIT8,
            Key.NUMPAD9, Key.DIGIT9
    ));

    public IPAddressInputBox(IApplication application) {
        super(application, VALID_INPUT, MAX_LENGTH);
    }

    public String getText() {
        return mText.toString();
    }

    @Override
    public void update() {
        final List<Key> validInput = getValidInput();
        final IInput input = getApplication().getInput();
        for (Key key : validInput) {
            if (input.isPressed(key) || input.isPressing(key)) {
                switch (key) {
                    case LEFT: {
                        if (--mCurrentTextIndex < 0)
                            mCurrentTextIndex = 0;
                        break;
                    }
                    case RIGHT: {
                        if (++mCurrentTextIndex > mText.length())
                            mCurrentTextIndex = mText.length();
                        break;
                    }
                    case BACK_SPACE: {
                        if (mCurrentTextIndex > 0)
                            mText.deleteCharAt(--mCurrentTextIndex);
                        break;
                    }
                    case DELETE: {
                        if (mCurrentTextIndex < mText.length())
                            mText.deleteCharAt(mCurrentTextIndex);
                        break;
                    }
                    default: {
                        char ch = parseKeyToChar(key);
                        if (mText.length() < mMaxLength)
                            mText.insert(mCurrentTextIndex, ch);
                    }
                }
                break;
            }
        }
    }
}
