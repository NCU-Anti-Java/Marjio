package io.github.antijava.marjio.scene;

import io.github.antijava.marjio.common.*;
import io.github.antijava.marjio.common.input.Key;
import io.github.antijava.marjio.common.input.Request;
import io.github.antijava.marjio.common.input.SyncList;
import io.github.antijava.marjio.constant.Constant;
import io.github.antijava.marjio.window.WindowCommand;
import io.github.antijava.marjio.window.WindowPlayerList;
import io.github.antijava.marjio.common.network.ClientInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Room Scene
 *
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
        application.getLogger().info("Enter RoomScene");

        mIsServer = isServer;
        mCurrentChoice = 0;

        initWindows();
    }

    @Override
    public void update() {
        super.update();

        // Check if server started
        if (mIsServer && !getApplication().getServer().isRunning()) {
            final Logger logger = getApplication().getLogger();
            try {
                getApplication().getServer().start();
                mWindowPlayerList.addPlayer(getApplication().getServer().getMyId().toString());
                logger.info("Server started. ");
            } catch (IOException e) {
                final ISceneManager sceneManager = getApplication().getSceneManager();

                // TODO: Let user know the port of game use is occupied, so we can't start server

                logger.info("Host game failed. The port of game using is occupied.");
                sceneManager.translationTo(new MainScene(getApplication()));
                return;
            }
        }


        mWindowCommand.update();
        mWindowPlayerList.update();
        checkKeyState();

        if (mIsServer) {
            checkClientRequest();
        } else {
            updatePlayerList();
            checkServerStatus();
        }
    }

    @Override
    public void dispose() {
        super.dispose();

        getApplication().getLogger().info("Leave RoomScene");
        mWindowPlayerList.dispose();
        mWindowCommand.dispose();
    }

    /**
     * Create windows
     */
    private void initWindows() {
        final IApplication application = getApplication();

        mWindowCommand = new WindowCommand(application, 180, MENU_TEXT);
        mWindowCommand.setActive(true);
        mWindowPlayerList = new WindowPlayerList(application, 600, 570);

        mWindowCommand.setX(600);
        mWindowCommand.setY(490);
    }

    // region Common
    /**
     * Check key trigger
     */
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
        else if(input.isPressed(Key.ENTER)) {
            mCurrentChoice = mWindowCommand.getIndex();
            select();
        }
    }

    /**
     * Do option execution
     */
    private void select() {
        switch(mCurrentChoice) {
            case START_GAME: {
                if(mIsServer) {
                    final Request request = new Request(Request.Types.ClientCanStartGame);
                    getApplication().getServer().broadcastTCP(request);
                    getApplication().getSceneManager().translationTo(new StageScene(getApplication(), true, 1));
                }
                break;
            }

            case EXIT_ROOM: {
                final ISceneManager sceneManager = getApplication().getSceneManager();

                if (mIsServer) {
                    final IServer server = getApplication().getServer();
                    final Request cancelRequest = new Request(Request.Types.ServerCancelRoom);

                    server.broadcastTCP(cancelRequest);
                    server.stop();
                } else {
                    final IClient client = getApplication().getClient();
                    final Request exitRequest = new Request(Request.Types.ClientWannaExitRoom);
                    exitRequest.setClientID(client.getMyId());
                    client.sendTCP(exitRequest);

                    // To prevent message sending be interrupt
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    client.stop();
                }
                sceneManager.translationTo(new MainScene(getApplication()));
                break;
            }
        }
    }
    // endregion Common

    // region ServerSideOnly
    /**
     * broadcast playlist to client with TCP
     */
    private void broadcastPlayerList() {
        ArrayList playerList = (ArrayList) mWindowPlayerList.getPlayerList();
        SyncList syncList = new SyncList(playerList);
        getApplication().getServer().broadcastTCP(syncList);
    }

    /**
     * Check if there has client wanna join or exit
     */
    private void checkClientRequest() {
        final IInput input = getApplication().getInput();
        final IServer server = getApplication().getServer();
        final Logger logger = getApplication().getLogger();

        List<Request> requests = input.getRequest();
        List<ClientInfo> clients = server.getClients();

        for (Request request : requests) {

            // Find request is asked from which client
            ClientInfo client = null;
            for (ClientInfo clientInfo : clients) {
                if (clientInfo.getClientID().equals(request.getClientID())) {
                    client = clientInfo;
                    break;
                }
            }
            if (client == null) {
                return;
            }
            logger.info("Get client's request from " + client.getClientID().toString() + ".");

            // Client Join
            if (request.getType() == Request.Types.ClientWannaJoinRoom) {
                server.sendTCP(new Request(Request.Types.ClientCanJoinRoom), client.getClientID());
                client.setIsJoined(true);
                mWindowPlayerList.addPlayer(client.getClientID().toString());
                logger.info("Player {" + client.getClientID().toString() + "} join room.");
            }

            // Client Exit
            else if (request.getType() == Request.Types.ClientWannaExitRoom) {
                client.setIsJoined(false);
                mWindowPlayerList.delPlayer(client.getClientID().toString());
                logger.info("Player {" + client.getClientID().toString() + "} leave room.");
            }

            logger.info("Server broadcast player list to clients.");
            broadcastPlayerList();
        }
    }
    // endregion ServerSideOnly

    // region ClientSideOnly
    /**
     * Check request from server
     */
    private void checkServerStatus() {
        final IInput input = getApplication().getInput();
        final ISceneManager sceneManager = getApplication().getSceneManager();
        final Logger logger = getApplication().getLogger();
        List<Request> requests = input.getRequest();

        for (Request request : requests) {
            if(request.getType() == Request.Types.ServerCancelRoom) {
                logger.info("Server canceled game.");
                sceneManager.translationTo(new MainScene(getApplication()));
                break;
            } else if (request.getType() == Request.Types.ClientCanStartGame) {
                logger.info("Server start game.");
                sceneManager.translationTo(new StageScene(getApplication(), false, 1));
                break;
            }
        }
    }

    /**
     * Update player list of room
     */
    private void updatePlayerList() {
        final IInput input = getApplication().getInput();
        final Logger logger = getApplication().getLogger();

        List<SyncList> syncLists = input.getSyncList();

        for (SyncList syncList : syncLists) {
            mWindowPlayerList.updatePlayerList((List<String>) syncList.getData());
        }

        logger.info("Client update player list from server.");
    }
    // endregion ClientSideOnly
}
