package io.github.antijava.marjio.common.exception;

/**
 * Created by Davy on 2015/12/25.
 */
public class ObjectDisposedException extends ApplicationException {
    private static final long serialVersionUID = -5768652531023380352L;

    @Override
    public String getMessage() {
        return "This object is disposed.";
    }
}
