package com.common.app.utils;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Copyright (c) 2018, wangpeng@aipark.com All Rights Reserved.
 * #
 * 项目名称:    SZproject
 * 包名:        sz.zhht.ipark.app.view
 * 类名:      CountDownTextView
 * 描述:    倒计时文本控件，适合做短信验证码倒计时，启动页/广告页倒计时等等
 *          已做防内存泄漏处理，并可设置在倒计时期间无法点击 ;
 *          可设置页面关闭后再次开启，倒计时依然精确运行
 *          可正向计时和倒计时
 * 创建人:         王鹏
 * 创建日期:     18/12/3 15:29
 * 更新人:     吕红雁
 * 更新日期:     19/02/16 22:41
 * 更新说明:   新增mShowFormatTimeOther判断时间是样式
 */
public class CountDownTextView extends android.support.v7.widget.AppCompatTextView implements LifecycleObserver, View.OnClickListener {
    private static final String SHARED_PREFERENCES_FILE = "CountDownTextView";
    private static final String SHARED_PREFERENCES_FIELD_TIME = "last_count_time";
    private static final String SHARED_PREFERENCES_FIELD_TIMESTAMP = "last_count_timestamp";
    private static final String SHARED_PREFERENCES_FIELD_INTERVAL = "count_interval";
    private static final String SHARED_PREFERENCES_FIELD_COUNTDOWN = "is_countdown";

    private CountDownTimer mCountDownTimer;
    private OnCountDownStartListener mOnCountDownStartListener;
    private OnCountDownTickListener mOnCountDownTickListener;
    private OnCountDownFinishListener mOnCountDownFinishListener;
    private String mNormalText;
    private String mCountDownText;
    private OnClickListener mOnClickListener;
    /**
     * 倒计时期间是否允许点击
     */
    private boolean mClickable = false;
    /**
     * 页面关闭后倒计时是否保持，再次开启倒计时继续；
     */
    private boolean mCloseKeepCountDown = false;
    /**
     * 是否把时间格式化成时分秒
     */
    private boolean mShowFormatTime = false;
    /**
     * 是否把时间格式化成时分秒的其他格式
     */
    private int mShowFormatTimeOther = 1;
    /**
     * 倒计时间隔
     */
    private TimeUnit mIntervalUnit = TimeUnit.SECONDS;

    public CountDownTextView(Context context) {
        this( context, null );
    }

    public CountDownTextView(Context context, @Nullable AttributeSet attrs) {
        this( context, attrs, 0 );
    }

    public CountDownTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super( context, attrs, defStyleAttr );
        init( context );
    }

    private void init(Context context) {
        autoBindLifecycle( context );
    }

    /**
     * 控件自动绑定生命周期,宿主可以是activity或者fragment
     */
    private void autoBindLifecycle(Context context) {
        if (context instanceof FragmentActivity) {
            FragmentActivity activity = (FragmentActivity) context;
            FragmentManager fm = activity.getSupportFragmentManager();
            List<Fragment> fragments = fm.getFragments();
            for (Fragment fragment : fragments) {
                View parent = fragment.getView();
                if (parent != null) {
                    View find = parent.findViewById( getId() );
                    if (find == this) {
                        fragment.getLifecycle().addObserver( this );
                        return;
                    }
                }
            }
        }
        if (context instanceof LifecycleOwner) {
            ((LifecycleOwner) context).getLifecycle().addObserver( this );
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private void onResume() {
        if (mCountDownTimer == null) {
            checkLastCountTimestamp();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void onDestroy() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        onDestroy();
    }

    /**
     * 非倒计时状态文本
     *
     * @param normalText 文本
     */
    public CountDownTextView setNormalText(String normalText) {
        mNormalText = normalText;
        setText( normalText );
        return this;
    }

    /**
     * 设置倒计时文本内容
     *
     * @param front  倒计时文本前部分
     * @param latter 倒计时文本后部分
     */
    public CountDownTextView setCountDownText(String front, String latter) {
        mCountDownText = front + "%1$s" + latter;
        return this;
    }

    /**
     * 设置倒计时间隔
     *
     * @param intervalUnit
     */
    public CountDownTextView setIntervalUnit(TimeUnit intervalUnit) {
        mIntervalUnit = intervalUnit;
        return this;
    }

    /**
     * 顺序计时，非倒计时
     *
     * @param second 计时时间秒
     */
    public void startCount(long second) {
        startCount( second, TimeUnit.SECONDS );
    }

    public void startCount(long time, final TimeUnit timeUnit) {
        if (mCloseKeepCountDown && checkLastCountTimestamp()) {
            return;
        }
        count( time, 0, timeUnit, false );
    }

    /**
     * 默认按秒倒计时
     *
     * @param second 多少秒
     */
    public void startCountDown(long second) {
        startCountDown( second, TimeUnit.SECONDS );
    }

    public void startCountDown(long time, final TimeUnit timeUnit) {
        if (mCloseKeepCountDown && checkLastCountTimestamp()) {
            return;
        }
        count( time, 0, timeUnit, true );
    }

    /**
     * 计时方案
     *
     * @param time        计时时长
     * @param timeUnit    时间单位
     * @param isCountDown 是否是倒计时，false正向计时
     */
    private void count(final long time, final long offset, final TimeUnit timeUnit, final boolean isCountDown) {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
        setEnabled( mClickable );
        final long millisInFuture = timeUnit.toMillis( time ) + 500;
        long interval = TimeUnit.MILLISECONDS.convert( 1, mIntervalUnit );
        if (mCloseKeepCountDown && offset == 0) {
            setLastCountTimestamp( millisInFuture, interval, isCountDown );
        }
        if (offset == 0 && mOnCountDownStartListener != null) {
            mOnCountDownStartListener.onStart();
        }
        if (TextUtils.isEmpty( mCountDownText )) {
            mCountDownText = getText().toString();
        }
        mCountDownTimer = new CountDownTimer( millisInFuture, interval ) {
            @Override
            public void onTick(long millisUntilFinished) {
                long count = isCountDown ? millisUntilFinished : (millisInFuture - millisUntilFinished + offset);
                long l = timeUnit.convert( count, TimeUnit.MILLISECONDS );
                String showTime;
                if (mShowFormatTime) {
                    if (mShowFormatTimeOther==0) {
                        showTime = generateTimeOther( count );
                    } else if (mShowFormatTimeOther==1){
                        showTime = generateTime( count );
                    } else {
                        showTime = generateTimeDay( count );
                    }
                } else {
                    showTime = String.valueOf( l );
                }
                setText( String.format( mCountDownText, showTime ) );
                if (mOnCountDownTickListener != null) {
                    mOnCountDownTickListener.onTick( l );
                }
            }

            @Override
            public void onFinish() {
                setEnabled( true );
                mCountDownTimer = null;
                setText( mNormalText );
                if (mOnCountDownFinishListener != null) {
                    mOnCountDownFinishListener.onFinish();
                }
            }
        };
        mCountDownTimer.start();
    }

    public CountDownTextView setOnCountDownStartListener(OnCountDownStartListener onCountDownStartListener) {
        mOnCountDownStartListener = onCountDownStartListener;
        return this;
    }

    public CountDownTextView setOnCountDownTickListener(OnCountDownTickListener onCountDownTickListener) {
        mOnCountDownTickListener = onCountDownTickListener;
        return this;
    }

    public CountDownTextView setOnCountDownFinishListener(OnCountDownFinishListener onCountDownFinishListener) {
        mOnCountDownFinishListener = onCountDownFinishListener;
        return this;
    }

    /**
     * 倒计时期间，点击事件是否响应
     *
     * @param clickable 是否响应
     */
    public CountDownTextView setCountDownClickable(boolean clickable) {
        mClickable = clickable;
        return this;
    }

    /**
     * 关闭页面是否保持倒计时
     *
     * @param keep 是否保持
     */
    public CountDownTextView setCloseKeepCountDown(boolean keep) {
        mCloseKeepCountDown = keep;
        return this;
    }

    /**
     * 是否格式化时间
     *
     * @param formatTime 是否格式化
     */
    public CountDownTextView setShowFormatTime(boolean formatTime) {
        mShowFormatTime = formatTime;
        return this;
    }

    /**
     * 是否格式化时间
     *
     * @param formatTime 是否格式化
     */
    public CountDownTextView setShowFormatTimeOther(int formatTime) {
        mShowFormatTimeOther = formatTime;
        return this;
    }

    public interface OnCountDownStartListener {
        /**
         * 计时开始回调;反序列化时不会回调
         */
        void onStart();
    }

    public interface OnCountDownTickListener {
        /**
         * 计时回调
         *
         * @param untilFinished 剩余时间,单位为开始计时传入的单位
         */
        void onTick(long untilFinished);
    }

    public interface OnCountDownFinishListener {
        /**
         * 计时结束回调
         */
        void onFinish();
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        mOnClickListener = l;
        super.setOnClickListener( this );
    }

    @Override
    public void onClick(View v) {
        if (mCountDownTimer != null && !mClickable) {
            return;
        }
        if (mOnClickListener != null) {
            mOnClickListener.onClick( v );
        }
    }

    /**
     * 持久化
     *
     * @param time        倒计时时长
     * @param interval    倒计时间隔
     * @param isCountDown 是否是倒计时而不是正向计时
     */
    @SuppressLint("ApplySharedPref")
    private void setLastCountTimestamp(long time, long interval, boolean isCountDown) {
        getContext()
                .getSharedPreferences( SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE )
                .edit()
                .putLong( SHARED_PREFERENCES_FIELD_TIME + getId(), time )
                .putLong( SHARED_PREFERENCES_FIELD_TIMESTAMP + getId(), Calendar.getInstance().getTimeInMillis() + time )
                .putLong( SHARED_PREFERENCES_FIELD_INTERVAL + getId(), interval )
                .putBoolean( SHARED_PREFERENCES_FIELD_COUNTDOWN + getId(), isCountDown )
                .commit();

    }

    /**
     * 检查持久化参数
     *
     * @return 是否要保持持久化计时
     */
    private boolean checkLastCountTimestamp() {
        SharedPreferences sp = getContext().getSharedPreferences( SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE );
        long lastCountTimestamp = sp.getLong( SHARED_PREFERENCES_FIELD_TIMESTAMP + getId(), -1 );
        long nowTimeMillis = Calendar.getInstance().getTimeInMillis();
        long diff = lastCountTimestamp - nowTimeMillis;
        if (diff <= 0) {
            return false;
        }
        long time = sp.getLong( SHARED_PREFERENCES_FIELD_TIME + getId(), -1 );
        long interval = sp.getLong( SHARED_PREFERENCES_FIELD_INTERVAL + getId(), -1 );
        boolean isCountDown = sp.getBoolean( SHARED_PREFERENCES_FIELD_COUNTDOWN + getId(), true );
        for (TimeUnit timeUnit : TimeUnit.values()) {
            long convert = timeUnit.convert( interval, TimeUnit.MILLISECONDS );
            if (convert == 1) {
                long last = timeUnit.convert( diff, TimeUnit.MILLISECONDS );
                long offset = time - diff;
                count( last, offset, timeUnit, isCountDown );
                return true;
            }
        }
        return false;
    }

    /**
     * 将毫秒转时分秒
     */
    @SuppressLint("DefaultLocale")
    public static String generateTime(long time) {
        String format;
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
//        if (hours > 0) {
        format = String.format( "%02d:%02d:%02d", hours, minutes, seconds );
        /*} else if (minutes > 0) {
            format = String.format("0:%02d分%02d秒", minutes, seconds);
        } else {
            format = String.format("0:0:%2d秒", seconds);
        }*/
        return format;
    }

    /**
     * 将毫秒转时分秒
     */
    @SuppressLint("DefaultLocale")
    public static String generateTimeOther(long time) {
        String format;
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600 % 24;
        int day = totalSeconds / 3600 / 24;
        if (day > 0) {
            format = String.format( "%02d天%02d小时%02d分%02d秒", day, hours, minutes, seconds );
        } else if (hours > 0) {
            format = String.format( "%02d小时%02d分%02d秒", hours, minutes, seconds );
        } else if (minutes > 0) {
            format = String.format( "%02d分%02d秒", minutes, seconds );
        } else {
            format = String.format( "%2d秒", seconds );
        }
        return format;
    }

    /**
     * 将毫秒转时分秒
     */
    @SuppressLint("DefaultLocale")
    public static String generateTimeDay(long time) {
        String format;
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600 % 24;
        int day = totalSeconds / 3600 / 24;
        if (day > 0) {
            format = String.format( "%2d天%02d:%02d:%02d", day, hours, minutes, seconds );
        } else if (hours > 0) {
            format = String.format( "%02d:%02d:%02d", hours, minutes, seconds );
        } else if (minutes > 0) {
            format = String.format( "00:%02d:%02d", minutes, seconds );
        } else {
            format = String.format( "00:00:%2d", seconds );
        }
        return format;
    }
}

