package io.github.antijava.marjio.inputBox;

import io.github.antijava.marjio.common.IApplication;
import io.github.antijava.marjio.common.Key;
import io.github.antijava.marjio.common.graphics.ISprite;
import io.github.antijava.marjio.graphics.SpriteBase;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by Zheng-Yuan on 12/26/2015.
 */
abstract public class InputBox extends SpriteBase {
    private final List<Key> mValidInput;
    private final IApplication mApplication;

    public InputBox(@NotNull IApplication application, List<Key> validInput) {
        super(application.getGraphics().getDefaultViewport());

        mApplication = application;
        mValidInput = validInput;
    }

    protected List<Key> getValidInput() {
        return mValidInput;
    }

    protected IApplication getApplication() {
        return mApplication;
    }
}
