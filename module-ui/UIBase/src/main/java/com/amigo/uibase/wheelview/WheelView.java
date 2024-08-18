package com.amigo.uibase.wheelview;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


import com.amigo.tool.UiExtensionKt;
import com.amigo.uibase.R;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class WheelView extends View {
    private static final String[] TIME_NUM = new String[]{"00", "01", "02", "03", "04", "05", "06", "07", "08", "09"};
    private DividerType dividerType;
    private Context context;
    private Handler handler;
    private GestureDetector gestureDetector;
    private OnItemSelectedListener onItemSelectedListener;
    private boolean isOptions;
    private boolean isCenterLabel;
    private ScheduledExecutorService mExecutor;
    private ScheduledFuture<?> mFuture;
    private Paint paintOuterText;
    private Paint paintCenterText;
    private Paint paintIndicator;
    private WheelAdapter adapter;
    private String label;
    private int textSize;
    private int maxTextWidth;
    private int maxTextHeight;
    private int textXOffset;
    private float itemHeight;
    private Typeface typeface;
    private int textColorOut;
    private int textColorCenter;
    private int dividerColor;
    private int dividerWidth;
    private float lineSpacingMultiplier;
    private boolean isLoop;
    private float firstLineY;
    private float secondLineY;
    private float centerY;
    private float totalScrollY;
    private int initPosition;
    private int selectedItem;
    private int preCurrentIndex;
    private int itemsVisible;
    private int measuredHeight;
    private int measuredWidth;
    private int radius;
    private int mOffset;
    private float previousY;
    private long startTime;
    private static final int VELOCITY_FLING = 5;
    private int widthMeasureSpec;
    private int mGravity;
    private int drawCenterContentStart;
    private int drawOutContentStart;
    private static final float SCALE_CONTENT = 0.8F;
    private float CENTER_CONTENT_OFFSET;
    private boolean isAlphaGradient;

    public WheelView(Context context) {
        this(context, (AttributeSet) null);
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.isOptions = false;
        this.isCenterLabel = true;
        this.mExecutor = Executors.newSingleThreadScheduledExecutor();
        this.typeface = Typeface.MONOSPACE;
        this.lineSpacingMultiplier = 1.6F;
        this.itemsVisible = 11;
        this.mOffset = 0;
        this.previousY = 0.0F;
        this.startTime = 0L;
        this.mGravity = 17;
        this.drawCenterContentStart = 0;
        this.drawOutContentStart = 0;
        this.isAlphaGradient = false;
        this.textSize = this.getResources().getDimensionPixelSize(R.dimen.pickerview_textsize);
        DisplayMetrics dm = this.getResources().getDisplayMetrics();
        float density = dm.density;
        if (density < 1.0F) {
            this.CENTER_CONTENT_OFFSET = 2.4F;
        } else if (1.0F <= density && density < 2.0F) {
            this.CENTER_CONTENT_OFFSET = 4.0F;
        } else if (2.0F <= density && density < 3.0F) {
            this.CENTER_CONTENT_OFFSET = 6.0F;
        } else if (density >= 3.0F) {
            this.CENTER_CONTENT_OFFSET = density * 2.5F;
        }

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.pickerview, 0, 0);
            this.mGravity = a.getInt(R.styleable.pickerview_wheelview_gravity, 17);
            this.textColorOut = a.getColor(R.styleable.pickerview_wheelview_textColorOut, -5723992);
            this.textColorCenter = a.getColor(R.styleable.pickerview_wheelview_textColorCenter, -14013910);
            this.dividerColor = a.getColor(R.styleable.pickerview_wheelview_dividerColor, -2763307);
            this.dividerWidth = a.getDimensionPixelSize(R.styleable.pickerview_wheelview_dividerWidth, 2);
            this.textSize = a.getDimensionPixelOffset(R.styleable.pickerview_wheelview_textSize, this.textSize);
            this.lineSpacingMultiplier = a.getFloat(R.styleable.pickerview_wheelview_lineSpacingMultiplier, this.lineSpacingMultiplier);
            a.recycle();
        }

        this.judgeLineSpace();
        this.initLoopView(context);
    }

    private void judgeLineSpace() {
        if (this.lineSpacingMultiplier < 1.0F) {
            this.lineSpacingMultiplier = 1.0F;
        } else if (this.lineSpacingMultiplier > 4.0F) {
            this.lineSpacingMultiplier = 4.0F;
        }

    }

    private void initLoopView(Context context) {
        this.context = context;
        this.handler = new MessageHandler(this);
        this.gestureDetector = new GestureDetector(context, new LoopViewGestureListener(this));
        this.gestureDetector.setIsLongpressEnabled(false);
        this.isLoop = true;
        this.totalScrollY = 0.0F;
        this.initPosition = -1;
        this.initPaints();
    }

    private void initPaints() {
        this.paintOuterText = new Paint();
        this.paintOuterText.setColor(this.textColorOut);
        this.paintOuterText.setAntiAlias(true);
        this.paintOuterText.setTypeface(this.typeface);
        this.paintOuterText.setTextSize((float) this.textSize);
        this.paintCenterText = new Paint();
        this.paintCenterText.setColor(this.textColorCenter);
        this.paintCenterText.setAntiAlias(true);
        this.paintCenterText.setTextScaleX(1F);
        this.paintCenterText.setTypeface(this.typeface);
        this.paintCenterText.setTextSize((float) this.textSize);
        this.paintIndicator = new Paint();
        this.paintIndicator.setColor(this.dividerColor);
        this.paintIndicator.setAntiAlias(true);
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, (Paint) null);
    }

    private void reMeasure() {
        if (this.adapter != null) {
            this.measureTextWidthHeight();
            int halfCircumference = (int) (this.itemHeight * (float) (this.itemsVisible - 1));
            this.measuredHeight = (int) ((double) (halfCircumference * 2) / 3.141592653589793D);
            this.radius = (int) ((double) halfCircumference / 3.141592653589793D);
            this.measuredWidth = MeasureSpec.getSize(this.widthMeasureSpec);
            this.firstLineY = ((float) this.measuredHeight - this.itemHeight) / 2.0F;
            this.secondLineY = ((float) this.measuredHeight + this.itemHeight) / 2.0F;
            this.centerY = this.secondLineY - (this.itemHeight - (float) this.maxTextHeight) / 2.0F - this.CENTER_CONTENT_OFFSET;
            if (this.initPosition == -1) {
                if (this.isLoop) {
                    this.initPosition = (this.adapter.getItemsCount() + 1) / 2;
                } else {
                    this.initPosition = 0;
                }
            }

            this.preCurrentIndex = this.initPosition;
        }
    }

    private void measureTextWidthHeight() {
        Rect rect = new Rect();

        for (int i = 0; i < this.adapter.getItemsCount(); ++i) {
            String s1 = this.getContentText(this.adapter.getItem(i));
            this.paintCenterText.getTextBounds(s1, 0, s1.length(), rect);
            int textWidth = rect.width();
            if (textWidth > this.maxTextWidth) {
                this.maxTextWidth = textWidth;
            }
        }

        this.paintCenterText.getTextBounds("星期", 0, 2, rect);
        this.maxTextHeight = rect.height();
        this.itemHeight = this.lineSpacingMultiplier * (float) this.maxTextHeight;
    }

    public void smoothScroll(ACTION action) {
        this.cancelFuture();
        if (action == ACTION.FLING || action == ACTION.DAGGLE) {
            this.mOffset = (int) ((this.totalScrollY % this.itemHeight + this.itemHeight) % this.itemHeight);
            if ((float) this.mOffset > this.itemHeight / 2.0F) {
                this.mOffset = (int) (this.itemHeight - (float) this.mOffset);
            } else {
                this.mOffset = -this.mOffset;
            }
        }

        this.mFuture = this.mExecutor.scheduleWithFixedDelay(new SmoothScrollTimerTask(this, this.mOffset), 0L, 10L, TimeUnit.MILLISECONDS);
    }

    public final void scrollBy(float velocityY) {
        this.cancelFuture();
        this.mFuture = this.mExecutor.scheduleWithFixedDelay(new InertiaTimerTask(this, velocityY), 0L, 5L, TimeUnit.MILLISECONDS);
    }

    public void cancelFuture() {
        if (this.mFuture != null && !this.mFuture.isCancelled()) {
            this.mFuture.cancel(true);
            this.mFuture = null;
        }

    }

    public final void setCyclic(boolean cyclic) {
        this.isLoop = cyclic;
    }

    public final void setTypeface(Typeface font) {
        this.typeface = font;
        this.paintOuterText.setTypeface(this.typeface);
        this.paintCenterText.setTypeface(this.typeface);
    }

    public final void setTextSize(float size) {
        if (size > 0.0F) {
            this.textSize = (int) (this.context.getResources().getDisplayMetrics().density * size);
            this.paintOuterText.setTextSize((float) this.textSize);
            this.paintCenterText.setTextSize((float) this.textSize);
        }

    }

    public final void setCurrentItem(int currentItem) {
        this.selectedItem = currentItem;
        this.initPosition = currentItem;
        this.totalScrollY = 0.0F;
        this.invalidate();
    }

    public final void setOnItemSelectedListener(OnItemSelectedListener OnItemSelectedListener) {
        this.onItemSelectedListener = OnItemSelectedListener;
    }

    public final void setAdapter(WheelAdapter adapter) {
        this.adapter = adapter;
        this.reMeasure();
        this.invalidate();
    }

    public void setItemsVisibleCount(int visibleCount) {
        if (visibleCount % 2 == 0) {
            ++visibleCount;
        }

        this.itemsVisible = visibleCount + 2;
    }

    public void setAlphaGradient(boolean alphaGradient) {
        this.isAlphaGradient = alphaGradient;
    }

    public final WheelAdapter getAdapter() {
        return this.adapter;
    }

    public final int getCurrentItem() {
        if (this.adapter == null) {
            return 0;
        } else {
            return !this.isLoop || this.selectedItem >= 0 && this.selectedItem < this.adapter.getItemsCount() ? Math.max(0, Math.min(this.selectedItem, this.adapter.getItemsCount() - 1)) : Math.max(0, Math.min(Math.abs(Math.abs(this.selectedItem) - this.adapter.getItemsCount()), this.adapter.getItemsCount() - 1));
        }
    }

    public final void onItemSelected() {
        if (this.onItemSelectedListener != null) {
            this.postDelayed(new Runnable() {
                public void run() {
                    WheelView.this.onItemSelectedListener.onItemSelected(WheelView.this.getCurrentItem());
                }
            }, 200L);
        }

    }

    protected void onDraw(Canvas canvas) {
        if (this.adapter != null) {
            this.initPosition = Math.min(Math.max(0, this.initPosition), this.adapter.getItemsCount() - 1);
            int change = (int) (this.totalScrollY / this.itemHeight);

            try {
                this.preCurrentIndex = this.initPosition + change % this.adapter.getItemsCount();
            } catch (ArithmeticException var14) {
                Log.e("WheelView", "出错了！adapter.getItemsCount() == 0，联动数据不匹配");
            }

            if (!this.isLoop) {
                if (this.preCurrentIndex < 0) {
                    this.preCurrentIndex = 0;
                }

                if (this.preCurrentIndex > this.adapter.getItemsCount() - 1) {
                    this.preCurrentIndex = this.adapter.getItemsCount() - 1;
                }
            } else {
                if (this.preCurrentIndex < 0) {
                    this.preCurrentIndex += this.adapter.getItemsCount();
                }

                if (this.preCurrentIndex > this.adapter.getItemsCount() - 1) {
                    this.preCurrentIndex -= this.adapter.getItemsCount();
                }
            }

            float itemHeightOffset = this.totalScrollY % this.itemHeight;
            float startX;
            float endX;
            if (this.dividerType == DividerType.WRAP) {
                if (TextUtils.isEmpty(this.label)) {
                    startX = (float) ((this.measuredWidth - this.maxTextWidth) / 2 - 12);
                } else {
                    startX = (float) ((this.measuredWidth - this.maxTextWidth) / 4 - 12);
                }

                if (startX <= 0.0F) {
                    startX = 10.0F;
                }

                endX = (float) this.measuredWidth - startX;
                canvas.drawLine(startX, this.firstLineY, endX, this.firstLineY, this.paintIndicator);
                canvas.drawLine(startX, this.secondLineY, endX, this.secondLineY, this.paintIndicator);
            } else if (this.dividerType == DividerType.CIRCLE) {
                this.paintIndicator.setStyle(Style.STROKE);
                this.paintIndicator.setStrokeWidth((float) this.dividerWidth);
                if (TextUtils.isEmpty(this.label)) {
                    startX = (float) (this.measuredWidth - this.maxTextWidth) / 2.0F - 12.0F;
                } else {
                    startX = (float) (this.measuredWidth - this.maxTextWidth) / 4.0F - 12.0F;
                }

                if (startX <= 0.0F) {
                    startX = 10.0F;
                }

                endX = (float) this.measuredWidth - startX;
                float radius = Math.max(endX - startX, this.itemHeight) / 1.8F;
                canvas.drawCircle((float) this.measuredWidth / 2.0F, (float) this.measuredHeight / 2.0F, radius, this.paintIndicator);
            } else if (this.dividerType == DividerType.FILL) {
                canvas.drawLine(0.0F, this.firstLineY, (float) this.measuredWidth, this.firstLineY, this.paintIndicator);
                canvas.drawLine(0.0F, this.secondLineY, (float) this.measuredWidth, this.secondLineY, this.paintIndicator);
            } else {
                int size = UiExtensionKt.dpToPx(10,context);
                int end = this.measuredWidth - size;
                RectF rect = new RectF(size, (int) firstLineY, end, (int) secondLineY);
                canvas.drawRoundRect(rect, 10, 10, this.paintIndicator);
            }

            int counter;
            if (!TextUtils.isEmpty(this.label) && this.isCenterLabel) {
                counter = this.measuredWidth - this.getTextWidth(this.paintCenterText, this.label);
                canvas.drawText(this.label, (float) counter - this.CENTER_CONTENT_OFFSET, this.centerY, this.paintCenterText);
            }

            for (counter = 0; counter < this.itemsVisible; ++counter) {
                int index = this.preCurrentIndex - (this.itemsVisible / 2 - counter);
                Object showText;
                if (this.isLoop) {
                    index = this.getLoopMappingIndex(index);
                    showText = this.adapter.getItem(index);
                } else if (index < 0) {
                    showText = "";
                } else if (index > this.adapter.getItemsCount() - 1) {
                    showText = "";
                } else {
                    showText = this.adapter.getItem(index);
                }

                canvas.save();
                double radian = (double) ((this.itemHeight * (float) counter - itemHeightOffset) / (float) this.radius);
                float angle = (float) (90.0D - radian / 3.141592653589793D * 180.0D);
                if (angle <= 80.0F && angle >= -80.0F) {
                    String contentText;
                    if (!this.isCenterLabel && !TextUtils.isEmpty(this.label) && !TextUtils.isEmpty(this.getContentText(showText))) {
                        contentText = this.getContentText(showText) + this.label;
                    } else {
                        contentText = this.getContentText(showText);
                    }

                    float offsetCoefficient = (float) Math.pow((double) (Math.abs(angle) / 90.0F), 2.2D);
                    this.reMeasureTextSize(contentText);
                    this.measuredCenterContentStart(contentText);
                    this.measuredOutContentStart(contentText);
                    float translateY = (float) ((double) this.radius - Math.cos(radian) * (double) this.radius - Math.sin(radian) * (double) this.maxTextHeight / 2.0D);
                    canvas.translate(0.0F, translateY);
                    if (translateY <= this.firstLineY && (float) this.maxTextHeight + translateY >= this.firstLineY) {
                        canvas.save();
                        canvas.clipRect(0.0F, 0.0F, (float) this.measuredWidth, this.firstLineY - translateY);
                        canvas.scale(1.0F, (float) Math.sin(radian) * 0.8F);
                        this.setOutPaintStyle(offsetCoefficient, angle);
                        canvas.drawText(contentText, (float) this.drawOutContentStart, (float) this.maxTextHeight, this.paintOuterText);
                        canvas.restore();
                        canvas.save();
                        canvas.clipRect(0.0F, this.firstLineY - translateY, (float) this.measuredWidth, (float) ((int) this.itemHeight));
                        canvas.scale(1.0F, (float) Math.sin(radian) * 1.0F);
                        canvas.drawText(contentText, (float) this.drawCenterContentStart, (float) this.maxTextHeight - this.CENTER_CONTENT_OFFSET, this.paintCenterText);
                        canvas.restore();
                    } else if (translateY <= this.secondLineY && (float) this.maxTextHeight + translateY >= this.secondLineY) {
                        canvas.save();
                        canvas.clipRect(0.0F, 0.0F, (float) this.measuredWidth, this.secondLineY - translateY);
                        canvas.scale(1.0F, (float) Math.sin(radian) * 1.0F);
                        canvas.drawText(contentText, (float) this.drawCenterContentStart, (float) this.maxTextHeight - this.CENTER_CONTENT_OFFSET, this.paintCenterText);
                        canvas.restore();
                        canvas.save();
                        canvas.clipRect(0.0F, this.secondLineY - translateY, (float) this.measuredWidth, (float) ((int) this.itemHeight));
                        canvas.scale(1.0F, (float) Math.sin(radian) * 0.8F);
                        this.setOutPaintStyle(offsetCoefficient, angle);
                        canvas.drawText(contentText, (float) this.drawOutContentStart, (float) this.maxTextHeight, this.paintOuterText);
                        canvas.restore();
                    } else if (translateY >= this.firstLineY && (float) this.maxTextHeight + translateY <= this.secondLineY) {
                        float Y = (float) this.maxTextHeight - this.CENTER_CONTENT_OFFSET;
                        canvas.drawText(contentText, (float) this.drawCenterContentStart, Y, this.paintCenterText);
                        this.selectedItem = this.preCurrentIndex - (this.itemsVisible / 2 - counter);
                    } else {
                        canvas.save();
                        canvas.clipRect(0, 0, this.measuredWidth, (int) this.itemHeight);
                        this.paintOuterText.setTextSize(this.textSize - 10);
                        canvas.drawText(contentText, (float) this.drawOutContentStart + 10 + (float) this.textXOffset * offsetCoefficient, (float) this.maxTextHeight, this.paintOuterText);
                        canvas.restore();
                    }

                    canvas.restore();
                    this.paintCenterText.setTextSize((float) this.textSize);
                } else {
                    canvas.restore();
                }
            }

        }
    }

    private void setOutPaintStyle(float offsetCoefficient, float angle) {
        float DEFAULT_TEXT_TARGET_SKEW_X = 0.5F;
        int multiplier = 0;
        if (this.textXOffset > 0) {
            multiplier = 1;
        } else if (this.textXOffset < 0) {
            multiplier = -1;
        }

        this.paintOuterText.setTextSkewX((float) (multiplier * (angle > 0.0F ? -1 : 1)) * DEFAULT_TEXT_TARGET_SKEW_X * offsetCoefficient);
        int alpha = this.isAlphaGradient ? (int) ((90.0F - Math.abs(angle)) / 90.0F * 255.0F) : 255;
        this.paintOuterText.setAlpha(alpha);
    }

    private void reMeasureTextSize(String contentText) {
        Rect rect = new Rect();
        this.paintCenterText.getTextBounds(contentText, 0, contentText.length(), rect);
        int width = rect.width();

        int size;
        for (size = this.textSize; width > this.measuredWidth; width = rect.width()) {
            --size;
            this.paintCenterText.setTextSize((float) size);
            this.paintCenterText.getTextBounds(contentText, 0, contentText.length(), rect);
        }

        this.paintOuterText.setTextSize((float) size);
    }

    private int getLoopMappingIndex(int index) {
        if (index < 0) {
            index += this.adapter.getItemsCount();
            index = this.getLoopMappingIndex(index);
        } else if (index > this.adapter.getItemsCount() - 1) {
            index -= this.adapter.getItemsCount();
            index = this.getLoopMappingIndex(index);
        }

        return index;
    }

    private String getContentText(Object item) {
        if (item == null) {
            return "";
        } else if (item instanceof IPickerViewData) {
            return ((IPickerViewData) item).getPickerViewText();
        } else {
            return item instanceof Integer ? this.getFixNum((Integer) item) : item.toString();
        }
    }

    private String getFixNum(int timeNum) {
        return timeNum >= 0 && timeNum < 10 ? TIME_NUM[timeNum] : String.valueOf(timeNum);
    }

    private void measuredCenterContentStart(String content) {
        Rect rect = new Rect();
        this.paintCenterText.getTextBounds(content, 0, content.length(), rect);
        switch (this.mGravity) {
            case 3:
                this.drawCenterContentStart = 0;
                break;
            case 5:
                this.drawCenterContentStart = this.measuredWidth - rect.width() - (int) this.CENTER_CONTENT_OFFSET;
                break;
            case 17:
                if (!this.isOptions && this.label != null && !this.label.equals("") && this.isCenterLabel) {
                    this.drawCenterContentStart = (int) ((double) (this.measuredWidth - rect.width()) * 0.25D);
                } else {
                    this.drawCenterContentStart = (int) ((double) (this.measuredWidth - rect.width()) * 0.5D);
                }
        }

    }

    private void measuredOutContentStart(String content) {
        Rect rect = new Rect();
        this.paintOuterText.getTextBounds(content, 0, content.length(), rect);
        switch (this.mGravity) {
            case 3:
                this.drawOutContentStart = 0;
                break;
            case 5:
                this.drawOutContentStart = this.measuredWidth - rect.width() - (int) this.CENTER_CONTENT_OFFSET;
                break;
            case 17:
                if (!this.isOptions && this.label != null && !this.label.equals("") && this.isCenterLabel) {
                    this.drawOutContentStart = (int) ((double) (this.measuredWidth - rect.width()) * 0.25D);
                } else {
                    this.drawOutContentStart = (int) ((double) (this.measuredWidth - rect.width()) * 0.5D);
                }
        }

    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.widthMeasureSpec = widthMeasureSpec;
        this.reMeasure();
        this.setMeasuredDimension(this.measuredWidth, this.measuredHeight);
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean eventConsumed = this.gestureDetector.onTouchEvent(event);
        boolean isIgnore = false;
        float top = (float) (-this.initPosition) * this.itemHeight;
        float bottom = (float) (this.adapter.getItemsCount() - 1 - this.initPosition) * this.itemHeight;
        float ratio = 0.25F;
        switch (event.getAction()) {
            case 0:
                this.startTime = System.currentTimeMillis();
                this.cancelFuture();
                this.previousY = event.getRawY();
                break;
            case 1:
            default:
                if (!eventConsumed) {
                    float y = event.getY();
                    double L = Math.acos((double) (((float) this.radius - y) / (float) this.radius)) * (double) this.radius;
                    int circlePosition = (int) ((L + (double) (this.itemHeight / 2.0F)) / (double) this.itemHeight);
                    float extraOffset = (this.totalScrollY % this.itemHeight + this.itemHeight) % this.itemHeight;
                    this.mOffset = (int) ((float) (circlePosition - this.itemsVisible / 2) * this.itemHeight - extraOffset);
                    if (System.currentTimeMillis() - this.startTime > 120L) {
                        this.smoothScroll(ACTION.DAGGLE);
                    } else {
                        this.smoothScroll(ACTION.CLICK);
                    }
                }
                break;
            case 2:
                float dy = this.previousY - event.getRawY();
                this.previousY = event.getRawY();
                this.totalScrollY += dy;
                if (!this.isLoop) {
                    if ((this.totalScrollY - this.itemHeight * ratio >= top || dy >= 0.0F) && (this.totalScrollY + this.itemHeight * ratio <= bottom || dy <= 0.0F)) {
                        isIgnore = false;
                    } else {
                        this.totalScrollY -= dy;
                        isIgnore = true;
                    }
                }
        }

        if (!isIgnore && event.getAction() != 0) {
            this.invalidate();
        }

        return true;
    }

    public int getItemsCount() {
        return this.adapter != null ? this.adapter.getItemsCount() : 0;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void isCenterLabel(boolean isCenterLabel) {
        this.isCenterLabel = isCenterLabel;
    }

    public void setGravity(int gravity) {
        this.mGravity = gravity;
    }

    public int getTextWidth(Paint paint, String str) {
        int iRet = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);

            for (int j = 0; j < len; ++j) {
                iRet += (int) Math.ceil((double) widths[j]);
            }
        }

        return iRet;
    }

    public void setIsOptions(boolean options) {
        this.isOptions = options;
    }

    public void setTextColorOut(int textColorOut) {
        this.textColorOut = textColorOut;
        this.paintOuterText.setColor(this.textColorOut);
    }

    public void setTextColorCenter(int textColorCenter) {
        this.textColorCenter = textColorCenter;
        this.paintCenterText.setColor(this.textColorCenter);
    }

    public void setTextXOffset(int textXOffset) {
        this.textXOffset = textXOffset;
        if (textXOffset != 0) {
            this.paintCenterText.setTextScaleX(1.0F);
        }

    }

    public void setDividerWidth(int dividerWidth) {
        this.dividerWidth = dividerWidth;
        this.paintIndicator.setStrokeWidth((float) dividerWidth);
    }

    public void setDividerColor(int dividerColor) {
        this.dividerColor = dividerColor;
        this.paintIndicator.setColor(dividerColor);
    }

    public void setDividerType(DividerType dividerType) {
        this.dividerType = dividerType;
    }

    public void setLineSpacingMultiplier(float lineSpacingMultiplier) {
        if (lineSpacingMultiplier != 0.0F) {
            this.lineSpacingMultiplier = lineSpacingMultiplier;
            this.judgeLineSpace();
        }

    }

    public boolean isLoop() {
        return this.isLoop;
    }

    public float getTotalScrollY() {
        return this.totalScrollY;
    }

    public void setTotalScrollY(float totalScrollY) {
        this.totalScrollY = totalScrollY;
    }

    public float getItemHeight() {
        return this.itemHeight;
    }

    public int getInitPosition() {
        return this.initPosition;
    }

    public Handler getHandler() {
        return this.handler;
    }

    public static enum DividerType {
        FILL,
        WRAP,
        CIRCLE,
        FILL_INSIDE;

        private DividerType() {
        }
    }

    public static enum ACTION {
        CLICK,
        FLING,
        DAGGLE;

        private ACTION() {
        }
    }
}
