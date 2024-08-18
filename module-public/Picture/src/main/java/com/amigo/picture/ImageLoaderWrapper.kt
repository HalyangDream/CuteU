package com.amigo.picture

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter.Blur
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import coil.Coil
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.VideoFrameDecoder
import coil.disk.DiskCache
import coil.executeBlocking
import coil.imageLoader
import coil.load
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.request.Parameters
import coil.request.SuccessResult
import coil.request.animatedTransformation
import coil.request.onAnimationStart
import coil.request.repeatCount
import coil.request.videoFrameMillis
import coil.size.Scale
import coil.size.Size
import coil.transform.AnimatedTransformation
import coil.transform.CircleCropTransformation
import coil.transform.PixelOpacity
import coil.transform.RoundedCornersTransformation
import coil.transform.Transformation
import coil.util.CoilUtils
import coil.util.DebugLogger
import com.amigo.picture.transformation.BlurTransformation
import com.amigo.picture.transformation.BorderTransformation
import com.amigo.picture.transformation.CircleBorderTransformation
import com.amigo.picture.transformation.RoundRadius
import java.time.Duration

object ImageLoaderWrapper {

    fun initialize(context: Context) {
        val imageLoader = ImageLoader.Builder(context).diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED).components {
                add(VideoFrameDecoder.Factory())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }.allowRgb565(true).logger(DebugLogger()).build()
        Coil.setImageLoader(imageLoader)
    }

}

fun ImageView.loadImage(
    res: Any,
    @DrawableRes placeholderRes: Int? = null,
    @DrawableRes errorRes: Int? = null,
    width: Int? = null,
    height: Int? = null
) {
    loadImage(res, placeholderRes, errorRes, width = width, height = height, roundRadius = null)
}

fun ImageView.loadCrossFade(
    res: Any,
    durationMillis: Int = 500,
    @DrawableRes placeholderRes: Int? = null,
    @DrawableRes errorRes: Int? = null,
) {

    load(res, builder = {
        crossfade(durationMillis)
        if (placeholderRes != null) {
            this.placeholder(placeholderRes)
        }
        if (errorRes != null) {
            this.error(errorRes)
        }
    })
}

fun ImageView.loadImage(
    res: Any,
    @DrawableRes placeholderRes: Int? = null,
    @DrawableRes errorRes: Int? = null,
    width: Int? = null,
    height: Int? = null,
    @ColorRes borderColor: Int = android.R.color.transparent,
    borderWidthPx: Int = 0,
    roundedCorners: Float = 0f,
    blurTransformation: BlurTransformation? = null
) {
    loadImage(
        res = res,
        placeholderRes = placeholderRes,
        errorRes = errorRes,
        width = width, height = height,
        borderColor = borderColor,
        borderWidthPx = borderWidthPx,
        roundRadius = if (roundedCorners > 0) RoundRadius(
            roundedCorners, roundedCorners, roundedCorners, roundedCorners
        ) else null,
        blurTransformation = blurTransformation
    )
}

fun ImageView.loadImage(
    res: Any,
    @DrawableRes placeholderRes: Int? = null,
    @DrawableRes errorRes: Int? = null,
    width: Int? = null,
    height: Int? = null,
    @ColorRes borderColor: Int = android.R.color.transparent,
    borderWidthPx: Int = 0,
    roundRadius: RoundRadius? = null,
    blurTransformation: BlurTransformation? = null
) {
    this.load(res, builder = {
        crossfade(300)
        if (width != null && height != null) {
            this.size(Size(width, height))
        }
        if (placeholderRes != null) {
            this.placeholder(placeholderRes)
        }
        if (errorRes != null) {
            this.error(errorRes)
        }

        val list = mutableListOf<Transformation>()
        if (blurTransformation != null) {
            list.add(blurTransformation)
        }
        if (roundRadius != null) {
            list.add(
                RoundedCornersTransformation(
                    topLeft = roundRadius.topLeft,
                    topRight = roundRadius.topRight,
                    bottomLeft = roundRadius.bottomLeft,
                    bottomRight = roundRadius.bottomRight
                )
            )
        }
        if (borderWidthPx > 0) {
            val newRadius = roundRadius ?: RoundRadius()
            val borderTransformation = BorderTransformation(
                ContextCompat.getColor(context, borderColor), borderWidthPx.toFloat(), newRadius
            )
            list.add(borderTransformation)
        }
        this.transformations(list)
    }, imageLoader = this@loadImage.context.imageLoader)
}

fun ImageView.loadImage(
    res: Any,
    @DrawableRes placeholderRes: Int? = null,
    @DrawableRes errorRes: Int? = null,
    placeholderDrawable: Drawable? = null,
    width: Int? = null,
    height: Int? = null,
    @ColorRes borderColor: Int = android.R.color.transparent,
    borderWidthPx: Int = 0,
    isCircle: Boolean = false,
    blurTransformation: BlurTransformation? = null
) {
    this.load(res, builder = {
        crossfade(300)
        if (width != null && height != null) {
            this.size(Size(width, height))
            this.scale(Scale.FILL)
        }
        if (placeholderRes != null) {
            this.placeholder(placeholderRes)
        }
        if (placeholderDrawable != null) {
            this.placeholder(placeholderDrawable)
        }
        if (errorRes != null) {
            this.error(errorRes)
        }

        val list = mutableListOf<Transformation>()
        if (blurTransformation != null) {
            list.add(blurTransformation)
        }
        if (isCircle) {
            list.add(CircleCropTransformation())
        }
        if (borderWidthPx > 0) {
            val transformation = if (isCircle) CircleBorderTransformation(
                ContextCompat.getColor(context, borderColor), borderWidthPx.toFloat()
            ) else {
                BorderTransformation(
                    ContextCompat.getColor(context, borderColor),
                    borderWidthPx.toFloat(),
                    RoundRadius()
                )
            }
            list.add(transformation)
        }
        this.transformations(list)

    }, imageLoader = this@loadImage.context.imageLoader)
}

fun ImageView.loadDrawable(@DrawableRes drawableRes: Int) {
    val drawable = context.getDrawable(drawableRes)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && drawable is AnimatedImageDrawable) {
        drawable.start()
    }
    setImageDrawable(drawable)
}

fun ImageView.loadVideo(
    videoUrl: String,
    frameMillis: Long = 1000,
    roundedCorners: Float = 0f,
    @DrawableRes placeholderRes: Int? = null,
    @DrawableRes errorRes: Int? = null,
    blurTransformation: BlurTransformation? = null
) {
    this.load(videoUrl, builder = {
        decoderFactory { result, options, _ -> VideoFrameDecoder(result.source, options) }
        if (frameMillis > 0) {
            videoFrameMillis(frameMillis)
        }
        if (placeholderRes != null) {
            this.placeholder(placeholderRes)
        }
        if (errorRes != null) {
            this.error(errorRes)
        }
        val list = mutableListOf<Transformation>()
        if (blurTransformation != null) {
            list.add(blurTransformation)
        }
        if (roundedCorners > 0f) {
            list.add(RoundedCornersTransformation(roundedCorners))
        }
        this.transformations(list)
    }, imageLoader = this@loadVideo.context.imageLoader)
}

fun getBitmapFromMemory(context: Context, url: Any, width: Int, height: Int): Bitmap? {
    val request = ImageRequest.Builder(context).size(Size(width, height)).data(url)
        .diskCachePolicy(CachePolicy.ENABLED).memoryCachePolicy(CachePolicy.ENABLED)
        .networkCachePolicy(CachePolicy.DISABLED).allowConversionToBitmap(true).build()
    val result = Coil.imageLoader(context).executeBlocking(request)
    if (result is SuccessResult) {
        return (result.drawable as BitmapDrawable).bitmap
    }
    return null
}




