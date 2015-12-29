package io.github.antijava.marjio.scene.sceneObject;

import io.github.antijava.marjio.common.graphics.Rectangle;
import io.github.antijava.marjio.common.graphics.Viewport;
import io.github.antijava.marjio.graphics.Bitmap;

/**
 * Created by Zheng-Yuan on 12/28/2015.
 */
public class Block extends SceneObjectObjectBase {
    // TODO: Different type with different block image.
    // TODO: Need images.
    public enum Type {
        /**
         * Not touchable
         */
        AIR(0),
        /**
         * Not breakable block
         */
        GROUND(1),
        /**
         * breakable block
         */
        WOOD(2);

        private final int mValue;
        Type(int value) {
            mValue = value;
        }

        public int getValue() {
            return mValue;
        }
    }

    private Type mType;

    public Block(int type, int x, int y, Viewport viewport) {
        super(viewport);
        setType(type);
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

    public Type getType() {
        return mType;
    }

    private void setType(int type) {
        switch (type) {
            case 0:
                mType = Type.AIR;
                break;
            case 1:
                mType = Type.GROUND;
                break;
            case 2:
                mType = Type.WOOD;
                break;
        }
    }
}
