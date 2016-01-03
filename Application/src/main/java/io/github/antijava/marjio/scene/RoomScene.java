package io.github.antijava.marjio.scene;

import io.github.antijava.marjio.SceneManager;
import io.github.antijava.marjio.common.*;
import io.github.antijava.marjio.common.input.Key;
import io.github.antijava.marjio.common.input.Request;
import io.github.antijava.marjio.common.input.Status;
import io.github.antijava.marjio.common.input.SyncList;
import io.github.antijava.marjio.constant.Constant;
import io.github.antijava.marjio.window.WindowCommand;
import io.github.antijava.marjio.window.WindowPlayerList;
import io.github.antijava.marjio.common.network.ClientInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    @Override
    public void update() {
        super.update();

        // Check if server started
        if (mIsServer && !getApplication().getServer().isRunning()) {
            try {
                getApplication().getServer().start();
                mWindowPlayerList.addPlayer(getApplication().getServer().getMyId().toString());
            } catch (IOException e) {
                final Logger logger = getApplication().getLogger();
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
    private void broadcastPlayerList() {
        // TODO: broadcast player list
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

        List<Request> requests = input.getRequest();
        List<ClientInfo> clients = server.getClients();

        for (Request request : requests) {

            // Find request is asked from which client
            getApplication().getLogger().log(Level.INFO, request.getClientID().toString());
            ClientInfo client = null;
            for (ClientInfo clientInfo : clients) {
                if (clientInfo.getClientID().equals(request.getClientID())) {
                    client = clientInfo;
                }
            }
            if (client == null) {
                return;
            }

            // Client Join
            if (request.getType() == Request.Types.ClientWannaJoinRoom) {
                server.sendTCP(new Request(Request.Types.ClientCanJoinRoom), client.getClientID());
                client.setIsJoined(true);
                mWindowPlayerList.addPlayer(client.getClientID().toString());
            }

            // Client Exit
            else if (request.getType() == Request.Types.ClientWannaExitRoom) {
                getApplication().getLogger().log(Level.INFO, "del!!!");
                client.setIsJoined(false);
                mWindowPlayerList.delPlayer(client.getClientID().toString());
            }

            broadcastPlayerList();
        }
    }
    // endregion ServerSideOnly

    // region ClientSideOnly
    private void checkServerStatus() {
        final IInput input = getApplication().getInput();
        final ISceneManager sceneManager = getApplication().getSceneManager();
        List<Request> requests = input.getRequest();

        for (Request request : requests) {
            if(request.getType() == Request.Types.ServerCancelRoom) {
                sceneManager.translationTo(new MainScene(getApplication()));
                break;
            } else if (request.getType() == Request.Types.ClientCanStartGame) {
                sceneManager.translationTo(new StageScene(getApplication(), false, 1));
                break;
            }
        }
    }

    private void updatePlayerList() {
        // TODO: get input and update list
        final IInput input = getApplication().getInput();

        List<SyncList> syncLists = input.getSyncList();

        for (SyncList syncList : syncLists) {
            mWindowPlayerList.updatePlayerList((List<String>)syncList.getData());
        }
    }
    // endregion ClientSideOnly
}
