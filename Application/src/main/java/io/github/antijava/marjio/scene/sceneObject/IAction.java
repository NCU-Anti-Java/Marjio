package io.github.antijava.marjio.scene.sceneObject;

/**
 * Created by firejox on 2015/12/29.
 */
public interface IAction {
    int TIME_LIMIT = 5;

    boolean isFinish();
    double getVelocityModifyX();
    double getVelocityModifyY();

}
