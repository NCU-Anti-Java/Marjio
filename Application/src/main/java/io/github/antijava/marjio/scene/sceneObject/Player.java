package io.github.antijava.marjio.scene.sceneObject;

import io.github.antijava.marjio.common.graphics.Color;
import io.github.antijava.marjio.common.graphics.IBitmap;
import io.github.antijava.marjio.common.graphics.Rectangle;
import io.github.antijava.marjio.common.graphics.Viewport;
import io.github.antijava.marjio.common.input.Key;
import io.github.antijava.marjio.common.input.Status;
import io.github.antijava.marjio.constant.Constant;

import java.util.*;

/**
 * Created by firejox on 2015/12/28.
 */
public class Player extends SceneObjectObjectBase implements Constant {
    public static final double VELOCITY_LIMIT =
            (double)BLOCK_SIZE / 2.0D - 1.0D;

    public static final Key[] action_keys = {
       Key.MOVE_LEFT, Key.MOVE_RIGHT, Key.JUMP, Key.CAST
    };

    public static final Map<Color, IBitmap[]> player_styles;

    static {
        player_styles = new HashMap<>();
    }


    UUID mId;

    int mTick;

    int mX;
    int mY;

    double mVelocityX;
    double mVelocityXModify;
    double mVelocityY;

    double mAccelerationX;
    double mAccelerationY;

    boolean mStatusUpdate;

    Item mHave;

    Queue<IAction> mEffect;


    public Player(Viewport viewport, UUID id) {
        super(viewport);
        mId = id;

        mTick = 0;

        mX = super.getX();
        mY = super.getY();

        mVelocityX = 0;
        mVelocityY = 0;

        mAccelerationX = 0;
        mAccelerationY = 0;

        mStatusUpdate = false;

        mHave = null;

        mVelocityXModify = 1.0;

    }

    public void reset() {
        mX = 0;
        mY = 0;

        mVelocityX = 0;
        mVelocityY = 0;

        mAccelerationX = 0;
        mAccelerationY = 0;

        mStatusUpdate = false;
    }

    @Override
    public void update() {

        mX += Math.round(mVelocityX);
        mY += Math.round(mVelocityY);

        super.setX(mX);
        super.setY(mY);

        mTick++;
        mStatusUpdate = false;
    }

    public UUID getmId() {
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
        return mVelocityY;
    }

    public void setVelocityX (double vx) {
        mVelocityX = vx;
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

        mVelocityY += mAccelerationY + PhysicsConstant.gravity;
    }

    public void preUpdateStatus(Status data) {
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

    public Status getStatus() {
        Status data = new Status(Status.DataTypes.Player);

        data.setClientID(mId);

        data.tick = mTick;
        data.x = mX;
        data.y = mY;
        data.vx = mVelocityX;
        data.vy = mVelocityY;
        data.ax = mAccelerationX;
        data.ay = mAccelerationY;

        return data;
    }


    public boolean isValidData (Status data) {
        if (data.getDataType() != Status.DataTypes.Player)
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
