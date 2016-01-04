package io.github.antijava.marjio.scene;

import io.github.antijava.marjio.common.IApplication;
import io.github.antijava.marjio.common.IInput;
import io.github.antijava.marjio.common.ISceneManager;
import io.github.antijava.marjio.common.exception.ObjectDisposedException;
import io.github.antijava.marjio.common.input.Key;
import io.github.antijava.marjio.constant.Constant;
import io.github.antijava.marjio.window.WindowCommand;

/**
 * Created by Zheng-Yuan on 12/24/2015.
 */
public class MainScene extends SceneBase implements Constant {
    private static final String[] MENU_TEXT = {"Create Game", "Join Game", "Exit"};
    private static final int HOST_GAME = 0;
    private static final int JOIN_GAME = 1;
    private static final int EXIT = 2;
    private final WindowCommand mWindowCommand;

    public MainScene(IApplication application) {
        super(application);

        mWindowCommand = new WindowCommand(application, 200, MENU_TEXT);
        mWindowCommand.setX((GAME_WIDTH - mWindowCommand.getWidth()) / 2);
        mWindowCommand.setY(380);
        mWindowCommand.setActive(true);
    }

    public MainScene(IApplication application, final int option) {
        this(application);

        mWindowCommand.setIndex(option);
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
