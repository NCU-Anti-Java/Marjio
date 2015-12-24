package io.github.antijava.marjio.SceneManager;

import io.github.antijava.marjio.common.*;

/**
 * Created by Zheng-Yuan on 12/24/2015.
 */
public class SceneManager implements ISceneManager {

    private IApplication application;
    private IScene nowScene;
    private IInput input;
    private boolean isInTranslation;

    public SceneManager(IApplication application){

        this.application = application;
        isInTranslation = false;

    }

    @Override
    public void update(){

        if(isInTranslation)
            // scene translation
            ;
        else
            nowScene.update();

    }

    @Override
    public void setScene(IScene scene){
        nowScene = scene;
        nowScene.initialize();
    }

}
