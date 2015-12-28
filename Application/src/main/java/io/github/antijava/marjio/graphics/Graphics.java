package io.github.antijava.marjio.graphics;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import io.github.antijava.marjio.common.IApplication;
import io.github.antijava.marjio.common.IGraphics;
import io.github.antijava.marjio.common.graphics.IBitmap;
import io.github.antijava.marjio.common.graphics.IFont;
import io.github.antijava.marjio.common.graphics.ISprite;
import io.github.antijava.marjio.common.graphics.Viewport;
import io.github.antijava.marjio.window.WindowBase;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.nio.file.NoSuchFileException;

/**
 * @author Davy
 * @author Jason
 */
public class Graphics implements IGraphics {
    private final Viewport mDefaultViewport = new Viewport();
    private final IApplication mApplication;
    private final ArrayList<Font> mFonts;
    private final ArrayList<Viewport> mViewports;
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

        mFonts = new ArrayList<>();
        mFonts.add(new Font("MingLiu", 12, false, false));

        mViewports = new ArrayList<>();
        mViewports.add(mDefaultViewport);
    }

    public void touch() {
        try {
            windowBase = new WindowBase(mApplication, loadBitmap("windowskin/default.png"), 200, 200);
        } catch (NoSuchFileException e) {
            windowBase = null;
            System.exit(1);
        }
    }

    @Override
    public void update() {
        // TODO: Finish implement
        ArrayList<ISprite> sprites = SpriteBase.getSprites();
        for (ISprite sprite : sprites) {
            IBitmap bitmap = sprite.getBitmap();
            Viewport viewport = sprite.getViewport();
        }

        windowBase.setWidth((windowBase.getWidth() + 1) % 200 + 200);
        windowBase.setHeight((windowBase.getHeight() + 3) % 200 + 200);
        windowBase.update();
        final java.awt.Graphics g = bufferedImage.getGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, mPanel.getWidth(), mPanel.getHeight());
        g.drawImage(((Bitmap) windowBase.getBitmap()).mImage, 0, 0, null);
        mPanel.repaint();
    }

    @Override
    public IFont getDefaultFont() {
        return mFonts.get(0);
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
            return null;
        }
    }
}
