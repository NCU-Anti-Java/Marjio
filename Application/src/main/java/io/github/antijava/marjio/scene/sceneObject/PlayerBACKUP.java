package io.github.antijava.marjio.scene.sceneObject;

import io.github.antijava.marjio.common.graphics.Rectangle;
import io.github.antijava.marjio.common.graphics.Viewport;

/**
 * Created by Zheng-Yuan on 12/28/2015.
 */
public class PlayerBACKUP extends SceneObjectObjectBase {
    /**
     * TODO: PlayerBACKUP should contain variety of action frames.
     */
    private final int VX = 5;
    private final int VY = 10;
    private final int AGAINST_GRAVITY_COUNTER = 5;
    private int mAgainstGravityCounter;
    private int mNextX;
    private int mNextY;

    private boolean mIsUp;
    private boolean mIsLeft;
    private boolean mIsRight;
    private boolean mIsSpace;

    private boolean mIsJumping;
    private boolean mIsDroping;
    private boolean mIsMovingLeft;
    private boolean mIsMovingRight;
    private boolean mIsDoingMagic;

    public PlayerBACKUP(Viewport viewport) {
        super(viewport);
        mIsUp = false;
        mIsLeft = false;
        mIsRight = false;
        mIsSpace = false;
        mIsMovingLeft = false;
        mIsMovingRight = false;
        mIsJumping = false;
        mIsDroping = false;
    }

    @Override
    public void update() {
        setX(mNextX);
        setY(mNextY);
        // TODO: Draw on graphics.
    }

    /**
     * Pre-calculate the status and next position.
     */
    public void preUpdate() {
        updateStatus();
        updateNextPosition();
    }

    private void updateStatus() {
        mIsMovingLeft = mIsLeft;
        mIsMovingRight = mIsRight;
        mIsDoingMagic = mIsSpace;
        if (mIsJumping || mIsDroping) {
            if (mAgainstGravityCounter < 0) {
                mIsJumping = false;
                mIsDroping = true;
            }
        }
        else {
            mIsJumping = mIsUp;
            if (mIsJumping)
                mAgainstGravityCounter = AGAINST_GRAVITY_COUNTER;
        }
    }

    private void updateNextPosition() {
        if (mIsMovingLeft) {
            mNextX = getX() - VX;
        }
        else if (mIsMovingRight) {
            mNextX = getX() + VX;
        }
        if (mIsJumping) {
            mNextY = getY() + VY;
            mAgainstGravityCounter--;
        }
        else if (mIsDroping) {
            mNextY = getY() - VY;
        }
    }

    public void setUp(boolean isUp) {
        mIsUp = isUp;
    }

    public void setLeft(boolean isLeft) {
        mIsLeft = isLeft;
    }

    public void setRight(boolean isRight) {
        mIsRight = isRight;
    }

    public void setSpace(boolean isSpace) {
        mIsSpace = isSpace;
    }

    public void setIsDroping(boolean isDroping) {
        mIsDroping = isDroping;
    }

    public void setIsJumping(boolean isJumping) {
        mIsJumping = isJumping;
    }

    public void setIsMovingLeft(boolean isMovingLeft) {
        mIsMovingLeft = isMovingLeft;
    }

    public void setIsMovingRight(boolean isMovingRight) {
        mIsMovingRight = isMovingRight;
    }

    public void setNextX(int nextX) {
        mNextX = nextX;
    }

    public void setNextY(int nextY) {
        mNextY = nextY;
    }

    /**
     *
     * @return
     */
    @Override
    public Rectangle getOccupiedSpace() {
        final int realX = getViewport().x + mNextX;
        final int realY = getViewport().y + mNextY;
        return new Rectangle(realX - PLAYER_SIZE / 2, realY + PLAYER_SIZE, PLAYER_SIZE, PLAYER_SIZE);
    }
}
