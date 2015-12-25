package io.github.antijava.marjio.common;

import java.util.logging.Logger;

/**
 * Created by fntsr on 2015/12/23.
 */
public interface IApplication {
    void run();
    Logger getLogger();
    ISceneManager getSceneManager();
    IInput getInput();
    IServer getServer();
    IClient getClient();
    IGraphics getGraphics();
}
