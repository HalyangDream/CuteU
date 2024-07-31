//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.cute.uibase.wheelview;

import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

public final class LoopViewGestureListener extends SimpleOnGestureListener {
    private final WheelView wheelView;

    public LoopViewGestureListener(WheelView wheelView) {
        this.wheelView = wheelView;
    }

    @Override
    public final boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        this.wheelView.scrollBy(velocityY);
        return true;
    }
}
