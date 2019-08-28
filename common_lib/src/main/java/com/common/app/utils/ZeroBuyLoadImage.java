package com.common.app.utils;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.common.BuildConfig;
import com.common.R;
import com.common.app.base.BaseApplication;
import com.common.app.http.bean.FriendsLoadBean;
import com.common.app.http.bean.FriendsShareBean;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/9/3
 * @describe:
 */

public class ZeroBuyLoadImage {


    private List<String> mShareUrl;//网络图片集合
    private List<File> mFileList;//存储本地的图片地址
    private int count = 0;
    private OnLoadImageLisenter mOnLoadImageLisenter;
    protected CustomProgressDialog progressDialog;
    private boolean mIsCancle = false;//是否取消了下载

    private static class LazySingle {
        public static ZeroBuyLoadImage instance = new ZeroBuyLoadImage();
    }

    private ZeroBuyLoadImage() {
    }

    public static ZeroBuyLoadImage getInstance() {
        return LazySingle.instance;
    }

    @Deprecated
    public void startLoadImages(Context context, boolean isCancle, List<String> shareUrl, OnLoadImageLisenter onLoadImageLisenter) {
        if (mShareUrl != null) {
            mShareUrl.clear();
        }
        this.mShareUrl = shareUrl;
        this.mOnLoadImageLisenter = onLoadImageLisenter;
        count = 0;
        mIsCancle = false;

        //下载
        startLoad(context, isCancle);
    }

    /**
     * 朋友圈下载图片方式
     * @param context
     * @param isCancle
     * @param shareUrl
     * @param onLoadImageLisenter
     */
    private List<FriendsLoadBean> mFriendsLoadBeans;
    public void startLoadImages(Context context,boolean isCancle, ArrayList<FriendsLoadBean> friendsLoadBeans, OnLoadImageLisenter onLoadImageLisenter){
        if (mFriendsLoadBeans == null) {
            mFriendsLoadBeans = new ArrayList<>();
        }
        mFriendsLoadBeans.clear();
        this.mFriendsLoadBeans.addAll(friendsLoadBeans);
        this.mOnLoadImageLisenter = onLoadImageLisenter;
        count = 0;
        mIsCancle = false;
        startLoad(context,isCancle, friendsLoadBeans);
    }

    private void startLoad(final Context context, boolean isCancle, final ArrayList<FriendsLoadBean> friendsLoadBeans) {
        if (mFileList == null) {
            mFileList = new ArrayList<>();
        }
        mFileList.clear();

        for (int i = 0; i < mFriendsLoadBeans.size(); i++) {
            mFileList.add(null);
        }

        if (this.mFriendsLoadBeans == null || mFriendsLoadBeans.size() <= 0) {
            if (mOnLoadImageLisenter != null) {
                mOnLoadImageLisenter.onSuccess(mFileList, 0, this.mIsCancle);
            }
            return;
        }

        createProgressDialog(context, isCancle);

        for (int i = 0; i < mFriendsLoadBeans.size(); i++) {
            final int finalI = i;
            Glide.with(BaseApplication.getApplication()).load(mFriendsLoadBeans.get(i).productUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    File newFile = new File(Environment.getExternalStorageDirectory() + "/" + BuildConfig.FILE_PATH);
                    if (!newFile.exists()) {
                        newFile.mkdir();
                    }
                    File shareFile = new File(newFile, FileUtils.getNameFromDate() + ".png");

                    try {

                        //先保存商品图片到本地
                        FileOutputStream fileOutputStream = new FileOutputStream(shareFile);
                        resource.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                        fileOutputStream.flush();
                        fileOutputStream.close();

                        if (mFriendsLoadBeans.get(finalI).drawCode){//需要合成二维码
                            File shareFileCode = new File(newFile, FileUtils.getNameFromDate() + ".png");
                            //获取二维码图片
                            if (friendsLoadBeans != null && !TextUtils.isEmpty(friendsLoadBeans.get(finalI).qrCodeUrl)) {

                                boolean createCode = ZxingUtils.createQRImage(friendsLoadBeans.get(finalI).qrCodeUrl, 150, 150, 0, null, shareFileCode.getAbsolutePath());

                                if (createCode) {
                                    FriendsShareBean friendsShareBean = new FriendsShareBean();
                                    friendsShareBean.shopType = friendsLoadBeans.get(finalI).shopType;
                                    friendsShareBean.cfProductTitle = friendsLoadBeans.get(finalI).cfProductTitle;
                                    friendsShareBean.cfCouponAfterPrice = friendsLoadBeans.get(finalI).cfCouponAfterPrice;
                                    friendsShareBean.cfPrice = friendsLoadBeans.get(finalI).cfPrice;
                                    friendsShareBean.couponPrice = friendsLoadBeans.get(finalI).couponPrice;
                                    friendsShareBean.preShareImgLoadUrl = shareFile.getAbsolutePath();
                                    friendsShareBean.preShareCodeLoadUrl = shareFileCode.getAbsolutePath();
                                    ShareImageView shareImageView = new ShareImageView(context);
                                    shareImageView.setShareImageBean(friendsShareBean, new ShareImageView.IShareImageView() {
                                        @Override
                                        public void shareImageView(File bitmap) {
                                            mFileList.add(finalI, bitmap);
                                        }
                                    });
                                }else {
                                    mFileList.add(finalI, shareFile);
                                }
                            }
                        }else {//不需要合成二维码
                            mFileList.add(finalI, shareFile);
                        }

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    count++;
                    if (!ZeroBuyLoadImage.this.mIsCancle && count >= mFriendsLoadBeans.size() && mOnLoadImageLisenter != null) {
                        mOnLoadImageLisenter.onSuccess(mFileList, count - mFileList.size(), false);
                        stopProgressDialog();
                    }
                }

                @Override
                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                    super.onLoadFailed(e, errorDrawable);
                    count++;
                    if (!ZeroBuyLoadImage.this.mIsCancle && count >= mShareUrl.size() && mOnLoadImageLisenter != null) {
                        mOnLoadImageLisenter.onSuccess(mFileList, count - mFileList.size(), false);
                        stopProgressDialog();
                    }
                }
            });
        }

        showProgressDialog();

    }

    /**
     * 分享APP的下载方式
     * @param context
     * @param isCancle
     * @param shareUrl
     * @param addTagBean
     * @param onLoadImageLisenter
     */
    public void startLoadImages(Context context, boolean isCancle, List<String> shareUrl, AddTagBean addTagBean, OnLoadImageLisenter onLoadImageLisenter) {
        if (mShareUrl != null) {
            mShareUrl.clear();
        }
        this.mShareUrl = shareUrl;
        this.mOnLoadImageLisenter = onLoadImageLisenter;
        count = 0;
        mIsCancle = false;

        //下载
        startLoad(context, isCancle, addTagBean);
    }

    @Deprecated
    private void startLoad(Context context, boolean isCancle) {
        if (mFileList == null) {
            mFileList = new ArrayList<>();
        }
        mFileList.clear();

        for (int i = 0; i < mShareUrl.size(); i++) {
            mFileList.add(null);
        }

        if (this.mShareUrl == null || mShareUrl.size() <= 0) {
            if (mOnLoadImageLisenter != null) {
                mOnLoadImageLisenter.onSuccess(mFileList, 0, this.mIsCancle);
            }
            return;
        }

        createProgressDialog(context, isCancle);

//        for (String url : mShareUrl) {
        for (int i = 0; i < mShareUrl.size(); i++) {
            final int finalI = i;
            Glide.with(BaseApplication.getApplication()).load(mShareUrl.get(i)).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    File newFile = new File(Environment.getExternalStorageDirectory() + "/" + BuildConfig.FILE_PATH);
                    if (!newFile.exists()) {
                        newFile.mkdir();
                    }
                    File shareFile = new File(newFile, FileUtils.getNameFromDate() + ".png");
                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(shareFile);
                        resource.compress(Bitmap.CompressFormat.JPEG, 100,
                                fileOutputStream);
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        mFileList.add(finalI, shareFile);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    count++;
                    if (!ZeroBuyLoadImage.this.mIsCancle && count >= mShareUrl.size() && mOnLoadImageLisenter != null) {
                        mOnLoadImageLisenter.onSuccess(mFileList, count - mFileList.size(), false);
                        stopProgressDialog();
                    }
                }

                @Override
                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                    super.onLoadFailed(e, errorDrawable);
                    count++;
                    if (!ZeroBuyLoadImage.this.mIsCancle && count >= mShareUrl.size() && mOnLoadImageLisenter != null) {
                        mOnLoadImageLisenter.onSuccess(mFileList, count - mFileList.size(), false);
                        stopProgressDialog();
                    }
                }
            });
        }

        showProgressDialog();

    }

    private Paint mPaint;
    /**
     * 分享APP的下载方式
     * @param context
     * @param isCancle
     * @param addTagBean
     */
    private void startLoad(final Context context, boolean isCancle, final AddTagBean addTagBean) {
        mPaint = new Paint();
        //调整邀请码的字体大小和颜色
        mPaint.setTextSize(dp2px(context,addTagBean.size));
        mPaint.setColor(Color.BLACK);
        if (mFileList == null) {
            mFileList = new ArrayList<>();
        }
        mFileList.clear();

        for (int i = 0; i < mShareUrl.size(); i++) {
            mFileList.add(null);
        }

        if (this.mShareUrl == null || mShareUrl.size() <= 0) {
            if (mOnLoadImageLisenter != null) {
                mOnLoadImageLisenter.onSuccess(mFileList, 0, this.mIsCancle);
            }
            return;
        }

        createProgressDialog(context, isCancle);

//        for (String url : mShareUrl) {
        for (int i = 0; i < mShareUrl.size(); i++) {
            final int finalI = i;
            Glide.with(BaseApplication.getApplication()).load(mShareUrl.get(i)).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    File newFile = new File(Environment.getExternalStorageDirectory() + "/" + BuildConfig.FILE_PATH);
                    if (!newFile.exists()) {
                        newFile.mkdir();
                    }
                    File shareFile = new File(newFile, FileUtils.getNameFromDate() + ".png");
                    File shareFileCode = new File(newFile, FileUtils.getNameFromDate() + ".png");
                    try {
                        //获取二维码图片
                        if (addTagBean != null && !TextUtils.isEmpty(addTagBean.codeHttp)) {
                            boolean createCode = ZxingUtils.createQRImage(addTagBean.codeHttp, addTagBean.xWidth, addTagBean.xWidth, 0, null, shareFileCode.getAbsolutePath());
                            if (createCode) {
                                Bitmap codeBitmap = BitmapFactory.decodeFile(shareFileCode.getAbsolutePath());
                                resource = addCodeToBitmap(context, resource, codeBitmap, addTagBean);
                            }
                        }
                        FileOutputStream fileOutputStream = new FileOutputStream(shareFile);
                        resource.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        mFileList.add(finalI, shareFile);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    count++;
                    if (!ZeroBuyLoadImage.this.mIsCancle && count >= mShareUrl.size() && mOnLoadImageLisenter != null) {
                        mOnLoadImageLisenter.onSuccess(mFileList, count - mFileList.size(), false);
                        stopProgressDialog();
                    }
                }

                @Override
                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                    super.onLoadFailed(e, errorDrawable);
                    count++;
                    if (!ZeroBuyLoadImage.this.mIsCancle && count >= mShareUrl.size() && mOnLoadImageLisenter != null) {
                        mOnLoadImageLisenter.onSuccess(mFileList, count - mFileList.size(), false);
                        stopProgressDialog();
                    }
                }
            });
        }

        showProgressDialog();

    }

    private Bitmap addCodeToBitmap(Context context, Bitmap src, Bitmap code, AddTagBean addTagBean) {
        if (src == null) {
            return null;
        }

        if (code == null) {
            return src;
        }

        //获取图片的宽高
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        int logoWidth = code.getWidth();
        int logoHeight = code.getHeight();

        if (srcWidth == 0 || srcHeight == 0) {
            return null;
        }

        if (logoWidth == 0 || logoHeight == 0) {
            return src;
        }

        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);//生成的目标图片
        try {
            Rect rect = new Rect();
            mPaint.getTextBounds(addTagBean.codeNum, 0, addTagBean.codeNum.length(), rect);
            int width = rect.width();//文字宽
            int height = rect.height();//文字高

            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);//绘制目标图片
            //调整邀请码与二维码的位置
            if (addTagBean.small){
                canvas.drawText(addTagBean.codeNum, (srcWidth - width-addTagBean.xBitmap) / 2, srcHeight - height - context.getResources().getDimension(addTagBean.yBitmap), mPaint);
                canvas.drawBitmap(code, (srcWidth - logoWidth-addTagBean.xCodeNum) / 2, srcHeight - logoHeight - context.getResources().getDimension(addTagBean.yCodeNum), null);//绘制二维码到目标图片上
            }else {
                canvas.drawText(addTagBean.codeNum, (srcWidth - width-addTagBean.xBitmap) / 2, srcHeight - height - context.getResources().getDimension(addTagBean.yBitmap), mPaint);
                canvas.drawBitmap(code, (srcWidth - logoWidth-addTagBean.xCodeNum) / 2, srcHeight - logoHeight - context.getResources().getDimension(addTagBean.yCodeNum), null);//绘制二维码到目标图片上
            }

//            canvas.drawBitmap(code, addTagBean.xBitmap, addTagBean.yBitmap, null);//绘制二维码到目标图片上

            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }

        return bitmap;
    }

    /**
     * dp转换成px
     */
    private int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px转换成dp
     */
    private int px2dp(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * px转换成sp
     */
    private int px2sp(Context context, float pxValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 创建进度条实例
     */
    private void createProgressDialog(Context cxt, final boolean canCancle) {
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
            if (progressDialog == null) {

                progressDialog = new CustomProgressDialog(cxt, R.style.CustomProgressDialog);
                progressDialog.setContentView(R.layout.progress_dialog_layout);

                ImageView iv_loading_logo = (ImageView) progressDialog.findViewById(R.id.iv_loading_logo);
                ObjectAnimator animator = ObjectAnimator.ofFloat(iv_loading_logo, "rotation", 0F, 360F);
                animator.setInterpolator(new LinearInterpolator());
                animator.setRepeatCount(1000);
                animator.setDuration(2000).start();

                progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            if (canCancle && mOnLoadImageLisenter != null) {
                                mIsCancle = true;
                                mOnLoadImageLisenter.onSuccess(mFileList, count, mIsCancle);
                                stopProgressDialog();
                                return false;
                            } else {
                                return true;
                            }
                        }
                        return false;
                    }
                });

                progressDialog.setCanceledOnTouchOutside(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动加载进度条
     */
    private void showProgressDialog() {
        try {
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
    private void stopProgressDialog() {
        mIsCancle = true;
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface OnLoadImageLisenter {

        void onSuccess(List<File> path, int failNum, boolean isCancle);
    }

    public static class AddTagBean {
        public String codeHttp = "";//二维码连接
        public String codeNum = "";//邀请码
        public int xWidth = 200;//二维码的宽高
        public int xBitmap = 0;
        public int yBitmap = 0;
        public int xCodeNum = 0;
        public int yCodeNum = 0;
        public int size = 30;
        public static boolean small=false;

    }

}
