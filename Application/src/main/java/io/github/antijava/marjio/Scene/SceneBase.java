package io.github.antijava.marjio.scene;

import io.github.antijava.marjio.common.*;
import io.github.antijava.marjio.common.exception.ObjectDisposedException;

/**
 * Created by Zheng-Yuan on 12/24/2015.
 */
abstract public class SceneBase implements IScene {
    private final IApplication mApplication;
    private boolean mDisposed = false;

    public SceneBase(IApplication application) {
        mApplication = application;
    }

    protected IApplication getApplication() {
        return mApplication;
    }

    @Override
    public void update() throws ObjectDisposedException {
        if (isDisposed())
            throw new ObjectDisposedException();
    }

    @Override
    public void dispose() {
        this.mDisposed = true;
    }

    @Override
    public boolean isDisposed() {
        return mDisposed;
    }
}
