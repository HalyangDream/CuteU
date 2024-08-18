//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.amigo.uibase.wheelview;

import java.util.TimerTask;

public final class InertiaTimerTask extends TimerTask {
    private float mCurrentVelocityY;
    private final float mFirstVelocityY;
    private final WheelView mWheelView;

    public InertiaTimerTask(WheelView wheelView, float velocityY) {
        this.mWheelView = wheelView;
        this.mFirstVelocityY = velocityY;
        this.mCurrentVelocityY = 2.14748365E9F;
    }

    public final void run() {
        if (this.mCurrentVelocityY == 2.14748365E9F) {
            if (Math.abs(this.mFirstVelocityY) > 2000.0F) {
                this.mCurrentVelocityY = this.mFirstVelocityY > 0.0F ? 2000.0F : -2000.0F;
            } else {
                this.mCurrentVelocityY = this.mFirstVelocityY;
            }
        }

        if (Math.abs(this.mCurrentVelocityY) >= 0.0F && Math.abs(this.mCurrentVelocityY) <= 20.0F) {
            this.mWheelView.cancelFuture();
            this.mWheelView.getHandler().sendEmptyMessage(2000);
        } else {
            int dy = (int)(this.mCurrentVelocityY / 100.0F);
            this.mWheelView.setTotalScrollY(this.mWheelView.getTotalScrollY() - (float)dy);
            if (!this.mWheelView.isLoop()) {
                float itemHeight = this.mWheelView.getItemHeight();
                float top = (float)(-this.mWheelView.getInitPosition()) * itemHeight;
                float bottom = (float)(this.mWheelView.getItemsCount() - 1 - this.mWheelView.getInitPosition()) * itemHeight;
                if ((double)this.mWheelView.getTotalScrollY() - (double)itemHeight * 0.25D < (double)top) {
                    top = this.mWheelView.getTotalScrollY() + (float)dy;
                } else if ((double)this.mWheelView.getTotalScrollY() + (double)itemHeight * 0.25D > (double)bottom) {
                    bottom = this.mWheelView.getTotalScrollY() + (float)dy;
                }

                if (this.mWheelView.getTotalScrollY() <= top) {
                    this.mCurrentVelocityY = 40.0F;
                    this.mWheelView.setTotalScrollY((float)((int)top));
                } else if (this.mWheelView.getTotalScrollY() >= bottom) {
                    this.mWheelView.setTotalScrollY((float)((int)bottom));
                    this.mCurrentVelocityY = -40.0F;
                }
            }

            if (this.mCurrentVelocityY < 0.0F) {
                this.mCurrentVelocityY += 20.0F;
            } else {
                this.mCurrentVelocityY -= 20.0F;
            }

            this.mWheelView.getHandler().sendEmptyMessage(1000);
        }
    }
}
