package io.github.antijava.marjio.scene;

import io.github.antijava.marjio.common.*;

/**
 * Created by Zheng-Yuan on 12/24/2015.
 */
abstract public class SceneBase implements IScene {

    protected IApplication application;

    public SceneBase(IApplication application){
        this.application = application;
    }

}
