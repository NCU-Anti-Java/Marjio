package io.github.antijava.marjio.scene;

import io.github.antijava.marjio.common.*;
import io.github.antijava.marjio.common.exception.ObjectDisposedException;
import io.github.antijava.marjio.common.input.Key;
import io.github.antijava.marjio.constant.Constant;
import io.github.antijava.marjio.window.WindowCommand;
import io.github.antijava.marjio.window.WindowSelectableBase;

/**
 * Created by Zheng-Yuan on 12/24/2015.
 */
public class MainScene extends SceneBase implements Constant {
    private final String[] MENU_TEXT = {"Host Game", "Join Game", "Exit"};
    private final int HOST_GAME = 0;
    private final int JOIN_GAME = 1;
    private final int EXIT = 2;
    private int mCurrentChoice;
    private final WindowCommand mWindowCommand;

    public MainScene(IApplication application) {
        super(application);

        mCurrentChoice = 0;
        mWindowCommand = new WindowCommand(application, 200, MENU_TEXT);
        mWindowCommand.setX((GAME_WIDTH - mWindowCommand.getWidth()) / 2);
        mWindowCommand.setY(380);
        mWindowCommand.setActive(true);
    }

    @Override
    public void update() throws ObjectDisposedException {
        super.update();

        mWindowCommand.update();

        final IInput input = getApplication().getInput();
        if (input.isPressed(Key.ENTER))
            select();
    }

    @Override
    public void dispose() {
        super.dispose();

        mWindowCommand.dispose();
    }

    private void select() {
        final ISceneManager sceneManager = getApplication().getSceneManager();
        switch (mWindowCommand.getIndex()) {
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
}
