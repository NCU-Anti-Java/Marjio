package io.github.antijava.marjio.application;

import io.github.antijava.marjio.SceneManager;
import io.github.antijava.marjio.common.*;
import io.github.antijava.marjio.constant.Constant;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Davy on 2015/12/24.
 */
public class Application implements IApplication, Constant {
    static private Application sApplication = null;
    private final Logger mLogger;
    private final ISceneManager mSceneManager;
    private final IInput mInput;
    private final IServer mServer;
    private final IClient mClient;
    private final IGraphics mGraphics;

    public Application() {
        mLogger = Logger.getLogger(LOGGER_NAME);
        mLogger.setLevel(Level.INFO);
        // TODO: Other modules
        mSceneManager = new SceneManager(this);
        mInput = null;
        mServer = null;
        mClient = null;
        mGraphics = null;
    }

    @Override
    public Logger getLogger() {
        return mLogger;
    }

    @Override
    @Nullable
    public ISceneManager getSceneManager() {
        return mSceneManager;
    }

    @Override
    @Nullable
    public IInput getInput() {
        return mInput;
    }

    @Override
    @Nullable
    public IServer getServer() {
        return mServer;
    }

    @Override
    @Nullable
    public IClient getClient() {
        return mClient;
    }

    @Override
    @Nullable
    public IGraphics getGraphics() {
        return mGraphics;
    }
}
