package io.github.antijava.marjio.input;

import io.github.antijava.marjio.common.input.Event;
import io.github.antijava.marjio.common.IInput;
import io.github.antijava.marjio.common.input.IKeyInfo;
import io.github.antijava.marjio.common.input.Key;
import io.github.antijava.marjio.common.input.Status;
import io.github.antijava.marjio.common.input.IKeyInfo.KeyState;


import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;


/**
 * Created by firejox on 2015/12/25.
 */
public final class Input implements IInput {
    static final Map<Key, Key> keymap;

    static {
        keymap = new EnumMap(Key.class);

        keymap.put(Key.JUMP, Key.UP);
        keymap.put(Key.MOVE_RIGHT, Key.RIGHT);
        keymap.put(Key.MOVE_LEFT, Key.LEFT);
        keymap.put(Key.CROUCH, Key.DOWN);
    }

    Set<Key> pro_keys;
    Set<Key> cur_keys;
    Set<Key> pre_keys;
    Vector<Status> statuses;
    Vector<Status> statuses_cached;


    Input() {

        pro_keys = Collections.synchronizedSet(EnumSet.noneOf(Key.class));
        cur_keys = Collections.synchronizedSet(EnumSet.noneOf(Key.class));
        pre_keys = Collections.synchronizedSet(EnumSet.noneOf(Key.class));

        statuses_cached = new Vector<>();
        statuses = new Vector<>();
    }

    @Override
    public void update() {
        pre_keys.clear();
        pre_keys.addAll(cur_keys);

        cur_keys.clear();
        cur_keys.addAll(pro_keys);

        statuses_cached.clear();
        statuses_cached.addAll(statuses);
        statuses.clear();
    }


    @Override
    public boolean isPressing(Key key) {
        key = keymap.getOrDefault(key, key);

        return  key != Key.UNDEFINED &&
                (pre_keys.contains(key) &&
                cur_keys.contains(key));
    }

    @Override
    public boolean isPressed(Key key) {
        key = keymap.getOrDefault(key, key);

        return key != Key.UNDEFINED &&
                (!pre_keys.contains(key) &&
                cur_keys.contains(key));
    }

    @Override
    public boolean isReleased(Key key) {
        key = keymap.getOrDefault(key, key);

        return  key != Key.UNDEFINED &&
                (pre_keys.contains(key) &&
                !cur_keys.contains(key));
    }

    @Override
    public boolean isTrigger(Key key) {

        return isPressed(key) || isReleased(key);
    }

    @Override
    public List<Status> getStatuses() {

        return statuses_cached;
    }

    @Override
    public void triggerEvent(Event evt) {
        switch (evt.getType()) {
            case Keyboard: {
                IKeyInfo info = (IKeyInfo)evt.getData();

                if (info.getKeyState() == KeyState.KEY_PRESSED)
                    pro_keys.add(info.getKey());
                else if (info.getKeyState() == KeyState.KEY_RELEASED)
                    pro_keys.remove(info.getKey());

                break;
            }

            // TODO: Data to Status
            case NetWorkClient:
            case NetworkServer: {

                break;
            }

        }
    }
}


