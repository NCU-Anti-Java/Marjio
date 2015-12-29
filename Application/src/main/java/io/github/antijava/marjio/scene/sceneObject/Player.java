package io.github.antijava.marjio.scene.sceneObject;

import io.github.antijava.marjio.common.graphics.Rectangle;
import io.github.antijava.marjio.common.graphics.Viewport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by firejox on 2015/12/28.
 */
public class Player extends SceneObjectObjectBase {
    static public final List<IAction> actions;
    static public final List<Map<IInterruptable, IAction>> action_table;

    static {
        actions = new ArrayList<>();
        action_table = new ArrayList<>();



    }

    int move_action_id;
    int time_counter;

    int st_x;
    int st_y;


    public Player(Viewport viewport) {
        super(viewport);
        st_x = getX();
        st_y = getY();
        time_counter = 0;
        move_action_id = 0;
    }


    @Override
    public void update() {
        time_counter++;

        setX(st_x + actions.get(move_action_id).getActionX(time_counter));
        setY(st_y + actions.get(move_action_id).getActionY(time_counter));
    }

    public int getMoveActionId() {
        return move_action_id;
    }

    public void setMoveActionId(int id) {
        move_action_id = id;
    }

    public int getStartX() {
        return st_x;
    }

    public int getStartY() {
        return st_y;
    }

    public void setStartX(int x) {
        st_x = x;
    }

    public void setStartY(int y) {
        st_y = y;
    }

    public int getTimeCounter() {
        return time_counter;
    }

    public void setTimeCounter(int tick) {
        time_counter = tick;
    }

    public void updateNewAction(int action_id) {
        st_x += actions.get(move_action_id).getActionX(time_counter);
        st_y += actions.get(move_action_id).getActionY(time_counter);

        move_action_id = action_id;
        time_counter = 0;
    }

    @Override
    Rectangle getOccupiedSpace() {
        final int real_x = getViewport().x + getX();
        final int real_y = getViewport().y + getY();

        return new Rectangle(real_x - PLAYER_SIZE / 2,
                             real_y + PLAYER_SIZE,
                             PLAYER_SIZE, PLAYER_SIZE);
    }
}
