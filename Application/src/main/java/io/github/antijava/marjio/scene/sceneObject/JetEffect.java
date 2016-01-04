package io.github.antijava.marjio.scene.sceneObject;

/**
 * Created by freyr on 2016/1/4.
 */
public class JetEffect implements IEffect {
    int time;

    public JetEffect() {
        time = 0;
    }

    @Override
    public void update() {
        time++;
    }

    @Override
    public boolean isFinish() {
        return time >= TIME_LIMIT;
    }

    @Override
    public double getVelocityModifyX() {
        return 2.0;
    }

    @Override
    public double getVelocityModifyY() {
        return 1.0;
    }
}
