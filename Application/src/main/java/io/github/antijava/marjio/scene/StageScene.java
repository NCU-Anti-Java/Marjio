package io.github.antijava.marjio.scene;

import io.github.antijava.marjio.common.IApplication;
import io.github.antijava.marjio.common.IGraphics;
import io.github.antijava.marjio.common.IInput;
import io.github.antijava.marjio.common.IServer;
import io.github.antijava.marjio.common.graphics.Rectangle;
import io.github.antijava.marjio.common.graphics.Viewport;
import io.github.antijava.marjio.common.input.Status;
import io.github.antijava.marjio.graphics.Graphics;
import io.github.antijava.marjio.network.StatusData;
import io.github.antijava.marjio.scene.sceneObject.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Zheng-Yuan on 12/27/2015.
 */
public class StageScene extends SceneBase {
    private final static int START_GAME_COUNTER = 5;
    private final Viewport mBackground;
    private final Viewport mIntermediate;
    private final Viewport mForeground;
    private int mStartGameCounter;
    private SceneMap mMap;
    UUID mYourPlayerID;
    Map<UUID, Player> mPlayers;

    boolean mIsServer;

    public StageScene(IApplication application, int stage) {
        super(application);
        mMap = new SceneMap(application, stage);
        mBackground = getApplication().getGraphics().createViewport();
        mIntermediate = getApplication().getGraphics().createViewport();
        mForeground = getApplication().getGraphics().createViewport();
        mStartGameCounter = START_GAME_COUNTER;
    }

    @Override
    public void update() {
        super.update();

        if (mStartGameCounter-- > 0) {
            // TODO: Draw counter on the graphics.
            return ;
        }

        mPlayers.values().forEach(Player::preUpdate);

        checkKeyState();
        checkStatus();


        mPlayers.values().stream()
                .filter(player -> !mIsServer || !checkBump(player))
                .forEach(Player::update);
    }

    public List<Status> getValidStatuses() {
        return getApplication()
                .getInput()
                .getStatuses()
                .stream()
                .filter(st -> st.getData() instanceof StatusData &&
                        mPlayers.containsKey(((StatusData)st.getData()).uuid))
                .collect(Collectors.toList());
    }

    public void checkStatus () {
        List<Status> fetchedStatus = getValidStatuses();
        final IServer server = getApplication().getServer();

        for (Status st : fetchedStatus) {
            StatusData data = (StatusData) st.getData();
            Player player = mPlayers.get(data.uuid);

            switch (st.getType()) {
                case ClientMessage: {
                    if (mIsServer) {
                        if (player.isValidNextAction(data.action_id)) {
                            player.preUpdateNewAction(data.action_id);
                            data = player.getStatusData();

                            final Status new_st = new Status(data,
                                    Status.Type.ServerMessage);

                            if (mIsServer) {
                                try {
                                    server.broadcast(new_st);

                                } catch (Exception ex) {

                                }
                            }

                        } else {
                            data = player.getStatusData();
                            data.query = false;
                            final Status new_st = new Status(data,
                                    Status.Type.ServerVerification);

                            // TODO: Server send to client verify message

                            /*try {
                                server.send(new_st, player.getId());

                            } case (Exception ex) {

                            }*/
                        }
                    }
                    break;
                }

                case ServerMessage: {
                    if (data.uuid != mYourPlayerID)
                        player.preUpdateStatusData(data);

                    break;
                }

                case ServerVerification: {
                    if (!data.query)
                        player.preUpdateStatusData(data);

                    break;
                }

            }

        }
    }

    private void checkKeyState() {
        IInput input = getApplication().getInput();

        Player player = mPlayers.get(mYourPlayerID);
        int action_id = player.getMoveActionId();

        Map<IInterruptable, Integer> action_map = Player
                                                    .action_table
                                                        .get(action_id);

        action_map
                .keySet()
                .stream()
                .filter(it -> it.check(input) &&
                        IAction.time_counter_limit <= player.getTimeCounter())
                .forEach(it -> player.preUpdateNewAction(action_map.get(it)));

    }

    public boolean checkBump(Player player) {
        List<Block> entityBlocks = mMap.getAdjacentBlocks(player).stream()
                .filter(block -> block.getType() != Block.Type.AIR)
                .collect(Collectors.toList());

        final List<SceneObjectObjectBase> objects = new ArrayList<>();
        objects.addAll(entityBlocks);
        objects.addAll(mPlayers.values());
        objects.remove(player);

        for (SceneObjectObjectBase object: objects) {
            if (bumpTest(player.getOccupiedSpace(), object.getOccupiedSpace())) {
                return true;
            }
        }
        return false;
    }

    public static boolean bumpTest(Rectangle a, Rectangle b) {
        return isInsideRectangle(a.x, a.y, b) ||
                isInsideRectangle(a.x, a.y - a.height, b) ||
                isInsideRectangle(a.x + a.width, a.y, b) ||
                isInsideRectangle(a.x + a.width, a.y - a.height, b);
    }

    public static boolean isInsideRectangle(int x, int y, Rectangle rect) {
        return x > rect.x && x < rect.x + rect.width &&
                y > rect.y && y < rect.y + rect.height;
    }

    @Override
    public void dispose() {
        super.dispose();

        final IGraphics graphics = getApplication().getGraphics();
        graphics.removeViewport(mBackground);
        graphics.removeViewport(mIntermediate);
        graphics.removeViewport(mForeground);
    }
}
