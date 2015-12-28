package io.github.antijava.marjio.graphics;

import io.github.antijava.marjio.common.IApplication;
import io.github.antijava.marjio.common.IGraphics;
import io.github.antijava.marjio.common.graphics.*;
import io.github.antijava.marjio.common.graphics.Rectangle;
import io.github.antijava.marjio.window.WindowBase;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.logging.Level;

/**
 * Created by Davy on 2015/12/28.
 */
public class Graphics implements IGraphics {
    private final Viewport mDefaultViewport = new Viewport();
    private final IApplication mApplication;
    private final JFrame mFrame;
    private final JPanel mPanel;
    private WindowBase windowBase;
    private BufferedImage bufferedImage;

    public Graphics(IApplication application) {
        mApplication = application;
        mFrame = new JFrame();
        mFrame.setSize(800, 600);
        mFrame.setResizable(false);
        mFrame.setLocationRelativeTo(null);
        bufferedImage = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        mPanel = new JPanel() {
            @Override
            public void paint(java.awt.Graphics g) {
                g.drawImage(bufferedImage, 0, 0, null);
            }
        };
        mFrame.add(mPanel);
        mFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        new Thread(() -> {
            mFrame.setVisible(true);
        }).run();
    }

    public void touch() {
        try {
            windowBase = new WindowBase(mApplication, loadBitmap("windowskin/default.png"), 200, 200);
            windowBase.setActive(true);
        } catch (NoSuchFileException e) {
            windowBase = null;
            System.exit(1);
        }
    }

    @Override
    public void update() {
        windowBase.setWidth((windowBase.getWidth() + 1) % 200 + 200);
        windowBase.setHeight((windowBase.getHeight() + 3) % 200 + 200);
        windowBase.setCursorRect(new Rectangle(0, 0, 100, 150));
        windowBase.update();
        final java.awt.Graphics g = bufferedImage.getGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, mPanel.getWidth(), mPanel.getHeight());
        g.drawImage(((Bitmap) windowBase.getBitmap()).mImage, 0, 0, null);
        mPanel.repaint();
    }

    @Override
    public IFont getDefaultFont() {
        return new IFont() {
            @Override
            public String getName() {
                return "Consolas";
            }

            @Override
            public int getSize() {
                return 12;
            }

            @Override
            public boolean isItalic() {
                return false;
            }

            @Override
            public boolean isBold() {
                return false;
            }
        };
    }

    @Override
    public Viewport getDefaultViewport() {
        return mDefaultViewport;
    }

    @Override
    public IBitmap createBitmap(int width, int height) {
        return new Bitmap(this, width, height);
    }

    @Override
    public IBitmap loadBitmap(String path) throws NoSuchFileException {
        try {
            final ClassLoader classLoader = getClass().getClassLoader();
            final File file = new File(classLoader.getResource(path).getFile());
            final BufferedImage bufferedImage = ImageIO.read(file);
            return new Bitmap(this, bufferedImage);
        } catch (IOException e) {

        }
        return null;
    }
}
