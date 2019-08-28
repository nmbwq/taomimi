package java.com.lechuang.module.treasurebox;

import android.content.Context;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

public class AutoScrollLayoutManager extends LinearLayoutManager {


    public AutoScrollLayoutManager(Context context) {
        super(context);
    }

    public AutoScrollLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public AutoScrollLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {
                    @Nullable
                    @Override
                    public PointF computeScrollVectorForPosition(int targetPosition) {
                        return AutoScrollLayoutManager.this.computeScrollVectorForPosition
                                (targetPosition);
                    }

                    @Override
                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                        // 计算滑动每个像素需要的时间，这里应该与屏幕适配；
                        return 40f / displayMetrics.density;
                    }
                };
        linearSmoothScroller.setTargetPosition(position);

        startSmoothScroll(linearSmoothScroller);

    }
}
