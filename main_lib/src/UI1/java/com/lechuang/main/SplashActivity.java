package java.com.lechuang.main;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.bumptech.glide.Glide;
import com.common.BuildConfig;
import com.common.app.base.BaseApplication;
import com.common.app.base.BaseOtherActivity;
import com.common.app.constants.Constant;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.http.cancle.ApiCancleManager;
import com.common.app.utils.DeviceUtils;
import com.common.app.utils.FileUtils;
import com.common.app.utils.SPUtils;
import com.lechuang.main.R;
import com.umeng.analytics.MobclickAgent;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.com.lechuang.main.bean.AdverBean;
import java.com.lechuang.main.bean.GuideBean;
import java.com.lechuang.main.bean.QueryShowHideBean;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class SplashActivity extends BaseOtherActivity {

    private boolean mFirstOpenApp;
    private Timer mTimer;
//    private GifImageView gifImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isTaskRoot()) {
            finish();
            return;
        }
    }

    @Override
    protected int getLayoutId() {
        //布局设置在样式里面
        return R.layout.activity_splash;
    }

    @Override
    protected void findViews() {
//        gifImageView = $(R.id.iv_splash_load_gif);
    }

    @Override
    protected void initView() {
        //初始化首页的参数
//        queryShowHide();
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        SPUtils.getInstance().putString(BaseApplication.getApplication(), "appHost","");
    }


    @Override
    protected void getData() {
//        try {
//            GifDrawable gifDrawable = new GifDrawable(getResources(), R.drawable.splashgif);
//            gifImageView.setImageDrawable(gifDrawable);
//            gifDrawable.setLoopCount(1);
//            gifDrawable.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        //获取是否第一次打开APP
//        mFirstOpenApp = SPUtils.getInstance().getBoolean(BaseApplication.getApplication(), Constant.ISFIRSTOPEN, true);
        mFirstOpenApp=false;
        //如果第一次登录，加载引导页图片，并跳转到引导页(这个地方改为加载本地的数据，)
        if (mFirstOpenApp) {

            //加载引导页数据
//            getGuideData();
        } else {
            //否则的话加载广告页图片，并跳转到广告页

            //加载广告页数据
//            getAdverData();
        }
        AndPermission.with(this)
                .permission(Permission.Group.STORAGE)
                .onGranted(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        //设置启动页停留时间
                        startTimer();
                        //这里需要读写的权限
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                clearCache();
                            }
                        }).start();
                    }
                })
                .onDenied(new Action() {
                    @Override
                    public void onAction(@NonNull List<String> permissions) {
                        //设置启动页停留时间
                        startTimer();
                    }
                })
                .start();
    }
    /**
     * 清除缓存
     */
    private void clearCache() {
        File newFile = new File(Environment.getExternalStorageDirectory() + "/" + BuildConfig.FILE_PATH);
        if (!newFile.exists()) {
            newFile.mkdir();
        }
        FileUtils.deleteDir(newFile);

        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.get(BaseApplication.getApplication()).clearDiskCache();
                    }
                }).start();
            } else {
                Glide.get(BaseApplication.getApplication()).clearDiskCache();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startTimer() {

        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //跳转到main
                        if (mTimer != null) {
                            mTimer.cancel();
                            mTimer = null;
                        }
//                        if (mFirstOpenApp && mGuide != null && mGuide.size() > 0){
//                        if (mFirstOpenApp){
//                            //todo 跳转引导页
////                            Intent intent = new Intent(SplashActivity.this,GuideActivity.class);
////                            intent.putStringArrayListExtra("mGuide",mGuide);
////                            startActivity(intent);
//
//                            //之前是网络请求图片进入引导页，现在直接跳。加载本地图
//                            Intent intent = new Intent(SplashActivity.this,GuideActivity.class);
//                            startActivity(intent);
//                        }else if (mAdverStringBean != null){
//                            //todo 跳转广告页
//                            Intent intent = new Intent(SplashActivity.this,AdverActivity.class);
//                            intent.putExtra("mAdverStringBean",mAdverStringBean);
//                            startActivity(intent);
//                        }else {
                            Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                            startActivity(intent);
//                        }
                        finish();
                    }
                });
            }
        }, 500);
    }

    /**
     * 加载引导页数据
     */
    private ArrayList<String> mGuide;
    private void getGuideData() {

        //todo 只是获取网络数据
        NetWork.getInstance()
                .setTag(Qurl.guide)
                .getApiService(MainApi.class)
                .guide()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<GuideBean>(SplashActivity.this, true, false) {

                    @Override
                    public void onSuccess(GuideBean result) {
                        if (result == null || result.list == null || result.list.size() <= 0) {
                            return;
                        }
                        if (mGuide == null){
                            mGuide = new ArrayList<>();
                        }

                        mGuide.clear();
                        for (GuideBean.ListBean listBean : result.list){
                            mGuide.add(listBean.startImage);
                        }

                    }
                });

    }

    /**
     * 加载广告页数据
     */
    private AdverBean.AdvertisingImgBean mAdverStringBean;
    private void getAdverData() {
        //todo 只是获取网络数据
        NetWork.getInstance()
                .setTag(Qurl.adverInfo)
                .getApiService(MainApi.class)
                .adverInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<AdverBean>(SplashActivity.this, true, false) {

                    @Override
                    public void onSuccess(AdverBean result) {
                        if (result == null || result.advertisingImg == null) {
                            return;
                        }
                        mAdverStringBean = result.advertisingImg;
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB && Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        ApiCancleManager.getInstance().removeAll();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
