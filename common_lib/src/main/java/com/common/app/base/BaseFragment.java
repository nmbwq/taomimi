package com.common.app.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.common.R;
import com.common.app.utils.StatusBarUtil;
import com.common.app.utils.Utils;
import com.common.app.view.CommonDialog;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {

    protected Unbinder unbinder;
    protected View mInflate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (getLayoutId() != 0) {
            mInflate = inflater.inflate(getLayoutId(), container,false);
            unbinder = ButterKnife.bind(this, mInflate);
            return mInflate;
        } else if (getLayoutView(inflater, container, savedInstanceState) != null) {
            mInflate = getLayoutView(inflater, container, savedInstanceState);
            unbinder = ButterKnife.bind(this, mInflate);
            return mInflate;
        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    protected void initDataView() {
        //查找控件
        findViews();

        //初始化控件
        initView();

        //用于初始化数据使用
        initData();

        //获取数据
        getData();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){//用户切换fragment时需要刷新数据使用
            updataRequest();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //初始化数据和控件
        initDataView();
    }

    protected void setTranslucentHeader(){
        StatusBarUtil.setTranslucentForImageView(getActivity(), setTransAlpha(),null);
    }

    protected int setTransAlpha(){
        return 0;
    }
    //获取布局id
    protected abstract int getLayoutId();

    protected View getLayoutView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        return null;
    }

    protected abstract void findViews();

    protected abstract void initView();

    protected void initData(){}

    protected abstract void getData();

    protected void updataRequest(){}


    @Override
    public void onDetach() {
        super.onDetach();
        if (unbinder != null){
            unbinder.unbind();
        }
    }

    public <T extends View> T $(int id){
        return (T) mInflate.findViewById (id);
    }

    public void toast(@StringRes int s){
        Utils.toast(s);
    }

    public void toast(String s){
        Utils.toast(s);
    }


    //添加通用的为开发功能的显示
    private CommonDialog mCommonDialog;

    protected void showKaiFaDialog(){

        if (mCommonDialog != null) {
            mCommonDialog.dismiss();
            mCommonDialog = null;
        }
        if (mCommonDialog == null){

            mCommonDialog = new CommonDialog(getActivity(), R.layout.dialog_kaifa);
        }
        mCommonDialog.getViewId(R.id.btn_dialog_know).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCommonDialog.dismiss();
            }
        });
        mCommonDialog.show();
    }
}
