package io.github.antijava.marjio.common.utils;

/**
 * Created by Davy on 2015/12/25.
 */

public final class NumberUtils {
    public static double round(final double v, final int _10base) {
        final double base = Math.pow(10, _10base);
        return Math.round(v * base) / base;
    }
}
