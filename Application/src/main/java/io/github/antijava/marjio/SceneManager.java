package io.github.antijava.marjio;

import io.github.antijava.marjio.common.IApplication;
import io.github.antijava.marjio.common.IScene;
import io.github.antijava.marjio.common.ISceneManager;

/**
 * Manage the current scene and provides translation.
 */
public class SceneManager implements ISceneManager {
    private final IApplication mApplication;
    private IScene mScene = null;
    private IScene mLastScene = null;
    private boolean mTranslating = false;

    public SceneManager(IApplication application) {
        mApplication = application;
    }

    @Override
    public boolean update() {
        if (translate())
            return false;

        if (mScene == null)
            return true;

        if (!mScene.isDisposed())
            mScene.update();
        return false;
    }

    @Override
    public void translationTo(IScene scene) {
        if (mScene == scene)
            return;

        mLastScene = mScene;
        if (mLastScene != null)
            mLastScene.dispose();
        mScene = scene;

        prepareTranslation();
    }

    // region Translating
    /**
     * Scene translating.
     * @return if break update chain, that is, not to update the scenes.
     */
    private boolean translate() {
        if (!mTranslating)
            return false;

        // TODO: Add translating animation

        // Finish translation
        mTranslating = false;
        mLastScene = null;
        return false;
    }

    private void prepareTranslation() {
        mTranslating = true;
        // TODO: Add translating animation
    }
    // endregion Translating
}
