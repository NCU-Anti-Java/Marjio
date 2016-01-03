package io.github.antijava.marjio.scene;

import io.github.antijava.marjio.common.IApplication;
import io.github.antijava.marjio.common.IClient;
import io.github.antijava.marjio.common.IInput;
import io.github.antijava.marjio.common.ISceneManager;
import io.github.antijava.marjio.common.graphics.Color;
import io.github.antijava.marjio.common.graphics.IBitmap;
import io.github.antijava.marjio.common.input.Key;
import io.github.antijava.marjio.common.input.Request;
import io.github.antijava.marjio.constant.Constant;
import io.github.antijava.marjio.window.WindowBase;
import io.github.antijava.marjio.window.WindowCommand;
import io.github.antijava.marjio.window.WindowIPAddressInput;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Join Scene that let client join server's game
 *
 * Created by Zheng-Yuan on 12/24/2015.
 */
public class JoinScene extends SceneBase implements Constant {
    private WindowBase mWindowBack;
    private WindowIPAddressInput mWindowIPAddressInput;
    private WindowCommand mWindowCommand;

    // unit: how many frame
    private int mTimeout = 0;
    private final static int DEFAULT_TIMEOUT = 600;

    public JoinScene(IApplication application) {
        super(application);

        initWindows();
        moveWindows((GAME_WIDTH - mWindowBack.getWidth()) / 2,
                (GAME_HEIGHT - mWindowBack.getHeight()) / 2);
    }

    @Override
    public void update() {
        super.update();

        final IInput input = getApplication().getInput();

        // Check whether response of request is back
        if (mTimeout > 0 && checkJoin()) {
            return;
        }

        mWindowBack.update();
        if (input.isPressed(Key.ENTER)) {
            if (mWindowCommand.isActive()) {
                if (processOption()) {
                    return;
                }
            }
        }
        if (input.isRepeat(Key.LEFT)) {
            if (mWindowIPAddressInput.isActive() && mWindowIPAddressInput.getIndex() == 0) {
                mWindowIPAddressInput.setActive(false);
                mWindowCommand.setActive(true);
            } else if (mWindowCommand.isActive()) {
                mWindowCommand.setActive(false);
                mWindowIPAddressInput.setIndex(14);
                mWindowIPAddressInput.update();
                mWindowIPAddressInput.setActive(true);
                return;
            }
        }
        if (input.isRepeat(Key.RIGHT)) {
            if (mWindowIPAddressInput.isActive() && mWindowIPAddressInput.getIndex() == 14) {
                mWindowIPAddressInput.setActive(false);
                mWindowCommand.setActive(true);
            } else if (mWindowCommand.isActive()) {
                mWindowCommand.setActive(false);
                mWindowIPAddressInput.setIndex(0);
                mWindowIPAddressInput.update();
                mWindowIPAddressInput.setActive(true);
                return;
            }
        }
        mWindowIPAddressInput.update();
        mWindowCommand.update();
    }

    @Override
    public void dispose() {
        super.dispose();

        mWindowBack.dispose();
        mWindowCommand.dispose();
        mWindowIPAddressInput.dispose();
    }

    /**
     * Do option execution
     *
     * @return does it do something
     */
    private boolean processOption() {
        switch (mWindowCommand.getIndex()) {

            // Confirm option, connect to server
            case 0: {
                try {
                    final IClient client = getApplication().getClient();
                    final Request joinRequest = new Request(UUID.randomUUID() ,Request.Types.ClientWannaJoinRoom);
                    final Logger logger = getApplication().getLogger();

                    if (client.isRunning()) {
                        getApplication().getLogger().warning("Client is running");
                    }
                    client.start(mWindowIPAddressInput.getAddress());
                    client.sendTCP(joinRequest);
                    resetTimeout();

                    logger.log(Level.INFO, "Client sent \"ClientWannaJoinRoom\" request to server.");

                    // TODO: Let user know we are waiting response (eg. unable option)

                    return true;
                }

                // Unable to connect server. throw by client.start
                catch (IOException e) {
                    final Logger logger = getApplication().getLogger();
                    logger.info(e.getMessage());

                    // TODO: Let user know connection failed
                }

                // If there got exception, means the program have something wrong
                catch (Exception e) {
                    e.printStackTrace();
                }

                return false;
            }

            // Cancel option, return to main scene
            case 1: {
                final ISceneManager sceneManager = getApplication().getSceneManager();
                sceneManager.translationTo(new MainScene(getApplication(), 1));

                return true;
            }
        }

        return false;
    }

    /**
     * Create windows
     */
    private void initWindows() {
        final IApplication application = getApplication();

        mWindowIPAddressInput = new WindowIPAddressInput(application);
        mWindowCommand = new WindowCommand(application, 140, new String[] {"確定", "返回"});
        mWindowBack = new WindowBase(application,
                mWindowIPAddressInput.getWidth() + mWindowCommand.getWidth(),
                mWindowCommand.getHeight()) {
            @Override
            public void update() {
                super.update();

                final IBitmap readmeContent = getContent();
                readmeContent.clear();
                readmeContent.drawText("輸入伺服器 IP: ", 0, 0, -1, 24, Color.WHITE);
            }
        };

        mWindowIPAddressInput.setBackgroundOpacity(255);
        mWindowIPAddressInput.setZ(10);
        mWindowCommand.setIndex(1);
        mWindowCommand.setBackgroundOpacity(255);
        mWindowCommand.setZ(10);

        mWindowIPAddressInput.setActive(true);
    }

    /**
     * Move windows to specify location
     */
    private void moveWindows(final int x, final int y) {
        mWindowBack.setX(x);
        mWindowBack.setY(y);
        mWindowIPAddressInput.setX(x);
        mWindowIPAddressInput.setY(y + 24);
        mWindowCommand.setX(mWindowIPAddressInput.getWidth() + x);
        mWindowCommand.setY(y);
    }

    /**
     * Reset timeout of request waiting response
     */
    private void resetTimeout() {
        mTimeout = DEFAULT_TIMEOUT;
    }

    /**
     * Check if server give response let client know it can join room
     *
     * @return if it get specify response
     */
    private boolean checkJoin() {
        final ISceneManager sceneManager = getApplication().getSceneManager();
        final IInput input = getApplication().getInput();
        final List<Request> requests = input.getRequest();
        final IClient client = getApplication().getClient();
        final Logger logger = getApplication().getLogger();

        for (Request request : requests) {
            if (request.getType() == Request.Types.ClientCanJoinRoom) {
                client.setMyId(request.getClientID());
                mTimeout = 0;
                sceneManager.translationTo(new RoomScene(getApplication(), false));
                logger.log(Level.INFO, "Client get \"ClientCanJoinRoom\" request from server.");

                return true;
            }
        }
        mTimeout--;

        return false;
    }
}
