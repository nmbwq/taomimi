package com.common.app.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by cmd on 2018/7/14.
 */

public abstract class LazyBaseFragment extends BaseFragment {

    private boolean mInitSuccess = false;//判断是否初始化view
    protected boolean mIsFirstVisible = true;//判断是否是第一次加载，如果加载失败，可以在实现类设置为true，当再次显示时可以重新加载


    @Override
    protected void initDataView() {
        //查找控件
        findViews();

        //初始化控件
        initView();

        //用于初始化数据使用
        initData();
    }

    /**
     * 当viewpager和fragment嵌套使用的时候，需要借助setUserVisibleHint来进行数据预加载显示
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        prepareLoadData(isVisibleToUser);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mInitSuccess = true;
        prepareLoadData(getUserVisibleHint());
    }

    private void prepareLoadData(boolean isVisibleToUser){
        if (isVisibleToUser && mInitSuccess && mIsFirstVisible){//当前显示，切初始化之后再加载数据
            mIsFirstVisible = false;
            //获取数据
            getData();

        }
    }

}
