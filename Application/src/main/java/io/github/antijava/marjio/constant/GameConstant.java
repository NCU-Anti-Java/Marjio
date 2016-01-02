package io.github.antijava.marjio.constant;

/**
 * Created by Davy on 2015/12/24.
 */
public interface GameConstant {
    int FPS = 60;
    /**
     * Unit: Nano second.
     */
    long FRAMERATE = 1_000_000_000L / FPS;
    int GAME_WIDTH = 800;
    int GAME_HEIGHT = 600;
}
