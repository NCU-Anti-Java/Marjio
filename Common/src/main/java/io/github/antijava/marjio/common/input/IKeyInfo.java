package io.github.antijava.marjio.common.input;


/**
 * Created by firejox on 2015/12/25.
 */


public interface IKeyInfo {
    Key getKey();
    KeyState getKeyState();

    enum KeyState {
        KEY_PRESSED,
        KEY_RELEASED,
        KEY_UNKNOWN
    }
}
