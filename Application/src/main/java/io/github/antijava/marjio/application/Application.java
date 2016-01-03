package io.github.antijava.marjio.application;

import io.github.antijava.marjio.SceneManager;
import io.github.antijava.marjio.common.*;
import io.github.antijava.marjio.constant.Constant;
import io.github.antijava.marjio.graphics.FpsMeter;
import io.github.antijava.marjio.graphics.Graphics;
import io.github.antijava.marjio.input.Input;
import io.github.antijava.marjio.network.Network;
import io.github.antijava.marjio.scene.MainScene;
import io.github.antijava.marjio.scene.SceneBase;
import io.github.antijava.marjio.resourcemanager.ResourcesManager;
import io.github.antijava.marjio.scene.StageScene;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;
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
    private final ResourcesManager mResourcesManager;
    private final FpsMeter mFpsMeter;

    public Application() {
        mLogger = Logger.getLogger(LOGGER_NAME);
        mLogger.setLevel(Level.INFO);
        // TODO: Other components
        mSceneManager = new SceneManager(this);
        mInput = new Input();
        mServer = new Network(this);
        mClient = new Network(this);
        mGraphics = new Graphics(this);
        mResourcesManager = new ResourcesManager(this);
        mFpsMeter = new FpsMeter();

        mSceneManager.translationTo(new MainScene(this));
    }

    /**
     * Main loop.
     *
     * If return value of scene manager update is true, break the loop.
     */
    @Override
    public void run() {
        mFpsMeter.start();

        long overSleepTime = 0;
        while (true) {
            final long beforeUpdateTime = System.nanoTime();
            getInput().update();
            if (getSceneManager().update())
                break;
            getGraphics().update();
            final long afterUpdateTime = System.nanoTime();

            final long elapsedTime = afterUpdateTime - beforeUpdateTime;
            final long sleepTime = FRAMERATE - elapsedTime - overSleepTime;
            try {
                // Let Java take a rest for garbage collection.
                if (TimeUnit.NANOSECONDS.toMillis(sleepTime) < GC_TIME)
                    Thread.sleep(GC_TIME);
                else
                    TimeUnit.NANOSECONDS.sleep(sleepTime);
            } catch (InterruptedException e) {
                // TODO: throw exception?
                break;
            }
            mFpsMeter.tick();
            overSleepTime = (System.nanoTime() - afterUpdateTime) - sleepTime;
        }
        System.exit(0);
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
    public IGraphics getGraphics() {
        return mGraphics;
    }

    public ResourcesManager getResourcesManager() {
        return mResourcesManager;
    }
    // endregion Components

    public double getRealFps() {
        return mFpsMeter.getActualFps();
    }

    public double getFps() {
        return mFpsMeter.getFps();
    }
}
