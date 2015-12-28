package io.github.antijava.marjio.scene;

import io.github.antijava.marjio.common.*;
import io.github.antijava.marjio.common.exception.ObjectDisposedException;
import io.github.antijava.marjio.common.input.Key;
import io.github.antijava.marjio.window.WindowSelectableBase;

/**
 * Created by Zheng-Yuan on 12/24/2015.
 */
public class MainScene extends SceneBase {
    private final String[] MENU_TEXT = {"Host Game", "Join Game", "Exit"};
    private final int HOST_GAME = 0;
    private final int JOIN_GAME = 1;
    private final int EXIT = 2;
    private int mCurrentChoice;
    private final WindowSelectableBase mWindowSelectable;

    public MainScene(IApplication application) {
        super(application);

        mCurrentChoice = 0;
        mWindowSelectable = new WindowSelectableBase(application, 200, 300);
    }

    @Override
    public void update() throws ObjectDisposedException {
        super.update();

        mWindowSelectable.update();
        checkKeyState();
        // TODO: draw menu background and text. select choice mark as other color.
    }

    @Override
    public void dispose() {
        super.dispose();

        // mWindowSelectable.dispose();
    }

    private void select() {
        final ISceneManager sceneManager = getApplication().getSceneManager();
        switch (mCurrentChoice) {
            case HOST_GAME: {
                sceneManager.translationTo(new RoomScene(getApplication(), true));
                break;
            }
            case JOIN_GAME: {
                sceneManager.translationTo(new JoinScene(getApplication()));
                break;
            }
            case EXIT: {
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
