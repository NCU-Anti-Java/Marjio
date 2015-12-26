package io.github.antijava.marjio.scene;

import io.github.antijava.marjio.common.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Zheng-Yuan on 12/24/2015.
 */
public class JoinScene extends SceneBase {
    private final static List<Key> VALID_INPUT = new ArrayList<Key>(Arrays.asList(
            Key.LEFT, Key.RIGHT, Key.UP, Key.DOWN,
            Key.BACK_SPACE, Key.DELETE, Key.DECIMAL,
            Key.NUMPAD0, Key.DIGIT0,
            Key.NUMPAD1, Key.DIGIT1,
            Key.NUMPAD2, Key.DIGIT2,
            Key.NUMPAD3, Key.DIGIT3,
            Key.NUMPAD4, Key.DIGIT4,
            Key.NUMPAD5, Key.DIGIT5,
            Key.NUMPAD6, Key.DIGIT6,
            Key.NUMPAD7, Key.DIGIT7,
            Key.NUMPAD8, Key.DIGIT8,
            Key.NUMPAD9, Key.DIGIT9,
            Key.ENTER
    ));
    private final String[] MENU_TEXT = {"Remote Address", "Go Back"};
    private final int IPADDRESS_MAX_LENGTH = 15;
    private final int INPUT_IPADDRESS = 0;
    private final int GO_BACK = 1;
    private int mCurrentChoice;
    private StringBuffer mIPAddress;
    private int mCurrentAddressIndex;

    public JoinScene(IApplication application) {
        super(application);
        mCurrentChoice = 0;
        mCurrentAddressIndex = 0;
        mIPAddress = new StringBuffer();
    }

    @Override
    public void update() {
        super.update();
        
        checkKeyState();
        // TODO: Draw view. Mark current choice as other color.
    }

    private void checkKeyState() {
        final IInput input = getApplication().getInput();

        for (Iterator it = VALID_INPUT.iterator(); it.hasNext(); ) {
            Key key = (Key)it.next();
            if (input.isPressed(key) || input.isPressing(key)) {
                switch (key) {
                    case UP: {
                        if (--mCurrentChoice < 0)
                            mCurrentChoice = 0;
                        break;
                    }
                    case DOWN: {
                        if (++mCurrentChoice >= MENU_TEXT.length)
                            mCurrentChoice = MENU_TEXT.length - 1;
                        break;
                    }
                    case ENTER: {
                        select();
                        break;
                    }
                    default: {
                        // TODO: Do we need man-made inputBox box? Deal with valid inputBox such as 0..9 , '.', backspace, delete.
                        if (mCurrentChoice == INPUT_IPADDRESS)
                            ;
                        break;
                    }
                }
                break;
            }
        }
    }

    private void select() {
        switch (mCurrentChoice) {
            case INPUT_IPADDRESS: {
                try {
                    final IClient client = getApplication().getClient();
                    client.start(mIPAddress.toString());
                    // TODO: Transate to room scene with client state.
                }
                catch (Exception ex) {
                    // TODO: Show Error.
                }
                break;
            }
            case GO_BACK: {
                final ISceneManager sceneManager = getApplication().getSceneManager();
                sceneManager.translationTo(new MainScene(getApplication()));
                break;
            }
        }
    }
}
