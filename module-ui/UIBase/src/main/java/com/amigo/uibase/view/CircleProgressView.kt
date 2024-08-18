package com.amigo.uibase.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.amigo.uibase.R

/**
 * 圆形进度条控件
 */
class CircleProgressView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val _paint: Paint
    private val _rectF: RectF
    private val _rect: Rect
    private var _current = 0
    private var _max = 100

    private var progressBackgroundColor = Color.GRAY
    private var progressColor = Color.YELLOW

    //圆弧（也可以说是圆环）的宽度
    private var _arcWidth = 4f
    private var _bgArcWidth = 4f

    //控件的宽度
    private var _width = 0f

    init {
        _paint = Paint()
        _paint.isAntiAlias = true
        _rectF = RectF()
        _rect = Rect()
        initAttrs(attrs)
    }

    private fun initAttrs(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar)
        progressColor =
            typedArray.getColor(R.styleable.CircleProgressBar_progressColor, Color.YELLOW)
        progressBackgroundColor =
            typedArray.getColor(R.styleable.CircleProgressBar_progressBackgroundColor, Color.GRAY)
        _arcWidth = typedArray.getDimension(R.styleable.CircleProgressBar_arcWidth, 4f)
        _bgArcWidth = typedArray.getDimension(R.styleable.CircleProgressBar_bgArcWidth, 4f)
        typedArray.recycle()
    }

    fun setCurrent(current: Int) {
        this._current = current
        invalidate()
    }

    fun setMax(max: Int) {
        this._max = max
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //getMeasuredWidth获取的是view的原始大小，也就是xml中配置或者代码中设置的大小
        //getWidth获取的是view最终显示的大小，这个大小不一定等于原始大小
        _width = measuredWidth.toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //绘制圆形
        //设置为空心圆，如果不理解绘制弧线是什么意思就把这里的属性改为“填充”，跑一下瞬间就明白了
        _paint.style = Paint.Style.STROKE
        //设置圆弧的宽度（圆环的宽度）
        _paint.strokeWidth = _arcWidth
        _paint.color = progressBackgroundColor
        //大圆的半径
        val bigCircleRadius = _width / 2
        //小圆的半径
        val smallCircleRadius = bigCircleRadius - _arcWidth
        //绘制小圆
        canvas.drawCircle(bigCircleRadius, bigCircleRadius, smallCircleRadius, _paint)
        _paint.color = progressColor
        _rectF[_arcWidth, _arcWidth, _width - _arcWidth] = _width - _arcWidth
        //绘制圆弧
        canvas.drawArc(_rectF, 90f, (_current * 360 / _max).toFloat(), false, _paint)
//        //计算百分比
//        val txt = (_current * 100 / _max).toString() + "%"
//        _paint.strokeWidth = 0f
//        _paint.textSize = 40f
//        _paint.getTextBounds(txt, 0, txt.length, _rect)
//        _paint.color = Color.GREEN
//        //绘制百分比
//        canvas.drawText(
//            txt,
//            bigCircleRadius - _rect.width() / 2,
//            bigCircleRadius + _rect.height() / 2,
//            _paint
//        )
    }

}