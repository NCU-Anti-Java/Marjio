package io.github.antijava.marjio.scene.sceneObject;

import io.github.antijava.marjio.common.graphics.Rectangle;
import io.github.antijava.marjio.common.graphics.Viewport;
import io.github.antijava.marjio.network.StatusData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by firejox on 2015/12/28.
 */
public class Player extends SceneObjectObjectBase {
    static public final List<IAction> actions;
    static public final List<Map<IInterruptable, Integer>> action_table;

    static {
        actions = new ArrayList<>();
        action_table = new ArrayList<>();




    }

    UUID id;

    /**
     * Player action is according by `action_table`. Such Like
     * MoveLeftAction, MoveRightAction, babara...
     * */
    int move_action_id;
    int next_move_action_id;


    int time_counter;
    int next_time_counter;

    int st_x;
    int next_st_x;

    int st_y;
    int next_st_y;


    public Player(Viewport viewport, UUID id) {
        super(viewport);
        this.id = id;
        next_st_x = st_x = getX();
        next_st_y = st_y = getY();
        next_time_counter = time_counter = 0;
        next_move_action_id = move_action_id = 0;
    }


    @Override
    public void update() {
        time_counter = next_time_counter;
        move_action_id = next_move_action_id;
        st_x = next_st_x;
        st_y = next_st_y;

        setX(st_x + actions.get(move_action_id).getActionX(time_counter));
        setY(st_y + actions.get(move_action_id).getActionY(time_counter));
    }

    public int getMoveActionId() {
        return move_action_id;
    }

    public int getNextX() {
        return next_st_x + actions
                        .get(next_move_action_id)
                            .getActionX(next_time_counter);
    }

    public int getNextY() {
        return next_st_y + actions
                        .get(move_action_id)
                            .getActionY(next_time_counter);
    }


    public int getTimeCounter() {
        return time_counter;
    }

    public UUID getId() {
        return id;
    }


    public void preUpdateStatusData(StatusData data) {
        next_st_x = data.st_x;
        next_st_y = data.st_y;
        next_move_action_id = data.action_id;
        next_time_counter = data.time_counter;

    }

    /**
     * Get new action according Keyboard Input or
     * Server accept action change.
     *
     * */
    public void preUpdateNewAction(int new_action_id) {
        next_st_x = st_x + actions
                                .get(move_action_id)
                                    .getActionX(time_counter);
        next_st_y = st_y + actions
                                .get(move_action_id)
                                    .getActionY(time_counter);

        next_move_action_id = new_action_id;
        time_counter = 0;

    }

    public void preUpdate() {
        next_time_counter = time_counter + 1;
        next_st_x = st_x;
        next_st_y = st_y;
        next_move_action_id = move_action_id;

    }

    public StatusData getStatusData() {
        StatusData data = new StatusData();

        data.uuid = id;
        data.type = StatusData.Player;

        data.st_x = next_st_x;
        data.st_y = next_st_y;
        data.action_id = next_move_action_id;
        data.time_counter = next_time_counter;

        return data;
    }

    public boolean isValidNextAction(int action_id) {
        return action_table.get(move_action_id)
                    .values().contains(action_id) &&
                IAction.time_counter_limit <= time_counter;
    }


    @Override
    public Rectangle getOccupiedSpace() {
        final int real_x = getViewport().x + getNextX();
        final int real_y = getViewport().y + getNextY();

        return new Rectangle(real_x - PLAYER_SIZE / 2,
                             real_y + PLAYER_SIZE,
                             PLAYER_SIZE, PLAYER_SIZE);
    }
}
