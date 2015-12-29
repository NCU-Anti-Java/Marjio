package io.github.antijava.marjio.scene;

import io.github.antijava.marjio.common.IApplication;
import io.github.antijava.marjio.common.IInput;
import io.github.antijava.marjio.common.graphics.Rectangle;
import io.github.antijava.marjio.common.graphics.Viewport;
import io.github.antijava.marjio.common.input.Key;
import io.github.antijava.marjio.common.input.Status;
import io.github.antijava.marjio.graphics.Bitmap;
import io.github.antijava.marjio.scene.sceneObject.Block;
import io.github.antijava.marjio.scene.sceneObject.Player;
import io.github.antijava.marjio.scene.sceneObject.SceneMap;
import io.github.antijava.marjio.scene.sceneObject.SceneObjectObjectBase;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;

/**
 * Created by Zheng-Yuan on 12/27/2015.
 */
public class StageScene extends SceneBase {
    private final static int START_GAME_COUNTER = 5;
    private int mStartGameCounter;
    private SceneMap mMap;
    private Player mYourPlayer;
    private List<Player> mOtherPlayers;

    public StageScene(IApplication application, int stage) {
        super(application);
        mMap = new SceneMap(application, stage);
        mStartGameCounter = START_GAME_COUNTER;
    }

    @Override
    public void update() {
        super.update();

        if (mStartGameCounter-- > 0) {
            // TODO: Draw counter on the graphics.
            return ;
        }

        checkKeyState();
        checkStatus();

        List<Player> players = new ArrayList<>();
        players.add(mYourPlayer);
        players.addAll(mOtherPlayers);

        players.forEach(Player::preUpdate);
        players.stream()
                .filter(player -> !checkBump(player))
                .forEach(Player::update);
    }

    public void checkStatus () {
        List<Status> fetchedStatus = getApplication().getInput().getStatuses();
    }

    private void checkKeyState() {
        final IInput input = getApplication().getInput();

        if (input.isPressed(Key.LEFT) || input.isPressing(Key.LEFT)) {
            mYourPlayer.setLeft(true);
        }
        else if (input.isReleased(Key.LEFT)) {
            mYourPlayer.setLeft(false);
        }
        if (input.isPressed(Key.RIGHT) || input.isPressing(Key.RIGHT)) {
            mYourPlayer.setRight(true);
        }
        else if (input.isReleased(Key.RIGHT)) {
            mYourPlayer.setRight(false);
        }
        if (input.isPressed(Key.UP) || input.isPressing(Key.UP)) {
            mYourPlayer.setUp(true);
        }
        else if (input.isReleased(Key.UP)) {
            mYourPlayer.setUp(false);
        }
        if (input.isPressed(Key.SPACE) || input.isPressing(Key.SPACE)) {
            mYourPlayer.setSpace(true);
        }
        else if (input.isReleased(Key.SPACE)) {
            mYourPlayer.setSpace(false);
        }
    }

    public boolean checkBump(Player player) {
        List<Block> blocks = mMap.getAdjacentBlocks(player);

        final List<SceneObjectObjectBase> objects = new ArrayList<>();
        objects.addAll(blocks);
        objects.add(mYourPlayer);
        objects.addAll(mOtherPlayers);
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
        return x > rect.x && x < rect.x + rect.width && y > rect.y && y < rect.y + rect.height;
    }
}
