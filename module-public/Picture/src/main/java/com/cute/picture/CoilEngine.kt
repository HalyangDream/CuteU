package com.cute.picture

import android.content.Context
import android.widget.ImageView
import coil.load
import coil.size.Size
import com.luck.picture.lib.engine.ImageEngine

class CoilEngine private constructor() : ImageEngine {


    companion object {

        fun create(): CoilEngine {
            return CoilEngine()
        }
    }

    override fun loadImage(context: Context?, url: String, imageView: ImageView?) {
        imageView?.load(url)
    }

    override fun loadImage(
        context: Context?,
        imageView: ImageView?,
        url: String?,
        maxWidth: Int,
        maxHeight: Int
    ) {
        imageView?.load(url, builder = {
            this.size(Size(maxWidth, maxHeight))
        })
    }

    override fun loadAlbumCover(context: Context?, url: String, imageView: ImageView?) {
        imageView?.load(url)
    }

    override fun loadGridImage(context: Context?, url: String, imageView: ImageView?) {
        imageView?.load(url)
    }

    override fun pauseRequests(context: Context) {
    }

    override fun resumeRequests(context: Context) {
    }
}