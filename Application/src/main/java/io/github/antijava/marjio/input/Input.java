package io.github.antijava.marjio.input;

import io.github.antijava.marjio.common.IInput;
import io.github.antijava.marjio.common.input.Event;
import io.github.antijava.marjio.common.input.IKeyInfo;
import io.github.antijava.marjio.common.input.IKeyInfo.KeyState;
import io.github.antijava.marjio.common.input.Key;
import io.github.antijava.marjio.common.input.Status;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * Created by firejox on 2015/12/25.
 */
public final class Input implements IInput {
    static final Map<Key, Key> keymap;

    static {
        keymap = new EnumMap<>(Key.class);

        keymap.put(Key.JUMP, Key.UP);
        keymap.put(Key.MOVE_RIGHT, Key.RIGHT);
        keymap.put(Key.MOVE_LEFT, Key.LEFT);
        keymap.put(Key.CROUCH, Key.DOWN);
    }

    Set<Key> pro_keys;
    Set<Key> cur_keys;
    Set<Key> pre_keys;
    Map<Key, Integer> key_count;

    Vector<Status> statuses;
    Vector<Status> statuses_cached;

    ReadWriteLock lock;


    public Input() {

        pro_keys = EnumSet.noneOf(Key.class);
        cur_keys = EnumSet.noneOf(Key.class);
        pre_keys = EnumSet.noneOf(Key.class);

        key_count = new EnumMap<>(Key.class);

        statuses_cached = new Vector<>();
        statuses = new Vector<>();

        lock = new ReentrantReadWriteLock();
    }

    @Override
    public void update() {
        Set<Key> tmp_keys;
        Vector<Status> tmp_statuses;


        /**
         * for cached current states
         * */
        lock.writeLock().lock();

        pre_keys.retainAll(pro_keys);
        pre_keys.addAll(pro_keys);

        /* rolling optimized */
        tmp_keys = pre_keys;
        pre_keys = cur_keys;
        cur_keys = pro_keys;
        pro_keys = tmp_keys;

        statuses_cached.clear();

        tmp_statuses = statuses_cached;
        statuses_cached = statuses;
        statuses = tmp_statuses;

        lock.writeLock().unlock();


        for (Key key : key_count.keySet())
            if (!cur_keys.contains(key))
                key_count.put(key, 0);

        for (Key key : cur_keys)
            key_count.put(key, key_count.getOrDefault(key, 0) + 1);



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
    public boolean isRepeat(Key key) {
        key = keymap.getOrDefault(key, key);
        final int count = key_count.getOrDefault(key, 0);

        return (key != Key.UNDEFINED) &&
                (count >= key_start_ticks) &&
                (0 == ((count - key_start_ticks) % key_repeat_ticks));
    }

    @Override
    public List<Status> getStatuses() {

        return statuses_cached;
    }

    @Override
    public void triggerEvent(Event evt) {
        lock.readLock().lock();

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
                statuses.add((Status)evt.getData());
                break;
            }

        }

        lock.readLock().unlock();
    }
}


