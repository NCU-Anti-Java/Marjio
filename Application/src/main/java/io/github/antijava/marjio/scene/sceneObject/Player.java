package io.github.antijava.marjio.scene.sceneObject;

import io.github.antijava.marjio.application.Application;
import io.github.antijava.marjio.common.IApplication;
import io.github.antijava.marjio.common.graphics.Color;
import io.github.antijava.marjio.common.graphics.IBitmap;
import io.github.antijava.marjio.common.graphics.Rectangle;
import io.github.antijava.marjio.common.graphics.Viewport;

import io.github.antijava.marjio.common.input.Key;
import io.github.antijava.marjio.common.input.Status;
import io.github.antijava.marjio.constant.Constant;
import io.github.antijava.marjio.graphics.Bitmap;
import io.github.antijava.marjio.resourcemanager.ResourcesManager;

import io.github.antijava.marjio.common.input.SceneObjectStatus;
import io.github.antijava.marjio.common.input.Status;


import java.util.*;

/**
 * Created by firejox on 2015/12/28.
 */
public class Player extends SceneObjectObjectBase implements Constant {
    public static final double VELOCITY_LIMIT =
            (double)BLOCK_SIZE / 2.0D - 1.0D;
    public static final double HUMAN_LIMTT = 6.0;

    public static final Key[] action_keys = {
       Key.MOVE_LEFT, Key.MOVE_RIGHT, Key.JUMP, Key.CAST
    };

    private enum Animation {
        JUMP(0),
        WALK1(1),
        WALK2(2),
        WALK3(3),
        STOP(3);

        private final int mValue;
        Animation(int value) {
            mValue = value;
        }

        public int getValue() {
            return mValue;
        }
    };

    private enum Face {
        LEFT(0), RIGHT(1);
        private final int mValue;
        Face (int value) { mValue = value; }
        public int getValue() { return mValue; }
    }

    private static final String MARIO_FILE_NAME = "mario.png";
    private static Map<Color, List<IBitmap[]>> sPlayer_styles;
    private static boolean sStyleLoaded = false;

    private Color mClothColor;
    private int mAnimationCounter;
    private Face mFinalFace;

    UUID mId;

    int mTick;

    int mX;
    int mY;

    double mVelocityX;
    double mVelocityXModify;
    double mVelocityY;
    double mVelocityYModify;


    double mAccelerationX;
    double mAccelerationY;

    boolean mStatusUpdate;

    Item mHave;

    Queue<IAction> mEffect;


    public Player(final IApplication application, Viewport viewport, UUID id, Color clothColor) {
        super(viewport);
        mId = id;

        mTick = 0;

        mX = super.getX() + BLOCK_SIZE * 1;
        mY = super.getY();

        mVelocityX = 0;
        mVelocityY = 0;

        mAccelerationX = 0;
        mAccelerationY = 0;

        mStatusUpdate = false;

        mHave = null;

        mVelocityXModify = 1.0;
        mVelocityYModify = 1.0;

        mClothColor = clothColor;

        if (!sStyleLoaded) {
            final ResourcesManager resourceManager = ((Application)application).getResourcesManager();
            sPlayer_styles = new HashMap<>();
            Color[] colors = new Color[]{Color.BLUE, Color.RED};
            for (Color color : colors) {
                IBitmap[] leftAnimation = new IBitmap[4];
                IBitmap[] rightAnimation = new IBitmap[4];
                int index = 0;
                if (color == Color.BLUE) {
                    index = 0;
                }
                else if (color == Color.RED) {
                    index = 1;
                }
                for (int i = 0; i < 4; i++) {
                    leftAnimation[i] = resourceManager.mario(MARIO_FILE_NAME, i, index * 2 + Face.LEFT.getValue());
                    rightAnimation[i] = resourceManager.mario(MARIO_FILE_NAME, i, index * 2 + Face.RIGHT.getValue());
                }
                List<IBitmap[]> marioAnimation = new ArrayList<>(Arrays.asList(leftAnimation, rightAnimation));
                sPlayer_styles.put(color, marioAnimation);
            }
            sStyleLoaded = true;
        }

        mFinalFace = Face.RIGHT;
        setZoomX(BLOCK_SIZE * 1.0D / 16.0D);
        setZoomY(BLOCK_SIZE * 1.0D / 16.0D);
        setBitmap(sPlayer_styles.get(mClothColor).get(mFinalFace.getValue())[Animation.STOP.getValue()]);
        mAnimationCounter = 0;
    }

    public void reset() {
        mX = BLOCK_SIZE;
        mY = 0;

        mVelocityX = 0;
        mVelocityY = 0;

        mAccelerationX = 0;
        mAccelerationY = 0;

        mStatusUpdate = false;
    }

    @Override
    public void update() {

        mX = getNextX();
        mY = getNextY();

        super.setX(mX);
        super.setY(mY);

        mTick++;
        mStatusUpdate = false;

        if (mVelocityX > 0)
            mFinalFace = Face.RIGHT;
        else if (mVelocityX < 0)
            mFinalFace = Face.LEFT;
        updateAnimation();
    }

    public UUID getId() {
        return mId;
    }

    public void setTick(int tick) {
        mTick = tick;
    }

    public int getTick() {
        return mTick;

    }

    @Override
    public void setX(int x)  {
        mX = x;
    }

    @Override
    public void setY(int y) {
        mY = y;
    }

    @Override
    public int getX() {
        return mX;
    }

    @Override
    public int getY() {
        return mY;
    }

    public int getNextX() {
        return mX + (int)Math.round(normalizeVelocityX());
    }

    public int getNextY() {
        return mY + (int)Math.round(normalizeVelocityY());
    }


    public double getVelocityX (){
        return mVelocityX * mVelocityXModify;
    }

    public double getVelocityY (){
        return mVelocityY * mVelocityYModify;
    }

    public void setVelocityX (double vx) {
        mVelocityX = vx;
    }

    public void setVelocityXWithModify(double vx) {
        if (mVelocityXModify < 0.01)
            mVelocityX = 0.0;
        else
            mVelocityX = vx / mVelocityXModify;
    }

    public void setVelocityYWithModify(double vy) {
        if (mVelocityYModify < 0.01)
            mVelocityY = 0.0;
        else
            mVelocityY = vy / mVelocityYModify;
    }

    public void setVelocityY (double vy) {
        mVelocityY = vy;
    }

    public void setAccelerationX (double ax) {
        mAccelerationX = ax;
    }

    public void addAccelerationX (double ax) {
        mAccelerationX += ax;
    }

    public void addAccelerationY (double ay) {
        mAccelerationY += ay;
    }

    public void setAccelerationY (double ay) {
        mAccelerationY = ay;
    }

    public void preUpdate() {

        mVelocityX += mAccelerationX;

        if (Math.abs(mVelocityX) <= PhysicsConstant.friction)
            mVelocityX = 0.0;
        else
            mVelocityX = Math.signum(mVelocityX) *
                    (Math.abs(mVelocityX) - PhysicsConstant.friction);

        if (mVelocityX > 0)
            mVelocityX = Math.min(HUMAN_LIMTT, mVelocityX);
        else
            mVelocityX = Math.max(-HUMAN_LIMTT, mVelocityX);

        if (Math.abs(Math.abs(mVelocityX) - HUMAN_LIMTT) < 0.01)
            mAccelerationX = Math.signum(mVelocityX) *
                    (PhysicsConstant.friction - 1e-5);

        mVelocityY += mAccelerationY + PhysicsConstant.gravity;
    }

    public void preUpdateStatus(SceneObjectStatus data) {
        mTick = data.tick;
        mX = data.x;
        mY = data.y;
        mVelocityX = data.vx;
        mVelocityY = data.vy;
        mAccelerationX = data.ax;
        mAccelerationY = data.ay;
        mStatusUpdate = true;
    }

    public boolean isStatusUpdate() {
        return mStatusUpdate;
    }

    public SceneObjectStatus getStatus() {
        SceneObjectStatus data = new SceneObjectStatus(mId,
                SceneObjectStatus.SceneObjectTypes.Player);


        data.tick = mTick;
        data.x = mX;
        data.y = mY;
        data.vx = mVelocityX;
        data.vy = mVelocityY;
        data.ax = mAccelerationX;
        data.ay = mAccelerationY;

        return data;
    }


    public boolean isValidData (SceneObjectStatus data) {
        if (data.getDataType() != SceneObjectStatus.SceneObjectTypes.Player)

            return false;

        return data.isValidKeySets();
    }


    public double normalizeVelocityX() {
        final double vx = getVelocityX();

        if (vx < 0)
            return Math.max(vx, -VELOCITY_LIMIT);
        else
           return Math.min(vx, VELOCITY_LIMIT);
    }

    public double normalizeVelocityY() {
        final double vy = getVelocityY();
        return Math.min(vy, VELOCITY_LIMIT);
    }

    public void updateAnimation() {
        // TODO: Solve the frame of jumping state at the top. or not to solve?
        if (Math.abs(mVelocityY) > 1.0) {
            setBitmap(sPlayer_styles.get(mClothColor).get(mFinalFace.getValue())[Animation.JUMP.getValue()]);
            mAnimationCounter = 0;
        }
        else {
            if (mVelocityX == 0 && mVelocityY == 0) {
                setBitmap(sPlayer_styles.get(mClothColor).get(mFinalFace.getValue())[Animation.STOP.getValue()]);
                mAnimationCounter = 0;
            }
            else {
                if (mAnimationCounter >= 6)
                    setBitmap(sPlayer_styles.get(mClothColor).get(mFinalFace.getValue())[Animation.WALK3.getValue()]);
                else if (mAnimationCounter >= 3)
                    setBitmap(sPlayer_styles.get(mClothColor).get(mFinalFace.getValue())[Animation.WALK1.getValue()]);
                else
                    setBitmap(sPlayer_styles.get(mClothColor).get(mFinalFace.getValue())[Animation.WALK2.getValue()]);
                mAnimationCounter = ++mAnimationCounter % 9;
            }
        }
    }

    @Override
    public Rectangle getOccupiedSpace() {
        final int real_x = Math.round(getViewport().x + mX);
        final int real_y = Math.round(getViewport().y + mY);

        return new Rectangle(real_x - PLAYER_SIZE / 2,
                             real_y + PLAYER_SIZE,
                             PLAYER_SIZE, PLAYER_SIZE);
    }

    @Override
    public String toString() {
        return "player x:" + mX +" y:" + mY +
                " vx: " + mVelocityX + " vy:" + mVelocityY +
                " ax: " + mAccelerationX + " ay:" + mAccelerationY;
    }
}
