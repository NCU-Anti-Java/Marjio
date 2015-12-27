package io.github.antijava.marjio.inputBox;

import io.github.antijava.marjio.common.IApplication;
import io.github.antijava.marjio.common.input.Key;

import java.util.List;

/**
 * Created by Zheng-Yuan on 12/26/2015.
 */
abstract public class InputTextBox extends InputBox {
    protected StringBuffer mText;
    protected int mMaxLength;
    protected int mCurrentTextIndex;

    public InputTextBox(IApplication application, List<Key> validInput, int maxLength) {
        super(application, validInput);
        mText = new StringBuffer();
        mCurrentTextIndex = 0;
        mMaxLength = maxLength;
    }

    static protected char parseKeyToChar(Key key) {
        // TODO: Add more keys?
        switch (key) {
            case A:
                return 'A';
            case B:
                return 'B';
            case C:
                return 'C';
            case D:
                return 'D';
            case E:
                return 'E';
            case F:
                return 'F';
            case G:
                return 'G';
            case H:
                return 'H';
            case I:
                return 'I';
            case J:
                return 'J';
            case K:
                return 'K';
            case L:
                return 'L';
            case M:
                return 'M';
            case N:
                return 'N';
            case O:
                return 'O';
            case P:
                return 'P';
            case Q:
                return 'Q';
            case R:
                return 'R';
            case S:
                return 'S';
            case T:
                return 'T';
            case U:
                return 'U';
            case V:
                return 'V';
            case W:
                return 'W';
            case X:
                return 'X';
            case Y:
                return 'Y';
            case Z:
                return 'Z';
            case NUMPAD0:
            case DIGIT0:
                return '0';
            case NUMPAD1:
            case DIGIT1:
                return '1';
            case NUMPAD2:
            case DIGIT2:
                return '2';
            case NUMPAD3:
            case DIGIT3:
                return '3';
            case NUMPAD4:
            case DIGIT4:
                return '4';
            case NUMPAD5:
            case DIGIT5:
                return '5';
            case NUMPAD6:
            case DIGIT6:
                return '6';
            case NUMPAD7:
            case DIGIT7:
                return '7';
            case NUMPAD8:
            case DIGIT8:
                return '8';
            case NUMPAD9:
            case DIGIT9:
                return '9';
            case PERIOD:
                return '.';
            default:
                throw new UnsupportedOperationException("The key is not implemented converting to char.");
        }
    }
}
