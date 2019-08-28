package com.common.app.base;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.NavCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.common.R;
import com.common.app.arouter.ARouters;
import com.common.app.base.bean.BaseResponseBean;
import com.common.app.base.bean.UrlToTitleBean;
import com.common.app.constants.Constant;
import com.common.app.http.INetStateLisenter;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.receiver.NetWorkChangReceiver;
import com.common.app.utils.CustomProgressDialog;
import com.common.app.utils.LogUtils;
import com.common.app.utils.Logger;
import com.common.app.utils.SPUtils;
import com.common.app.utils.StatusBarUtil;
import com.common.app.utils.StringUtils;
import com.common.app.utils.Utils;
import com.common.app.view.CommonDialog;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author: zhengjr
 * @since: 2018/6/1
 * @describe:
 */

public abstract class BaseActivity extends AppCompatActivity implements INetStateLisenter {
    protected final String TAG = "BaseActivity";
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

        //ButterKnife绑定
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
        StatusBarUtil.setTranslucentForImageView(BaseActivity.this, setTransAlpha(),null);
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
    protected void onResume() {
        super.onResume();
        try {
            openZhiNengSearch();
        }catch (Exception e){
            Logger.e(TAG,e.toString());
        }

    }
    private CommonDialog mZhiNengSearch;

    private void openZhiNengSearch() {

        try {
            if (SPUtils.getInstance().getBoolean(BaseApplication.getApplication(), Constant.ISFIRSTOPEN, true)){
//                SPUtils.getInstance().putBoolean(BaseApplication.getApplication(), Constant.ISFIRSTOPEN, false);
                return;
            }
            String copyString = "";

            ClipData clipData = ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).getPrimaryClip();

            if (clipData == null){
                return;
            }

            ClipDescription description = clipData.getDescription();
            if (description != null){
                String label = (String) description.getLabel();
                if (!TextUtils.isEmpty(label) && label.startsWith("app")) {
                    return;
                }
            }


            ClipData.Item item = clipData.getItemAt(0);
            if (item == null){
                return;
            }
            copyString = item.getText().toString().trim();
            if (TextUtils.isEmpty ( copyString )){
                return;
            }
            ClipData clipDataed = ClipData.newPlainText("app_searched", copyString);
            ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(clipDataed);
            //新增判断字符串里是否是纯数字
            if (TextUtils.isEmpty(copyString)||isNumberic(copyString)){
                return;
            }
            //判断字符串中是否含有http，用以区分用户是复制标题还是复制链接
//            if(copyString.indexOf ( "http" )!=-1){
                getTitleData(copyString);
//                return;
//            }
            int lengths =copyString.replaceAll ( "[^\\x00-\\xff]","**" ).length ();
            if (lengths>120||copyString.indexOf( "\n" )!=-1){
                return;
            }
                /*clipDataed = ClipData.newPlainText("app_searched", copyString);
                ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(clipDataed);*/

                if (mZhiNengSearch != null) {
                    mZhiNengSearch.dismiss();
                    mZhiNengSearch = null;
                }
                if (mZhiNengSearch == null) {
                    mZhiNengSearch = new CommonDialog(BaseActivity.this, R.layout.dialog_zhinengsou_layout);
                }

                mZhiNengSearch.setTextView(R.id.tv_dialog_content, copyString);
                final String finalCopyString = copyString;
                mZhiNengSearch.getViewId(R.id.tv_dialog_sure).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mZhiNengSearch.dismiss();
                        //搜索
                        ARouter.getInstance().build(ARouters.PATH_SEARCH_RESULT)
                                .withString(Constant.CHANNEL, "淘宝")//默认搜索淘宝
                                .withString(Constant.SEARCHTEXT, finalCopyString)
                                .navigation();

                    }
                });
                mZhiNengSearch.getViewId(R.id.tv_dialog_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mZhiNengSearch.dismiss();
                    }
                });
                mZhiNengSearch.show();

        }catch (Exception e){

        }

    }

    public boolean isNumberic(String str){
        Pattern pattern = Pattern.compile ( "[0-9]*" );
        Matcher isNum = pattern.matcher ( str );
        return isNum.matches ();
    }

    private void getTitleData(String copysString){
        String type = StringUtils.zhiNengSouType(copysString);
        if (type=="1"){
            //淘口令
            copysString=StringUtils.getTKL(copysString);
        }else if (type=="2"){
            //链接
            copysString=StringUtils.getURL(copysString);
        }else {
            return;
        }
        if (TextUtils.isEmpty ( copysString )){
            return;
        }
        if (type=="1"||type=="2"){
            Map<String, String> allParam = new HashMap<> ();
            allParam.put ( "type",type );
            allParam.put ( "tbpwd",copysString );

            NetWork.getInstance ()
                    .setTag ( Qurl.urlToTitle )
                    .getApiService ( CommonApi.class )
                    .getTitle ( allParam )
                    .subscribeOn ( Schedulers.io () )
                    .observeOn ( AndroidSchedulers.mainThread () )
                    .subscribe ( new RxObserver<UrlToTitleBean> ( BaseActivity.this, false, false ) {

                        @Override
                        public void onSuccess(UrlToTitleBean result) {
                            if (result == null || TextUtils.isEmpty(result.productName)) {
                                return ;
                            }
                            String copyString=result.productName;
                            try{
//                            ClipData clipDataed = ClipData.newPlainText("app_searched", copyString);
//                            ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(clipDataed);

                                if (mZhiNengSearch != null) {
                                    mZhiNengSearch.dismiss();
                                    mZhiNengSearch = null;
                                }
                                if (mZhiNengSearch == null) {
                                    mZhiNengSearch = new CommonDialog(BaseActivity.this, R.layout.dialog_zhinengsou_layout);
                                }

                                mZhiNengSearch.setTextView(R.id.tv_dialog_content, copyString);
                                final String finalCopyString = copyString;
                                mZhiNengSearch.getViewId(R.id.tv_dialog_sure).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mZhiNengSearch.dismiss();
                                        //搜索
                                        ARouter.getInstance().build(ARouters.PATH_SEARCH_RESULT)
                                                .withString(Constant.CHANNEL, "淘宝")//默认搜索淘宝
                                                .withString(Constant.SEARCHTEXT, finalCopyString)
                                                .navigation();

                                    }
                                });
                                mZhiNengSearch.getViewId(R.id.tv_dialog_cancel).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mZhiNengSearch.dismiss();
                                    }
                                });
                                mZhiNengSearch.show();
                                return;
                            }catch (Exception e){

                            }
                        }

                        @Override
                        public void onFailed(int errorCode, String moreInfo) {
                            super.onFailed ( errorCode, moreInfo );
                        }

                        @Override
                        public void onError(Throwable e) {
                            super.onError ( e );
                        }
                    } );
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
                progressDialog = CustomProgressDialog.createDialog(cxt, true);
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
}
