package io.github.antijava.marjio.scene;

import io.github.antijava.marjio.common.IApplication;
import io.github.antijava.marjio.common.IClient;
import io.github.antijava.marjio.common.IGraphics;
import io.github.antijava.marjio.common.IInput;
import io.github.antijava.marjio.common.ISceneManager;
import io.github.antijava.marjio.common.IServer;
import io.github.antijava.marjio.common.graphics.Color;
import io.github.antijava.marjio.common.graphics.IBitmap;
import io.github.antijava.marjio.common.graphics.Viewport;

import io.github.antijava.marjio.common.input.GameSet;
import io.github.antijava.marjio.common.input.IKeyInput;
import io.github.antijava.marjio.common.input.Key;
import io.github.antijava.marjio.common.input.SceneObjectStatus;
import io.github.antijava.marjio.common.input.Status;
import io.github.antijava.marjio.common.input.TickRequest;
import io.github.antijava.marjio.common.network.ClientInfo;
import io.github.antijava.marjio.constant.Constant;
import io.github.antijava.marjio.graphics.Font;
import io.github.antijava.marjio.graphics.Sprite;
import io.github.antijava.marjio.graphics.SpriteBase;
import io.github.antijava.marjio.scene.sceneObject.Block;
import io.github.antijava.marjio.scene.sceneObject.Item;
import io.github.antijava.marjio.scene.sceneObject.PhysicsConstant;
import io.github.antijava.marjio.scene.sceneObject.Player;
import io.github.antijava.marjio.scene.sceneObject.SceneMap;
import io.github.antijava.marjio.scene.sceneObject.SceneObjectObjectBase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
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
    private List<Item> mItems;

    private boolean mGameSet;

    boolean mIsServer;

    /*
     //TODO: fake data
    final WindowBase ba;
    final Player p;
*/



    public StageScene(IApplication application, boolean IsServer, int stage) {
        super(application);
        final IGraphics graphics = application.getGraphics();
        GameViewPort = application.getGraphics().createViewport();

        mMap = new SceneMap(application, stage, GameViewPort);


        mTick = - START_GAME_TICKS;

        mTimer = new SpriteBase(GameViewPort);
        mTimer.setBitmap(graphics.createBitmap(GAME_WIDTH, GAME_HEIGHT));
        mTimer.setZ(99);

        mGameSet = false;

        mIsServer = IsServer;
        mPlayers = new HashMap<>();
        mItems = new ArrayList<>();

        final Logger logger = application.getLogger();

        if (mIsServer) {
            mYourPlayerID = application.getServer().getMyId();
            mPlayers.put(mYourPlayerID, new Player(application,
                    GameViewPort, mYourPlayerID, Color.BLUE));

            final List<ClientInfo> infos = application.getServer().getClients();

            for (final ClientInfo info : infos) {
                mPlayers.put(info.getClientID(), new Player(application,
                        GameViewPort,
                        info.getClientID(), Color.RED));

            }
        }
        else {
            mYourPlayerID = application.getClient().getMyId();

            mPlayers.put(mYourPlayerID, new Player(application,
                        GameViewPort,
                        mYourPlayerID, Color.BLUE));
        }

    }

    @Override
    public void dispose() {
        super.dispose();
        //mPlayers.values().forEach(Player::dispose);
        //ba.dispose();
        mMap.dispose();
        for (UUID uuid : mPlayers.keySet()) {
            mPlayers.get(uuid).dispose();
        }
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
        checkPlayerBump(players);
        checkGameSet();

        if (mGameSet) {
            if (mIsServer) {
                UUID[] rankTable = new UUID[mPlayers.size()];
                Map<UUID, Boolean> used = new HashMap<>();
                for (int i = 0; i < mPlayers.size(); i++) {
                    UUID maxID = null;
                    double x = -1;
                    for (UUID uuid : mPlayers.keySet()) {
                        if (used.containsKey(uuid)) continue;
                        if (mPlayers.get(uuid).getX() > x) {
                            x = mPlayers.get(uuid).getX();
                            maxID = uuid;
                        }
                    }
                    rankTable[i] = maxID;
                    used.put(maxID, true);
                }
                final GameSet data = new GameSet(mYourPlayerID);
                final IGraphics graphics = getApplication().getGraphics();
                data.setData(rankTable);
                getApplication().getServer().broadcastTCP(data);
                getApplication().getSceneManager().translationTo(new ScoreBoardScene(
                        getApplication(), mYourPlayerID, rankTable, graphics.snapToBitmap()
                ));
            }
            return ;
        }
        checkDead(players);

        players.forEach(Player::update);

        final int playerCenterX = player.getX() + PLAYER_SIZE / 2;
        if (playerCenterX > GameViewPort.ox + GAME_WIDTH - MAP_SCROLL_PADDING)
            GameViewPort.ox = playerCenterX - (GAME_WIDTH - MAP_SCROLL_PADDING);
        else if (playerCenterX < GameViewPort.ox + MAP_SCROLL_PADDING)
            GameViewPort.ox = playerCenterX - MAP_SCROLL_PADDING;

        if (GameViewPort.ox + GAME_WIDTH > mMap.getCol() * BLOCK_SIZE)
            GameViewPort.ox = mMap.getCol() * BLOCK_SIZE - GAME_WIDTH;
        else if (GameViewPort.ox < 0)
            GameViewPort.ox = 0;

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
                if (y >= 0) { //drop in hole
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

    private List<Status> getValidStatuses() {
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

    private void checkStatus (final Collection<Player> players) {
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
                                        st.getClientID(), Color.RED);

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

    private void checkGameSet() {
        final IInput input = getApplication().getInput();
        final List<GameSet> gameSets = input.getGameSet();
        for (GameSet gameSet : gameSets) {
            final ISceneManager sceneManager = getApplication().getSceneManager();
            final IGraphics graphics = getApplication().getGraphics();
            final UUID[] uuids = gameSet.getData();

            sceneManager.translationTo(new ScoreBoardScene(
                    getApplication(), mYourPlayerID, uuids, graphics.snapToBitmap()));
            mGameSet = true;
            break;
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

        if (input.isPressed(Key.CAST)) {
            final Item item = player.getHave();
        }

    }

    /**
     * Check whether players bump and execute their behavior
     * @param players
     */
    private void checkPlayerBump(final Collection<Player> players) {
        //TODO: BUG need fix
        //time synchronized for players in client

        // bump anticipation of client
        if (!mIsServer) {
            while (players.stream().anyMatch(p -> p.getTick() < mTick)) {
                players.stream()
                        .filter(p -> p.getTick() < mTick)
                        .sorted((player1, player2) -> {
                            if (player1.getX() == player2.getX())
                                return (player1.getY() > player2.getY()) ? 1 : -1;
                            else
                                return (player1.getX() > player2.getX()) ? 1 : -1;
                        })
                        .forEach(p -> {
                            checkPlayerBumpObject(p);
                            p.update();
                            p.preUpdate();
                        });
            }
        }

        players.stream()
                .sorted((player1, player2) -> {
                        if (player1.getX() == player2.getX())
                            return (player1.getY() > player2.getY()) ? 1 : -1;
                        else
                            return (player1.getX() > player2.getX()) ? 1 : -1;
                    })
                .forEach(this::checkPlayerBumpObject);
    }


    /**
     * Check whether player bump scene objects (except other players)
     * @param player player
     */
    private void checkPlayerBumpObject(Player player) {
        List<Block> entityBlocks = mMap.getAdjacentBlocks(player).stream()
                .filter(block -> block.getType() != Block.Type.AIR && block.getType() != Block.Type.WIN_LINE )
                .collect(Collectors.toList());

        for (Block b : entityBlocks) {
            if (bumpValidation(b, player)) {

                //TODO: setup reflect direction
                final double vx = player.getVelocityX();
                final double vy = player.getVelocityY();

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

                        List<Block.Type> itemTypes = new ArrayList<>();
                        itemTypes.add(Block.Type.ITEM_BLOCK_SUPER_BULLET);
                        itemTypes.add(Block.Type.ITEM_BLOCK_SUPER_GUN);
                        itemTypes.add(Block.Type.ITEM_BLOCK_SUPER_SPEED);
                        itemTypes.add(Block.Type.ITEM_BLOCK_SUPER_TRAP);
                        itemTypes.add(Block.Type.ITEM_BLOCK_TRAP);

                        if (b.getType() == Block.Type.WOOD || itemTypes.contains(b.getType())) {
                            int col = (int) Math.floor(b.getX() / BLOCK_SIZE);
                            int row = (int) Math.floor(b.getY() / BLOCK_SIZE);
                            int x = b.getX();
                            int y = b.getY();
                            int z = b.getZ();

                            Block airBlock = new Block(Block.Type.AIR.getValue(), x, y, GameViewPort, null);
                            mMap.getBlock(row, col).dispose();
                            mMap.setBlock(row, col, airBlock);

                            if (itemTypes.contains(b.getType())) {
                                Item.ItemType type = Item.ItemType.values()[b.getType().getValue() - 4];
                                mItems.add(new Item(GameViewPort, getApplication(), x, y, z, type));
                            }
                        }
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
            }
        }

        if (mIsServer) {
            // region touchable block
            List<Block> winBlocks = mMap.getAdjacentBlocks(player).stream()
                    .filter(block -> block.getType() == Block.Type.WIN_LINE )
                    .collect(Collectors.toList());

            winBlocks.stream().filter(b -> bumpValidation(b, player)).forEach(b -> {
                if (mGameSet)   return;

                mGameSet = true;
            });
        }
    }

    /**
     * Validate if player bump to SceneObjectObjectBase
     * @param sceneObject scene object
     * @param player player
     * @return does it bump or not
     */
    private static boolean bumpValidation(SceneObjectObjectBase sceneObject, Player player) {
        final double dx =  (double)(sceneObject.getX() - player.getNextX()) / BLOCK_SIZE;
        final double dy =  (double)(sceneObject.getY() - player.getNextY()) / BLOCK_SIZE;

        return ((dx*dx*dx*dx) + (dy*dy*dy*dy)) <= 1.0D;
    }
}
