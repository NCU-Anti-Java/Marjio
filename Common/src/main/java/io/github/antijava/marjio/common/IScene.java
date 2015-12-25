package io.github.antijava.marjio.common;

import io.github.antijava.marjio.common.exception.ObjectDisposedException;

/**
 * Created by fntsr on 2015/12/23.
 */
public interface IScene extends Disposable {
    void update() throws ObjectDisposedException;
}
