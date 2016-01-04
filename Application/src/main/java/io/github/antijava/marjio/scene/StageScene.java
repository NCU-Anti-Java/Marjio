package io.github.antijava.marjio.scene;

import io.github.antijava.marjio.common.*;
import io.github.antijava.marjio.common.graphics.Color;
import io.github.antijava.marjio.common.graphics.IBitmap;
import io.github.antijava.marjio.common.graphics.Rectangle;
import io.github.antijava.marjio.common.graphics.Viewport;
import io.github.antijava.marjio.common.input.*;

import io.github.antijava.marjio.common.network.ClientInfo;
import io.github.antijava.marjio.constant.Constant;
import io.github.antijava.marjio.graphics.Font;
import io.github.antijava.marjio.graphics.Sprite;
import io.github.antijava.marjio.graphics.SpriteBase;
import io.github.antijava.marjio.scene.sceneObject.Block;
import io.github.antijava.marjio.scene.sceneObject.PhysicsConstant;
import io.github.antijava.marjio.scene.sceneObject.Player;
import io.github.antijava.marjio.scene.sceneObject.SceneMap;

import java.util.*;
import java.util.logging.Logger;
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



    public StageScene(IApplication application, boolean IsServer, int stage) {
        super(application);
        final IGraphics graphics = application.getGraphics();

        mMap = new SceneMap(application, stage);

        GameViewPort = application.getGraphics().getDefaultViewport();

        mTick = - START_GAME_TICKS;

        mTimer = new SpriteBase(GameViewPort);
        mTimer.setBitmap(graphics.createBitmap(GAME_WIDTH, GAME_HEIGHT));
        mTimer.setZ(99);

        mIsServer = IsServer;
        mPlayers = new HashMap<>();

        final Logger logger = application.getLogger();

        if (mIsServer) {
            mYourPlayerID = application.getServer().getMyId();
            mPlayers.put(mYourPlayerID, new Player(application,
                    GameViewPort, mYourPlayerID));

            final List<ClientInfo> infos = application.getServer().getClients();

            for (final ClientInfo info : infos) {
                mPlayers.put(info.getClientID(), new Player(application,
                        GameViewPort,
                        info.getClientID()));

            }
        }
        else {
            mYourPlayerID = application.getClient().getMyId();

            mPlayers.put(mYourPlayerID, new Player(application,
                        GameViewPort,
                        mYourPlayerID));
        }

    }

    @Override
    public void dispose() {
        super.dispose();
        //mPlayers.values().forEach(Player::dispose);
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

                /**
                 * find the newest ticks in TickRequests
                 * */
                Optional<TickRequest> tick_req = reqs
                        .stream()
                        .max((q1, q2) -> Integer.compare(q1.getReceiveTime(), q2.getReceiveTime()));


                TickRequest tickRequest;

                if (tick_req.isPresent()) {
                    tickRequest = tick_req.get();
                    mTick = tickRequest.getNewTime(mTick);
                    tickRequest.setStartTime(mTick);
                } else {
                    tickRequest = new TickRequest(mTick);
                    tickRequest.setClientID(mYourPlayerID);
                }

                try {
                    client.send(tickRequest);
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
        final IKeyInput input = getApplication().getInput();
        final Collection<Player> players = mPlayers.values();

        players.forEach(Player::preUpdate);

        checkStatus(players);
        checkKeyState(input, player);
        solveBumps(players);
        checkDead(players);

        players.forEach(Player::update);

        //TODO : slide viewport when player is running or out of viewport
        if (Math.abs(Math.abs(player.getVelocityX()) - Player.HUMAN_LIMTT) < 1e-3)
            GameViewPort.x -= player.getVelocityX();


        /*
        //TODO: fake data
         ba.setX(p.getX());
         ba.setY(p.getY());
         ba.update();
        */

        if (mIsServer) {
            IServer server = getApplication().getServer();
            SceneObjectStatus data = player.getStatus();
            data.send_tick = data.recieve_tick = mTick;

            data.setType(Status.Types.ServerMessage);

            try {
                server.broadcast(data);
            } catch (Exception e) {

            }

        } else {
            IClient client = getApplication().getClient();
            SceneObjectStatus data = player.getStatus();

            for (final Key key : Player.action_keys) {
                if (input.isPressed(key))
                    data.pressed.add(key);
                else if (input.isPressing(key))
                    data.pressing.add(key);
                else if (input.isReleased(key))
                    data.released.add(key);

                if (input.isRepeat(key))
                    data.repeat.add(key);
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
                    p.setX(BLOCK_SIZE * 1);
                    p.setVelocityX(0.0);
                }
                if(p == mPlayers.get(mYourPlayerID))
                    GameViewPort.x = 0;
            }

        });

    }

    public List<Status> getValidStatuses() {
        final Stream<Status> statuses = getApplication()
                .getInput()
                .getStatuses()
                .stream();

        if (mIsServer)
            return statuses
                    .filter(st -> mPlayers.containsKey(st.getClientID()))
                    .sorted((st, st2) ->
                            ((SceneObjectStatus)st).send_tick > ((SceneObjectStatus)st2).send_tick ? 1 : -1)
                    .collect(Collectors.toList());
        else
            return statuses
                    .sorted((st, st2) ->
                            ((SceneObjectStatus)st).recieve_tick > ((SceneObjectStatus)st2).recieve_tick ? 1 : -1)
                    .collect(Collectors.toList());

    }

    public void checkStatus (final Collection<Player> players) {
        final Logger logger = getApplication().getLogger();
        final List<Status> fetchedStatus = getValidStatuses();
        final IServer server = getApplication().getServer();
        int send_tick = 0;
        final int last_recieve_tick =
                fetchedStatus.isEmpty() ? 0 :
                        ((SceneObjectStatus)fetchedStatus.get(fetchedStatus.size() -1)).recieve_tick;

        for (final Status st : fetchedStatus) {
            Player player = mPlayers.get(st.getClientID());
            if (!mIsServer && player == null) {
                logger.info("client id: " + st.getClientID().toString());
            }

            switch (st.getType()) {
                case ClientMessage: {
                    if (mIsServer) {
                        final boolean check = player.isValidData((SceneObjectStatus) st);
                        logger.info("client message");
                        if (check)
                            checkKeyState((SceneObjectStatus)st, player);

                        final SceneObjectStatus new_st = player.getStatus();

                        new_st.send_tick = ((SceneObjectStatus)st).send_tick;
                        new_st.recieve_tick = mTick;
                        new_st.query = check;

                        try {
                            if (check) {
                                new_st.setType(Status.Types.ServerMessage);
                                server.broadcast(new_st);
                            } else {
                                new_st.setType(Status.Types.ServerVerification);
                                server.send(new_st, player.getId());
                            }

                        } catch (Exception ex) {

                        }
                    }
                    break;
                }

                case ServerMessage: {
                    if (!mIsServer)
                        logger.info("get server message");

                    if (player == null) {
                        player = new Player(getApplication(),
                                        GameViewPort,
                                        st.getClientID());

                        mPlayers.put(st.getClientID(), player);
                    }

                    player.preUpdateStatus((SceneObjectStatus)st);

                    if (player.getId().equals(mYourPlayerID))
                        send_tick = ((SceneObjectStatus)st).send_tick;

                    break;
                }

                case ServerVerification: {
                    if (!mIsServer)
                        logger.info("get server verify message");

                    if (!((SceneObjectStatus)st).query) {
                        player.preUpdateStatus((SceneObjectStatus)st);

                        send_tick = ((SceneObjectStatus)st).send_tick;
                    }
                    break;
                }

            }

        }


        /**
         * time synchronized for players in client
         * */
        if (!mIsServer) {
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

    }

    private void checkKeyState(IKeyInput input, Player player) {

        if (input.isPressed(Key.MOVE_LEFT)) {
            player.setAccelerationX(-0.5);
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
                player.setAccelerationY(0.0);
            }

        }

    }

    private void solveBumps(final Collection<Player> players) {



        //TODO: BUG need fix
        //time synchronized for players in client

        if (!mIsServer) {
            while (players.stream().anyMatch(p -> p.getTick() < mTick)) {
                final Stream<Player> s_players = players.stream();
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
            }
        }

        final Stream<Player> s_players = players.stream();
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
                .filter(block -> block.getType() != Block.Type.AIR && block.getType() != Block.Type.WIN_LINE )
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

                        player.setVelocityXWithModify(nvx);

                        player.setAccelerationX(0.0);

                    } else if (b.getX() <= player.getX() - BLOCK_SIZE) {
                        final double nvx = b.getX() - player.getX() + BLOCK_SIZE;

                        player.setVelocityXWithModify(nvx);

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

                        player.setVelocityYWithModify(nvy);

                        player.setAccelerationY(-PhysicsConstant.gravity);

                    } else if (b.getY() < player.getY() - BLOCK_SIZE) {
                        final double nvy = b.getY() - player.getY() + BLOCK_SIZE;

                        player.setVelocityYWithModify(nvy);

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
