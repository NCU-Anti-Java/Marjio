package io.github.antijava.marjio.scene;

import io.github.antijava.marjio.common.IApplication;
import io.github.antijava.marjio.common.IInput;
import io.github.antijava.marjio.window.WindowScoreBoard;

import java.util.List;
import java.util.UUID;

/**
 * Created by Zheng-Yuan on 12/30/2015.
 */
public class ScoreBoardScene extends SceneBase {
    private final WindowScoreBoard mWindowScoreBoard;

    public ScoreBoardScene(IApplication application, UUID yourPlayerUUID, List<UUID> rankTable) {
        super(application);
        mWindowScoreBoard = new WindowScoreBoard(application, yourPlayerUUID, rankTable);
    }

    @Override
    public void update() {
        final IInput input = getApplication().getInput();

        // TODO: Need IInput press ANY KEY.
        // if (input.isPressed(Key.ANY))
        //  tranlateTo(new MainScene())
    }


}
