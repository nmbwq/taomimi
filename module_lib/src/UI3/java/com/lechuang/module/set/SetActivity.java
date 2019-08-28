package java.com.lechuang.module.set;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.common.BuildConfig;
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
import com.common.app.http.cancle.ApiCancleManager;
import com.common.app.receiver.TagAliasOperatorHelper;
import com.common.app.utils.FileUtils;
import com.common.app.utils.Logger;
import com.common.app.utils.OnClickEvent;
import com.common.app.utils.SPUtils;
import com.common.app.view.CommonDialog;
import com.common.app.view.GlideRoundTransform;
import com.lechuang.module.R;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import org.greenrobot.eventbus.EventBus;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.SetUserBean;
import java.com.lechuang.module.bean.UpFileBean;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


@Route(path = ARouters.PATH_SET)
public class SetActivity extends BaseActivity implements View.OnClickListener {
    @Autowired
    public String mCacheSize;
    private ImageView mIvBack, mIvHead;
    private RelativeLayout mRlUserName, mRlPhone, mRlWeiXin, mRlZhiFuBao, mRlPassWord, mRlCache;
    private LinearLayout mLlExit;
    private TextView  mTvName, mTvPhone, mTvWeixin, mTvZhifubao,mTvCacheSize,mTvSetExit, popFinish, popClear;
    private PopupWindow mPopupWindow;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_set;
    }

    @Override
    protected void findViews() {
        mIvBack = $(R.id.iv_common_back);
        mIvHead = $(R.id.iv_set_head);
        mRlUserName = $(R.id.rl_username);
        mRlPhone = $(R.id.rl_phone);
        mRlWeiXin = $(R.id.rl_weixin);
        mRlZhiFuBao = $(R.id.rl_zhifubao);
        mRlPassWord = $(R.id.rl_password);
        mRlCache = $(R.id.rl_cache);
        mTvSetExit = $(R.id.tv_set_exit);
        mTvCacheSize = $(R.id.tv_cache_size);
        mTvName = $(R.id.tv_set_name);
        mTvPhone = $(R.id.tv_set_phone);
        mTvWeixin = $(R.id.tv_set_weixin);
        mTvZhifubao = $(R.id.tv_set_zhifubao);
        ((TextView)$(R.id.iv_common_title)).setText("设置");
        $(R.id.ll_set_updata_photo).setOnClickListener(this);
        $(R.id.iv_common_back).setOnClickListener(this);
        mIvBack.setOnClickListener(this);
        mRlUserName.setOnClickListener(this);
        mRlPhone.setOnClickListener(this);
        mRlWeiXin.setOnClickListener(this);
        mRlZhiFuBao.setOnClickListener(this);
        mRlPassWord.setOnClickListener(this);
        mRlCache.setOnClickListener( new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                clearCache();
            }
        } );
        mTvSetExit.setOnClickListener(this);
        mIvBack.setEnabled(false);
    }

    @Override
    protected void initView() {

        ARouter.getInstance().inject(this);
    }

    @Override
    protected void getData() {
    }

    /**
     * 获取用户信息
     */
    private void getUserInfo() {
        ApiCancleManager.getInstance().removeAll();
        NetWork.getInstance()
                .setTag(Qurl.userInfo)
                .getApiService(ModuleApi.class)
                .userInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<SetUserBean>(SetActivity.this, false, true) {

                    @Override
                    public void onSuccess(SetUserBean result) {
                        if (result == null) {
                            return;
                        }
                        setMineData(result);
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                    }
                });
    }

    /**
     * 处理mine界面的数据
     *
     * @param result
     */
    private void setMineData(SetUserBean result) {
        try {
            mTvCacheSize.setText(mCacheSize);
            //用户头像
            Glide.with(BaseApplication.getApplication())
                    .load(UserHelper.getInstence().getUserInfo().getPhoto())
                    .placeholder(R.drawable.ic_common_user_def)
                    .transform(new GlideRoundTransform(BaseApplication.getApplication(), 100))
                    .into(mIvHead);
            if (TextUtils.isEmpty(result.nickName)) {
                mTvName.setText(result.phone.substring(0, 3) + "****" + result.phone.substring(7, 11));
            } else {
                int numb = result.nickName.length();
                if (numb > 9) {
                    mTvName.setText(TextUtils.isEmpty(result.nickName) ? result.phone : result.nickName.substring(0, 10) + "...");//用户昵称(昵称为空，显示手机号)
                } else {
                    mTvName.setText(TextUtils.isEmpty(result.nickName) ? result.phone : result.nickName);//用户昵称(昵称为空，显示手机号)
                }
            }

            mTvPhone.setText(result.phone.substring(0, 3) + "****" + result.phone.substring(7, 11));
            if (TextUtils.isEmpty(result.weixinName)) {
                mTvWeixin.setText("未绑定");
            } else {
                mTvWeixin.setText(result.weixinName);
            }
            if (TextUtils.isEmpty(result.alipayNumber)) {
                mTvZhifubao.setText("未绑定");
            } else {
                mTvZhifubao.setText(result.alipayNumber);
            }
            String phone = UserHelper.getInstence().getUserInfo().getPhone();
            UserInfoBeanDao userInfoDao = UserHelper.getInstence().getUserInfoDao();
            UserInfoBean unique = userInfoDao.queryBuilder().where(UserInfoBeanDao.Properties.Phone.eq(phone)).build().unique();
            if (unique != null) {
                unique.setZhifubaoNum(result.alipayNumber);
                userInfoDao.update(unique);
            }


        } catch (Exception e) {
            toast(e.toString());
        }

    }

    @Override
    protected void onResume() {
        super.onResume();


        getUserInfo();
        mIvBack.setEnabled(true);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.iv_common_back) {
            finish();
        } else if (i == R.id.ll_set_updata_photo) {
            updataPnoto();
        } else if (i == R.id.rl_username) {
            //修改昵称
            ARouter.getInstance().build(ARouters.PATH_NICK).navigation();
        } else if (i == R.id.rl_phone) {
            //修改手机号
            ARouter.getInstance().build(ARouters.PATH_UPDATA_PHONE).navigation();
        } else if (i == R.id.rl_weixin) {
            if (!mTvWeixin.getText().toString().equalsIgnoreCase("未绑定")) {
                toast("已绑定");
                return;
            }
            //暂时取消微信绑定换绑功能
            //通过WXAPIFactory工厂获取IWXApI的示例
            SPUtils.getInstance().putBoolean(BaseApplication.getApplication(), "tagBound", true);
            IWXAPI api = WXAPIFactory.createWXAPI(this, BuildConfig.WXAPPID, true);
            //将应用的appid注册到微信
            api.registerApp(BuildConfig.WXAPPID);
            SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";//
            req.state = "app_wechat";
            api.sendReq(req);
        } else if (i == R.id.rl_zhifubao) {
            if (TextUtils.isEmpty(UserHelper.getInstence().getUserInfo().getZhifubaoNum())) {
                ARouter.getInstance().build(ARouters.PATH_VERIFY).navigation();
            } else {
                ARouter.getInstance().build(ARouters.PATH_COMPLETE_BIND).navigation();
            }
        } else if (i == R.id.rl_password) {
            //修改密码
            ARouter.getInstance().build(ARouters.PATH_UPDATA_PSD).navigation();
        /*} else if (i == R.id.rl_message) {
            //消息
            ARouter.getInstance().build(ARouters.PATH_NEW_NOTIFY).navigation();*/
        /*} else if (i == R.id.rl_cache) {

//            showPopupWindow();*/
        } else if (i == R.id.tv_set_exit) {
            try {
                logout();
            }catch (Exception e){

            }

        } else if (i == R.id.tv_popwindow_finish) {
            mPopupWindow.dismiss();
        } else if (i == R.id.tv_popwindow_clear) {
            clearCache();
            mPopupWindow.dismiss();
        }
    }

    private void showPopupWindow() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.popupwind_setactivity_clear, null);
        mPopupWindow = new PopupWindow(contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        popFinish = (TextView) contentView.findViewById(R.id.tv_popwindow_finish);
        popClear = (TextView) contentView.findViewById(R.id.tv_popwindow_clear);
        popFinish.setOnClickListener(this);
        popClear.setOnClickListener(this);
        mPopupWindow.setContentView(contentView);
        mPopupWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getCache();//删除后从新获取文件大小
                                toast( "缓存清理成功" );
                            }
                        });
                    }
                }).start();
            } else {
                Glide.get(BaseApplication.getApplication()).clearDiskCache();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 更新头像
     */
    private CommonDialog mOpenCameraDialog = null;

    private void updataPnoto() {
        if (mOpenCameraDialog != null && mOpenCameraDialog.isShowing()) {
            mOpenCameraDialog.dismiss();
            mOpenCameraDialog = null;
        }
        mOpenCameraDialog = new CommonDialog(this, R.layout.dialog_open_camera)
                .setGravity(Gravity.BOTTOM);

        mOpenCameraDialog.getViewId(R.id.tv_paizhao).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //拍照
                openPermission(true);
                mOpenCameraDialog.dismiss();
            }
        });
        mOpenCameraDialog.getViewId(R.id.tv_xiangce).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //从相册选择
                openPermission(false);
                mOpenCameraDialog.dismiss();
            }
        });
        mOpenCameraDialog.getViewId(R.id.tv_quxiao).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOpenCameraDialog.dismiss();
            }
        });
        mOpenCameraDialog.show();
    }

    /**
     * true 为拍照，false 为从相册选择
     *
     * @param type
     */
    private void openPermission(final boolean type) {
        AndPermission.with(this)
                .permission(Permission.Group.CAMERA, Permission.Group.STORAGE)
                .onGranted(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        if (type) {
                            //true 为拍照
                            if (FileUtils.isExternalStorageReadable()) {
                                //用来存放拍照之后的图片存储路径文件夹
                                File newFile = new File(Environment.getExternalStorageDirectory() + "/" + BuildConfig.FILE_PATH);
                                if (!newFile.exists()) {
                                    newFile.mkdir();
                                }

                                File headerFile = new File(newFile, FileUtils.getNameFromDate() + ".png");
                                if (FileUtils.createFileByDeleteOldFile(headerFile)) {
                                    mHeaderFile = null;
                                    headerFileUri = null;
                                    //android 7.0
                                    //适配7.0和7.0一下的地址
                                    if (Build.VERSION.SDK_INT >= 24) {
                                        headerFileUri = FileProvider.getUriForFile(SetActivity.this, BuildConfig.FILE_PATH + ".fileProvider", headerFile);
                                    } else {
                                        headerFileUri = Uri.fromFile(headerFile);
                                    }
                                    Intent intent = new Intent();
                                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, headerFileUri);
                                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                                    startActivityForResult(intent, 300);
                                }
                            }

                        } else {
                            //false 为从相册选择
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                            startActivityForResult(intent, 100);
                        }
                    }
                })
                .onDenied(new Action() {
                    @Override
                    public void onAction(@NonNull List<String> permissions) {
                        if (AndPermission.hasAlwaysDeniedPermission(SetActivity.this, permissions)) {
                            //这个里面提示的是一直不过的权限
                            List<String> permissionNames = Permission.transformText(SetActivity.this, permissions);

                            CommonDialog commonDialog = null;
                            if (commonDialog != null && commonDialog.isShowing()) {
                                commonDialog.dismiss();
                                commonDialog = null;
                            }
                            commonDialog = new CommonDialog(SetActivity.this, R.layout.dialog_layout);

                            commonDialog.setTextView(R.id.tv_dialog_content, TextUtils.join("\n", permissionNames));
                            final CommonDialog finalCommonDialog = commonDialog;
                            commonDialog.getViewId(R.id.tv_dialog_cancel).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    finalCommonDialog.dismiss();
                                }
                            });

                            commonDialog.setCanceledOnTouchOutside(false);
                            commonDialog.show();
                        }
                    }
                })
                .start();
    }

    /**
     * 裁剪图片
     *
     * @param data
     */
    private File mHeaderFile;

    private Uri cropPic(Uri data) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(data, "image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);

        //用来存放拍照之后的图片存储路径文件夹
        File newFile = new File(Environment.getExternalStorageDirectory() + "/" + BuildConfig.FILE_PATH);
        if (!newFile.exists()) {
            newFile.mkdir();
        }
        //裁剪后的图片Uri路径，uritempFile为Uri类变量
        mHeaderFile = new File(newFile, FileUtils.getNameFromDate() + ".png");

        Uri uritempFile = Uri.fromFile(mHeaderFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        startActivityForResult(intent, 200);
        return uritempFile;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    //用来存放拍照之后的图片存储路径文件
    private Uri headerFileUri;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 300 && headerFileUri != null) {
            headerFileUri = cropPic(headerFileUri);

        }
        if (requestCode == 100 && data != null && data.getData() != null) {
            headerFileUri = cropPic(data.getData());
        } else if (requestCode == 200 && headerFileUri != null) {
            // 裁剪时,这样设置 cropIntent.putExtra("return-data", true); 处理方案如下
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(headerFileUri));
                // todo 设置图片,上传图片地址
                mIvHead.setImageBitmap(bitmap);

                String headerPath = mHeaderFile.getAbsolutePath();//图片地址，直接上传
                File file = new File(headerPath);
                updataPhoto(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * 退出登录
     */
    private void logout() {
        String id = UserHelper.getInstence().getUserInfo().getId();
        TagAliasOperatorHelper.TagAliasBean alias = new TagAliasOperatorHelper.TagAliasBean();
        alias.setAction(TagAliasOperatorHelper.ACTION_DELETE);
        alias.setAlias(id);
        alias.setAliasAction(true);
        Logger.e("---Alias", id);
        TagAliasOperatorHelper.getInstance().handleAction(this, TagAliasOperatorHelper.sequence, alias);
        NetWork.getInstance()
                .setTag(Qurl.login)
                .getApiService(ModuleApi.class)
                .logout()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<String>(SetActivity.this, false, true) {

                    @Override
                    public void onSuccess(String result) {
                        if (result == null) {
                            return;
                        }
                        toast(result);
                        UserHelper.getInstence().deleteAllUserInfo();
                        //退出成功，发送通知刷新界面
                        EventBus.getDefault().post(Constant.LOGOUT_SUCCESS);
                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        UserHelper.getInstence().deleteAllUserInfo();
                        //退出成功，发送通知刷新界面
                        EventBus.getDefault().post(Constant.LOGOUT_SUCCESS);
                        finish();
                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed(errorCode, moreInfo);
                        UserHelper.getInstence().deleteAllUserInfo();
                        //退出成功，发送通知刷新界面
                        EventBus.getDefault().post(Constant.LOGOUT_SUCCESS);
                        finish();
                    }
                });
    }

    /**
     * 更新用户头像
     *
     * @param file
     */
    private void updataPhoto(File file) {
        String safeToken = UserHelper.getInstence().getUserInfo().getSafeToken();
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part part = MultipartBody
                .Part.createFormData("file", file.getName(), fileBody);

        RequestBody requestBody = builder.addPart(part).build();

        Request request = new Request.Builder()
                .url(Qurl.HOST + Qurl.fileUpload)
                .addHeader("safeToken", TextUtils.isEmpty(safeToken) ? "" : safeToken)
                .post(requestBody)
                .build();
        Call call = new OkHttpClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                String string = response.body().string();
                final UpFileBean jsonObject = JSON.parseObject(string, new TypeReference<UpFileBean>() {
                });
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            if (jsonObject.errorCode == 200 && !TextUtils.isEmpty(jsonObject.data.imageId) && jsonObject.data.url != null && jsonObject.data.url.size() > 0 && !TextUtils.isEmpty(jsonObject.data.url.get(0))) {
                                updata(jsonObject);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void updata(final UpFileBean upFileBean) {
        Map<String, Object> allParam = new HashMap<>();
        allParam.put("photo", upFileBean.data.imageId);
        NetWork.getInstance()
                .setTag(Qurl.updateAppUser)
                .getApiService(ModuleApi.class)
                .updateAppUserPhono(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<String>(SetActivity.this, false, true) {

                    @Override
                    public void onSuccess(String result) {
                        if (result == null) {
                            return;
                        }

                        String phone = UserHelper.getInstence().getUserInfo().getPhone();

                        //更新数据
                        UserInfoBeanDao userInfoDao = UserHelper.getInstence().getUserInfoDao();
                        UserInfoBean unique = userInfoDao.queryBuilder().where(UserInfoBeanDao.Properties.Phone.eq(phone)).build().unique();
                        if (unique != null) {
                            unique.setPhoto(upFileBean.data.url.get(0));
                            userInfoDao.update(unique);
                        }

                        Glide.with(BaseApplication.getApplication())
                                .load(UserHelper.getInstence().getUserInfo().getPhoto())
                                .placeholder(R.drawable.ic_common_user_def)
                                .transform(new GlideRoundTransform(BaseApplication.getApplication(), 100))
                                .into(mIvHead);
                        getCache();
                        EventBus.getDefault().post(Constant.UPDATA_USERINFO);
                    }
                });

    }

    private void getCache() {
        //存储路径文件夹
        File newFile = new File(Environment.getExternalStorageDirectory() + "/" + BuildConfig.FILE_PATH);
        if (!newFile.exists()) {
            newFile.mkdir();
        }
        String glideFile = getCacheDir() + "/" + InternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR;
        String localFile = newFile.getAbsolutePath();
        mCacheSize = FileUtils.getAutoFileOrFilesSize(glideFile, localFile);
        mTvCacheSize.setText(mCacheSize);
    }

}
