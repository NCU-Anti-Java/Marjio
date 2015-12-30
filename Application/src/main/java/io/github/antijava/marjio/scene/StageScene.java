package io.github.antijava.marjio.scene;

import io.github.antijava.marjio.common.IApplication;
import io.github.antijava.marjio.common.IGraphics;
import io.github.antijava.marjio.common.IInput;
import io.github.antijava.marjio.common.IServer;
import io.github.antijava.marjio.common.graphics.Color;
import io.github.antijava.marjio.common.graphics.IBitmap;
import io.github.antijava.marjio.common.graphics.Rectangle;
import io.github.antijava.marjio.common.graphics.Viewport;
import io.github.antijava.marjio.common.input.Status;
import io.github.antijava.marjio.constant.GameConstant;
import io.github.antijava.marjio.graphics.Font;
import io.github.antijava.marjio.graphics.Graphics;
import io.github.antijava.marjio.graphics.Sprite;
import io.github.antijava.marjio.graphics.SpriteBase;
import io.github.antijava.marjio.network.StatusData;
import io.github.antijava.marjio.scene.sceneObject.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Zheng-Yuan on 12/27/2015.
 */
public class StageScene extends SceneBase implements GameConstant {
    private final static int COUNT_DOWN_SECOND = 5;
    private final Viewport mBackground;
    private final Viewport mIntermediate;
    private final Viewport mForeground;
    private final Sprite mTimer;
    private int mCountDown;
    private final SceneMap mMap;
    private final UUID mYourPlayerID;
    private final Map<UUID, Player> mPlayers;

    boolean mIsServer;

    public StageScene(IApplication application, int stage) {
        super(application);
        final IGraphics graphics = application.getGraphics();

        mMap = new SceneMap(application, stage);
        mBackground = graphics.createViewport();
        mIntermediate = graphics.createViewport();
        mForeground = graphics.createViewport();
        mTimer = new SpriteBase(mForeground);
        mTimer.setBitmap(graphics.createBitmap(GAME_WIDTH, GAME_HEIGHT));
        mPlayers = new HashMap<>();
        mYourPlayerID = UUID.randomUUID();
        mPlayers.put(mYourPlayerID, new Player(mIntermediate, mYourPlayerID));
        mCountDown = COUNT_DOWN_SECOND * FRAMERATE;
    }

    @Override
    public void update() {
        super.update();

        if (mCountDown > 0) {

            // TODO: If you are client, you should receive server's count down time.
            mCountDown--;
            mTimer.getBitmap().clear();
            mTimer.getBitmap().setFont(new Font("Consolas", 48, false, true));
            mTimer.getBitmap().drawText(Integer.toString(mCountDown / FRAMERATE), 0, 0, GAME_WIDTH, GAME_HEIGHT, Color.WHITE, IBitmap.TextAlign.CENTER);
            mTimer.update();

            return ;
        }
        else {
            mTimer.dispose();
        }

        try {

            mPlayers.values().forEach(Player::preUpdate);

            checkKeyState();
            checkStatus();


            mPlayers.values().stream()
                    .filter(player -> !mIsServer || !checkBump(player))
                    .forEach(Player::update);

        }
        catch (Exception ex) {

        }
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
        mTimer.dispose();
    }
}
