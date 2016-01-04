package io.github.antijava.marjio.scene.sceneObject;


/**
 * Created by freyr on 2016/1/4.
 */
public class TrapEffect implements IEffect {
    int time;

    public TrapEffect() {
        time = 0;
    }


    @Override
    public void update() {
        time ++;
    }

    @Override
    public boolean isFinish() {
        return time >= 3;
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
