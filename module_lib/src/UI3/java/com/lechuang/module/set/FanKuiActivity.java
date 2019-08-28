package java.com.lechuang.module.set;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.base.BaseApplication;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.utils.Logger;
import com.common.app.utils.Utils;
import com.lechuang.module.R;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.com.lechuang.module.ModuleApi;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_FANKUI)
public class FanKuiActivity extends BaseActivity implements View.OnClickListener, BaseQuickAdapter.OnItemClickListener {

    private RecyclerView mRvFanKuiImg;
    private EditText mEtFanKui;
    private TextView mTvFanHint,mTvFanKuiComlpete;
    private BaseQuickAdapter<String, BaseViewHolder> mBaseQuickAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_fan_kui;
    }

    @Override
    protected void findViews() {

        ((TextView) $(R.id.iv_common_title)).setText("意见反馈");
        mRvFanKuiImg = $(R.id.rv_fankui_img);
        mEtFanKui = $(R.id.et_fankui);
        mTvFanHint = $(R.id.tv_fankui_hint);
        mTvFanKuiComlpete = $(R.id.tv_fankui_complete);

        $(R.id.iv_common_back).setOnClickListener(this);
        mTvFanKuiComlpete.setOnClickListener(this);
    }

    @Override
    protected void initView() {
        mEtFanKui.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                mTvFanHint.setText("您还能输入" + (300 - s.toString().length()) + "字");
                Logger.e("-----", s + "");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private ArrayList<String> localFiles;

    @Override
    protected void getData() {
        mEtFanKui.setText("");
        if (localFiles == null) {
            localFiles = new ArrayList<>();
        }
        localFiles.clear();
        if (localFiles.size() < 4) {
            localFiles.add("");
        }
        if (mBaseQuickAdapter == null) {
            mBaseQuickAdapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_fankui, localFiles) {

                @Override
                protected void convert(BaseViewHolder helper, String item) {
                    try {
                        ImageView ivFanKui = helper.getView(R.id.iv_fankui_img);
                        if (TextUtils.equals("", item)) {
                            Glide.with(BaseApplication.getApplication()).load(R.drawable.img_fankui).into(ivFanKui);
                        } else {
                            Glide.with(BaseApplication.getApplication()).load(item).into(ivFanKui);
                        }
                    } catch (Exception e) {

                    }
                }
            };
            GridLayoutManager gridLayoutManager = new GridLayoutManager(FanKuiActivity.this, 4);
            mRvFanKuiImg.setLayoutManager(gridLayoutManager);
            mRvFanKuiImg.setAdapter(mBaseQuickAdapter);
            mBaseQuickAdapter.setOnItemClickListener(this);
        } else {
            mBaseQuickAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.iv_common_back) {
            finish();
        } else if (id == R.id.tv_fankui_complete) {
            mTvFanKuiComlpete.setEnabled(false);
            //提交
            submitData();
        }
    }

    private List<File> shareFile;

    public void submitData() {
        shareFile = null;

        String fkTxt = mEtFanKui.getText().toString();
        if (shareFile == null) {
            shareFile = new ArrayList<>();
        }

        for (int i = 0; localFiles != null && i < localFiles.size(); i++) {
            if (!TextUtils.isEmpty(localFiles.get(i))) {
                shareFile.add(new File(localFiles.get(i)));
            }
        }

        if (TextUtils.isEmpty(fkTxt) && shareFile.size() <= 0) {
            Utils.toast("反馈内容不能为空！");
            mTvFanKuiComlpete.setEnabled(true);
            return;
        }


        Map<String, Object> allParam = new HashMap<>();
        allParam.clear();
        if (!TextUtils.isEmpty(fkTxt)) {
            allParam.put("opinion", fkTxt);
        }
        String leg = "";
        if (shareFile.size() > 0) {
            for (int i = 0; i < shareFile.size(); i++) {
                try {

                    leg += bitmapToString(shareFile.get(i).getAbsolutePath()) + "%lechuang%";
                } catch (Exception e) {
                    e.printStackTrace();
                    mTvFanKuiComlpete.setEnabled(true);
                }
            }
        }
        allParam.put("images", leg);

        mTvFanKuiComlpete.setEnabled(true);
        NetWork.getInstance()
                .setTag(Qurl.feedback)
                .getApiService(ModuleApi.class)
                .feedback(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<String>(FanKuiActivity.this, true, true) {

                    @Override
                    public void onSuccess(String result) {
                        if (result != null) {
                            Utils.toast(result);
                        }
                        if (result.indexOf(R.string.s_set_succes) != 1) {
                            finish();
                        }
//                        getData();
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

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

        if (TextUtils.isEmpty(localFiles.get(position))) {
            AndPermission.with(this)
                    .permission(Permission.Group.CAMERA, Permission.Group.STORAGE)
                    .onGranted(new Action() {
                        @Override
                        public void onAction(List<String> permissions) {
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                            startActivityForResult(intent, 100);
                        }
                    })
                    .onDenied(new Action() {
                        @Override
                        public void onAction(@NonNull List<String> permissions) {
                        }
                    })
                    .start();

        } else {//预览

            if (TextUtils.isEmpty(localFiles.get(localFiles.size() - 1))) {
                List<String> strings = localFiles.subList(0, localFiles.size() - 1);
                ArrayList<String> list = new ArrayList<>();
                list.addAll(strings);
                ARouter.getInstance().build(ARouters.PATH_PREIMG)
                        .withStringArrayList("urls", list)
                        .withInt("position", position)
                        .withBoolean("withDelete", true)
                        .navigation(this, 200);
            } else if (localFiles.size() == 4) {
                List<String> strings = localFiles.subList(0, localFiles.size());
                ArrayList<String> list = new ArrayList<>();
                list.addAll(strings);
                ARouter.getInstance().build(ARouters.PATH_PREIMG)
                        .withStringArrayList("urls", list)
                        .withInt("position", position)
                        .withBoolean("withDelete", true)
                        .navigation(this, 200);
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && data != null && data.getData() != null) {
            String filePathByUri;
            Uri uri = data.getData();

            if (!TextUtils.isEmpty(uri.getAuthority())) {
                Cursor cursor = getContentResolver().query(uri,
                        new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                if (null == cursor) {
                    Utils.toast("图片没找到");
                    return;
                }
                cursor.moveToFirst();
                filePathByUri = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                cursor.close();
            } else {
                filePathByUri = uri.getPath();
            }

            if (localFiles == null) {
                localFiles = new ArrayList<>();
            }
            if (!TextUtils.isEmpty(filePathByUri)) {
                localFiles.add(0, filePathByUri);
            }

            if (localFiles.size() > 3) {
                localFiles.subList(4, localFiles.size()).clear();
            }
            if (mBaseQuickAdapter != null) {
                mBaseQuickAdapter.notifyDataSetChanged();
            }
        } else if (resultCode == 200) {
            int position = data.getIntExtra("position", -1);
            if (position >= 0 && localFiles != null && position <= localFiles.size()) {
                localFiles.remove(position);
                int tag = 0;
                for (String s : localFiles) {
                    if (TextUtils.isEmpty(s)) {
                        tag++;
                    }
                }
                if (tag == 0) {
                    localFiles.add("");
                }
                if (mBaseQuickAdapter != null) {
                    mBaseQuickAdapter.notifyDataSetChanged();
                }
            }

        }
    }

    // 根据路径获得图片并压缩，返回bitmap用于显示
    public static Bitmap getSmallBitmap(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 480, 800);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }

    //计算图片的缩放值
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    //把bitmap转换成String
    public static String bitmapToString(String filePath) {
        Bitmap bm = getSmallBitmap(filePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        //1.5M的压缩后在100Kb以内，测试得值,压缩后的大小=94486字节,压缩后的大小=74473字节
        //这里的JPEG 如果换成PNG，那么压缩的就有600kB这样
        bm.compress(Bitmap.CompressFormat.JPEG, 40, baos);
        byte[] b = baos.toByteArray();
        Logger.d("d", "压缩后的大小=" + b.length);
        return Base64.encodeToString(b, Base64.DEFAULT);
    }


}
