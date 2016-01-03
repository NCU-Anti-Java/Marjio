import io.github.antijava.marjio.application.Application;
import io.github.antijava.marjio.constant.Constant;

import java.util.logging.Logger;

/**
 * Created by Davy on 2015/12/24.
 */
public class Main implements Constant {
    static public void main(String[] args) {
        try {
            new Application().run();
        } catch (Exception e) {
            Logger logger = Logger.getLogger(LOGGER_NAME);
            logger.warning("Unhandled Exception");
            e.printStackTrace();
        }
    }
}
