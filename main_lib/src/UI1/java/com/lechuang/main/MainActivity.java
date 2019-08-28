package java.com.lechuang.main;


import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.base.BaseApplication;
import com.common.app.constants.Constant;
import com.common.app.database.UserInfoBeanDao;
import com.common.app.database.bean.UserInfoBean;
import com.common.app.database.manger.UserHelper;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.http.api.RetrofitServer;
import com.common.app.http.bean.GetHostUrlBean;
import com.common.app.http.bean.HuoDongRedBean;
import com.common.app.jumprouter.LinkRouterUtils;
import com.common.app.jumprouter.RouterBean;
import com.common.app.receiver.TagAliasOperatorHelper;
import com.common.app.service.AppUpdataService;
import com.common.app.service.MyServiceBinder;
import com.common.app.utils.ActivityStackManager;
import com.common.app.utils.DeviceUtils;
import com.common.app.utils.LogUtils;
import com.common.app.utils.Logger;
import com.common.app.utils.OnClickEvent;
import com.common.app.utils.SPUtils;
import com.common.app.view.CommonDialog;
import com.lechuang.main.R;
import com.umeng.socialize.UMShareAPI;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.com.lechuang.main.bean.MainHintBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_MAIN)
public class MainActivity extends BaseActivity implements View.OnClickListener {

    private final String TAG = "MainActivity";

    TextView mMainItem0;
//    TextView mMainItem1;
//    TextView mMainItem2;
    TextView mMainItem3;
    TextView mMainItem4;
    private ImageView mImage,mFinish;

    private List<Fragment> mFragments;
    private ArrayList<String> mTags;//用户存放fragment的的标志
    private List<TextView> mTextViews;

    private int mOldSelectPosition = 0;//默认选中的导航下标
    private Intent mStartIntent;
    private String mDownloadQnyUrl;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void findViews() {
        mMainItem0 = findViewById(R.id.main_item0);
//        mMainItem1 = findViewById(R.id.main_item1);
//        mMainItem2 = findViewById(R.id.main_item2);
        mMainItem3 = findViewById(R.id.main_item3);
        mMainItem4 = findViewById(R.id.main_item4);

        mMainItem0.setOnClickListener(this);
//        mMainItem1.setOnClickListener(this);
//        mMainItem2.setOnClickListener(this);
        mMainItem3.setOnClickListener(this);
        mMainItem4.setOnClickListener(this);

        mTextViews = new ArrayList<>();
        mTextViews.add(mMainItem0);
//        mTextViews.add(mMainItem1);
//        mTextViews.add(mMainItem2);
        mTextViews.add(mMainItem3);
        mTextViews.add(mMainItem4);
    }


    @Override
    protected void initView() {

        EventBus.getDefault().register(this);

        mTextViews.get(this.mOldSelectPosition).setSelected(true);
        //开启一个service
        mStartIntent = new Intent(this, AppUpdataService.class);
        startService(mStartIntent);

        //登录状态下，清空分享图片
        if (UserHelper.getInstence().getUserInfo().isLogin) {
            String phone = UserHelper.getInstence().getUserInfo().getPhone();
            UserInfoBeanDao userInfoDao = UserHelper.getInstence().getUserInfoDao();
            UserInfoBean unique = userInfoDao.queryBuilder().where(UserInfoBeanDao.Properties.Phone.eq(phone)).build().unique();
            if (unique != null) {
                unique.setImageShare(new ArrayList<String>());
                userInfoDao.update(unique);
            }
        }
    }

    private CommonDialog mPopupWindow;
    private void getPopwindow(String image,int type){

        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
            mPopupWindow = null;
        }
        if (mPopupWindow == null) {
            mPopupWindow = new CommonDialog(MainActivity.this, R.layout.popupwind_main_hint);
        }

        mImage = (ImageView) mPopupWindow.getViewId( R.id.ll_qqkong );
        mFinish = (ImageView) mPopupWindow.getViewId( R.id.iv_finish );
        Glide.with(BaseApplication.getApplication()).load(image).into(mImage);
        if (type==1) {//跳转登录
            mImage.setOnClickListener( new OnClickEvent() {
                @Override
                public void singleClick(View v) {
                    if (mPopupWindow != null){
                        mPopupWindow.dismiss();
                        mPopupWindow = null;
                    }
                    ARouter.getInstance().build(ARouters.PATH_LOGIN).navigation();
                }
            } );
        }else if (type==2){
            mImage.setOnClickListener( new OnClickEvent() {
                @Override
                public void singleClick(View v) {
                    if (mPopupWindow != null){
                        mPopupWindow.dismiss();
                        mPopupWindow = null;
                    }
                    ARouter.getInstance().build(ARouters.PATH_MY_CARD).navigation();
                }
            } );
        }

        mFinish.setOnClickListener( new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                if (mPopupWindow != null){
                    mPopupWindow.dismiss();
                    mPopupWindow = null;
                }
            }
        } );
        mPopupWindow.show();


    }

    /**
     * 更改导航的样式
     *
     * @param currentSelectPosition
     */
    private void changeNavStyle(int currentSelectPosition) {

        try {
            if (currentSelectPosition == 4 && !UserHelper.getInstence().isLogin()) {
                ARouter.getInstance().build(ARouters.PATH_LOGIN).navigation();
                return;
            }
            if (currentSelectPosition != this.mOldSelectPosition) {

                FragmentTransaction ft = getSupportFragmentManager()
                        .beginTransaction();

                ft.hide(mFragments.get(mOldSelectPosition));

                if (!mFragments.get(currentSelectPosition).isAdded()) {
                    ft.add(R.id.fl_content, mFragments.get(currentSelectPosition), mFragments.get(currentSelectPosition).getClass().getName());
                }

                ft.show(mFragments.get(currentSelectPosition)).commitAllowingStateLoss();
                mTextViews.get(this.mOldSelectPosition).setSelected(false);
                mTextViews.get(currentSelectPosition).setSelected(true);
                this.mOldSelectPosition = currentSelectPosition;
            }
        } catch (Exception e) {
            Logger.e(TAG, e.toString());
        }
    }

    private Fragment getFragment(String path) {
        Fragment fragment = (Fragment) ARouter.getInstance()
                .build(path)
                .navigation();
        return fragment;
    }

    @Override
    protected void getData() {

        try {
            checkUpdataApp();
            if (UserHelper.getInstence().getUserInfo().isLogin) {
                String id = UserHelper.getInstence().getUserInfo().getId();
                TagAliasOperatorHelper.TagAliasBean tag = new TagAliasOperatorHelper.TagAliasBean();
                tag.setAction(TagAliasOperatorHelper.ACTION_SET);
                tag.setAlias(id);
                tag.setAliasAction(true);
                TagAliasOperatorHelper.getInstance().handleAction(this, TagAliasOperatorHelper.sequence, tag);
            } else {
                JPushInterface.deleteAlias(this, TagAliasOperatorHelper.sequence);
            }
        } catch (Exception e) {

        }
        //获取活动红包，每次打开APP的时候弹出就行
        try {
            getHuoDongRed();
        } catch (Exception e) {
            Logger.e(TAG, e.toString());
        }

    }

    CommonDialog mHuoDongDialog;

    private void huoDongDialog(final HuoDongRedBean result) {

        mHuoDongShowed = true;
        if (mHuoDongDialog != null) {
            mHuoDongDialog.dismiss();
            mHuoDongDialog = null;
        }
        if (mHuoDongDialog == null) {
            mHuoDongDialog = new CommonDialog(MainActivity.this, R.layout.dialog_huodong_layout);
        }

        final ImageView ivHuoDong = (ImageView) mHuoDongDialog.getViewId(R.id.iv_huodong);
        Glide.with(BaseApplication.getApplication()).load(result.activityDesc.activeImage).into(ivHuoDong);

//        Glide.with(BaseApplication.getApplication()).load(result.activityDesc.activeImage).asBitmap().into(new SimpleTarget<Bitmap>() {
//            @Override
//            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//
//                ViewGroup.LayoutParams params = ivHuoDong.getLayoutParams();
//                params.width = resource.getWidth();
//                params.height = resource.getHeight();
//                ivHuoDong.setLayoutParams(params);
//                ivHuoDong.setImageBitmap(resource);
//            }
//        });

        mHuoDongDialog.getViewId(R.id.iv_huodong_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHuoDongDialog.dismiss();
            }
        });
        mHuoDongDialog.getViewId(R.id.cl_huodong_parent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    RouterBean routerBean = new RouterBean();
//                    routerBean.type = 25;// 淘宝活动链接（手淘）；
                    routerBean.id = result.activityDesc.id;
                    routerBean.activeImage = result.activityDesc.activeImage;
                    routerBean.activeName = result.activityDesc.activeName;
                    routerBean.activeUrl = result.activityDesc.activeUrl;

                    routerBean.img = result.activityDesc.img;
                    routerBean.link = result.activityDesc.link;
                    routerBean.type = result.activityDesc.type;
                    routerBean.mustParam = result.activityDesc.mustParam;
                    routerBean.attachParam = result.activityDesc.attachParam;
                    routerBean.rootName = result.activityDesc.rootName;
                    routerBean.obJump = result.activityDesc.obJump;
                    routerBean.linkAllows = result.activityDesc.linkAllows;
                    routerBean.commandCopy = result.activityDesc.commandCopy;

                    LinkRouterUtils.getInstance().setRouterBean(MainActivity.this, routerBean);
                } catch (Exception e) {
                }
                mHuoDongDialog.dismiss();
            }
        });

        /*mHuoDongDialog.getViewId(R.id.iv_huodong).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (result.activityDesc.type == 5) {
                        RouterBean routerBean = new RouterBean();
                        routerBean.type = 25;// 淘宝活动链接（手淘）；
                        routerBean.id = result.activityDesc.id;
                        routerBean.activeImage = result.activityDesc.activeImage;
                        routerBean.activeName = result.activityDesc.activeName;
                        routerBean.activeUrl = result.activityDesc.activeUrl;

                        LinkRouterUtils.getInstance().setRouterBean(MainActivity.this, routerBean);
                    } else {
                        toast("暂未开发type = " + result.activityDesc.type + "的情况！");
                    }
                } catch (Exception e) {
                }
                mHuoDongDialog.dismiss();
            }
        });*/
        /*mHuoDongDialog.getViewId(R.id.cl_huodong_parent).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHuoDongDialog.dismiss();
            }
        } );*/

        /*mHuoDongDialog.getViewId(R.id.iv_huodong).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
        mHuoDongDialog.show();


    }

    /**
     * 获取活动红包
     */
    private boolean mHuoDongShowed = false;//判断活动是否弹出过
    private void getHuoDongRed() {
        NetWork.getInstance()
                .setTag(Qurl.getHuoDongRed2_0)
                .getApiService(RetrofitServer.class)
                .getHuoDongRed()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<HuoDongRedBean>(MainActivity.this, true, false) {

                    @Override
                    public void onSuccess(HuoDongRedBean result) {
                        if (result == null) {
                            SPUtils.getInstance().putBoolean(BaseApplication.getApplication(), Constant.ISFIRSTOPEN, false);
                            return;
                        }

                        if (result.activityDesc != null && !mHuoDongShowed){
                            huoDongDialog(result);
                        }
                        boolean iSFirst = SPUtils.getInstance().getBoolean(BaseApplication.getApplication(), Constant.ISFIRSTOPEN, true);
                        if (result.miCardPeck != null){
                            if (iSFirst){
                                getPopwindow(result.miCardPeck.activeImage,1);
                            }else if (UserHelper.getInstence().isLogin()){
                                getPopwindow(result.miCardPeck.activeImage,2);
                            }
                        }
                        SPUtils.getInstance().putBoolean(BaseApplication.getApplication(), Constant.ISFIRSTOPEN, false);
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed(errorCode, moreInfo);
                        SPUtils.getInstance().putBoolean(BaseApplication.getApplication(), Constant.ISFIRSTOPEN, false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        SPUtils.getInstance().putBoolean(BaseApplication.getApplication(), Constant.ISFIRSTOPEN, false);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.main_item0) {
            changeNavStyle(0);
        }
//        else if (v.getId() == R.id.main_item1) {
//            changeNavStyle(1);
//        } else if (v.getId() == R.id.main_item2) {
//            changeNavStyle(2);
//        }

        else if (v.getId() == R.id.main_item3) {
            changeNavStyle(1);
        } else if (v.getId() == R.id.main_item4) {
            changeNavStyle(2);
        }
    }

    @Override
    protected void savedInstance(@Nullable Bundle savedInstanceState) {
        super.savedInstance(savedInstanceState);
        if (savedInstanceState != null) {

            //恢复之前的选中状态
            this.mOldSelectPosition = savedInstanceState.getInt("mOldSelectPosition");
            mTags = savedInstanceState.getStringArrayList("mTags");
            if (this.mFragments == null) {
                this.mFragments = new ArrayList<>();
            } else {
                mFragments.clear();
            }
            for (String fName : mTags) {
                mFragments.add(getSupportFragmentManager().findFragmentByTag(fName));
            }

        } else {

            if (mFragments == null) {
                mFragments = new ArrayList<>();
            }

            //todo main更换不同的界面是只需要更换这里的东西就行
            mFragments.add(getFragment(ARouters.PATH_HOME));//首页
            /*Fragment brand = (Fragment) ARouter.getInstance()
                    .build(ARouters.PATH_BRAND)
                    .withString(Constant.TITLE, "品牌闪购")
                    .navigation();
            mFragments.add(brand);//品牌闪购*/
//            mFragments.add(getFragment(ARouters.PATH_SUPERENTRANCE));//超级入口
//            mFragments.add(getFragment(ARouters.PATH_BRAND));//品牌闪购

            /*Fragment superVip = (Fragment) ARouter.getInstance()
                    .build(ARouters.PATH_SUPER_VIP)
                    .withString(Constant.TITLE, "会员")
                    .withString("loadUrl", Qurl.shengji + "?userId=" + UserHelper.getInstence().getUserInfo().getId())
                    .withInt(Constant.TYPE, 4)
                    .navigation();
            mFragments.add(superVip);//超级会员*/
//            mFragments.add(getFragment(ARouters.PATH_UP_GRADE_F));//赚钱
            mFragments.add(getFragment(ARouters.PATH_FRIENDS));//APP圈
            mFragments.add(getFragment(ARouters.PATH_MINE));//我的

            if (mTags == null) {
                mTags = new ArrayList<>();
            } else {
                mTags.clear();
            }

            for (int i = 0; i < this.mFragments.size(); i++) {

                //这里存放的是fragment的name，以便于跟切换状态里面的一样
                mTags.add(mFragments.get(i).getClass().getName());
            }

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fl_content, mFragments.get(0), mFragments.get(0).getClass().getName())
                    .show(mFragments.get(this.mOldSelectPosition)).commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putInt("mOldSelectPosition", this.mOldSelectPosition);
        outState.putStringArrayList("mTags", this.mTags);

        for (int i = 0; i < mFragments.size(); i++) {
            if (!mFragments.get(i).isAdded()) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.fl_content, mFragments.get(i), mFragments.get(i).getClass().getName())
                        .hide(mFragments.get(i))
                        .commit();
            }
        }

        super.onSaveInstanceState(outState);
    }

    //记录用户首次点击返回键的时间
    private long firstTime = 0;

    @Override
    public void onBackPressed() {
        long secondTime = System.currentTimeMillis();
        if (secondTime - firstTime > 2000) {
            toast("再按一次退出程序");
            firstTime = secondTime;
        } else {
            ActivityStackManager.getInstance().finishAllActivity();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String appHost = SPUtils.getInstance().getString(BaseApplication.getApplication(), "appHost");
        if (TextUtils.isEmpty(appHost)) {
            //获取分享连接图片的域名
            getShareHost();
        }
        if (!UserHelper.getInstence().isLogin()){
            EventBus.getDefault().post(Constant.HOME_ALL_BOTTOM);
        }
    }

    private void getShareHost() {
        NetWork.getInstance()
                .setTag(Qurl.getShareProductUrl)
                .getApiService(RetrofitServer.class)
                .getShareProductUrl()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<GetHostUrlBean>(MainActivity.this, false, false) {

                    @Override
                    public void onSuccess(GetHostUrlBean result) {
                        if (result == null || result.show == null || TextUtils.isEmpty(result.show.appHost)) {
                            return;
                        }
                        SPUtils.getInstance().putString(BaseApplication.getApplication(), "appHost", result.show.appHost);
                    }
                });
    }

    /**
     * 检查APP更新
     */
    private void checkUpdataApp() {

        Map<String, String> allParam = new HashMap<>();
        allParam.put("type", 1 + "");
        NetWork.getInstance()
                .setTag(Qurl.appUpdata)
                .getApiService(RetrofitServer.class)
                .appUpdata(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<com.common.app.http.bean.AppUpdataBean>(MainActivity.this, true, false) {

                    @Override
                    public void onSuccess(com.common.app.http.bean.AppUpdataBean result) {
                        if (result == null || result.maxApp == null || TextUtils.isEmpty(result.maxApp.versionNumber)) {
                            return;
                        }
                        String localVersionName = DeviceUtils.getLocalVersionName(BaseApplication.getApplication());
                        String versionNumber = result.maxApp.versionNumber;


                        long localVersion = getCode(localVersionName);
                        long httpVersion = getCode(versionNumber);

                        if (localVersion < httpVersion) {
                            //APP有更新
                            mDownloadQnyUrl = result.maxApp.downloadQnyUrl;
                            showUpdataDialog(result.maxApp.versionDescribe);
                        }

//                        if (!TextUtils.equals(localVersionName, result.maxApp.versionNumber)) {
//                            //APP有更新
//                            mDownloadQnyUrl = result.maxApp.downloadQnyUrl;
//                            showUpdataDialog(result.maxApp.versionDescribe);
//                        }

                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed(errorCode, moreInfo);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });
    }

    private long getCode(String version) {
        String[] versionNum = version.split("\\.");
        StringBuilder sb = new StringBuilder();
        if (versionNum.length <= 0) {
            return 0;
        }
        for (int i = 0; i < versionNum.length; i++) {
            sb.append(versionNum[i]);
        }
        return Integer.valueOf(sb.toString());

    }

    private boolean serviceIsOpen = false;
    private MyServiceBinder mMyServiceBinder;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMyServiceBinder = (MyServiceBinder) service;
            mMyServiceBinder.startDownload(mDownloadQnyUrl);
            serviceIsOpen = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    //APP更新弹出框
    private CommonDialog mAppUpdataDialog;

    /**
     * 显示APP更新提示
     */
    private void showUpdataDialog(String versionDescribe) {

        if (mAppUpdataDialog != null) {
            mAppUpdataDialog.dismiss();
            mAppUpdataDialog = null;
        }
        if (mAppUpdataDialog == null) {
            mAppUpdataDialog = new CommonDialog(MainActivity.this, R.layout.dialog_app_updata);
        }
        ((TextView) mAppUpdataDialog.getViewId(R.id.tv_dialog_content)).setMovementMethod(ScrollingMovementMethod.getInstance());
        mAppUpdataDialog.setTextView(R.id.tv_dialog_content, versionDescribe);
        mAppUpdataDialog.getViewId(R.id.tv_dialog_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndPermission.with(MainActivity.this)
                        .permission(Permission.Group.STORAGE)
                        .onGranted(new Action() {
                            @Override
                            public void onAction(List<String> permissions) {

                                //权限申请成功，绑定更新服务
                                Intent bindIntent = new Intent(MainActivity.this, AppUpdataService.class);
                                bindService(bindIntent, mServiceConnection, BIND_AUTO_CREATE);
                                mAppUpdataDialog.dismiss();
                            }
                        })
                        .onDenied(new Action() {
                            @Override
                            public void onAction(List<String> permissions) {

                                //权限申请失败
                            }
                        }).start();
            }
        });
//        mAppUpdataDialog.getViewId(R.id.tv_dialog_cancel).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mAppUpdataDialog.dismiss();
//            }
//        });
        mAppUpdataDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mStartIntent != null) {
            stopService(mStartIntent);
        }
        if (mServiceConnection != null && serviceIsOpen) {
            unbindService(mServiceConnection);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeNav(String message) {
        if (message.equalsIgnoreCase(Constant.LOGIN_SUCCESS) || message.equalsIgnoreCase(Constant.LOGOUT_SUCCESS)) {
            if (this.mOldSelectPosition == 4) {
                finish();
                startActivity(getIntent());
                changeNavStyle(0);
            }
            if (message.equalsIgnoreCase(Constant.LOGIN_SUCCESS)) {
                finish();
                startActivity(getIntent());
                String id = UserHelper.getInstence().getUserInfo().getId();
                TagAliasOperatorHelper.TagAliasBean tag = new TagAliasOperatorHelper.TagAliasBean();
                tag.setAction(TagAliasOperatorHelper.ACTION_SET);
                tag.setAlias(id);
                tag.setAliasAction(true);
                Logger.e("---tag", id);
                TagAliasOperatorHelper.getInstance().handleAction(this, TagAliasOperatorHelper.sequence, tag);
            }else {
                finish();
                startActivity(getIntent());
                JPushInterface.clearAllNotifications(this);
            }
            try{
                getHuoDongRed();
            }catch (Exception e){

            }
        } else if (message.equalsIgnoreCase(Constant.ONEKEY_HOME)) {
            finish();
            startActivity(getIntent());
            changeNavStyle(0);
            ActivityStackManager.getInstance().finishActivityToLast();
        }else if (message.equalsIgnoreCase(Constant.THREEKEY_HOME)) {
            //商品订单购买支付成功以后   返回到赚钱界面
            changeNavStyle(2);
            ActivityStackManager.getInstance().finishActivityToLast();
        }
    }

}
