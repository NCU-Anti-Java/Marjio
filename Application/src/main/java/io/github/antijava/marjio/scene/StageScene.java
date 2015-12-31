package io.github.antijava.marjio.scene;

import io.github.antijava.marjio.common.*;
import io.github.antijava.marjio.common.graphics.Color;
import io.github.antijava.marjio.common.graphics.IBitmap;
import io.github.antijava.marjio.common.graphics.Rectangle;
import io.github.antijava.marjio.common.graphics.Viewport;
import io.github.antijava.marjio.common.input.Key;
import io.github.antijava.marjio.common.input.Status;
import io.github.antijava.marjio.constant.Constant;
import io.github.antijava.marjio.constant.SceneObjectConstant;
import io.github.antijava.marjio.graphics.Font;
import io.github.antijava.marjio.graphics.Sprite;
import io.github.antijava.marjio.graphics.SpriteBase;
import io.github.antijava.marjio.network.StatusData;
import io.github.antijava.marjio.scene.sceneObject.Block;
import io.github.antijava.marjio.scene.sceneObject.PhysicsConstant;
import io.github.antijava.marjio.scene.sceneObject.Player;
import io.github.antijava.marjio.scene.sceneObject.SceneMap;
import io.github.antijava.marjio.window.WindowBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Zheng-Yuan on 12/27/2015.
 */
public class StageScene extends SceneBase implements Constant {
    private final static int START_GAME_COUNTER = 5;
    private final Viewport GameViewPort;
    private SceneMap mMap;
    UUID mYourPlayerID;
    Map<UUID, Player> mPlayers;

    private final Sprite mTimer;
    private int mCountDown;


    boolean mIsServer;

    WindowBase ba;
    Player p;

    public StageScene(IApplication application, int stage) {
        super(application);
        final IGraphics graphics = application.getGraphics();

        mMap = new SceneMap(application, stage);

        mCountDown = START_GAME_COUNTER;
        GameViewPort = graphics.createViewport();

        mTimer = new SpriteBase(GameViewPort);
        mTimer.setBitmap(graphics.createBitmap(GAME_WIDTH, GAME_HEIGHT));


        /* //TODO: Fake data

        ba = new WindowBase(getApplication(), PLAYER_SIZE, PLAYER_SIZE);
        mYourPlayerID = UUID.randomUUID();

        p = new Player(application.getGraphics()
                .getDefaultViewport(), mYourPlayerID);
        mPlayers = new HashMap<>();
        mPlayers.put(mYourPlayerID, p);
        */
    }

    @Override
    public void dispose() {
        super.dispose();
        //ba.dispose();
    }

    @Override
    public void update() {
        super.update();
        //ba.update();

        if (mCountDown > 0) {

            // TODO: If you are client, you should receive server's count down time.
            mCountDown--;
            mTimer.getBitmap().clear();
            mTimer.getBitmap().setFont(new Font("Consolas", 48, false, true));
            mTimer.getBitmap().drawText(
                    Integer.toString(mCountDown / FRAMERATE), 0, 0,
                    GAME_WIDTH, GAME_HEIGHT, Color.WHITE, IBitmap.TextAlign.CENTER);
            mTimer.update();

            return ;
        }
        mPlayers.values().forEach(Player::preUpdate);

        checkKeyState();
        checkStatus();
        solveBumps();

        mPlayers.values().forEach(Player::update);
        // ba.setX(p.getX());
        // ba.setY(p.getY());

        //getApplication().getLogger().info(p.toString());

        if (!mIsServer) {
            IClient client = getApplication().getClient();
            StatusData data = mPlayers.get(mYourPlayerID).getStatusData();

            try {
                client.send(new Status(data, Status.Types.ClientMessage));
            } catch (Exception e) {
            }
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
                        if (player.isValidData(data)) {
                            player.preUpdateStatusData(data);

                            final Status new_st = new Status(data,
                                    Status.Types.ServerMessage);

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
                                    Status.Types.ServerVerification);

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

        if (input.isPressed(Key.MOVE_LEFT))
            player.addAccelerationX(-0.5);

        else if (input.isRepeat(Key.MOVE_LEFT) && !input.isPressed(Key.MOVE_RIGHT))
            player.setAccelerationX(-PhysicsConstant.friction - 1e-5);

        else if (input.isReleased(Key.MOVE_LEFT))
            player.setAccelerationX(0.0);


        if (input.isPressed(Key.MOVE_RIGHT))
            player.addAccelerationX(0.5);

        else if (input.isRepeat(Key.MOVE_RIGHT) && !input.isPressed(Key.MOVE_LEFT))
            player.setAccelerationX(PhysicsConstant.friction + 1e-5);

        else if (input.isReleased(Key.MOVE_RIGHT))
            player.setAccelerationX(0.0);

        if (input.isPressing(Key.MOVE_LEFT) && input.isPressing(Key.MOVE_RIGHT)) {
            player.setAccelerationX(0.0);
            player.setVelocityX(0.0);
        }


        if (input.isPressed(Key.JUMP)) {
            try {

                Block block = mMap.getBlock(
                        player.getY() / SceneObjectConstant.BLOCK_SIZE + 1,
                        player.getX() / SceneObjectConstant.BLOCK_SIZE);

                // prevent gravity problem
                if (block.getType() != Block.Type.AIR) {
                    player.setVelocityY(-20.0);
                }
            } catch (Exception ex) {

            }

        }

    }

    private void solveBumps() {
        for (Player player : mPlayers.values()) {
            try {
                Block block = mMap.getBlock(
                        player.getY() / SceneObjectConstant.BLOCK_SIZE + 1,
                        player.getX() / SceneObjectConstant.BLOCK_SIZE);

                // prevent gravity problem
                if (block.getType() != Block.Type.AIR) {
                    if (player.getVelocityY() > 0)
                        player.setVelocityY(0.0);
                }
            } catch (Exception ex) {

            }


            solveBumpBlock(player);
        }

        //TODO: Elastic collision for each player

        List<Player> players = new ArrayList<>(mPlayers.values());

        for (int i = 0; i < players.size(); i++) {
            for (int j = 0; j < players.size(); j++) {
                Player pi = players.get(i);
                Player pj = players.get(j);

                if (bumpTest(pi.getOccupiedSpace(), pj.getOccupiedSpace())) {
                    double tvx = pi.getVelocityX();
                    double tvy = pi.getVelocityY();

                    pi.setVelocityY(pj.getVelocityY());
                    pi.setVelocityX(pj.getVelocityY());

                    pj.setVelocityX(tvx);
                    pj.setVelocityY(tvy);
                }
            }

        }

    }

    private void solveBumpBlock (Player player) {
        List<Block> entityBlocks = mMap.getAdjacentBlocks(player).stream()
                .filter(block -> block.getType() != Block.Type.AIR)
                .collect(Collectors.toList());

        for (Block b : entityBlocks)
            if (bumpTest(player.getOccupiedSpace(), b.getOccupiedSpace())) {
                //TODO: setup reflect direction

                if (b.getX() > player.getX())
                    player.setVelocityX(- Math.abs(player.getVelocityX()));
                else
                    player.setVelocityX(Math.abs(player.getVelocityX()));

                if (b.getY() > player.getY())
                    player.setVelocityY(- Math.abs(player.getVelocityY()));
                else
                    player.setVelocityY(Math.abs(player.getVelocityY()));
            }

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
}
