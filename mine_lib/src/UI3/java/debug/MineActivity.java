package java.debug;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.common.app.base.BaseActivity;
import com.common.app.constants.Constant;
import com.common.app.http.DownNetWork;
import com.common.app.http.DownProgressRxObserver;
import com.common.app.view.CommonDialog;
import com.common.app.utils.FileUtils;
import com.lechuang.mine.BuildConfig;
import com.lechuang.mine.R;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import okhttp3.ResponseBody;

public class MineActivity extends BaseActivity {


    private ImageView mIv_result;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mine;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void findViews() {
        mIv_result = findViewById(R.id.iv_result);
    }

//    CommonDialog mAppUpdata;

    @Override
    protected void getData() {

//        findViewById(R.id.tv_check_updata).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (mAppUpdata != null && mAppUpdata.isShowing()) {
//                    mAppUpdata.dismiss();
//                    mAppUpdata = null;
//                }
//                mAppUpdata = new CommonDialog(MineActivity.this, R.layout.dialog_app_updata);
//                final SeekBar timeline = (SeekBar) mAppUpdata.getViewId(R.id.timeline);
//                final TextView tv_jindu = (TextView) mAppUpdata.getViewId(R.id.tv_jindu);
//                mAppUpdata.getViewId(R.id.tv_dialog_sure).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        //判断是否可用于存放数据文件
//                        if (FileUtils.isExternalStorageReadable()) {
//                            //用来存放拍照之后的图片存储路径文件夹
//                            File newFile = new File(Environment.getExternalStorageDirectory() + Constant.APPDOWN_URL);
//                            if (!newFile.exists()) {
//                                newFile.mkdir();
//                            }
//                            File appDownFile = new File(Environment.getExternalStorageDirectory() + Constant.APPDOWN_URL + "/" + FileUtils.getNameFromDate() + ".apk");
//                            DownNetWork.getInstance()
//                                    .setLoadUrl("http://apk.1kg.fun/shengxinyoupin_v1.4.0_201808141841_140_jiagu_sign.apk")
//                                    .setFile(appDownFile)
//                                    .callBack(new DownProgressRxObserver<ResponseBody>(MineActivity.this) {
//                                        @Override
//                                        public void onStart() {
//                                            timeline.setProgress(0);
//                                        }
//
//                                        @Override
//                                        public void onProgress(final int progress, int count) {
//                                            timeline.setMax(count);
//
//                                            timeline.setProgress(progress);
//                                            tv_jindu.setText(progress + "");
//                                        }
//
//                                        @Override
//                                        public void onPause() {
//
//                                        }
//
//                                        @Override
//                                        public void onSuccess(String path) {
//
//                                        }
//
//                                        @Override
//                                        public void onFailed(String errorInfo) {
//
//                                        }
//                                    } );
//
//                        }
//                    }
//                });
//                mAppUpdata.show();
//
//            }
//        });
    }

    /*private CommonDialog mOpenCameraDialog = null;

    public void onClick(View view) {

        if (mOpenCameraDialog != null && mOpenCameraDialog.isShowing()) {
            mOpenCameraDialog.dismiss();
            mOpenCameraDialog = null;
        }
        mOpenCameraDialog = new CommonDialog(MineActivity.this, R.layout.dialog_open_camera)
                .setGravity(Gravity.BOTTOM);

        mOpenCameraDialog.getViewId(R.id.tv_paizhao).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //拍照
                openPermission(true);
            }
        });
        mOpenCameraDialog.getViewId(R.id.tv_xiangce).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //从相册选择
                openPermission(false);
            }
        });
        mOpenCameraDialog.getViewId(R.id.tv_quxiao).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOpenCameraDialog.dismiss();
            }
        });
        mOpenCameraDialog.show();
    }*/

    /**
     * true 为拍照，false 为从相册选择
     *
     * @param type
     *//*
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
                                File newFile = new File(Environment.getExternalStorageDirectory() + "/" + BuildConfig.APPLICATION_ID);
                                if (!newFile.exists()) {
                                    newFile.mkdir();
                                }

                                File headerFile = new File(Environment.getExternalStorageDirectory() + "/" + BuildConfig.APPLICATION_ID + Constant.HEADER);
                                if (FileUtils.createFileByDeleteOldFile(headerFile)) {
                                    //android 7.0
                                    //适配7.0和7.0一下的地址
                                    if (Build.VERSION.SDK_INT >= 24) {
                                        headerFileUri = FileProvider.getUriForFile(MineActivity.this, BuildConfig.APPLICATION_ID + ".fileProvider", headerFile);
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
                            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image*//*");
                            startActivityForResult(intent, 100);
                        }
                    }
                })
                .onDenied(new Action() {
                    @Override
                    public void onAction(@NonNull List<String> permissions) {
                        if (AndPermission.hasAlwaysDeniedPermission(MineActivity.this, permissions)) {
                            //这个里面提示的是一直不过的权限
                            List<String> permissionNames = Permission.transformText(MineActivity.this, permissions);

                            CommonDialog commonDialog = null;
                            if (commonDialog != null && commonDialog.isShowing()) {
                                commonDialog.dismiss();
                                commonDialog = null;
                            }
                            commonDialog = new CommonDialog(MineActivity.this, R.layout.dialog_layout);

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
    }*/

    /**
     * 裁剪图片
     *
     * @param data
     */
    private Uri cropPic(Uri data) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(data, "image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);


        //裁剪后的图片Uri路径，uritempFile为Uri类变量
        File headerFile = new File(Environment.getExternalStorageDirectory() + "/" + BuildConfig.APPLICATION_ID + "/header/small.jpg");
        if (FileUtils.createFileByDeleteOldFile(headerFile)) {
            Uri uritempFile = Uri.fromFile(headerFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

            startActivityForResult(intent, 200);
            return uritempFile;
        }

        Uri uritempFile = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/header/small.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, 200);
        return uritempFile;
    }

    //用来存放拍照之后的图片存储路径文件,兼容小米
    private Uri headerFileUri;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 300) {

            headerFileUri = cropPic(headerFileUri);
        }
        if (requestCode == 100) {
            headerFileUri = cropPic(data.getData());
        } else if (requestCode == 200) {
            // 裁剪时,这样设置 cropIntent.putExtra("return-data", true); 处理方案如下
            if (headerFileUri != null) {
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(headerFileUri));
                    mIv_result.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                // 把裁剪后的图片保存至本地 返回路径
//                    String urlpath = FileUtilcll.saveFile(this, "crop.jpg", bitmap);
            }
        }

    }
}
