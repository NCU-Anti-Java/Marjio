package io.github.antijava.marjio.common;

/**
 * Created by fntsr on 2015/12/23.
 */
public interface IApplication {
    ISceneManager getSceneManager();
    IInput getInput();
    IServer getServer();
    IClient getClient();
    IGraphics getGraphics();
}
