package java.com.lechuang.module.kefu;


import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.common.BuildConfig;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.constants.Constant;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.utils.FileUtils;
import com.common.app.utils.ZxingUtils;
import com.lechuang.module.R;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.KeFuBean;
import java.io.File;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_WX_KEFU)
public class WxKeFuActivity extends BaseActivity implements View.OnClickListener {

    private PopupWindow mPopupWindow;
    private TextView mTvWxKfNum, tvPopKnow,mTvWxKf;
    private RelativeLayout tvPopWxkefu,tvPopWxkefuK;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_wx_ke_fu;
    }

    @Override
    protected void findViews() {
        mTvWxKfNum = $(R.id.tv_wx_kf_num);
        mTvWxKf = $(R.id.tv_zhongwen);
        ((TextView)$(R.id.iv_common_title)).setText("专属客服");

        $(R.id.ll_wx_kf_copy).setOnClickListener(this);
        $(R.id.iv_common_back).setOnClickListener(this);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void getData() {
        getKeFuData();
    }

    /**
     * 获取客服数据
     */
    private void getKeFuData() {

        NetWork.getInstance()
                .setTag(Qurl.sendVerifiCode)
                .getApiService(ModuleApi.class)
                .serviceCode()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<KeFuBean>(WxKeFuActivity.this) {

                    @Override
                    public void onSuccess(KeFuBean result) {
                        if (result == null && result.customerService != null) {
                            return;
                        }
                        try {

                            //用来存放拍照之后的图片存储路径文件夹
                            mTvWxKfNum.setText(TextUtils.isEmpty(result.customerService.wechatNumber)  ? "" : result.customerService.wechatNumber);
                            mTvWxKf.setVisibility( TextUtils.isEmpty(result.customerService.wechatNumber)  ? View.GONE : View.VISIBLE );
                            String mKfWXnum = mTvWxKfNum.getText().toString().trim();
                            if (!TextUtils.isEmpty(mTvWxKfNum.getText().toString().trim())){
                                // 获取系统剪贴板
                                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

                                // 创建一个剪贴数据集，包含一个普通文本数据条目（需要复制的数据）
                                ClipData clipData = ClipData.newPlainText("app_kefu", TextUtils.isEmpty(mKfWXnum) ? "" : mKfWXnum);

                                // 把数据集设置（复制）到剪贴板
                                clipboard.setPrimaryClip(clipData);
//                                toast("复制成功！");
                                showPopupWindow();
                            }else {
//                                toast("复制失败！");
                            }

                        } catch (Exception e) {

                        }

                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                    }
                });
    }

    //弹出提示
    private void showPopupWindow() {
        View contentView = LayoutInflater.from ( this ).inflate ( R.layout.popupwind_wxkefu_copy, null );
        mPopupWindow = new PopupWindow( contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true );
        mPopupWindow.setFocusable( true );
        mPopupWindow.setOutsideTouchable(false);
        tvPopKnow = (TextView) contentView.findViewById ( R.id.tv_popwindow_know );
        tvPopWxkefu = (RelativeLayout) contentView.findViewById ( R.id.rl_popwindow_wxkefu );
        tvPopWxkefuK = (RelativeLayout) contentView.findViewById ( R.id.rl_popwindow_wxkefu_kuang );
        /*contentView.setOnKeyListener( new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK){
                    Log.e( "tag1","返回值!!!!!!"+mPopupWindow.isShowing() );
                }
                return false;
            }
        } );*/
        /*contentView.setOnTouchListener( new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mPopupWindow.dismiss();
                return false;
            }
        } );*/
        tvPopKnow.setOnClickListener ( this );
        tvPopWxkefuK.setOnClickListener ( this );
        mPopupWindow.setBackgroundDrawable( new ColorDrawable(  ) );
        mPopupWindow.setContentView ( contentView );
        mPopupWindow.showAtLocation ( contentView, Gravity.CENTER, 0, 0 );
        contentView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        } );
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_common_back){
            finish();
        }else if (id == R.id.ll_wx_kf_copy) {
            String mKfWXnum = mTvWxKfNum.getText().toString().trim();
            if (!TextUtils.isEmpty(mTvWxKfNum.getText().toString().trim())){
                // 获取系统剪贴板
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

                // 创建一个剪贴数据集，包含一个普通文本数据条目（需要复制的数据）
                ClipData clipData = ClipData.newPlainText("app_kefu", TextUtils.isEmpty(mKfWXnum) ? "" : mKfWXnum);

                // 把数据集设置（复制）到剪贴板
                clipboard.setPrimaryClip(clipData);
                getWechatApi();
//                toast("复制成功！");
            }else {
//                toast("复制失败！");
            }

        } else if (id == R.id.tv_popwindow_know) {
            getWechatApi();
        } else if (id == R.id.rl_popwindow_wxkefu){
            mPopupWindow.dismiss ();
        } else if (id == R.id.rl_popwindow_wxkefu_kuang){
        }
    }
    /**
     * 跳转到微信
     */
    private void getWechatApi(){
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            ComponentName cmp = new ComponentName("com.tencent.mm","com.tencent.mm.ui.LauncherUI");
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            toast( "检查到您手机没有安装微信，请安装后使用该功能" );
        }
    }

    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e( "tag1","返回值"+mPopupWindow.isShowing() );
        if (keyCode == KeyEvent.KEYCODE_BACK){
            mPopupWindow.dismiss();
            if (mPopupWindow.isShowing()){
                mPopupWindow.dismiss();
            }else {
                finish(  );
            }
            return false;
        }
        return super.onKeyDown( keyCode, event );
    }*/
}
