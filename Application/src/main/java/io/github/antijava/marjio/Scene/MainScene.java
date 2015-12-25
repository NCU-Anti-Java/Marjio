package io.github.antijava.marjio.scene;

import io.github.antijava.marjio.common.*;
import io.github.antijava.marjio.common.exception.ObjectDisposedException;

/**
 * Created by Zheng-Yuan on 12/24/2015.
 */
public class MainScene extends SceneBase {
    private final String[] MENU_TEXT = {"Host Game", "Join Game", "Exit"};
    private final int HOST_GAME = 0;
    private final int JOIN_GAME = 1;
    private final int EXIT = 2;
    private int mCurrentChoice;

    public MainScene(IApplication application) {
        super(application);

        mCurrentChoice = 0;
    }

    @Override
    public void update() throws ObjectDisposedException {
        super.update();

        checkKeyState();
        // TODO: draw menu background and text. select choice mark as other color.
    }

    private void select() {
        switch (mCurrentChoice) {
            case HOST_GAME: {
                // TODO: Host scene
                break;
            }
            case JOIN_GAME: {
                // TODO: Join scene
                break;
            }
            case EXIT: {
                final ISceneManager sceneManager = getApplication().getSceneManager();
                sceneManager.translationTo(null);
                break;
            }
        }
    }

    private void checkKeyState() {
        final IInput input = getApplication().getInput();

        // TODO: Input keys
        if (input.isPressed() || input.isPressing()) {
            // UP
            if (--mCurrentChoice < 0)
                mCurrentChoice = 0;
        }
        else if (input.isPressed() || input.isPressing()) {
            // DOWN
            if (++mCurrentChoice >= MENU_TEXT.length)
                mCurrentChoice = MENU_TEXT.length - 1;
        }
        else if (input.isPressed() || input.isPressing()) {
            // CONFIRM
            select();
        }
    }
}
