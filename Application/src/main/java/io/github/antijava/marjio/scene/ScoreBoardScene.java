package io.github.antijava.marjio.scene;

import io.github.antijava.marjio.common.IApplication;
import io.github.antijava.marjio.common.IInput;
import io.github.antijava.marjio.common.ISceneManager;
import io.github.antijava.marjio.common.graphics.IBitmap;
import io.github.antijava.marjio.common.input.Key;
import io.github.antijava.marjio.graphics.SpriteBase;
import io.github.antijava.marjio.window.WindowScoreBoard;

import java.util.UUID;

/**
 * Created by Zheng-Yuan on 12/30/2015.
 */
public class ScoreBoardScene extends SceneBase {
    private final WindowScoreBoard mWindowScoreBoard;
    private final SpriteBase mBackground;

    public ScoreBoardScene(IApplication application, UUID yourPlayerUUID, UUID[] rankTable, IBitmap background) {
        super(application);
        mBackground = new SpriteBase(application.getGraphics().getDefaultViewport());
        mBackground.setBitmap(background);
        mWindowScoreBoard = new WindowScoreBoard(application, yourPlayerUUID, rankTable);
    }

    @Override
    public void update() {
        super.update();

        final IInput input = getApplication().getInput();

        mBackground.update();
        mWindowScoreBoard.update();

        // TODO: Draw background image.
        // TODO: Need IInput press ANY KEY.
        if (input.isPressed(Key.ENTER)) {
            final ISceneManager sceneManager = getApplication().getSceneManager();
            sceneManager.translationTo(new MainScene(getApplication()));
        }

    }

    @Override
    public void dispose() {
        super.dispose();

        mWindowScoreBoard.dispose();
        mBackground.dispose();
    }

}
