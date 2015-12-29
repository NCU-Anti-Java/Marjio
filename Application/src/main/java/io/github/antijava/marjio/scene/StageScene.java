package io.github.antijava.marjio.scene;

import io.github.antijava.marjio.common.IApplication;
import io.github.antijava.marjio.common.IInput;
import io.github.antijava.marjio.common.graphics.Rectangle;
import io.github.antijava.marjio.common.input.Key;
import io.github.antijava.marjio.common.input.Status;
import io.github.antijava.marjio.scene.sceneObject.Block;
import io.github.antijava.marjio.scene.sceneObject.Player;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by Zheng-Yuan on 12/27/2015.
 */
public class StageScene extends SceneBase {
    private final static int START_GAME_COUNTER = 5;
    private int mStartGameCounter;
    private int mRow;
    private int mCol;
    private Block mMap[][];
    private Player mYourPlayer;
    private List<Player> mOtherPlayers;



    public StageScene(IApplication application, String filepath) {
        super(application);
        loadStageFile(filepath);
        mStartGameCounter = START_GAME_COUNTER;
    }

    private void loadStageFile(String filepath) {
        try {
            File file = new File(StageScene.class.getResource(filepath).toURI());
            // TODO: Load the map information.
        }
        catch (NullPointerException ex) {
            getApplication().getLogger().log(Level.INFO, filepath + "can not be found.");
        }
        catch (Exception ex) {
            getApplication().getLogger().log(Level.INFO, ex.toString());
        }
    }

    @Override
    public void update() {
        super.update();

        if (mStartGameCounter-- > 0) {
            // TODO: Draw counter on the graphics.
            return ;
        }

        // TODO: check State
        checkKeyState();
        checkStatus();

        mYourPlayer.preUpdate();
        for (Iterator it = mOtherPlayers.iterator(); it.hasNext(); ) {
            Player player = (Player)it.next();
            player.preUpdate();
        }

        // TODO: Check players bump into blocks or other players.

        // TODO: Load the true data from server.

        // TODO: Draw scene on the graphics.
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
