package io.github.antijava.marjio.scene;

import io.github.antijava.marjio.common.*;

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
    }

    @Override
    public void update() {
        checkKeyState();
        // TODO: draw menu background and text. select choice mark as other color.
    }

    @Override
    public void initialize() {
        mCurrentChoice = 0;
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

        if (input.isPressed(Key.UP) || input.isPressing(Key.DOWN)) {
            if (--mCurrentChoice < 0)
                mCurrentChoice = 0;
        }
        else if (input.isPressed(Key.DOWN) || input.isPressing(Key.DOWN)) {
            if (++mCurrentChoice >= MENU_TEXT.length)
                mCurrentChoice = MENU_TEXT.length - 1;
        }
        else if (input.isPressed(Key.ENTER) || input.isPressing(Key.ENTER)) {
            select();
        }
    }
}
