package com.cute.picture.transformation

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import coil.size.Size
import coil.transform.Transformation

class BorderTransformation(
    private val borderColor: Int,
    private val borderWidthPx: Float,
    private val cornerRadius: RoundRadius // 添加一个圆角的参数
) : Transformation {
    override val cacheKey: String
        get() = "borderTransformation(borderColor=$borderColor, borderWidthPx=$borderWidthPx, cornerRadius=$cornerRadius)"

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        val output = Bitmap.createBitmap(input.width, input.height, input.config)
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
        paint.style = Paint.Style.FILL

        // 绘制原始位图
        canvas.drawBitmap(input, 0f, 0f, paint)

        // 绘制边框
        paint.color = borderColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = borderWidthPx

        // Create a path with different corner radii
        val path = Path()
        val radii = floatArrayOf(
            cornerRadius.topLeft,  cornerRadius.topLeft,
            cornerRadius.topRight,  cornerRadius.topRight,
            cornerRadius.bottomRight,  cornerRadius.bottomRight,
            cornerRadius.bottomLeft,  cornerRadius.bottomLeft
        )

        path.addRoundRect(
            RectF(
                borderWidthPx / 2,
                borderWidthPx / 2,
                input.width - borderWidthPx / 2,
                input.height - borderWidthPx / 2
            ),
            radii,
            Path.Direction.CW
        )

        // Draw the border
        canvas.drawPath(path, paint)

        // Recycle the original bitmap
        if (!input.isRecycled) {
            input.recycle()
        }

        return output
    }
}