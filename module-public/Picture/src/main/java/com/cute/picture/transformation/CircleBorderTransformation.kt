package com.cute.picture.transformation

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import coil.size.Size
import coil.transform.Transformation

class CircleBorderTransformation(
    private val borderColor: Int,
    private val borderWidthPx: Float
) : Transformation {

    override val cacheKey: String
        get() = "circleBorderTransformation(borderColor=$borderColor, borderWidthPx=$borderWidthPx)"

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        // 计算半径和中心点
        val output = Bitmap.createBitmap(input.width, input.height, input.config)
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.isAntiAlias = true
        // 绘制原始位图
        canvas.drawBitmap(input, 0f, 0f, paint)
        // 绘制边框
        paint.color = borderColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = borderWidthPx

        val radius = (input.width.coerceAtMost(input.height) - borderWidthPx) / 2f
        val centerX = input.width / 2f
        val centerY = input.height / 2f
        canvas.drawCircle(
            centerX,
            centerY,
            radius,
            paint
        )
        // 回收原始位图
        if (!input.isRecycled) {
            input.recycle()
        }

        return output
    }
}