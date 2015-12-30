package io.github.antijava.marjio.scene.sceneObject;

/**
 * Created by firejox on 2015/12/29.
 */
public interface IAction {
    int time_counter_limit = 5;

    int getActionX(int time_counter);
    int getActionY(int time_counter);
}
