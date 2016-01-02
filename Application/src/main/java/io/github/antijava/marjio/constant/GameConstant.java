package io.github.antijava.marjio.constant;

/**
 * Created by Davy on 2015/12/24.
 */
public interface GameConstant {
    int FPS = 60;
    int FRAMERATE = 1000 / FPS;
    int GAME_WIDTH = 800;
    int GAME_HEIGHT = 600;
    int START_GAME_COUNTER = 5;
    int START_GAME_TICKS = START_GAME_COUNTER * FPS;
}
