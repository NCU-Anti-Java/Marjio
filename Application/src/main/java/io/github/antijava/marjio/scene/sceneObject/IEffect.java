package io.github.antijava.marjio.scene.sceneObject;

import java.awt.*;
import java.io.Serializable;

/**
 * Created by firejox on 2015/12/29.
 */
public interface IEffect extends Serializable {
    int TIME_LIMIT = 5;

    void update();
    boolean isFinish();
    double getVelocityModifyX();
    double getVelocityModifyY();

}
