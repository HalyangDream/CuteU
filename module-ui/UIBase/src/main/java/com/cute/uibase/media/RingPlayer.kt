package com.cute.uibase.media

import android.content.Context
import android.media.MediaPlayer

object RingPlayer {

    private var mPlayer: MediaPlayer? = null


    /**
     * 停止拨号提示音
     */
    fun stopRinging() {
        try {
            if (mPlayer != null && mPlayer!!.isPlaying) {
                mPlayer!!.stop()
                mPlayer!!.release()
                mPlayer = null
            }
        } catch (e: Exception) {
        }
    }

    fun startRinging(context: Context, res: Int) {
        try {
            stopRinging()
            mPlayer = MediaPlayer.create(context, res)
            mPlayer!!.setLooping(true)
            mPlayer!!.start()
        } catch (e: Exception) {
        }
    }
}