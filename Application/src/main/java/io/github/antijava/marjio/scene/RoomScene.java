package io.github.antijava.marjio.scene;

import io.github.antijava.marjio.common.*;
import io.github.antijava.marjio.common.graphics.Color;
import io.github.antijava.marjio.common.graphics.IBitmap;
import io.github.antijava.marjio.common.input.Key;
import io.github.antijava.marjio.common.input.Status;
import io.github.antijava.marjio.constant.Constant;
import io.github.antijava.marjio.window.WindowBase;
import io.github.antijava.marjio.window.WindowCommand;
import io.github.antijava.marjio.window.WindowIPAddressInput;
import io.github.antijava.marjio.window.WindowPlayerList;

import java.util.List;

/**
 * Created by Zheng-Yuan on 12/27/2015.
 */
public class RoomScene extends SceneBase implements Constant {
    private final String[] MENU_TEXT = {"Start Game", "Exit Room"};
    private final int START_GAME = 0;
    private final int EXIT_ROOM = 1;
    private final boolean mIsServer;
    private int mCurrentChoice;

    private WindowCommand mWindowCommand;
    private WindowPlayerList mWindowPlayerList;

    public RoomScene(IApplication application, boolean isServer) {
        super(application);
        mIsServer = isServer;
        mCurrentChoice = 0;

        initWindows();
    }

    private void initWindows() {
        final IApplication application = getApplication();

        mWindowCommand = new WindowCommand(application, 180, MENU_TEXT);
        mWindowCommand.setActive(true);
        mWindowPlayerList = new WindowPlayerList(application, 600, 570);

        mWindowCommand.setX(600);
        mWindowCommand.setY(490);
    }

    @Override
    public void update() {
        super.update();
        mWindowCommand.update();
        mWindowPlayerList.update();
        checkKeyState();
        checkStatus();
    }

    private void checkKeyState() {
        final IInput input = getApplication().getInput();

        if (input.isPressed(Key.LEFT) || input.isPressing(Key.LEFT)) {
            if (--mCurrentChoice < 0)
                mCurrentChoice = 0;
            mWindowCommand.setActive(false);
            mWindowPlayerList.setActive(true);
        }
        else if(input.isPressed(Key.RIGHT) || input.isPressing(Key.RIGHT)) {
            if (++mCurrentChoice >= MENU_TEXT.length)
                mCurrentChoice = MENU_TEXT.length - 1;
            mWindowCommand.setActive(true);
            mWindowPlayerList.setActive(false);
        }
        else if(input.isPressed(Key.ENTER) || input.isPressing(Key.ENTER)) {
            mCurrentChoice = mWindowCommand.getIndex();
            select();
        }
    }

    public void checkStatus () {
        List<Status> fetchedStatus = getApplication().getInput().getStatuses();
    }

    private void select() {
        switch(mCurrentChoice) {
            case EXIT_ROOM: {
                final ISceneManager sceneManager = getApplication().getSceneManager();

                if (mIsServer) {
                    final IServer server = getApplication().getServer();
                    // TODO: Server should broadcast to clients that the room is canceled.
                    try {
                        server.stop();
                    } catch (InterruptedException e) {
                        // TODO
                    } catch (UnsupportedOperationException e) {
                        // TODO
                    }
                }
                else {
                    final IClient client = getApplication().getClient();
                    // TODO: Client should send message to server that I quit.
                    try {
                        client.stop();
                    } catch (InterruptedException e) {
                        // TODO
                    } catch (UnsupportedOperationException e) {
                        // TODO
                    }

                }
                sceneManager.translationTo(new MainScene(getApplication()));
                break;
            }
            case START_GAME: {
                // TODO: Only server can start game, then server broadcast to clients to start game.
                if(mIsServer) {

                }
                break;
            }
        }
    }
}
