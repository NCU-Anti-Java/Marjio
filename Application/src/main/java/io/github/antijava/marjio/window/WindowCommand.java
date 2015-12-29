package io.github.antijava.marjio.window;

import io.github.antijava.marjio.common.IApplication;
import io.github.antijava.marjio.common.graphics.Color;
import io.github.antijava.marjio.common.graphics.IBitmap;
import io.github.antijava.marjio.common.graphics.IBitmap.TextAlign;
import io.github.antijava.marjio.common.graphics.Rectangle;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Davy on 2015/12/29.
 */
public class WindowCommand extends WindowSelectableBase {
    private final List<String> mCommandList;

    public WindowCommand(@NotNull final IApplication application, final int width, final String[] commands) {
        super(application, width, commands.length * WINDOW_LINE_HEIGHT + 32);

        setItemMax(commands.length);
        mCommandList = new ArrayList<>(Arrays.asList(commands));
        refresh();
        setIndex(0);
    }

    private void refresh() {
        final IBitmap content = getContent();
        content.clear();
        for (int i = 0; i < mCommandList.size(); ++i)
            drawItem(i, true);
        dirty();
    }

    private void drawItem(final int index, final boolean enabled) {
        final IBitmap content = getContent();
        final Rectangle rect = getItemRect(index);
        final Color color = Color.WHITE; // TODO: enabled color?
        rect.x += 4;
        rect.width -= 8;
        content.clearRect(rect);
        content.drawText(mCommandList.get(index), rect, color, TextAlign.CENTER);
    }
}
