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
    static final int GC_TIME = 5;
    private final Logger mLogger;
    private final ISceneManager mSceneManager;
    private final IInput mInput;
    private final IServer mServer;
    private final IClient mClient;
    private final IGraphics mGraphics;

    public Application() {
        mLogger = Logger.getLogger(LOGGER_NAME);
        mLogger.setLevel(Level.INFO);
        // TODO: Other components
        mSceneManager = new SceneManager(this);
        mInput = null;
        mServer = null;
        mClient = null;
        mGraphics = null;
    }

    /**
     * Main loop.
     *
     * If return value of scene manager update is true, break the loop.
     */
    @Override
    public void run() {
        long lastUpdate = System.currentTimeMillis();
        while (true) {
            // TODO: getInput().update
            if (getSceneManager().update())
                break;
            // TODO: getGraphics().update

            final long elapsedTime = System.currentTimeMillis() - lastUpdate;
            try {
                // Let Java take a rest for garbage collection.
                if (FRAMERATE - elapsedTime < GC_TIME)
                    Thread.sleep(GC_TIME);
                else
                    Thread.sleep(FRAMERATE - elapsedTime);
            } catch (InterruptedException e) {
                // TODO: throw exception?
                break;
            }
            getLogger().log(Level.INFO, "fps: " + Math.ceil(1000.0 / (System.currentTimeMillis() - lastUpdate)));
            lastUpdate = System.currentTimeMillis();
        }
    }

    // region Components
    @Override
    public Logger getLogger() {
        return mLogger;
    }

    @Override
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
    // endregion Components
}
