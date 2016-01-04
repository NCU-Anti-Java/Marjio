package io.github.antijava.marjio.window;

import io.github.antijava.marjio.common.IApplication;
import io.github.antijava.marjio.common.IInput;
import io.github.antijava.marjio.common.graphics.IBitmap;
import io.github.antijava.marjio.common.input.Key;
import io.github.antijava.marjio.constant.GameConstant;
import org.jetbrains.annotations.NotNull;

import static io.github.antijava.marjio.common.graphics.Color.WHITE;
import static io.github.antijava.marjio.common.graphics.IBitmap.TextAlign.CENTER;

/**
 * @author Davy
 */
public class WindowMessage extends WindowBase {
    private final String mMessage;

    public WindowMessage(@NotNull IApplication application, final String message, final int width) {
        super(application, width, WINDOW_LINE_HEIGHT + 32);
        mMessage = message;

        setX((GameConstant.GAME_WIDTH - width) / 2);
        setY((GameConstant.GAME_HEIGHT - getHeight()) / 2);
        refresh();
    }

    private void refresh() {
        final IBitmap content = getContent();
        content.clear();

        content.drawText(mMessage, 0, 0, content.getWidth(), WINDOW_LINE_HEIGHT, WHITE, CENTER);
        dirty();
    }

    @Override
    public void update() {
        super.update();
        final IInput input = getApplication().getInput();

        if (!isActive())
            return;

        if (input.isPressed(Key.ENTER)) {
            setActive(false);
            super.update();
        }
    }

    @Override
    public void setActive(final boolean v) {
        super.setActive(v);

        setOpacity(v ? 0 : 255);
    }
}
