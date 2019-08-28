package com.common.app.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.TypefaceSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.BuildConfig;
import com.common.R;
import com.common.app.http.bean.FriendsShareBean;
import com.common.app.view.SquareImageView;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @author: zhengjr
 * @since: 2018/12/11
 * @describe:
 */

public class ShareImageView{

    private FriendsShareBean mFriendsShareBean;//需要设置的数据
    private View mView;//加载绘制的图
    private TextView mTvPreShareProductName, mTvPreShareQHJ, mTvPreShareYJ, mTvPreShareYHQ, mTvPreShareDPJ;
    private ImageView mTvPreShareCode,mIvPreshareChannle;
    private SquareImageView mTvPreShareImg;
    private IShareImageView mIShareImageView;//获取图片的接口
    private DisplayMetrics metric = new DisplayMetrics();
    private Context mContext;
    private int mWidth;
    private int mHeight;
    private SpannableString sStr;

    public ShareImageView(Context context) {
        this.mContext = context;
        init();
    }

    private void init() {
        mView = LayoutInflater.from(mContext).inflate(R.layout.layout_preshare_bitmap, null);
        mTvPreShareProductName = mView.findViewById(R.id.tv_preshare_product_name);
        mTvPreShareQHJ = mView.findViewById(R.id.tv_preshare_quanhoujia);
        mTvPreShareYJ = mView.findViewById(R.id.tv_preshare_yuanjia);
        mTvPreShareYHQ = mView.findViewById(R.id.tv_preshare_youhuiquan);
        mTvPreShareDPJ = mView.findViewById(R.id.tv_dibujiage);
        mTvPreShareImg = mView.findViewById(R.id.tv_preshare_product_img);
        mTvPreShareCode = mView.findViewById(R.id.iv_preshare_code);
        mIvPreshareChannle = mView.findViewById(R.id.iv_preshare_channle);
        ((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(metric);
        // 屏幕宽度（像素）
        mWidth = metric.widthPixels;
        // 屏幕高度（像素）
        mHeight = metric.heightPixels;
        //测量
        layoutView(mView, mWidth, mHeight);
    }

    /**
     * 获取需要分享的布局UI之后开始测量
     * 然后View和其内部的子View都具有了实际大小，也就是完成了布局，相当与添加到了界面上。接着就可以创建位图并在上面绘制了：
     * @param v
     * @param width
     * @param height
     */
    private void layoutView(View v, int width, int height) {
        // 整个View的大小 参数是左上角 和右下角的坐标
        v.layout(0, 0, width, height);
        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(10000, View.MeasureSpec.AT_MOST);
        /** 当然，measure完后，并不会实际改变View的尺寸，需要调用View.layout方法去进行布局。
         * 按示例调用layout函数后，View的大小将会变成你想要设置成的大小。
         */
        v.measure(measuredWidth, measuredHeight);
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
    }


    public void setShareImageBean(FriendsShareBean friendsShareImagesBean,IShareImageView iShareImageView) {
        this.mFriendsShareBean = friendsShareImagesBean;
        this.mIShareImageView = iShareImageView;
        requestLayoutDraw();
    }

    private void requestLayoutDraw() {
        if (this.mFriendsShareBean == null)
            return;
        mTvPreShareProductName.setText(mFriendsShareBean.cfProductTitle);//商品标题
        mIvPreshareChannle.setImageResource(mFriendsShareBean.shopType == 1 ? R.drawable.ic_preshare_tb : R.drawable.ic_preshare_tm);
        mTvPreShareQHJ.setText(mFriendsShareBean.cfCouponAfterPrice);//商品券后价，店铺价
        mTvPreShareYJ.setText("原   价 ￥" + StringUtils.stringToStringDeleteZero(mFriendsShareBean.cfPrice));//商品原价
        mTvPreShareYJ.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        mTvPreShareYHQ.setText(mFriendsShareBean.couponPrice);//优惠券
//        mTvPreShareDPJ.setText("9.99");//商品券后价，店铺价
        sStr=new SpannableString( "¥"+mFriendsShareBean.cfCouponAfterPrice );
        sStr.setSpan( new AbsoluteSizeSpan( 40 ),0,1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
        mTvPreShareDPJ.setText(sStr);
        Bitmap preShareImgBit = BitmapFactory.decodeFile(mFriendsShareBean.preShareImgLoadUrl);
        mTvPreShareImg.setImageBitmap(preShareImgBit);
        Bitmap preShareCodeBit = BitmapFactory.decodeFile(mFriendsShareBean.preShareCodeLoadUrl);
        mTvPreShareCode.setImageBitmap(preShareCodeBit);
        Bitmap bmp = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        canvas.drawColor(Color.WHITE);
        mView.layout(0, 0, mWidth, mHeight);
        mView.draw(canvas);

        File newFile = new File(Environment.getExternalStorageDirectory() + "/" + BuildConfig.FILE_PATH);
        if (!newFile.exists()) {
            newFile.mkdir();
        }
        File shareFile = new File(newFile, FileUtils.getNameFromDate() + ".png");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(shareFile);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100,
                    fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

            if (mIShareImageView != null){
                mIShareImageView.shareImageView(shareFile);
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
            if (mIShareImageView != null){
                mIShareImageView.shareImageView(null);
            }
        }


        /*

        setDrawingCacheEnabled(true);
        buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(getDrawingCache());
        setDrawingCacheEnabled(false);

        File newFile = new File(Environment.getExternalStorageDirectory() + "/" + BuildConfig.FILE_PATH);
        if (!newFile.exists()) {
            newFile.mkdir();
        }
        File shareFile = new File(newFile, FileUtils.getNameFromDate() + ".png");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(shareFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                    fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

            if (mIShareImageView != null){
                mIShareImageView.shareImageView(shareFile);
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
            if (mIShareImageView != null){
                mIShareImageView.shareImageView(null);
            }
        }*/


    }

    public interface IShareImageView{
        void shareImageView(File bitmap);
    }


}
