package io.github.antijava.marjio.scene;

import io.github.antijava.marjio.common.*;
import io.github.antijava.marjio.common.graphics.Color;
import io.github.antijava.marjio.common.graphics.IBitmap;
import io.github.antijava.marjio.common.graphics.Rectangle;
import io.github.antijava.marjio.common.graphics.Viewport;
import io.github.antijava.marjio.common.input.*;
import io.github.antijava.marjio.constant.Constant;
import io.github.antijava.marjio.graphics.Font;
import io.github.antijava.marjio.graphics.Sprite;
import io.github.antijava.marjio.graphics.SpriteBase;
import io.github.antijava.marjio.scene.sceneObject.Block;
import io.github.antijava.marjio.scene.sceneObject.PhysicsConstant;
import io.github.antijava.marjio.scene.sceneObject.Player;
import io.github.antijava.marjio.scene.sceneObject.SceneMap;
import io.github.antijava.marjio.window.WindowBase;

import java.util.*;
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

    /*
     //TODO: fake data
    final WindowBase ba;
    final Player p;
*/

    public StageScene(IApplication application, int stage) {
        super(application);
        final IGraphics graphics = application.getGraphics();

        mMap = new SceneMap(application, stage);

        mCountDown = START_GAME_COUNTER * FPS;
        GameViewPort = graphics.createViewport();

        mTimer = new SpriteBase(application.getGraphics().getDefaultViewport());
        mTimer.setBitmap(graphics.createBitmap(GAME_WIDTH, GAME_HEIGHT));
        mTimer.setZ(99);

/*

         //TODO: Fake data

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


        if (mCountDown > 0) {

            // TODO: If you are client, you should receive server's count down time.
            // Hint: NTP, Clock synchronization
            if (mIsServer) {
                IServer server = getApplication().getServer();
                List<TickRequest> reqs = getApplication()
                                            .getInput()
                                            .getTickRequest();

                reqs.forEach(tick_req -> {
                    tick_req.setReceiveTime(mCountDown);
                    try {
                        server.send(tick_req, tick_req.getClientID());
                    } catch (Exception e) {
                    }
                });

                mCountDown--;
            } else {
                IClient client = getApplication().getClient();
                List<TickRequest> reqs = getApplication()
                                            .getInput()
                                            .getTickRequest();

                TickRequest ret = null;

                for (TickRequest req : reqs) {
                    if (ret == null) {
                        ret = req;
                        continue;
                    } else if (ret.getReceiveTime() > req.getReceiveTime()) {
                        ret = req;
                    }
                }

                if (ret == null) {
                    ret = new TickRequest(mCountDown);
                    ret.setClientID(mYourPlayerID);

                } else {
                    mCountDown = ret.getReceiveTime()
                                    - ret.getTimeOffset(mCountDown);
                    ret.setStartTime(mCountDown);
                }

                try {
                    client.send(ret);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mCountDown--;

            }

            mTimer.getBitmap().clear();
            mTimer.getBitmap().setFont(new Font("Consolas", 48, false, true));
            mTimer.getBitmap().drawText(
                    Integer.toString(mCountDown / FPS), 0, 0,
                    GAME_WIDTH, GAME_HEIGHT, Color.WHITE, IBitmap.TextAlign.CENTER);
            mTimer.update();

            return ;
        } else if (mCountDown == 0) {
            mTimer.getBitmap().clear();
            mTimer.update();

            mCountDown--;
        }

        final Player player = mPlayers.get(mYourPlayerID);
        final IKeyInput input = (IKeyInput)getApplication().getInput();
        final Collection<Player> players = mPlayers.values();

        players.forEach(Player::preUpdate);

        checkKeyState(input, player);
        checkStatus();
        solveBumps(players);
        checkDead(players);

        players.forEach(Player::update);
        //TODO : slide viewport when player is running


        /*
        //TODO: fake data
         ba.setX(p.getX());
         ba.setY(p.getY());
         ba.update();
        */

        if (!mIsServer) {
            IClient client = getApplication().getClient();
            StatusData data = player.getStatusData();

            for (final Key key : Player.action_keys) {
                if (input.isPressed(key))
                    data.pressed.add(key);
                else if (input.isPressing(key))
                    data.pressing.add(key);
                else if (input.isReleased(key))
                    data.isReleased(key);

                if (input.isRepeat(key))
                    data.isRepeat(key);
            }

            try {
                client.send(new Status(data, Status.Types.ClientMessage));
            } catch (Exception e) {

            }
        }


    }

    private void checkDead(final Collection<Player> players) {
        players.forEach(p -> {
            final int x = (int) Math.round (
                    (double)p.getNextX() / BLOCK_SIZE);

            final int y = (int) Math.round (
                    (double)p.getNextY() / BLOCK_SIZE);

            try {

                mMap.getBlock(y, x);


            } catch (Exception e) {
                if (y > 0) { //drop in hole
                    p.reset();
                } else if (y < -1000) { //fly to death, it means you
                    p.reset();          // leave the atmosphere
                } else if (x < 0) { //prevent move out of start line
                    p.setX(0);
                    p.setVelocityX(0.0);
                }

            }
        });

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
        final List<Status> fetchedStatus = getValidStatuses();
        final IServer server = getApplication().getServer();

        for (final Status st : fetchedStatus) {
            StatusData data = (StatusData) st.getData();
            Player player = mPlayers.get(data.uuid);

            switch (st.getType()) {
                case ClientMessage: {
                    if (mIsServer) {
                        if (player.isValidData(data)) {
                            checkKeyState(data, player);

                            final Status new_st = new Status(player.getStatusData(),
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

                            try {
                                server.send(new_st, player.getId());

                            } catch (Exception ex) {

                            }
                        }
                    }
                    break;
                }

                case ServerMessage: {
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

    private void checkKeyState(IKeyInput input, Player player) {

        if (input.isPressed(Key.MOVE_LEFT)) {
            player.addAccelerationX(-0.5);
        }
        else if (input.isRepeat(Key.MOVE_LEFT)) {
            player.setVelocityX(-6.0);
            player.setAccelerationX(-PhysicsConstant.friction - 1e-5);
        }

        if (input.isPressed(Key.MOVE_RIGHT)) {
            player.setAccelerationX(0.5);
        }
        else if (input.isRepeat(Key.MOVE_RIGHT)) {
            player.setVelocityX(6.0);
            player.setAccelerationX(PhysicsConstant.friction + 1e-5);
        }


        if (input.isKeyDown(Key.MOVE_LEFT) &&
                input.isKeyDown(Key.MOVE_RIGHT)) {
            player.setAccelerationX(0.0);
            player.setVelocityX(0.0);
        } else if (input.isKeyUp(Key.MOVE_LEFT) &&
                input.isKeyUp(Key.MOVE_RIGHT)) {
            player.setAccelerationX(0.0);
        }


        if (input.isRepeat(Key.JUMP)) {
            try {

                Block block = mMap.getBlock(
                        (int)Math.round(
                                (double) player.getY() / BLOCK_SIZE) + 1,
                        (int)Math.round((double)player.getX() / BLOCK_SIZE));

                // prevent gravity problem
                if (block.getType() != Block.Type.AIR) {
                    player.setVelocityY(-20.0);
                    player.addAccelerationY(0.0);
                }
            } catch (Exception ex) {

            }

        }

    }

    private void solveBumps(final Collection<Player> players) {
        players.forEach(p -> {
            p.normalizeVelocity((double)BLOCK_SIZE/2.0 - 1.0);
            solveBumpBlock(p);
        });

    }

    private void solveBumpBlock (Player player) {
        List<Block> entityBlocks = mMap.getAdjacentBlocks(player).stream()
                .filter(block -> block.getType() != Block.Type.AIR)
                .collect(Collectors.toList());

        for (Block b : entityBlocks) {
            final double dx =
                    (double)(b.getX() - player.getNextX()) / BLOCK_SIZE;
            final double dy =
                    (double)(b.getY() - player.getNextY()) / BLOCK_SIZE;



            if (magicalbumpTest(dx, dy)) {

                //TODO: setup reflect direction
                final double vx = player.getVelocityX();
                final double vy = player.getVelocityY();

                //getApplication().getLogger().info(player.toString());
                //getApplication().getLogger().info(b.toString());


                if (Math.abs(b.getY() - player.getY()) <= BLOCK_SIZE/2) {
                    if (b.getX() >= player.getX() + BLOCK_SIZE) {
                        final double nvx = b.getX() - player.getX() - BLOCK_SIZE;

                        player.setVelocityX(nvx);

                        player.setAccelerationX(0.0);

                    } else if (b.getX() <= player.getX() - BLOCK_SIZE) {
                        final double nvx = b.getX() - player.getX() + BLOCK_SIZE;

                        player.setVelocityX(nvx);

                        player.setAccelerationX(0.0);
                    } else {
                        player.setVelocityX(-vx);
                        player.setX((int)
                                Math.round((double) player.getX() / BLOCK_SIZE)
                                * BLOCK_SIZE);
                    }
                }

                if (Math.abs(b.getX() - player.getX()) <= BLOCK_SIZE/2) {
                    if (b.getY() > player.getY() + BLOCK_SIZE) {
                        final double nvy = b.getY() - player.getY() - BLOCK_SIZE;

                        player.setVelocityY(nvy);

                        player.setAccelerationY(-PhysicsConstant.gravity);

                    } else if (b.getY() < player.getY() - BLOCK_SIZE) {
                        final double nvy = b.getY() - player.getY() + BLOCK_SIZE;

                        player.setVelocityY(nvy);

                        player.setAccelerationY(0.0);
                    } else if (b.getY() < player.getY()) {
                        player.setVelocityY(0.0);

                        player.setY(b.getY() + BLOCK_SIZE);
                        player.setAccelerationY(0.0);
                    } else {
                        player.setVelocityY(0.0);
                        player.setY(b.getY() - BLOCK_SIZE);
                        player.setAccelerationY(0.0);
                    }
                }

                //getApplication().getLogger().info(player.toString());
            }
        }
    }

    public static boolean magicalbumpTest(double dx, double dy) {

        return ((dx*dx*dx*dx) + (dy*dy*dy*dy)) <= 1.0D;
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
