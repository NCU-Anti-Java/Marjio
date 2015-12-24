package io.github.antijava.marjio.scene;

import io.github.antijava.marjio.common.*;

/**
 * Created by Zheng-Yuan on 12/24/2015.
 */
abstract public class SceneBase implements IScene {
    private final IApplication mApplication;

    public SceneBase(IApplication application) {
        mApplication = application;
    }

    protected IApplication getApplication() {
        return mApplication;
    }

    @Override
    public void initialize() {}

    @Override
    public void finalize() {}
}
