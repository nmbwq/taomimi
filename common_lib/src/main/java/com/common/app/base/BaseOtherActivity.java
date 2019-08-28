package com.common.app.base;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.common.R;
import com.common.app.http.INetStateLisenter;
import com.common.app.receiver.NetWorkChangReceiver;
import com.common.app.utils.CustomProgressDialog;
import com.common.app.utils.StatusBarUtil;
import com.common.app.utils.Utils;
import com.common.app.view.CommonDialog;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author: zhengjr
 * @since: 2018/10/19
 * @describe:
 */

public abstract class BaseOtherActivity extends Activity implements INetStateLisenter {
    protected final String TAG = "BaseOtherActivity";
    protected Unbinder mUnbinder;

    protected CustomProgressDialog progressDialog;
    private NetWorkChangReceiver mNetWorkChangReceiver;
    protected boolean mNetState = false; //网络的连接状态

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //两种设置布局的方式
        if (getLayoutId() != 0) {
            setContentView(getLayoutId());
        } else if (getLayoutView() != null) {
            setContentView(getLayoutView());
        }

        //设置沉浸式状态栏
        setTranslucentHeader();

        //设置是否更改状态栏颜色是否为深色,默认为深色
        changeStatusBarTextColor(setStatusBarTextColor());

//        //ButterKnife绑定
        mUnbinder = ButterKnife.bind(this);

        //数据保存
        savedInstance(savedInstanceState);

        //初始化数据和控件
        initDataView();
    }

    private void initDataView() {

        //获得intent
        initIntent();

        //查找控件
        findViews();

        //初始化控件
        initView();

        //初始化数据
        initData();

        //获取数据
        getData();

    }

    /**
     *设置是否更改状态栏颜色是否为深色
     * @return
     */
    protected boolean setStatusBarTextColor(){
        return true;
    }

    /**
     * 更改状态栏颜色是否为深色
     * @param isBlack
     */
    private void changeStatusBarTextColor(boolean isBlack) {
        if (isBlack){
            StatusBarUtil.setLightMode(this);
        }else {
            StatusBarUtil.setDarkMode(this);
        }
    }

    /**
     * 设置沉浸式状态栏
     */
    protected void setTranslucentHeader(){
        StatusBarUtil.setTranslucentForImageView(BaseOtherActivity.this, setTransAlpha(),null);
    }

    /**
     * 设置沉浸式状态栏透明度
     */
    protected int setTransAlpha(){
        return 0;
    }

    //获取布局id
    protected abstract int getLayoutId();

    //获取布局View
    protected View getLayoutView() {
        return null;
    }

    protected void initIntent() {
    }

    protected abstract void findViews();

    protected abstract void initView();

    protected void initData(){}

    protected void savedInstance(@Nullable Bundle savedInstanceState){}

    protected abstract void getData();

    /**
     * 关闭按钮
     * @param view
     */
    public void finish(View view){
        finish();
    }


    /**
     * 设置网络监听的广播
     * 获取焦点是设置
     * onResume()
     */
    protected void setNetReceiver() {
        if (mNetWorkChangReceiver == null) {
            mNetWorkChangReceiver = new NetWorkChangReceiver(this);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetWorkChangReceiver, filter);
    }

    /**
     * 移除网络监听的广播
     * 界面失去焦点时移除
     * onPause()
     */
    protected void removeNetReceiver() {
        //网络连接监听广播
        if (mNetWorkChangReceiver != null) {
            unregisterReceiver(mNetWorkChangReceiver);
            mNetWorkChangReceiver = null;
        }
    }

    @Override
    public void netStateLisenter(String connType, boolean netState) {
        mNetState = netState;
        if (mNetState) {
            toast("您正在使用" + connType);
        } else {
            toast("网络连接断开");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        hintKbTwo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }

    protected void toast(@StringRes int message) {
        Utils.toast(message);
    }

    protected void toast(String message) {
        Utils.toast(message);
    }

    public <T extends View> T $(int id){
        return (T) findViewById ( id );
    }

    /**
     * 启动加载进度条
     */
    protected void showProgressDialog() {
        try {
            createProgressDialog(this);
            if (progressDialog != null) {
                progressDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭加载进度条
     */
    protected void stopProgressDialog() {
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建进度条实例
     */
    protected void createProgressDialog(Context cxt) {
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
            if (progressDialog == null) {
                progressDialog = CustomProgressDialog.createDialog(cxt, false);
                progressDialog.setCanceledOnTouchOutside(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //添加通用的为开发功能的显示
    private CommonDialog mCommonDialog;

    protected void showKaiFaDialog(){

        if (mCommonDialog != null) {
            mCommonDialog.dismiss();
            mCommonDialog = null;
        }
        if (mCommonDialog == null){

            mCommonDialog = new CommonDialog(this, R.layout.dialog_kaifa);
        }
        mCommonDialog.getViewId(R.id.btn_dialog_know).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCommonDialog.dismiss();
            }
        });
        mCommonDialog.show();
    }

    /**
     * 此方法只是关闭软键盘
     */
    public void hintKbTwo() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive()&&getCurrentFocus()!=null){
            if (getCurrentFocus().getWindowToken()!=null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    protected void showKeyboard(boolean isShow) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (null == imm)
            return;

        if (isShow) {
            if (getCurrentFocus() != null) {
                //有焦点打开
                imm.showSoftInput(getCurrentFocus(), 0);
            } else {
                //无焦点打开
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        } else {
            if (getCurrentFocus() != null) {
                //有焦点关闭
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            } else {
                //无焦点关闭
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            }
        }
    }
}
