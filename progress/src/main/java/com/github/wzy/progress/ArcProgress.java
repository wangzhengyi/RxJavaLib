package com.github.wzy.progress;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

@SuppressWarnings("unused")
public class ArcProgress extends View {
    private static final String TAG = ArcProgress.class.getSimpleName();

    private static final String INSTANCE_STATE = "saved_instance";
    private static final String INSTANCE_ARC_ANGLE = "arc_angle";
    private static final String INSTANCE_STROKE_WIDTH = "stroke_width";
    private static final String INSTANCE_ARC_PROGRESS = "arc_progress";
    private static final String INSTANCE_ARC_MAX = "arc_max";
    private static final String INSTANCE_ARC_FINISHED_COLOR = "arc_finished_color";
    private static final String INSTANCE_ARC_UNFINISHED_COLOR = "arc_unfinished_color";
    private static final String INSTANCE_ARC_TEXT_SIZE = "arc_text_size";
    private static final String INSTANCE_ARC_TEXT_COLOR = "arc_text_color";
    private static final String INSTANCE_SUFFIX_TEXT = "suffix_text";
    private static final String INSTANCE_SUFFIX_TEXT_SIZE = "suffix_text_size";
    private static final String INSTANCE_SUFFIX_TEXT_PADDING = "suffix_text_padding";
    private static final String INSTANCE_BOTTOM_TEXT = "bottom_text";
    private static final String INSTANCE_BOTTOM_TEXT_SIZE = "bottom_text_size";

    private static final int DEFAULT_FINISHED_COLOR = Color.WHITE;
    private static final int DEFAULT_UNFINISHED_COLOR = Color.rgb(72, 106, 176);
    private static final int DEFAULT_TEXT_COLOR = Color.rgb(66, 145, 241);
    private static final int DEFAULT_MAX = 100;
    private static final float DEFAULT_ARC_ANGLE = 360 * 0.8f;
    private static final String DEFAULT_SUFFIX_TEXT = "%";

    private final float DEFAULT_TEXT_SIZE;
    private final float DEFAULT_SUFFIX_TEXT_SIZE;
    private final float DEFAULT_SUFFIX_PADDING;
    private final float DEFAULT_BOTTOM_TEXT_SIZE;
    private final float DEFAULT_STROKE_WIDTH;
    private final int DEFAULT_WIDTH;
    private final int DEFAULT_HEIGHT;

    private int mArcProgress;
    private float mArcAngle;
    private float mArcStrokeWidth;
    private int mArcMax;
    private int mArcUnfinishedColor;
    private int mArcFinishedColor;
    private float mArcTextSize;
    private int mArcTextColor;
    private float mArcSuffixTextSize;
    private float mArcSuffixTextPadding;
    private float mArcBottomTextSize;
    private String mArcBottomText;
    private String mArcSuffixText;

    private Paint mPaint;
    private Paint mTextPaint;
    private RectF mRectF = new RectF();

    public static float sp2px(Resources resources, float sp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, sp, resources.getDisplayMetrics());
    }

    public static float dp2px(Resources resources, float dp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
    }

    public ArcProgress(Context context) {
        this(context, null);
    }

    public ArcProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcProgress(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        DEFAULT_TEXT_SIZE = sp2px(getResources(), 40);
        DEFAULT_SUFFIX_TEXT_SIZE = sp2px(getResources(), 15);
        DEFAULT_BOTTOM_TEXT_SIZE = sp2px(getResources(), 10);
        DEFAULT_STROKE_WIDTH = dp2px(getResources(), 4);
        DEFAULT_SUFFIX_PADDING = dp2px(getResources(), 4);
        DEFAULT_WIDTH = (int) dp2px(getResources(), 100);
        DEFAULT_HEIGHT = (int) dp2px(getResources(), 100);

        final TypedArray ta = context.obtainStyledAttributes(
                attrs, R.styleable.ArcProgress, defStyle, 0);
        initAttributes(ta);
        ta.recycle();

        initPaint();
    }

    private void initAttributes(TypedArray ta) {
        mArcFinishedColor = ta.getColor(R.styleable.ArcProgress_arc_finished_color,
                DEFAULT_FINISHED_COLOR);
        mArcUnfinishedColor = ta.getColor(R.styleable.ArcProgress_arc_unfinished_color,
                DEFAULT_UNFINISHED_COLOR);
        mArcMax = ta.getInteger(R.styleable.ArcProgress_arc_max, DEFAULT_MAX);
        mArcProgress = ta.getInteger(R.styleable.ArcProgress_arc_progress, 0);
        mArcAngle = ta.getFloat(R.styleable.ArcProgress_arc_angle, DEFAULT_ARC_ANGLE);
        if (mArcAngle >= 360) {
            mArcAngle = DEFAULT_ARC_ANGLE;
        }
        mArcStrokeWidth = ta.getDimension(R.styleable.ArcProgress_arc_stroke_width,
                DEFAULT_STROKE_WIDTH);
        mArcTextSize = ta.getDimension(R.styleable.ArcProgress_arc_text_size, DEFAULT_TEXT_SIZE);
        mArcTextColor = ta.getColor(R.styleable.ArcProgress_arc_text_color, DEFAULT_TEXT_COLOR);
        mArcSuffixTextSize = ta.getDimension(R.styleable.ArcProgress_arc_suffix_text_size,
                DEFAULT_SUFFIX_TEXT_SIZE);
        mArcSuffixTextPadding = ta.getDimension(R.styleable.ArcProgress_arc_suffix_text_padding,
                DEFAULT_SUFFIX_PADDING);
        mArcBottomTextSize = ta.getDimension(R.styleable.ArcProgress_arc_bottom_text_size,
                DEFAULT_BOTTOM_TEXT_SIZE);
        mArcBottomText = ta.getString(R.styleable.ArcProgress_arc_bottom_text);
        mArcSuffixText = ta.getString(R.styleable.ArcProgress_arc_suffix_text);
        if (TextUtils.isEmpty(mArcSuffixText)) {
            mArcSuffixText = DEFAULT_SUFFIX_TEXT;
        }
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setDither(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = DEFAULT_WIDTH;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            if (widthMode == MeasureSpec.AT_MOST) {
                width = Math.min(DEFAULT_WIDTH, widthSize);
            }
        }

        int height = DEFAULT_HEIGHT;
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(DEFAULT_HEIGHT, heightSize);
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float minSize = Math.min(w - getPaddingLeft() - getPaddingRight() - 2 * mArcStrokeWidth,
                h - getPaddingTop() - getPaddingBottom() - 2 * mArcStrokeWidth);
        float centerX = getWidth() / 2.0f;
        float centerY = getHeight() / 2.0f;
        mRectF.set(centerX - minSize / 2, centerY - minSize / 2, centerX + minSize / 2,
                centerY + minSize / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawArcCircle(canvas);
        drawProgressArc(canvas);
        drawProgressText(canvas);
        drawBottomText(canvas);
    }

    private void drawArcCircle(Canvas canvas) {
        float startAngle = 90 + (360 - mArcAngle) / 2.0f;
        mPaint.setStrokeWidth(mArcStrokeWidth);
        mPaint.setColor(mArcUnfinishedColor);
        canvas.drawArc(mRectF, startAngle, mArcAngle, false, mPaint);
    }

    private void drawProgressArc(Canvas canvas) {
        float startAngle = 90 + (360 - mArcAngle) / 2.0f;
        float sweepAngle = (mArcProgress * 1.0f / mArcMax) * mArcAngle;
        mPaint.setStrokeWidth(mArcStrokeWidth);
        mPaint.setColor(mArcFinishedColor);
        canvas.drawArc(mRectF, startAngle, sweepAngle, false, mPaint);
    }

    private void drawProgressText(Canvas canvas) {
        // draw progress
        String progressText = String.valueOf(getProgress());
        mTextPaint.setTextSize(mArcTextSize);
        mTextPaint.setColor(mArcTextColor);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        float cx = getWidth() / 2;
        float cy = getHeight() / 2;
        Paint.FontMetricsInt fmi = mTextPaint.getFontMetricsInt();
        float baselineY = cy - (fmi.top + fmi.bottom) / 2.0f;
        canvas.drawText(progressText, cx, baselineY, mTextPaint);
        float progressTextWidth = mTextPaint.measureText(progressText);

        // draw suffix
        if (!TextUtils.isEmpty(mArcSuffixText)) {
            mTextPaint.setTextAlign(Paint.Align.LEFT);
            mTextPaint.setTextSize(mArcSuffixTextSize);
            float cx2 = cx + progressTextWidth / 2 + mArcSuffixTextPadding;
            canvas.drawText(mArcSuffixText, cx2, baselineY, mTextPaint);
        }
    }

    private void drawBottomText(Canvas canvas) {
        Log.e(TAG, "drawBottomText: " + mArcBottomText);
        if (!TextUtils.isEmpty(mArcBottomText)) {
            mTextPaint.setTextSize(mArcBottomTextSize);
            mTextPaint.setTextAlign(Paint.Align.CENTER);
            float angle = (360 - mArcAngle) / 2.0f;
            double yHeight = Math.cos(angle / 180 * Math.PI) * getWidth() / 2;
            Paint.FontMetricsInt fmi = mTextPaint.getFontMetricsInt();
            float cx = getWidth() / 2;
            float cy = getHeight() / 2;
            float baselineY = (float) (yHeight + cy - (fmi.bottom + fmi.top) / 2);
            canvas.drawText(mArcBottomText, cx, baselineY, mTextPaint);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putInt(INSTANCE_ARC_PROGRESS, getProgress());
        bundle.putFloat(INSTANCE_ARC_ANGLE, getArcAngle());
        bundle.putFloat(INSTANCE_STROKE_WIDTH, getStrokeWidth());
        bundle.putInt(INSTANCE_ARC_MAX, getMax());
        bundle.putInt(INSTANCE_ARC_FINISHED_COLOR, getFinishedColor());
        bundle.putInt(INSTANCE_ARC_UNFINISHED_COLOR, getUnfinishedColor());
        bundle.putFloat(INSTANCE_ARC_TEXT_SIZE, getTextSize());
        bundle.putInt(INSTANCE_ARC_TEXT_COLOR, getTextColor());
        bundle.putFloat(INSTANCE_SUFFIX_TEXT_SIZE, getSuffixTextSize());
        bundle.putFloat(INSTANCE_SUFFIX_TEXT_PADDING, getSuffixTextPadding());
        bundle.putString(INSTANCE_SUFFIX_TEXT, getSuffixText());
        bundle.putString(INSTANCE_BOTTOM_TEXT, getBottomText());
        bundle.putFloat(INSTANCE_BOTTOM_TEXT_SIZE, getBottomTextSize());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            mArcProgress = bundle.getInt(INSTANCE_ARC_PROGRESS);
            mArcAngle = bundle.getFloat(INSTANCE_ARC_ANGLE);
            mArcStrokeWidth = bundle.getFloat(INSTANCE_STROKE_WIDTH);
            mArcMax = bundle.getInt(INSTANCE_ARC_MAX);
            mArcFinishedColor = bundle.getInt(INSTANCE_ARC_FINISHED_COLOR);
            mArcUnfinishedColor = bundle.getInt(INSTANCE_ARC_UNFINISHED_COLOR);
            mArcTextSize = bundle.getFloat(INSTANCE_ARC_TEXT_SIZE);
            mArcTextColor = bundle.getInt(INSTANCE_ARC_TEXT_COLOR);
            mArcSuffixText = bundle.getString(INSTANCE_SUFFIX_TEXT);
            mArcSuffixTextSize = bundle.getFloat(INSTANCE_SUFFIX_TEXT_SIZE);
            mArcSuffixTextPadding = bundle.getFloat(INSTANCE_SUFFIX_TEXT_PADDING);
            mArcBottomText = bundle.getString(INSTANCE_BOTTOM_TEXT);
            mArcBottomTextSize = bundle.getFloat(INSTANCE_BOTTOM_TEXT_SIZE);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    public int getProgress() {
        return mArcProgress;
    }

    public void setProgress(int progress) {
        if (mArcProgress == progress) {
            return;
        }
        mArcProgress = progress;
        if (mArcProgress > mArcMax) {
            mArcProgress %= mArcMax;
        }
        invalidate();
    }

    public float getArcAngle() {
        return mArcAngle;
    }

    public void setArcAngle(float angle) {
        if (mArcAngle == angle) {
            return;
        }
        mArcAngle = angle;
        invalidate();
    }

    public int getMax() {
        return mArcMax;
    }

    public void setMax(int max) {
        if (mArcMax == max || max < 0) {
            return;
        }

        mArcMax = max;
        invalidate();
    }

    public float getTextSize() {
        return mArcTextSize;
    }

    public void setTextSize(float textSize) {
        if (mArcTextSize == textSize) {
            return;
        }
        mArcTextSize = textSize;
        invalidate();
    }

    public float getSuffixTextSize() {
        return mArcSuffixTextSize;
    }

    public void setSuffixTextSize(float textSize) {
        if (mArcSuffixTextSize == textSize) {
            return;
        }
        mArcSuffixTextSize = textSize;
        invalidate();
    }

    public float getSuffixTextPadding() {
        return mArcSuffixTextPadding;
    }

    public void setSuffixTextPadding(float padding) {
        if (mArcSuffixTextPadding == padding) {
            return;
        }
        mArcSuffixTextPadding = padding;
        invalidate();
    }

    public float getBttomTextSize() {
        return mArcBottomTextSize;
    }

    public void setButtomTextSize(float textSize) {
        if (mArcBottomTextSize == textSize) {
            return;
        }
        mArcBottomTextSize = textSize;
        invalidate();
    }

    public int getTextColor() {
        return mArcTextColor;
    }

    public void setTextColor(int color) {
        if (mArcTextColor == color) {
            return;
        }
        mArcTextColor = color;
        invalidate();
    }

    public int getFinishedColor() {
        return mArcFinishedColor;
    }

    public void setFinishedColor(int color) {
        if (mArcFinishedColor == color) {
            return;
        }
        mArcFinishedColor = color;
        invalidate();
    }

    public int getUnfinishedColor() {
        return mArcUnfinishedColor;
    }

    public void setUnfinishedColor(int color) {
        if (mArcUnfinishedColor == color) {
            return;
        }
        mArcUnfinishedColor = color;
        invalidate();
    }

    public String getSuffixText() {
        return mArcSuffixText;
    }

    public void setSuffixText(String suffixText) {
        if (mArcSuffixText.equals(suffixText)) {
            return;
        }
        mArcSuffixText = suffixText;
        invalidate();
    }

    public float getStrokeWidth() {
        return mArcStrokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        if (mArcStrokeWidth == strokeWidth) {
            return;
        }
        mArcStrokeWidth = strokeWidth;
        invalidate();
    }

    public String getBottomText() {
        return mArcBottomText;
    }

    public void setBottomText(String text) {
        if (TextUtils.isEmpty(text) || text.equals(mArcBottomText)) {
            return;
        }
        mArcBottomText = text;
        invalidate();
    }

    public float getBottomTextSize() {
        return mArcBottomTextSize;
    }

    public void setBottomTextSize(float size) {
        if (size == mArcBottomTextSize) {
            return;
        }
        mArcBottomTextSize = size;
        invalidate();
    }
}
