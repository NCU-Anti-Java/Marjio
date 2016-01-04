package io.github.antijava.marjio.graphics;

import io.github.antijava.marjio.common.IGraphics;
import io.github.antijava.marjio.common.exception.ObjectDisposedException;
import io.github.antijava.marjio.common.graphics.Color;
import io.github.antijava.marjio.common.graphics.IBitmap;
import io.github.antijava.marjio.common.graphics.IFont;
import io.github.antijava.marjio.common.graphics.Rectangle;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

/**
 * @author Jason
 * @author Davy
 */
public class Bitmap implements IBitmap {
    private Graphics2D mAwtGraphics2D;
    BufferedImage mImage;

    private IFont mFont;
    private Font mAwtTextFont;
    private final FontRenderContext mAwtFontRenderContext;

    // region Constructor
    public Bitmap(IGraphics graphics, int width, int height) {
        // BufferedImage and Graphics2D
        mImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        mAwtGraphics2D = mImage.createGraphics();
        mAwtGraphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        mAwtGraphics2D.setBackground(new java.awt.Color(255, 255, 255, 0));

        // Font properties
        setFont(graphics.getDefaultFont());
        mAwtFontRenderContext = new FontRenderContext(null, true, true);
    }

    public Bitmap(final IGraphics graphics, BufferedImage image) {
        this(graphics, image.getWidth(), image.getHeight());
        mAwtGraphics2D.drawImage(image, 0, 0, null);
    }
    // endregion Constructor

    // region Drawing
    @Override
    public void clear() {
        if (isDisposed())
            throw new ObjectDisposedException();
        clearRect(0, 0, mImage.getWidth(), mImage.getHeight());
    }

    @Override
    public Rectangle measureText(CharSequence text, int lineHeight) {
        if (isDisposed())
            throw new ObjectDisposedException();

        final FontMetrics metrics = mAwtGraphics2D.getFontMetrics(mAwtTextFont);
        return new Rectangle(metrics.stringWidth(text.toString()), metrics.getHeight());
    }

    @Override
    public void resize(int width, int height) {
        if (isDisposed())
            throw new ObjectDisposedException();

        final Image scaledImage = mImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        mImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        mAwtGraphics2D = mImage.createGraphics();
        mAwtGraphics2D.drawImage(scaledImage, 0, 0, null);
    }

    @Override
    public void blur() {
        final float weight = 1.0f/9.0f;
        final float[] elements = {
                weight, weight, weight,
                weight, weight, weight,
                weight, weight, weight
        };

        final Kernel k = new Kernel(3, 3, elements);
        final ConvolveOp op = new ConvolveOp(k);
        final BufferedImage dest = new BufferedImage(getWidth(), getHeight(), mImage.getType());
        op.filter(mImage, dest);

        clear();
        mAwtGraphics2D.drawImage(dest, 0, 0, null);
    }
    // endregion Drawing

    // region Draw Text
    @Override
    public void drawText(CharSequence text, int x, int y, int maxWidth, int lineHeight, Color color, TextAlign align) {
        if (isDisposed())
            throw new ObjectDisposedException();

        // Calculate clipping bounds
        final Rectangle bounds = measureText(text, lineHeight);
        final int boxWidth = maxWidth != -1 ? maxWidth : bounds.width;

        // Apply clipping bounds
        mAwtGraphics2D.clipRect(x, y, boxWidth, lineHeight);

        // Alignment
        if (align == TextAlign.CENTER)
            x += (maxWidth - bounds.width) / 2;
        if (align == TextAlign.RIGHT)
            x += maxWidth - bounds.width;

        // Calculate text height
        final GlyphVector gv = mAwtTextFont.layoutGlyphVector(
                mAwtFontRenderContext, text.toString().toCharArray(),
                0, text.length(), Font.LAYOUT_LEFT_TO_RIGHT);
        final Rectangle2D pixBounds = gv.getVisualBounds();
        final float textHeight = (float) pixBounds.getHeight();

        // Draw
        final TextLayout layout = new TextLayout(text.toString(), mAwtTextFont, mAwtFontRenderContext);
        mAwtGraphics2D.setColor(convertToAwtColor(color));
        layout.draw(mAwtGraphics2D, x, y + (lineHeight + textHeight) / 2);

        // Remove clipping bounds
        mAwtGraphics2D.setClip(null);
    }

    @Override
    public void drawText(CharSequence text, int x, int y, int maxWidth, int lineHeight, TextAlign align) {
        drawText(text, x, y, maxWidth, lineHeight, Color.BLACK, align);
    }

    @Override
    public void drawText(CharSequence text, int x, int y, int maxWidth, int lineHeight, Color color) {
        drawText(text, x, y, maxWidth, lineHeight, color, TextAlign.LEFT);
    }

    @Override
    public void drawText(CharSequence text, int x, int y, int maxWidth, int lineHeight) {
        drawText(text, x, y, maxWidth, lineHeight, Color.BLACK, TextAlign.LEFT);
    }

    @Override
    public void drawText(CharSequence text, Rectangle rect, Color color, TextAlign align) {
        drawText(text, rect.x, rect.y, rect.width, rect.height, color, align);
    }

    @Override
    public void drawText(CharSequence text, Rectangle rect, TextAlign align) {
        drawText(text, rect, Color.BLACK, align);
    }

    @Override
    public void drawText(CharSequence text, Rectangle rect, Color color) {
        drawText(text, rect, color, TextAlign.LEFT);
    }

    @Override
    public void drawText(CharSequence text, Rectangle rect, int lineHeight) {
        drawText(text, rect, Color.BLACK, TextAlign.LEFT);
    }
    // endregion Draw Text

    // region Clear Rectangle
    @Override
    public void clearRect(int x, int y, int width, int height) {
        if (isDisposed())
            throw new ObjectDisposedException();
        mAwtGraphics2D.clearRect(x, y, width, height);
    }

    @Override
    public void clearRect(Rectangle rect) {
        if (isDisposed())
            throw new ObjectDisposedException();
        clearRect(rect.x, rect.y, rect.width, rect.height);
    }
    // endregion Clear Rectangle

    // region Fill Rectangle
    @Override
    public void fillAll(Color color) {
        fillRect(0, 0, mImage.getWidth(), mImage.getHeight(), color);
    }

    @Override
    public void fillRect(int x, int y, int width, int height, Color color) {
        if (isDisposed())
            throw new ObjectDisposedException();

        mAwtGraphics2D.setColor(convertToAwtColor(color));
        mAwtGraphics2D.fillRect(x, y, width, height);
    }

    @Override
    public void fillRect(Rectangle rect, Color color) {
        fillRect(rect.x, rect.y, rect.width, rect.height, color);
    }
    // endregion Fill Rectangle

    // region Block transfer
    @Override
    public void blt(int x, int y, IBitmap src, Rectangle srcRect, int opacity) {
        if (isDisposed())
            throw new ObjectDisposedException();
        if (!(src instanceof Bitmap))
            throw new RuntimeException("Source bitmap should be " + Bitmap.class);

        final Bitmap srcBitmap = (Bitmap) src;
        final BufferedImage srcSubimage = srcBitmap.mImage.getSubimage(srcRect.x, srcRect.y, srcRect.width, srcRect.height);
        final Composite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f - ((float) opacity / 255.0f));
        final Composite oldComposite = mAwtGraphics2D.getComposite();
        mAwtGraphics2D.setComposite(composite);
        mAwtGraphics2D.drawImage(srcSubimage, x, y, null);
        mAwtGraphics2D.setComposite(oldComposite);
    }

    @Override
    public void stretchBlt(int x, int y, int width, int height, IBitmap src, Rectangle srcRect, int opacity) {
        if (isDisposed())
            throw new ObjectDisposedException();
        if (!(src instanceof Bitmap))
            throw new RuntimeException("Source bitmap should be " + Bitmap.class);

        final Bitmap srcBitmap = (Bitmap) src;
        final BufferedImage srcSubimage = srcBitmap.mImage.getSubimage(srcRect.x, srcRect.y, srcRect.width, srcRect.height);
        final Composite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f - ((float) opacity / 255.0f));
        final Composite oldComposite = mAwtGraphics2D.getComposite();
        mAwtGraphics2D.setComposite(composite);
        mAwtGraphics2D.drawImage(srcSubimage, x, y, width, height, null);
        mAwtGraphics2D.setComposite(oldComposite);
    }

    @Override
    public void stretchBlt(Rectangle rect, IBitmap src, Rectangle srcRect, int opacity) {
        stretchBlt(rect.x, rect.y, rect.width, rect.height, src, srcRect, opacity);
    }

    @Override
    public void tileBlt(int x, int y, int width, int height, IBitmap src, Rectangle srcRect, int opacity) {
        if (isDisposed())
            throw new ObjectDisposedException();
        if (!(src instanceof Bitmap))
            throw new RuntimeException("Source bitmap should be " + Bitmap.class);

        final Bitmap srcBitmap = (Bitmap) src;
        final BufferedImage srcSubimage = srcBitmap.mImage.getSubimage(srcRect.x, srcRect.y, srcRect.width, srcRect.height);
        final TexturePaint texturePaint = new TexturePaint(srcSubimage, new java.awt.Rectangle(srcRect.width, srcRect.height));
        final Composite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f - ((float) opacity / 255.0f));
        final Paint oldPaint = mAwtGraphics2D.getPaint();
        final Composite oldComposite = mAwtGraphics2D.getComposite();
        mAwtGraphics2D.setPaint(texturePaint);
        mAwtGraphics2D.setComposite(composite);
        mAwtGraphics2D.translate(x, y);
        mAwtGraphics2D.fillRect(0, 0, width, height);
        mAwtGraphics2D.translate(-x, -y);
        mAwtGraphics2D.setPaint(oldPaint);
        mAwtGraphics2D.setComposite(oldComposite);
    }

    @Override
    public void tileBlt(Rectangle rect, IBitmap src, Rectangle srcRect, int opacity) {
        tileBlt(rect.x, rect.y, rect.width, rect.height, src, srcRect, opacity);
    }
    // endregion Block Transfer

    // region Setter
    @Override
    public void setFont(IFont font) {
        mFont = font;

        int fontStyle = Font.PLAIN;
        if (font.isBold())
            fontStyle |= Font.BOLD;

        if (font.isItalic())
            fontStyle |= Font.ITALIC;

        mAwtTextFont = new Font(font.getName(), fontStyle, font.getSize());
    }

    @Override
    public void setPixel(int x, int y, Color color) {
        if (isDisposed())
            throw new ObjectDisposedException();

        mImage.setRGB(x, y, color.toIntBits());
    }
    // endregion Setter

    // region Getter
    @Override
    public int getHeight() {
        return mImage.getHeight();
    }

    @Override
    public int getWidth() {
        return mImage.getWidth();
    }

    @Override
    public IFont getFont() {
        return mFont;
    }

    @Override
    public Color getPixel(int x, int y) {
        if (isDisposed())
            throw new ObjectDisposedException();

        return new Color(mImage.getRGB(x, y));
    }

    @Override
    public Rectangle getRect() {
        return new Rectangle(mImage.getWidth(), mImage.getHeight());
    }
    // endregion Getter

    // region Disposable
    @Override
    public void dispose() {
        mAwtGraphics2D.dispose();
        mAwtGraphics2D = null;
        mImage = null;
        mAwtTextFont = null;
    }

    @Override
    public boolean isDisposed() {
        return mImage == null;
    }
    // endregion Disposable

    // region Helper
    private java.awt.Color convertToAwtColor(Color color) {
        return new java.awt.Color(color.toIntBits(), true);
    }
    // endregion Helper
}
