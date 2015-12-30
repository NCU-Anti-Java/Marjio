package io.github.antijava.marjio.scene.sceneObject;

import io.github.antijava.marjio.common.graphics.Rectangle;
import io.github.antijava.marjio.common.graphics.Viewport;
import io.github.antijava.marjio.constant.SceneObjectConstant;
import io.github.antijava.marjio.graphics.SpriteBase;

/**
 * Created by Zheng-Yuan on 12/28/2015.
 */
abstract public class SceneObjectObjectBase extends SpriteBase implements SceneObjectConstant {
    public SceneObjectObjectBase(Viewport viewport) {
        super(viewport);
    }

    public abstract Rectangle getOccupiedSpace();
}
