package rxjava.android.com.rxjavastudy.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import rxjava.android.com.rxjavastudy.R;

/**
 * Created by zhengyi.wzy on 2017/4/12.
 */

public class ScrimInsetsFrameLayout extends FrameLayout {
    private Drawable mInsetForeground;
    private Rect mInsets;
    private Rect mTempRect = new Rect();

    public ScrimInsetsFrameLayout(@NonNull Context context) {
        this(context, null);
    }

    public ScrimInsetsFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrimInsetsFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        final TypedArray ta = context.obtainStyledAttributes(attrs,
                R.styleable.ScrimInsetsFrameLayout, defStyleAttr, 0);
        if (ta == null) {
            return;
        }
        mInsetForeground = ta.getDrawable(R.styleable.ScrimInsetsFrameLayout_insetForeground);
        ta.recycle();
        setWillNotDraw(true);
    }

    @Override
    protected boolean fitSystemWindows(Rect insets) {
        mInsets = new Rect(insets);
        setWillNotDraw(mInsetForeground == null);
        ViewCompat.postInvalidateOnAnimation(this);
        return true;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        int width = getWidth();
        int height = getHeight();
        if (mInsets != null && mInsetForeground != null) {
            int sc = canvas.save();

            // Top
            mTempRect.set(0, 0, width, mInsets.top);
            mInsetForeground.setBounds(mTempRect);
            mInsetForeground.draw(canvas);


        }
    }
}
