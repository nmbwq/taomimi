package java.com.lechuang.module.productinfo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.LeadingMarginSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.common.BuildConfig;
import com.common.app.base.BaseActivity;
import com.common.app.base.BaseAdapter;
import com.common.app.base.BaseApplication;
import com.common.app.base.ComponentViewHolder;
import com.common.app.database.manger.UserHelper;
import com.common.app.utils.FileUtils;
import com.common.app.utils.LogUtils;
import com.common.app.utils.SPUtils;
import com.common.app.utils.StringUtils;
import com.common.app.utils.ZxingUtils;
import com.lechuang.module.R;

import java.com.lechuang.module.bean.PreShareImageBean;
import java.com.lechuang.module.bean.ProductionImgEntity;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ProductionImgActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTvSuccess;
    private RadioButton mRbIsShowLog;
    private boolean mDefchecked = true;//APPlogo的标识是否显示，默认第一次是不显示
    private RecyclerView mRvProImg;
    private BaseAdapter<ProductionImgEntity, ComponentViewHolder> mBaseAdapter;
    private List<PreShareImageBean> mPreShareImageBeans;
    private int mCurrentSelecterPosition = 0;
    private DisplayMetrics metric = new DisplayMetrics();
    private int mWidth;
    private int mHeight;
    private ImageView mIvProImgShowImg;
    private String mSavePath;
    private String num="0";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_production_img;
    }

    @Override
    protected void findViews() {
        ((TextView) $(R.id.iv_common_title)).setText("制作二维码分享图");
        mTvSuccess = $(R.id.tv_common_right);
        mTvSuccess.setTextColor(getResources().getColor(R.color.black));
        mRbIsShowLog = $(R.id.rb_proimg_showlog);
        mRvProImg = $(R.id.rv_proimg);
        mIvProImgShowImg = $(R.id.iv_proimg_showimg);

        mTvSuccess.setText("完成");
        mTvSuccess.setVisibility(View.VISIBLE);
        $(R.id.iv_common_back).setOnClickListener(this);
        mTvSuccess.setOnClickListener(this);


        mDefchecked = SPUtils.getInstance().getBoolean(BaseApplication.getApplication(),"isShowLog",mDefchecked);
        mRbIsShowLog.setChecked(mDefchecked);
    }

    @Override
    protected void initView() {

        getWindowManager().getDefaultDisplay().getMetrics(metric);
        // 屏幕宽度（像素）
        mWidth = metric.widthPixels;


        mRbIsShowLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDefchecked = !mDefchecked;
                mRbIsShowLog.setChecked(mDefchecked);
                productionImageShow(mCurrentSelecterPosition);
            }
        });
        String jsonString = getIntent().getStringExtra("mPreShareImageBeans");
        mPreShareImageBeans = JSON.parseArray(jsonString, PreShareImageBean.class);

        if (mPreShareImageBeans == null || mPreShareImageBeans.size() > 0) {
            return;
        }


    }

    private List<ProductionImgEntity> mProductionImgEntities;

    private String codeImagePath = "code fail !";
    private String codeImagePaths = "code fail !";

    @Override
    protected void getData() {

        if (mProductionImgEntities == null) {
            mProductionImgEntities = new ArrayList<>();
        }

        if (mPreShareImageBeans != null && mPreShareImageBeans.size() > 0) {
            for (int i = 0; i < mPreShareImageBeans.size(); i++) {
                ProductionImgEntity productionImgEntity = new ProductionImgEntity(ProductionImgEntity.TYPE_DEFITEM);
                productionImgEntity.mPreShareImageBean = mPreShareImageBeans.get(i);
                if (i == 0) {
                    productionImgEntity.mPreShareImageBean.isSelecter = true;
                }
                mProductionImgEntities.add(productionImgEntity);
            }

            Glide.with(BaseApplication.getApplication()).load(mPreShareImageBeans.get(0).imgUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    File newFile = new File(Environment.getExternalStorageDirectory() + "/" + BuildConfig.FILE_PATH);//图片路径
                    if (!newFile.exists()) {
                        newFile.mkdir();
                    }
                    String fileName = FileUtils.getNameFromDate() + ".png";//图片名
                    File imgDownFile = new File(newFile, fileName);
                    try {
                        FileOutputStream fos = new FileOutputStream(imgDownFile);
                        resource.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        mPreShareImageBeans.get(0).localImageCache = imgDownFile.getAbsolutePath();


                        String codeName = FileUtils.getNameFromDate() + ".png";//图片名
                        String codeNames = FileUtils.getNameFromDate() + ".png";//图片名
                        File imgCodeFile = new File(newFile, codeName);
                        File imgsCodeFile = new File(newFile, codeNames);

                        boolean qrImage = ZxingUtils.createQRImage(mPreShareImageBeans.get(0).qnyCodeUrl, 250, 250, 0, null, imgCodeFile.getAbsolutePath());
                        if (qrImage) {
                            codeImagePath = imgCodeFile.getAbsolutePath();
                            mPreShareImageBeans.get(0).localCodeCache = codeImagePath;
                            productionImageShow(0);
                        }
                        boolean qrsImage = ZxingUtils.createQRImage(mPreShareImageBeans.get(0).qnyCodeUrlShowLogo, 250, 250, 0, null, imgsCodeFile.getAbsolutePath());
                        if (qrsImage) {
                            codeImagePaths = imgsCodeFile.getAbsolutePath();
                            mPreShareImageBeans.get(0).localCodeCacheShowLogo = codeImagePaths;
                            productionImageShow(0);
                        }
                        setAdapter();
                        fos.flush();
                        fos.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    }

    private void setAdapter(){
        if (mBaseAdapter == null) {
            mBaseAdapter = new BaseAdapter<ProductionImgEntity, ComponentViewHolder>(mProductionImgEntities) {

                @Override
                protected void convert(ComponentViewHolder helper, final ProductionImgEntity item) {
                    if (helper.getItemViewType() == item.getItemType()) {//判断同一类型获取布局设置数据

                        final ImageView ivProShareImg = helper.getView(R.id.iv_preshare_shareimg);
                        Glide.with(BaseApplication.getApplication()).load(item.mPreShareImageBean.imgUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                ivProShareImg.setImageBitmap(resource);
                                File newFile = new File(Environment.getExternalStorageDirectory() + "/" + BuildConfig.FILE_PATH);//图片路径
                                if (!newFile.exists()) {
                                    newFile.mkdir();
                                }
                                String fileName = FileUtils.getNameFromDate() + ".png";//图片名
                                File imgDownFile = new File(newFile, fileName);
                                try {
                                    FileOutputStream fos = new FileOutputStream(imgDownFile);
                                    resource.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                    item.mPreShareImageBean.localImageCache = imgDownFile.getAbsolutePath();

                                    if (TextUtils.isEmpty(codeImagePath)) {
                                        String codeName = FileUtils.getNameFromDate() + ".png";//图片名
                                        String codeNames = FileUtils.getNameFromDate() + ".png";//图片名
                                        File imgCodeFile = new File(newFile, codeName);
                                        File imgsCodeFile = new File( newFile,codeNames );
                                        boolean qrImage = ZxingUtils.createQRImage(item.mPreShareImageBean.qnyCodeUrl, 250, 250, 0, null, imgCodeFile.getAbsolutePath());
                                        if (qrImage) {
                                            codeImagePath = imgCodeFile.getAbsolutePath();
                                        }
                                        boolean qrsImage = ZxingUtils.createQRImage(item.mPreShareImageBean.qnyCodeUrlShowLogo, 250, 250, 0, null, imgsCodeFile.getAbsolutePath());
                                        if (qrsImage) {
                                            codeImagePaths = imgsCodeFile.getAbsolutePath();
                                        }
                                    }
                                    item.mPreShareImageBean.localCodeCache = codeImagePath;
                                    item.mPreShareImageBean.localCodeCacheShowLogo = codeImagePaths;
                                    fos.flush();
                                    fos.close();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        });

//                        helper.setDisplayImage(R.id.iv_preshare_shareimg, item.mPreShareImageBean.imgUrl);
                        ImageView ivProImgSelect = helper.getView(R.id.iv_preshare_select);
                        ivProImgSelect.setSelected(item.mPreShareImageBean.isSelecter);
                    }
                }

                @Override
                protected void addItemTypeView() {
                    addItemType(ProductionImgEntity.TYPE_DEFITEM, R.layout.item_preshare_rv);
                }
            };
            mRvProImg.setHasFixedSize(true);
            mRvProImg.setNestedScrollingEnabled(false);
            mRvProImg.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            mRvProImg.setAdapter(mBaseAdapter);
            mBaseAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    int id = view.getId();
                    num=position+"";
                    changeItemState(position);
                }
            });
        } else {
            mBaseAdapter.notifyDataSetChanged();
        }
    }

    private void changeItemState(int position) {
        if (mCurrentSelecterPosition == position){
            return;
        }
        mCurrentSelecterPosition = position;
        if (mProductionImgEntities == null || mBaseAdapter == null) {
            return;
        }
        for (int i = 0; i < mProductionImgEntities.size(); i++) {
            mProductionImgEntities.get(i).mPreShareImageBean.isSelecter = false;
        }
        mProductionImgEntities.get(position).mPreShareImageBean.isSelecter = true;
        mBaseAdapter.notifyDataSetChanged();
        productionImageShow(position);
    }

    private void productionImageShow(int position) {
        PreShareImageBean productionImgBean = mProductionImgEntities.get(position).mPreShareImageBean;
        View inflate;
        if (!mDefchecked) {
            inflate = LayoutInflater.from(this).inflate(R.layout.layout_preshare_show, null);

            ImageView ivPreshareProductImg = inflate.findViewById(R.id.iv_preshare_product_img);
            ImageView ivPreshareChannle = inflate.findViewById(R.id.iv_preshare_channle);
            TextView tvMoreProduct = inflate.findViewById(R.id.stvmore_product);
            TextView tvPreShareQHJ = inflate.findViewById(R.id.tv_preshare_quanhoujia);
            TextView tvPreShareYJ = inflate.findViewById(R.id.tv_preshare_yuanjia);
            ImageView ivPreShareCode = inflate.findViewById(R.id.iv_preshare_code);
            TextView tvPreSharequan = inflate.findViewById(R.id.tv_preshare_quan);

            tvMoreProduct.setText(getSpannableString(productionImgBean.productName));
            ivPreshareChannle.setImageResource(productionImgBean.shopType == 1 ? com.common.R.drawable.ic_tb_more : com.common.R.drawable.ic_tm_more);

            tvPreShareQHJ.setText("¥" + productionImgBean.quanhoujia);//商品券后价，店铺价
            tvPreShareYJ.setText(StringUtils.stringToStringDeleteZero(productionImgBean.yuanjia));//商品原价
            tvPreShareYJ.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            tvPreSharequan.setText(productionImgBean.quan + "元");
            Bitmap preShareImgBit = BitmapFactory.decodeFile(productionImgBean.localImageCache);
            ivPreshareProductImg.setImageBitmap(preShareImgBit);

            Bitmap preShareCodeBit = BitmapFactory.decodeFile(productionImgBean.localCodeCache);
            ivPreShareCode.setImageBitmap(preShareCodeBit);

            // 屏幕高度（像素）
            mHeight = (int) ((metric.heightPixels + getBottomStatusHeight(this)) * 0.8);
        } else {
            inflate = LayoutInflater.from(this).inflate(R.layout.layout_preshare_bitmap, null);


            ImageView ivPreshareProductImg = inflate.findViewById(R.id.tv_preshare_product_img);
            ImageView ivPreshareChannle = inflate.findViewById(R.id.iv_preshare_channle);
            TextView tvMoreProduct = inflate.findViewById(R.id.tv_preshare_product_name);
            TextView tvPreShareQHJ = inflate.findViewById(R.id.tv_preshare_quanhoujia);
            TextView tvPreShareDBQHJ = inflate.findViewById(R.id.tv_dibujiage);
            TextView tvPreShareYJ = inflate.findViewById(R.id.tv_preshare_yuanjia);
            ImageView ivPreShareCode = inflate.findViewById(R.id.iv_preshare_code);
            TextView tvPreSharequan = inflate.findViewById(R.id.tv_preshare_youhuiquan);

            tvMoreProduct.setText(getSpannableString(productionImgBean.productName));
            ivPreshareChannle.setImageResource(productionImgBean.shopType == 1 ? com.common.R.drawable.ic_preshare_tb : com.common.R.drawable.ic_preshare_tm);

            tvPreShareQHJ.setText("" + productionImgBean.quanhoujia);//商品券后价，店铺价
            tvPreShareDBQHJ.setText("" + productionImgBean.quanhoujia);//商品券后价，店铺价
            tvPreShareYJ.setText("原  价 ¥ " + StringUtils.stringToStringDeleteZero(productionImgBean.yuanjia));//商品原价
            tvPreShareYJ.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            tvPreSharequan.setText(productionImgBean.quan);

            Bitmap preShareImgBit = BitmapFactory.decodeFile(productionImgBean.localImageCache);
            ivPreshareProductImg.setImageBitmap(preShareImgBit);
            Bitmap preShareCodeBit = BitmapFactory.decodeFile(productionImgBean.localCodeCacheShowLogo);
            ivPreShareCode.setImageBitmap(preShareCodeBit);

            // 屏幕高度（像素）
            mHeight = metric.heightPixels;
        }

        //测量
        layoutView(inflate, mWidth, mHeight);

        Bitmap bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        inflate.draw(canvas);

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
            mSavePath = shareFile.getAbsolutePath();
            mIvProImgShowImg.setImageBitmap(bitmap);

        } catch (IOException e) {
            e.printStackTrace();
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_common_back) {
            finish();
        }else if (id == R.id.tv_common_right){
            SPUtils.getInstance().putBoolean(BaseApplication.getApplication(),"isShowLog",mDefchecked);
            Intent intent = new Intent();
            intent.putExtra("savePath",mSavePath);
            intent.putExtra("number",num);
            setResult(100,intent);
            finish();
        }
    }

    /**
     * 首行缩进的SpannableString
     *
     * @param description 描述信息
     */
    private SpannableString getSpannableString(String description) {
        SpannableString spannableString = new SpannableString(description);
        LeadingMarginSpan leadingMarginSpan = new LeadingMarginSpan.Standard(dp2px(this, 20), 0);//仅首行缩进
        spannableString.setSpan(leadingMarginSpan, 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
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
}
