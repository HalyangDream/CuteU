//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.cute.uibase.wheelview;

import java.util.TimerTask;

public final class SmoothScrollTimerTask extends TimerTask {
    private int realTotalOffset;
    private int realOffset;
    private int offset;
    private final WheelView wheelView;

    public SmoothScrollTimerTask(WheelView wheelView, int offset) {
        this.wheelView = wheelView;
        this.offset = offset;
        this.realTotalOffset = 2147483647;
        this.realOffset = 0;
    }

    public final void run() {
        if (this.realTotalOffset == 2147483647) {
            this.realTotalOffset = this.offset;
        }

        this.realOffset = (int)((float)this.realTotalOffset * 0.1F);
        if (this.realOffset == 0) {
            if (this.realTotalOffset < 0) {
                this.realOffset = -1;
            } else {
                this.realOffset = 1;
            }
        }

        if (Math.abs(this.realTotalOffset) <= 1) {
            this.wheelView.cancelFuture();
            this.wheelView.getHandler().sendEmptyMessage(3000);
        } else {
            this.wheelView.setTotalScrollY(this.wheelView.getTotalScrollY() + (float)this.realOffset);
            if (!this.wheelView.isLoop()) {
                float itemHeight = this.wheelView.getItemHeight();
                float top = (float)(-this.wheelView.getInitPosition()) * itemHeight;
                float bottom = (float)(this.wheelView.getItemsCount() - 1 - this.wheelView.getInitPosition()) * itemHeight;
                if (this.wheelView.getTotalScrollY() <= top || this.wheelView.getTotalScrollY() >= bottom) {
                    this.wheelView.setTotalScrollY(this.wheelView.getTotalScrollY() - (float)this.realOffset);
                    this.wheelView.cancelFuture();
                    this.wheelView.getHandler().sendEmptyMessage(3000);
                    return;
                }
            }

            this.wheelView.getHandler().sendEmptyMessage(1000);
            this.realTotalOffset -= this.realOffset;
        }

    }
}
