package com.common.app.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.common.R;

/**
 * 这个可以用代码改，比较麻烦
 * https://blog.csdn.net/sun_lianqiang/article/details/80902827
 */
public class NumSeekBar extends RelativeLayout {

    private int mTextSize = 10;
    private int mTextColor;
    private SeekBar mSeekBar;
    private TextView mTvProgress;

    public NumSeekBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumSeekBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private void initAttrs(@Nullable AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.numSeekBar);
        mTextSize = typedArray.getDimensionPixelOffset(R.styleable.numSeekBar_textSize, 10);

        mTextColor = typedArray.getColor(R.styleable.numSeekBar_textColor, getResources().getColor(R.color.white));
        typedArray.recycle();

        mSeekBar = new SeekBar(getContext());

        LayoutParams paramsSeekBar = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        paramsSeekBar.addRule(RelativeLayout.CENTER_VERTICAL);
        paramsSeekBar.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

        mSeekBar.setProgressDrawable(getResources().getDrawable(R.drawable.pro_seekbar_12));
        mSeekBar.setPadding(0,0,0,0);
        mSeekBar.setThumb(null);
        mSeekBar.setMax(100);
        mSeekBar.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        addView(mSeekBar, paramsSeekBar);

        mTvProgress = new TextView(getContext());
        mTvProgress.setTextSize(mTextSize);
        mTvProgress.setTextColor(mTextColor);
        mTvProgress.setText("%");

        LayoutParams paramsProgress = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        paramsProgress.setMargins(10, 0, 0, 0);
        paramsProgress.addRule(RelativeLayout.CENTER_VERTICAL);
        paramsProgress.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        addView(mTvProgress, paramsProgress);

    }

    public TextView setProgress(int progress) {
        mTvProgress.setText(progress + "%");
        mSeekBar.setProgress(progress);
        invalidate();
        return mTvProgress;
    }

    public TextView setTextColor(int color) {
        mTvProgress.setTextColor(color);
        invalidate();
        return mTvProgress;
    }

    public TextView setTextSize(int textSize) {
        mTvProgress.setTextSize(textSize);
        invalidate();
        return mTvProgress;
    }

    public SeekBar setSeekBarStyle(int drawable){
        mSeekBar.setProgressDrawable(getResources().getDrawable(drawable));
        mSeekBar.invalidate();
        invalidate();
        return mSeekBar;
    }
}
