package com.common.app.view.coverflowviewpager;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.LayoutRes;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.common.R;
import com.common.app.base.BaseApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: zhengjr
 * @since: 2018/9/8
 * @describe:
 */

public class CoverFlowViewPager extends RelativeLayout implements OnPageSelectListener {


    /**
     * 适配器
     */
    private CoverFlowAdapter mAdapter;

    /**
     * 用于左右滚动
     */
    private ViewPager mViewPager;

    /**
     * 需要显示的视图集合
     */
    private List<View> mViewList = new ArrayList<>();

    private OnPageSelectListener listener;
    private OnPageClickListener mPageClickListener;

    public CoverFlowViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.widget_cover_flow,this);
        mViewPager = (ViewPager) findViewById(R.id.vp_conver_flow);
        init();
    }

    /**
     * 初始化方法
     */
    private void init() {
        // 构造适配器，传入数据源
        mAdapter = new CoverFlowAdapter(mViewList,getContext());
        // 设置选中的回调
        mAdapter.setOnPageSelectListener(this);
        // 设置适配器
        mViewPager.setAdapter(mAdapter);
        // 设置滑动的监听，因为adpter实现了滑动回调的接口，所以这里直接设置adpter
        mViewPager.addOnPageChangeListener(mAdapter);
        // 自己百度
        mViewPager.setOffscreenPageLimit(5);

        // 设置触摸事件的分发
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 传递给ViewPager 进行滑动处理
                return mViewPager.dispatchTouchEvent(event);
            }
        });

    }

    /**
     * 设置显示的数据，进行一层封装
     * @param imageUrls
     */
    public Map<Integer,Bitmap> mBitmapMap;
    public void setViewList(List<String> imageUrls, @LayoutRes int layoutItem){
        if(imageUrls==null){
            return;
        }
        if(mViewList == null){
            mViewList = new ArrayList<>();
        }
        mViewList.clear();
        if(mBitmapMap == null){
            mBitmapMap = new HashMap<>();
        }
        mBitmapMap.clear();

        for(int i = 0; i < imageUrls.size() ;i++){

            View inflate = LayoutInflater.from(getContext()).inflate(layoutItem, null);

            // 设置padding 值，默认缩小
            inflate.setScaleX(0.83f);
            inflate.setScaleY(0.83f);
            inflate.setAlpha(0.5f);
            inflate.setTranslationX(mAdapter.dp2px(-60));
            final ImageView vpItemImg = inflate.findViewById(R.id.vp_item_img);
            final int finalI = i;
            Glide.with(BaseApplication.getApplication()).load(imageUrls.get(i)).asBitmap().placeholder(R.drawable.bg_shareimg_loading).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    vpItemImg.setImageBitmap(resource);
                    mBitmapMap.put(finalI,resource);
                }
            });
            mViewList.add(inflate);
        }
        // 刷新数据
        mAdapter.notifyDataSetChanged();
        mViewList.get(0).bringToFront();

    }

    public void setCurrentSelect(int position){
        if (mViewPager != null){
            mViewPager.setCurrentItem(position,true);
        }
    }


    /**
     * 当将某一个作为最中央时的回调
     * @param listener
     */
    public void setOnPageSelectListener(OnPageSelectListener listener) {
        this.listener = listener;
    }


    public void setOnPageClickListener(final OnPageClickListener onPageClickListener){
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPageClickListener != null){
                    int currentItem = mViewPager.getCurrentItem();
                    onPageClickListener.onPageClick(currentItem);
                }
            }
        });
    }

    // 显示的回调
    @Override
    public void select(int position) {
        if(listener!=null){
            listener.select(position);
        }
    }
}
