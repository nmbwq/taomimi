package java.com.lechuang.module.aboutus;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.base.BaseApplication;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.http.api.RetrofitServer;
import com.common.app.http.bean.AppUpdataBean;
import com.common.app.service.AppUpdataService;
import com.common.app.service.MyServiceBinder;
import com.common.app.utils.DeviceUtils;
import com.common.app.view.CommonDialog;
import com.lechuang.module.R;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_ABOUT_US)
public class AboutUsActivity extends BaseActivity {


    private TextView mTvMineAboutusUpdataVersion;
    private String versionDescribe;//更新说明
    private String downloadQnyUrl;//下载地址
    private ImageView mIvAboutUs;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about_us;
    }

    @Override
    protected void findViews() {
        mTvMineAboutusUpdataVersion = $(R.id.tv_mine_aboutus_updata_version);
        mIvAboutUs = $(R.id.iv_about_us);

        ((TextView) findViewById(R.id.iv_common_title)).setText("关于我们");
        //设置当前版本号
        ((TextView) findViewById(R.id.tv_mine_aboutus_version_num)).setText(DeviceUtils.getLocalVersionName(BaseApplication.getApplication()));
        $(R.id.iv_common_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void initView() {
        mIvAboutUs.setImageResource(R.drawable.logo);
        mTvMineAboutusUpdataVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.equals(mTvMineAboutusUpdataVersion.getText().toString(), "暂无更新"))
                    showUpdataDialog(downloadQnyUrl, versionDescribe);
            }
        });
    }

    @Override
    protected void getData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUpdataApp();
    }

    /**
     * 检查APP更新
     */
    private void checkUpdataApp() {

        Map<String, String> allParam = new HashMap<>();
        allParam.put("type",1 + "");
        NetWork.getInstance()
                .setTag(Qurl.appUpdata)
                .getApiService(RetrofitServer.class)
                .appUpdata(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<AppUpdataBean>(AboutUsActivity.this, true, false) {

                    @Override
                    public void onSuccess(AppUpdataBean result) {
                        if (result == null || result.maxApp == null || TextUtils.isEmpty(result.maxApp.versionNumber)) {
                            return;
                        }
                        String localVersionName = DeviceUtils.getLocalVersionName(BaseApplication.getApplication());
                        String versionNumber = result.maxApp.versionNumber;

                        long localVersion = getCode(localVersionName);
                        long httpVersion = getCode(versionNumber);

                        if (localVersion < httpVersion) {
                            mTvMineAboutusUpdataVersion.setText(result.maxApp.versionNumber + "点击更新");
                            versionDescribe = result.maxApp.versionDescribe;
                            downloadQnyUrl = result.maxApp.downloadQnyUrl;
                        }
/*                        String localVersionName = DeviceUtils.getLocalVersionName(BaseApplication.getApplication());
                        if (!TextUtils.equals(localVersionName, result.maxApp.versionNumber)) {
                            mTvMineAboutusUpdataVersion.setText(result.maxApp.versionNumber + "点击更新");
                            versionDescribe = result.maxApp.versionDescribe;
                            downloadQnyUrl = result.maxApp.downloadQnyUrl;
                            //APP有更新
//                            showUpdataDialog(result.maxApp.downloadQnyUrl,result.maxApp.versionDescribe);
                        }*/

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
            mMyServiceBinder.startDownload(downloadQnyUrl);
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
    private void showUpdataDialog(final String loadUrl, String versionDescribe) {

        if (mAppUpdataDialog != null) {
            mAppUpdataDialog.dismiss();
            mAppUpdataDialog = null;
        }
        if (mAppUpdataDialog == null) {
            mAppUpdataDialog = new CommonDialog(AboutUsActivity.this, R.layout.dialog_app_updata);
        }

        ((TextView) mAppUpdataDialog.getViewId(R.id.tv_dialog_content)).setMovementMethod(ScrollingMovementMethod.getInstance());
        mAppUpdataDialog.setTextView(R.id.tv_dialog_content, versionDescribe);
        mAppUpdataDialog.getViewId(R.id.tv_dialog_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndPermission.with(AboutUsActivity.this)
                        .permission(Permission.Group.CAMERA, Permission.Group.STORAGE)
                        .onGranted(new Action() {
                            @Override
                            public void onAction(List<String> permissions) {

                                //权限申请成功，绑定更新服务
                                Intent bindIntent = new Intent(AboutUsActivity.this, AppUpdataService.class);
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
        if (mMyServiceBinder != null && serviceIsOpen){
            unbindService(mServiceConnection);
        }
    }
}
