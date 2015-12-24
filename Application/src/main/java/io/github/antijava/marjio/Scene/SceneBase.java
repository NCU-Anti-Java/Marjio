package io.github.antijava.marjio.Scene;

import io.github.antijava.marjio.common.*;

import java.awt.event.KeyListener;

/**
 * Created by Zheng-Yuan on 12/24/2015.
 */
abstract public class SceneBase implements IScene {

    protected IApplication application;

    public SceneBase(IApplication application){
        this.application = application;
    }

}
