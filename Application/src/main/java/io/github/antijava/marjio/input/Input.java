package io.github.antijava.marjio.input;

import io.github.antijava.marjio.common.IInput;
import io.github.antijava.marjio.common.input.*;
import io.github.antijava.marjio.common.input.IKeyInfo.KeyState;
import io.github.antijava.marjio.common.network.Packable;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by firejox on 2015/12/25.
 */
public final class Input implements IInput, IKeyInput {
    static final Map<Key, Key> sKeymap = new EnumMap<>(Key.class);

    static {
        sKeymap.put(Key.JUMP, Key.UP);
        sKeymap.put(Key.MOVE_RIGHT, Key.RIGHT);
        sKeymap.put(Key.MOVE_LEFT, Key.LEFT);
        sKeymap.put(Key.CROUCH, Key.DOWN);
    }

    private boolean  mAnyKeyPressed;
    private Set<Key> mNextKeys;
    private Set<Key> mCurrentKeys;
    private Set<Key> mPreviousKeys;
    private Map<Key, Integer> mKeyRepeatCount;

    private Map<Class<? extends Packable>, List<Packable>> mNetWorkData;
    private Map<Class<? extends Packable>, List<Packable>> mNetWorkDataCached;


    private ReadWriteLock mLock;

    public Input() {
        mAnyKeyPressed = false;

        mNextKeys = EnumSet.noneOf(Key.class);
        mCurrentKeys = EnumSet.noneOf(Key.class);
        mPreviousKeys = EnumSet.noneOf(Key.class);

        mKeyRepeatCount = new EnumMap<>(Key.class);

        mNetWorkData = new HashMap<>();
        mNetWorkDataCached = new HashMap<>();
        
        initNetWorkData();

        mLock = new ReentrantReadWriteLock();
    }

    private void initNetWorkData() {
        try {
            Class[] cArg = new Class[1];
            cArg[0] = Class.class;
            Method m = IInput.class.getMethod("getNetWorkData", cArg);
            NetWorkData data = m.getAnnotation(NetWorkData.class);

            for (Class c : data.value()) {
                mNetWorkData.put(c, new Vector<>());
                mNetWorkDataCached.put(c, new Vector<>());
            }

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        // Lock for swapping spaces
        mLock.writeLock().lock();

        mPreviousKeys.clear();
        mPreviousKeys.addAll(mNextKeys);

        // Rolling optimized
        final Set<Key> tmpKeys = mPreviousKeys;
        mPreviousKeys = mCurrentKeys;
        mCurrentKeys = mNextKeys;
        mNextKeys = tmpKeys;


        for (final Class c : mNetWorkData.keySet()) {
            final List<Packable> tempData = mNetWorkData.get(c);
            
            mNetWorkData.put(c, mNetWorkDataCached.get(c));
            mNetWorkDataCached.put(c, tempData);
            mNetWorkData.get(c).clear();
        }

        // Unlock
        mLock.writeLock().unlock();

        // If Any key Pressed then Exists some key not contain in PreviousKeys
        mAnyKeyPressed = !mPreviousKeys.containsAll(mCurrentKeys);

        // Reset released key's repeat count
        mKeyRepeatCount.keySet().stream()
                .filter(key -> !mCurrentKeys.contains(key))
                .forEach(key -> mKeyRepeatCount.put(key, -1));

        for (final Key key : mCurrentKeys)
            mKeyRepeatCount.put(key, mKeyRepeatCount.getOrDefault(key, -1) + 1);


    }

    @Override
    public boolean isPressing(final Key k) {
        final Key key = getRealKey(k);

        return key != Key.UNDEFINED &&
                (mPreviousKeys.contains(key) &&
                mCurrentKeys.contains(key));
    }

    @Override
    public boolean isPressed(final Key k) {
        final Key key = getRealKey(k);

        if (key == Key.UNDEFINED)
            return false;

        if (key == Key.ANY)
            return mAnyKeyPressed;

        return (!mPreviousKeys.contains(key) &&
                mCurrentKeys.contains(key));
    }

    @Override
    public boolean isReleased(final Key k) {
        final Key key = getRealKey(k);

        return key != Key.UNDEFINED &&
                (mPreviousKeys.contains(key) &&
                !mCurrentKeys.contains(key));
    }

    @Override
    public boolean isTrigger(final Key key) {
        return isPressed(key) || isReleased(key);
    }

    @Override
    public boolean isKeyUp(final Key k) {
        final Key key = getRealKey(k);

        return key != Key.UNDEFINED &&
                !mCurrentKeys.contains(key);
    }

    @Override
    public boolean isKeyDown(Key k) {
        final Key key = getRealKey(k);

        return key != Key.UNDEFINED &&
                mCurrentKeys.contains(k);
    }

    @Override
    public boolean isRepeat(final Key k) {
        final Key key = getRealKey(k);
        final int count = mKeyRepeatCount.getOrDefault(key, -1);

        if (key == Key.UNDEFINED || count < 0) // Invalid key or not pressing
            return false;

        if (count == 0) // Just pressed, give it a repeat.
            return true;

        return (count >= key_start_ticks) &&
                (0 == ((count - key_start_ticks) % key_repeat_ticks));
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Status> getStatuses() {
        return (List<Status>)getNetWorkData(Status.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Request> getRequest() {
        return (List<Request>)getNetWorkData(Request.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SyncList> getSyncList() {
        return (List<SyncList>)getNetWorkData(SyncList.class);
    }

    @Override
    public List<GameSet> getGameSet() {
        return (List<GameSet>)getNetWorkData(GameSet.class);
    }

    @Override
    public List<? extends Packable> getNetWorkData(Class c) {
        return mNetWorkDataCached.getOrDefault(c, Collections.emptyList());
    }

    @Override
    public List<TickRequest> getTickRequest() {
        return (List<TickRequest>)getNetWorkData(TickRequest.class);
    }

    @Override
    public void triggerEvent(Event evt) {
        mLock.readLock().lock();

        switch (evt.getType()) {
            case Keyboard: {
                final IKeyInfo info = (IKeyInfo) evt.getData();

                if (info.getKeyState() == KeyState.KEY_PRESSED)
                    mNextKeys.add(info.getKey());
                else if (info.getKeyState() == KeyState.KEY_RELEASED)
                    mNextKeys.remove(info.getKey());

                break;
            }

            // TODO: Data to Status
            case NetWorkClient:
            case NetworkServer: {
                Object data = evt.getData();

                if (data != null) {
                    mNetWorkData.forEach((klass, list) -> {
                        if (klass.isAssignableFrom(data.getClass())) {
                            list.add((Packable)(data));
                        }
                    });
                }
                break;
            }

        }

        mLock.readLock().unlock();
    }

    private Key getRealKey(final Key k) {
        return sKeymap.getOrDefault(k, k);
    }
}
