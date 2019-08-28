package java.com.lechuang.module.productinfo;

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
import android.text.style.LeadingMarginSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.common.BuildConfig;
import com.common.app.base.BaseApplication;
import com.common.app.database.manger.UserHelper;
import com.common.app.utils.FileUtils;
import com.common.app.utils.LogUtils;
import com.common.app.utils.Logger;
import com.common.app.utils.SPUtils;
import com.common.app.utils.StringUtils;
import com.common.app.view.SquareImageView;
import com.lechuang.module.R;

import java.com.lechuang.module.bean.PreShareImageBean;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;

/**
 * @author: zhengjr
 * @since: 2018/12/15
 * @describe:
 */

public class ShareImageView {

    private PreShareImageBean mPreShareImageBean;
    private View mView;//加载绘制的图
    private TextView mTvMoreProduct, mTvPreShareQHJ, mTvPreShareYJ, mTvPreSharequan, mTvPreShareDBQHJ;
    private ImageView mTvPreShareCode, mIvPreshareChannle;
    private IShareImageView mIShareImageView;//获取图片的接口
    private DisplayMetrics metric = new DisplayMetrics();
    private Context mContext;
    private int mWidth;
    private int mHeight;
    private SquareImageView mIvPreshareProductImg;
    private boolean mDefchecked;
    private SpannableString sStr;

    public ShareImageView(Context context) {
        this.mContext = context;
        init();
    }

    private void init() {
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(metric);
        // 屏幕宽度（像素）
        mWidth = metric.widthPixels;

        mDefchecked = SPUtils.getInstance().getBoolean(BaseApplication.getApplication(),"isShowLog",false);
        if (!mDefchecked){
            mView = LayoutInflater.from(mContext).inflate(R.layout.layout_preshare_show, null);
            mIvPreshareProductImg = mView.findViewById(R.id.iv_preshare_product_img);
            mIvPreshareChannle = mView.findViewById(R.id.iv_preshare_channle);
            mTvMoreProduct = mView.findViewById(R.id.stvmore_product);
            mTvPreShareQHJ = mView.findViewById(R.id.tv_preshare_quanhoujia);
            mTvPreShareYJ = mView.findViewById(R.id.tv_preshare_yuanjia);
            mTvPreShareCode = mView.findViewById(R.id.iv_preshare_code);
            mTvPreSharequan = mView.findViewById(R.id.tv_preshare_quan);
            // 屏幕高度（像素）
            mHeight = (int) ((metric.heightPixels + getBottomStatusHeight(mContext)) * 0.8);
        }else {

            mView = LayoutInflater.from(mContext).inflate(R.layout.layout_preshare_bitmap, null);
            mIvPreshareProductImg = mView.findViewById(R.id.tv_preshare_product_img);
            mIvPreshareChannle = mView.findViewById(R.id.iv_preshare_channle);
            mTvMoreProduct = mView.findViewById(R.id.tv_preshare_product_name);
            mTvPreShareQHJ = mView.findViewById(R.id.tv_preshare_quanhoujia);
            mTvPreShareDBQHJ = mView.findViewById(R.id.tv_dibujiage);
            mTvPreShareYJ = mView.findViewById(R.id.tv_preshare_yuanjia);
            mTvPreShareCode = mView.findViewById(R.id.iv_preshare_code);
            mTvPreSharequan = mView.findViewById(R.id.tv_preshare_youhuiquan);

            // 屏幕高度（像素）
            mHeight = metric.heightPixels;
        }
    }

    /**
     * 获取需要分享的布局UI之后开始测量
     * 然后View和其内部的子View都具有了实际大小，也就是完成了布局，相当与添加到了界面上。接着就可以创建位图并在上面绘制了：
     *
     * @param v
     * @param width
     * @param height
     */
    private void layoutView(View v, int width, int height) {
        // 整个View的大小 参数是左上角 和右下角的坐标
        v.layout(0, 0, width, height);
        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
        /** 当然，measure完后，并不会实际改变View的尺寸，需要调用View.layout方法去进行布局。
         * 按示例调用layout函数后，View的大小将会变成你想要设置成的大小。
         */
        v.measure(measuredWidth, measuredHeight);
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
    }

    /**
     * 获取 虚拟按键的高度
     *
     * @param context
     * @return
     */
    public static int getBottomStatusHeight(Context context) {
        int totalHeight = getDpi(context);

        int contentHeight = getScreenHeight(context);

        return totalHeight - contentHeight;
    }

    //获取屏幕原始尺寸高度，包括虚拟功能键高度
    public static int getDpi(Context context) {
        int dpi = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, displayMetrics);
            dpi = displayMetrics.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dpi;
    }

    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }


    public void setShareImageBean(PreShareImageBean friendsShareImagesBean, IShareImageView iShareImageView) {
        this.mPreShareImageBean = friendsShareImagesBean;
        this.mIShareImageView = iShareImageView;
        requestLayoutDraw();
    }

    private void requestLayoutDraw() {
        if (this.mPreShareImageBean == null)
            return;

        /*
        *
        * mIvPreshareProductImg = mView.findViewById(R.id.iv_preshare_product_img);
        mIvPreshareChannle = mView.findViewById(R.id.iv_preshare_channle);
        mTvMoreProduct = mView.findViewById(R.id.stvmore_product);
        mTvPreShareQHJ = mView.findViewById(R.id.tv_preshare_quanhoujia);
        mTvPreShareYJ = mView.findViewById(R.id.tv_preshare_yuanjia);
        mTvPreShareCode = mView.findViewById(R.id.iv_preshare_code);
        * */
//        SJ(mContext,mTvMoreProduct,mPreShareImageBean.productName,20);
        mTvMoreProduct.setText(getSpannableString(mPreShareImageBean.productName));


        if (!mDefchecked){
            mIvPreshareChannle.setImageResource(mPreShareImageBean.shopType == 1 ? com.common.R.drawable.ic_tb_more : com.common.R.drawable.ic_tm_more);
            mTvPreShareQHJ.setText(mPreShareImageBean.quanhoujia);//商品券后价，店铺价
            mTvPreShareYJ.setText(StringUtils.stringToStringDeleteZero(mPreShareImageBean.yuanjia));//商品原价
            mTvPreShareYJ.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            mTvPreSharequan.setText(mPreShareImageBean.quan + "元");
        }else {
            mIvPreshareChannle.setImageResource(mPreShareImageBean.shopType == 1 ? com.common.R.drawable.ic_preshare_tb : com.common.R.drawable.ic_preshare_tm);
            mTvPreShareQHJ.setText("" + mPreShareImageBean.quanhoujia);//商品券后价，店铺价
            //商品券后价，店铺价
//            mTvPreShareDBQHJ.setText(mPreShareImageBean.quanhoujia);
            sStr=new SpannableString( "¥"+mPreShareImageBean.quanhoujia );
            sStr.setSpan( new AbsoluteSizeSpan( 40 ),0,1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
            mTvPreShareDBQHJ.setText( sStr );
            mTvPreShareYJ.setText("原  价 ¥ " + StringUtils.stringToStringDeleteZero(mPreShareImageBean.yuanjia));//商品原价
            mTvPreShareYJ.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            mTvPreSharequan.setText(mPreShareImageBean.quan);
        }


        Bitmap preShareImgBit = BitmapFactory.decodeFile(mPreShareImageBean.localImageCache);
        mIvPreshareProductImg.setImageBitmap(preShareImgBit);
        if (mDefchecked){
            Bitmap preShareCodeBit = BitmapFactory.decodeFile(mPreShareImageBean.localCodeCacheShowLogo);
            mTvPreShareCode.setImageBitmap(preShareCodeBit);
        }else {
            Bitmap preShareCodeBit = BitmapFactory.decodeFile(mPreShareImageBean.localCodeCache);
            mTvPreShareCode.setImageBitmap(preShareCodeBit);
        }

        //测量
        layoutView(mView, mWidth, mHeight);
        Bitmap bmp = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        canvas.drawColor(Color.WHITE);


//        mView.layout(0, 0, mWidth, mHeight);
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

            if (mIShareImageView != null) {
                mIShareImageView.shareImageView(shareFile.getAbsolutePath());
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
            if (mIShareImageView != null) {
                mIShareImageView.shareImageView(null);
            }
        }
    }

    /**
     * 首行缩进的SpannableString
     *
     * @param description 描述信息
     */
    private SpannableString getSpannableString(String description) {
        SpannableString spannableString = new SpannableString(description);
        LeadingMarginSpan leadingMarginSpan = new LeadingMarginSpan.Standard(dp2px(mContext, 20), 0);//仅首行缩进
        spannableString.setSpan(leadingMarginSpan, 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public interface IShareImageView {
        void shareImageView(String bitmap);
    }

    /**
     * 将dp转换成px
     *
     * @param context
     * @param dpValue
     * @return
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
