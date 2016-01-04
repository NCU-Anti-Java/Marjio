package io.github.antijava.marjio.scene.sceneObject;

import io.github.antijava.marjio.common.network.Packable;

import java.io.Serializable;

/**
 * Created by freyr on 2016/1/4.
 */
public class AbstractEffect implements IEffect, Serializable {


    @Override
    public void update() {

    }

    @Override
    public boolean isFinish() {
        return false;
    }

    @Override
    public double getVelocityModifyX() {
        return 0;
    }

    @Override
    public double getVelocityModifyY() {
        return 0;
    }
}
