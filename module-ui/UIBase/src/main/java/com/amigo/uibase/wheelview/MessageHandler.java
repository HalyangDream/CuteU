package com.amigo.uibase.wheelview;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import android.os.Handler;
import android.os.Message;


public final class MessageHandler extends Handler {
    public static final int WHAT_INVALIDATE_LOOP_VIEW = 1000;
    public static final int WHAT_SMOOTH_SCROLL = 2000;
    public static final int WHAT_ITEM_SELECTED = 3000;
    private final WheelView wheelView;

    public MessageHandler(WheelView wheelView) {
        this.wheelView = wheelView;
    }

    public final void handleMessage(Message msg) {
        switch(msg.what) {
        case 1000:
            this.wheelView.invalidate();
            break;
        case 2000:
            this.wheelView.smoothScroll(WheelView.ACTION.FLING);
            break;
        case 3000:
            this.wheelView.onItemSelected();
        }

    }
}
