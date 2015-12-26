package io.github.antijava.marjio.inputBox;

import io.github.antijava.marjio.common.IApplication;
import io.github.antijava.marjio.common.Key;

import java.util.List;

/**
 * Created by Zheng-Yuan on 12/26/2015.
 */
abstract public class InputBox {
    private final List<Key> mValidInput;
    private final IApplication mApplication;

    public InputBox(IApplication application, List<Key> validInput) {
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
