import io.github.antijava.marjio.application.Application;
import io.github.antijava.marjio.common.IApplication;
import io.github.antijava.marjio.common.IGraphics;
import io.github.antijava.marjio.common.IInput;
import io.github.antijava.marjio.common.ISceneManager;
import io.github.antijava.marjio.constant.Constant;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Davy on 2015/12/24.
 */
public class Main implements Constant {
    static final int GC_TIME = 5;

    static public void main(String[] args) {
        final IApplication application = new Application();
        final IInput input = application.getInput();
        final ISceneManager sceneManager = application.getSceneManager();
        final IGraphics graphics = application.getGraphics();
        final Logger logger = application.getLogger();

        long lastUpdate = System.currentTimeMillis();
        while (true) {
            // TODO: input.cache
            sceneManager.update();
            // TODO: graphics.update

            final long elapsedTime = System.currentTimeMillis() - lastUpdate;
            try {
                // Let Java take a rest for garbage collection.
                if (FRAMERATE - elapsedTime < GC_TIME)
                    Thread.sleep(GC_TIME);
                else
                    Thread.sleep(FRAMERATE - elapsedTime);
            } catch (InterruptedException e) {
                // TODO: throw exception?
                break;
            }
            logger.log(Level.INFO, "fps: " + Math.ceil(1000.0 / (System.currentTimeMillis() - lastUpdate)));
            lastUpdate = System.currentTimeMillis();
        }
    }
}
