package io.github.antijava.marjio.scene;

import io.github.antijava.marjio.common.*;
import io.github.antijava.marjio.common.input.Key;
import io.github.antijava.marjio.inputBox.IPAddressInputBox;

/**
 * Created by Zheng-Yuan on 12/24/2015.
 */
public class JoinScene extends SceneBase {
    private final String[] MENU_TEXT = {"Remote Address", "Go Back"};
    private final int INPUT_IPADDRESS = 0;
    private final int GO_BACK = 1;
    private int mCurrentChoice;
    private IPAddressInputBox mIPAddressInputBox;

    public JoinScene(IApplication application) {
        super(application);
        mCurrentChoice = 0;
        mIPAddressInputBox = new IPAddressInputBox(application);
    }

    @Override
    public void update() {
        super.update();
        
        checkKeyState();
        // TODO: Draw view. Mark current choice as other color.
    }

    private void checkKeyState() {
        final IInput input = getApplication().getInput();

        if (input.isPressed(Key.UP) || input.isPressing(Key.UP)) {
            if (--mCurrentChoice < 0)
                mCurrentChoice = 0;
        }
        else if (input.isPressed(Key.DOWN) || input.isPressing(Key.DOWN)) {
            if (++mCurrentChoice >= MENU_TEXT.length)
                mCurrentChoice = MENU_TEXT.length - 1;
        }
        else if (input.isPressed(Key.ENTER) || input.isPressing(Key.ENTER)) {
            select();
        }
        else if (mCurrentChoice == INPUT_IPADDRESS){
            mIPAddressInputBox.update();
        }
    }

    private void select() {
        switch (mCurrentChoice) {
            case INPUT_IPADDRESS: {
                try {
                    final IClient client = getApplication().getClient();
                    client.start(mIPAddressInputBox.getText());
                    // TODO: Transate to room scene with client state.
                }
                catch (Exception ex) {
                    // TODO: Show Error.
                }
                break;
            }
            case GO_BACK: {
                final ISceneManager sceneManager = getApplication().getSceneManager();
                sceneManager.translationTo(new MainScene(getApplication()));
                break;
            }
        }
    }
}
