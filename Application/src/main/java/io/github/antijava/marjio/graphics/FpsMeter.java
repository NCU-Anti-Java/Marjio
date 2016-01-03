package io.github.antijava.marjio.graphics;

import io.github.antijava.marjio.common.utils.NumberUtils;

import java.util.Calendar;
import java.util.logging.Logger;

/**
 * Created by Davy on 2015/12/30.
 */
public class FpsMeter {
    private long mStartTime, mLastUpdateTime;
    private long mFramesTotal = 0, mDuringNano = 0;
    private long mActualFpsDuring = 0;
    private double mActualFps = 0;

    public FpsMeter() {}

    public void start() {
        mStartTime = System.nanoTime();
        mLastUpdateTime = mStartTime;
        mFramesTotal = 0;
        mDuringNano = 0;
        mActualFps = 0.0;
    }

    public void tick() {
        final long now = System.nanoTime();
        mDuringNano = now - mLastUpdateTime;
        mActualFpsDuring += mDuringNano;
        mFramesTotal++;
        if (mFramesTotal % 60 == 0) {
            mActualFps = NumberUtils.round(60 * 1000000000.0D / mActualFpsDuring, 2);
            mActualFpsDuring = 0;
        }
        mLastUpdateTime = now;
    }

    public long getDuringNano() {
        return mDuringNano;
    }

    public double getActualFps() {
        return mActualFps;
    }

    public double getFps() {
        return NumberUtils.round(mFramesTotal * 1000000000.0 / (mLastUpdateTime - mStartTime), 2);
    }

    public long getLastUpdateTime() {
        return mLastUpdateTime;
    }
}
