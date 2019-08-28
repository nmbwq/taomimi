package java.com.lechuang.module.kefu;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
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
public class WxKeFuActivity1 extends BaseActivity implements View.OnClickListener {


    private TextView mTvWxKeFuAppName;
    private ImageView mIvWxKeFu;
    private TextView mTvWxKfNum;
    private TextView mTvWxKfTime;
    private String mKfWXnum;//客服微信号
    private String mKfNumFile;//客服微信号

    @Override
    protected int getLayoutId() {
        return R.layout.activity_wx_ke_fu2;
    }

    @Override
    protected void findViews() {
        mTvWxKeFuAppName = $(R.id.tv_wx_kefu_appname);
        mIvWxKeFu = $(R.id.iv_wx_kefu);
        mTvWxKfNum = $(R.id.tv_wx_kf_num);
        mTvWxKfTime = $(R.id.tv_kefu_time);
        ((TextView)$(R.id.iv_common_title)).setText("客服");
        ((TextView)$(R.id.iv_common_title)).setTextColor(getResources().getColor(R.color.white));
        ((TextView)$(R.id.tv_status_bar)).setBackgroundColor(getResources().getColor(R.color.c_main));
        $(R.id.rl_common_background).setBackgroundColor(getResources().getColor(R.color.c_main));
        ((ImageView)$(R.id.iv_common_back)).setImageResource(R.drawable.ic_common_back_white);

        mTvWxKeFuAppName.setText(BuildConfig.APP_NAME + "微信公众号");

        $(R.id.ll_wx_kf_copy).setOnClickListener(this);
        $(R.id.ll_wx_kf_save).setOnClickListener(this);
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
                .subscribe(new RxObserver<KeFuBean>(WxKeFuActivity1.this) {

                    @Override
                    public void onSuccess(KeFuBean result) {
                        if (result == null && result.customerService != null) {
                            return;
                        }
                        try {

                            //用来存放拍照之后的图片存储路径文件夹
                            File newFile = new File(Environment.getExternalStorageDirectory() + "/" + BuildConfig.FILE_PATH);
                            if (!newFile.exists()) {
                                newFile.mkdir();
                            }

                            mKfWXnum = result.customerService.wechatNumber;
                            mTvWxKeFuAppName.setText(result.customerService.name);
                            mTvWxKfNum.setText("客服专属微信号:" + result.customerService.wechatNumber);
                            mTvWxKfTime.setText( result.customerService.serviceTime);

                            File kefu = new File(newFile, FileUtils.getNameFromDate() + ".png");
                            if (ZxingUtils.createQRImage(result.customerService.wechatQrcode, 200, 200, 5, null, kefu.getAbsolutePath())) {
                                mIvWxKeFu.setImageBitmap(BitmapFactory.decodeFile(kefu.getAbsolutePath()));
                                mKfNumFile = kefu.getAbsolutePath();
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_common_back){
            finish();
        }else if (id == R.id.ll_wx_kf_copy) {
            if (!TextUtils.isEmpty(mKfWXnum)){
                // 获取系统剪贴板
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

                // 创建一个剪贴数据集，包含一个普通文本数据条目（需要复制的数据）
                ClipData clipData = ClipData.newPlainText("kefu", TextUtils.isEmpty(mKfWXnum) ? "" : mKfWXnum);

                // 把数据集设置（复制）到剪贴板
                clipboard.setPrimaryClip(clipData);
                toast("复制成功！");
            }else {
                toast("复制失败！");
            }

        }else if(id == R.id.ll_wx_kf_save){

            if (!TextUtils.isEmpty(mKfNumFile)){
                toast("以保存至"+ mKfNumFile);
            }else {
                toast("保存失败！");
            }
        }
    }
}
