package com.cute.picture

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.fragment.app.Fragment
import coil.Coil
import coil.load
import coil.request.ImageRequest
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.engine.CompressFileEngine
import com.luck.picture.lib.engine.CropFileEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.language.LanguageConfig
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropImageEngine
import com.yalantis.ucrop.model.AspectRatio
import top.zibin.luban.Luban
import top.zibin.luban.OnNewCompressListener
import java.io.File


/**
 * author : mac
 * date   : 2022/1/14
 * e-mail : taolei@51cashbox.com
 */
object LocalAlbumManager {


    class Builder {

        private var activity: Activity? = null
        private var fragment: Fragment? = null
        private var mimeType: Int = SelectMimeType.ofAll()
        private var enableCrop = false
        private var enableCamera = true
        private var enableCompress = true
        private var enableZoomAnim = true
        private var maxSelectNum = 1
        private var compressQuality: Int = 80
        private var aspectRatioX: Float = 1f
        private var aspectRatioY: Float = 1f
        private var selectionModel =
            if (maxSelectNum > 1) SelectModeConfig.MULTIPLE else SelectModeConfig.SINGLE

        fun setActivity(activity: Activity): Builder {
            this.activity = activity
            return this
        }

        fun setFragment(fragment: Fragment): Builder {
            this.fragment = fragment
            return this
        }

        /**
         * 加载的数据类型
         * @param mimeType
         * PictureMimeType.ofAll()
         * PictureMimeType.ofImage()
         * PictureMimeType.ofVideo()
         */
        fun setMimeType(enum: PictureMimeEnum): Builder {
            this.mimeType = enum.value
            return this
        }

        /**
         * 是否启用裁剪
         * 默认不启用
         * @param enable 是否启用
         */
        fun setEnableCrop(enable: Boolean): Builder {
            this.enableCrop = enable
            return this
        }

        /**
         * 是否启用相机
         * 默认启用
         * @param enable 是否启用
         */
        fun setEnableCamera(enable: Boolean): Builder {
            this.enableCamera = enable
            return this
        }

        /**
         * 是否启用压缩
         * 默认启用
         * @param enable 是否启用
         */
        fun setEnableCompress(enable: Boolean): Builder {
            this.enableCompress = enable
            return this
        }

        /**
         * 是否启用查看相册的预览大图的缩放动画效果
         * 默认启用
         * @param enable 是否启用
         */
        fun setEnableZoomAnim(enable: Boolean): Builder {
            this.enableZoomAnim = enable
            return this
        }

        /**
         * 选择照片的最大数量
         */
        fun setMaxSelectNum(num: Int): Builder {
            this.maxSelectNum = num
            return this
        }

        /**
         * 压缩质量
         * @param quality 0-100
         * 默认80
         */
        fun setCompressQuality(quality: Int): Builder {
            this.compressQuality = quality
            return this
        }

        fun setAspectRatio(x: Float, y: Float): Builder {
            this.aspectRatioX = x
            this.aspectRatioY = y
            return this
        }

//        fun setMultiCropAspectRatio(x: Float, y: Float): Builder {
//            this.multiAspectRatioX = x
//            this.multiAspectRatioY = y
//            return this
//        }

        fun openGallery() {
            if (activity == null && fragment == null) return
            val selector = if (activity != null) PictureSelector.create(activity)
            else PictureSelector.create(fragment)

            val optionSelector =
                selector.openGallery(mimeType).setLanguage(LanguageConfig.SYSTEM_LANGUAGE)
                    .setImageEngine(CoilEngine.create()).setMaxSelectNum(maxSelectNum)
                    .isDisplayCamera(enableCamera).isCameraRotateImage(true)
                    .isDirectReturnSingle(true).setSelectionMode(SelectModeConfig.MULTIPLE)
                    .isSelectZoomAnim(enableZoomAnim)// 图片列表点击 缩放效果 默认true

            if (enableCompress) {
                optionSelector.setCompressEngine(compressListener)
            }
            if (enableCrop) {
                optionSelector.setCropEngine(cropListener)
            }
            optionSelector.forResult(PictureConfig.CHOOSE_REQUEST)
        }

        fun openCamera() {
            if (activity == null && fragment == null) return
            val selector = if (activity != null) PictureSelector.create(activity)
            else PictureSelector.create(fragment)
            val optionSelector =
                selector.openCamera(mimeType).setLanguage(LanguageConfig.SYSTEM_LANGUAGE)
                    .isCameraRotateImage(true)
            // Please refer to the Demo GlideEngine.java

            if (enableCompress) {
                optionSelector.setCompressEngine(compressListener)
            }
            if (enableCrop) {
                optionSelector.setCropEngine(cropListener)
            }
            optionSelector.forResultActivity(PictureConfig.REQUEST_CAMERA)
        }


        private val compressListener = CompressFileEngine { context, source, call ->
            Luban.with(context).load(source).ignoreBy(compressQuality)
                .setCompressListener(object : OnNewCompressListener {
                    override fun onStart() {}
                    override fun onSuccess(source: String?, compressFile: File) {
                        if (compressFile == null || !compressFile.exists()) {
                            call?.onCallback(source, null)
                        } else {
                            call?.onCallback(source, compressFile.absolutePath)
                        }
                    }

                    override fun onError(source: String, e: Throwable) {
                        call?.onCallback(source, source)
                    }
                }).launch()
        }

        private val cropListener = object : CropFileEngine {
            override fun onStartCrop(
                fragment: Fragment?,
                srcUri: Uri?,
                destinationUri: Uri?,
                dataSource: java.util.ArrayList<String>?,
                requestCode: Int
            ) {
                val uCrop = UCrop.of(srcUri!!, destinationUri!!, dataSource)
                uCrop.setImageEngine(object : UCropImageEngine {
                    override fun loadImage(
                        context: Context?, url: String?, imageView: ImageView?
                    ) {
                        if (context == null || imageView == null || url.isNullOrEmpty()) {
                            return
                        }
                        imageView.load(url)
                    }

                    override fun loadImage(
                        context: Context,
                        url: Uri?,
                        maxWidth: Int,
                        maxHeight: Int,
                        call: UCropImageEngine.OnCallbackListener<Bitmap>?
                    ) {
                        val request = ImageRequest.Builder(context)
                            .data(url)
                            .listener(onSuccess = { request, result ->
                                call?.onCall(drawableToBitmap(result.drawable))
                            }, onError = { request, result ->
                                call?.onCall(null)
                            }, onCancel = {
                                call?.onCall(null)
                            }, onStart = {

                            })
                            .build()
                        Coil.imageLoader(context).enqueue(request)
                    }

                })
//            selectionModel.showCropFrame(true)
//            selectionModel.showCropGrid(true)
//            selectionModel.scaleEnabled(true)
//            selectionModel.withAspectRatio(3, 4)
                val options = UCrop.Options()
                options.setShowCropGrid(true)
                options.setShowCropFrame(true)
                options.withAspectRatio(aspectRatioX, aspectRatioY)
                options.setMultipleCropAspectRatio(
                    AspectRatio("1", aspectRatioX, aspectRatioY),
                    AspectRatio("2", aspectRatioX, aspectRatioY),
                    AspectRatio("3", aspectRatioX, aspectRatioY)
                )
                uCrop.withOptions(options)
                uCrop.start(fragment!!.requireActivity(), fragment, requestCode)
            }
        }
    }

    /**
     * 解析数据
     */
    fun paresData(requestCode: Int, resultCode: Int, data: Intent?): MutableList<LocalMedia>? {
        if (requestCode != PictureConfig.CHOOSE_REQUEST && requestCode != PictureConfig.REQUEST_CAMERA) return null
        if (resultCode != Activity.RESULT_OK) return null
        return PictureSelector.obtainSelectorList(data)
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

}