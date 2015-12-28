package io.github.antijava.marjio.scene.sceneObject;

import io.github.antijava.marjio.common.graphics.Rectangle;
import io.github.antijava.marjio.common.graphics.Viewport;

/**
 * Created by Zheng-Yuan on 12/28/2015.
 */
public class Block extends SceneObjectObjectBase {
    // TODO: Different type, different block image.
    private int mType;

    public Block(int type, int row, int col, Viewport viewport) {
        super(viewport);
        mType = type;
        setX(row * BLOCK_SIZE);
        setY(col * BLOCK_SIZE);
    }

    @Override
    public void update() {
        // TODO:
    }

    @Override
    public Rectangle getOccupiedSpace() {
        return new Rectangle(getX(), getY(), BLOCK_SIZE, BLOCK_SIZE);
    }
}
