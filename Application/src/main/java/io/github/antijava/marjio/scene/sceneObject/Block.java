package io.github.antijava.marjio.scene.sceneObject;

import io.github.antijava.marjio.common.graphics.Rectangle;
import io.github.antijava.marjio.common.graphics.Viewport;
import io.github.antijava.marjio.graphics.Bitmap;

/**
 * Created by Zheng-Yuan on 12/28/2015.
 */
public class Block extends SceneObjectObjectBase {
    // TODO: Different type, different block image.
    public enum Type {
        /**
         * Not touchable
         */
        AIR,
        /**
         * Not breakable block
         */
        GROUND,
        /**
         * breakable block
         */
        WOOD
    }
    private int mType;

    public Block(int type, int x, int y, Viewport viewport) {
        super(viewport);
        mType = type;
        setX(x);
        setY(y);
    }

    @Override
    public void update() {
        // TODO:
    }

    @Override
    public Rectangle getOccupiedSpace() {
        return new Rectangle(getX(), getY(), BLOCK_SIZE, BLOCK_SIZE);
    }

    public int getType() {
        return mType;
    }
}
