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

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Zheng-Yuan on 12/27/2015.
 */
public class StageScene extends SceneBase implements Constant {

    private final Viewport GameViewPort;
    private SceneMap mMap;
    UUID mYourPlayerID;
    Map<UUID, Player> mPlayers;

    private final Sprite mTimer;
    private int mTick;


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

        mTick = - START_GAME_TICKS;
        GameViewPort = graphics.createViewport();

        mTimer = new SpriteBase(application.getGraphics().getDefaultViewport());
        mTimer.setBitmap(graphics.createBitmap(GAME_WIDTH, GAME_HEIGHT));
        mTimer.setZ(99);

/*


         //TODO: Fake data
        mIsServer = true;
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


        if (mTick < 0) {

            // TODO: If you are client, you should receive server's count down time.
            // Hint: NTP, Clock synchronization
            if (mIsServer) {
                IServer server = getApplication().getServer();
                List<TickRequest> reqs = getApplication()
                                            .getInput()
                                            .getTickRequest();

                reqs.forEach(tick_req -> {
                    tick_req.setReceiveTime(mTick);
                    try {
                        server.send(tick_req, tick_req.getClientID());
                    } catch (Exception e) {
                    }
                });
            } else {
                final IClient client = getApplication().getClient();
                final List<TickRequest> reqs = getApplication()
                                            .getInput()
                                            .getTickRequest();

                TickRequest tick_req = null;


                /**
                 * find the newest ticks in TickRequests
                 * */
                for (TickRequest req : reqs) {
                    if (tick_req == null) {
                        tick_req = req;
                        continue;
                    }

                    if (tick_req.getReceiveTime() < req.getReceiveTime()) {
                        tick_req = req;
                    }
                }

                if (tick_req == null) {
                    tick_req = new TickRequest(mTick);
                    tick_req.setClientID(mYourPlayerID);
                } else {
                    mTick = tick_req.getNewTime(mTick);
                    tick_req.setStartTime(mTick);
                }

                try {
                    client.send(tick_req);
                } catch (Exception e) {

                }
            }

            mTimer.getBitmap().clear();
            mTimer.getBitmap().setFont(new Font("Consolas", 48, false, true));
            mTimer.getBitmap().drawText(
                    Integer.toString((-mTick) / FPS), 0, 0,
                    GAME_WIDTH, GAME_HEIGHT, Color.WHITE, IBitmap.TextAlign.CENTER);
            mTimer.update();

            mTick++;

            return ;
        } else if (mTick == 0) {
            mTimer.getBitmap().clear();
            mTimer.update();
        }

        final Player player = mPlayers.get(mYourPlayerID);
        final IKeyInput input = (IKeyInput)getApplication().getInput();
        final Collection<Player> players = mPlayers.values();

        players.forEach(Player::preUpdate);

        checkStatus(players);
        checkKeyState(input, player);
        solveBumps(players);

        players.forEach(Player::update);

        //TODO : slide viewport when player is running


        /*
        //TODO: fake data
         ba.setX(p.getX());
         ba.setY(p.getY());
         ba.update();
        */

        if (mIsServer) {
            IServer server = getApplication().getServer();
            Status data = player.getStatus();
            data.send_tick = data.recieve_tick = mTick;

            data.setType(Status.Types.ServerMessage);

            try {
                server.broadcast(data);
            } catch (Exception e) {

            }

        } else {
            IClient client = getApplication().getClient();
            Status data = player.getStatus();

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

            data.setType(Status.Types.ClientMessage);
            data.send_tick = mTick;

            try {
                client.send(data);
            } catch (Exception e) {

            }

        }

        mTick++;
    }

    private void checkDead(final Collection<Player> players) {
        players.forEach(p -> {
            final int x = (int) Math.round (
                    (double)p.getNextX() / BLOCK_SIZE);

            final int y = (int) Math.round (
                    (double)p.getNextY() / BLOCK_SIZE);

            if (!mMap.isInMap(y, x)) {
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
        final Stream<Status> statuses = getApplication()
                .getInput()
                .getStatuses()
                .stream()
                .filter(st -> mPlayers.containsKey(st.getClientID()));

        if (mIsServer)
            return statuses
                    .sorted((st, st2) ->
                            st.send_tick > st2.send_tick ? 1 : -1)
                    .collect(Collectors.toList());
        else
            return statuses
                    .sorted((st, st2) ->
                            st.recieve_tick > st2.recieve_tick ? 1 : -1)
                    .collect(Collectors.toList());
    }

    public void checkStatus (final Collection<Player> players) {

        final List<Status> fetchedStatus = getValidStatuses();
        final IServer server = getApplication().getServer();
        int send_tick = 0;
        final int last_recieve_tick =
                fetchedStatus.isEmpty() ? 0 :
                        fetchedStatus.get(fetchedStatus.size() -1).recieve_tick;

        for (final Status st : fetchedStatus) {
            Player player = mPlayers.get(st.getClientID());

            switch (st.getType()) {
                case ClientMessage: {
                    if (mIsServer) {
                        final boolean check = player.isValidData(st);

                        if (check)
                            checkKeyState(st, player);

                        final Status new_st = player.getStatus();

                        new_st.send_tick = st.send_tick;
                        new_st.recieve_tick = mTick;
                        new_st.query = check;

                        try {
                            if (check) {
                                new_st.setType(Status.Types.ServerMessage);
                                server.send(new_st, player.getmId());
                            } else {
                                new_st.setType(Status.Types.ServerVerification);
                                server.broadcast(new_st);
                            }

                        } catch (Exception ex) {

                        }
                    }
                    break;
                }

                case ServerMessage: {
                    player.preUpdateStatus(st);

                    if (player.getmId().equals(mYourPlayerID))
                        send_tick = st.send_tick;

                    break;
                }

                case ServerVerification: {
                    if (!st.query) {
                        player.preUpdateStatus(st);

                        send_tick = st.send_tick;
                    }
                    break;
                }

            }

        }


        /**
         * time synchronized for players in client
         * */

        if (mPlayers.get(mYourPlayerID).isStatusUpdate()) {
            final int recieve_tick = mPlayers.get(mYourPlayerID).getTick();
            players.stream()
                    .filter(p -> !p.isStatusUpdate())
                    .forEach(p -> p.setTick(recieve_tick));

            mTick = Math.max(recieve_tick + (mTick - send_tick) / 2,
                            last_recieve_tick);
        } else {
            mTick = Math.max(mTick, last_recieve_tick);
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
            final int x = (int)Math.round((double)player.getX() / BLOCK_SIZE);
            final int y = (int)Math.round((double) player.getY() / BLOCK_SIZE) + 1;

            if (mMap.isInMap(y, x) &&
                    mMap.getBlock(y, x).getType() != Block.Type.AIR) {
                player.setVelocityY(-20.0);
                player.addAccelerationY(0.0);
            }

        }

    }

    private void solveBumps(final Collection<Player> players) {
        final Stream<Player> s_players = players.stream();

        /**
         * time synchronized for players in client
         * */
        while (s_players.anyMatch(p -> p.getTick() < mTick))
            s_players.filter(p -> p.getTick() < mTick)
                    .sorted((player1, player2) -> {
                        if (player1.getX() == player2.getX())
                            return (player1.getY() > player2.getY()) ? 1 : -1;
                        else
                            return (player1.getX() > player2.getX()) ? 1 : -1;
                    })
                    .forEach(p -> {
                        solveBumpBlock(p);
                        p.update();
                        p.preUpdate();
                    });

        s_players
                .sorted((player1, player2) -> {
                        if (player1.getX() == player2.getX())
                            return (player1.getY() > player2.getY()) ? 1 : -1;
                        else
                            return (player1.getX() > player2.getX()) ? 1 : -1;
                    })
                .forEach(this::solveBumpBlock);

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



            if (magicbumpTest(dx, dy)) {

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

    public static boolean magicbumpTest(double dx, double dy) {

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
