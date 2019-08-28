package java.com.lechuang.module.treasurebox;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class AutoScrollRecyclerView extends RecyclerView {
    public AutoScrollRecyclerView(Context context) {
        super(context);
    }

    public AutoScrollRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoScrollRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return true;
    }
}
