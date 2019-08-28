package java.com.lechuang.module.productinfo;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.baichuan.trade.biz.login.AlibcLogin;
import com.alibaba.baichuan.trade.biz.login.AlibcLoginCallback;
import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.BuildConfig;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.base.BaseApplication;
import com.common.app.constants.Constant;
import com.common.app.database.manger.UserHelper;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.http.api.RetrofitServer;
import com.common.app.http.bean.GetHostUrlBean;
import com.common.app.utils.FileUtils;
import com.common.app.utils.LogUtils;
import com.common.app.utils.Logger;
import com.common.app.utils.OnClickEvent;
import com.common.app.utils.SPUtils;
import com.common.app.utils.ShareUtils;
import com.common.app.utils.StringUtils;
import com.common.app.utils.ZxingUtils;
import com.common.app.view.CommonDialog;
import com.lechuang.module.R;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.AddShareBean;
import java.com.lechuang.module.bean.GetTaoBaoUrlBean;
import java.com.lechuang.module.bean.LongChangeShortBean;
import java.com.lechuang.module.bean.PreShareImageBean;
import java.com.lechuang.module.bean.ProductInfoBean;
import java.com.lechuang.module.view.CommonSuperWebViewActivity;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


@Route(path = ARouters.PATH_PRE_SHARE)
public class PreShareActivity extends BaseActivity implements View.OnClickListener, BaseQuickAdapter.OnItemClickListener {

    @Autowired(name = Constant.TYPE)
    public int type;
    private ProductInfoBean mProductInfoBean;

    private TextView mTvAward, mTvSelectNum, mTvCopy, mTvMShare;
    private LinearLayout mLlRule;
    private RecyclerView mRecyclerView;
    private EditText mEtClipText;

    private int mSelectCount = 0;//默认不选
    private RecyclerView mRvPreShare;
    private CommonDialog mDialogWenAn;
    private CommonDialog mDialogGuiZe;
    private ImageView mIvPreshareChannle;
    private ImageView mIvPreshareProductImg;
    private ImageView mIvPreshareCode;
    private TextView mIvPreshareProductName;
    private TextView mIvPreshareQuanHouJia, mIvPreshareQuanHouJias;
    private TextView mIvPreshareYuanJia;
    private TextView mIvPreshareYouHuiQuan;
    private TextView mIvPreshareWenAn;
    private LinearLayout mMTvAwardParent;
    private List<String> Url;
    private String image;
    private int num = 0;
    private boolean numboolean = true;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_pre_share;
    }

    @Override
    protected void findViews() {
        ((TextView) $(R.id.iv_common_title)).setText("创建分享");

        mTvAward = $(R.id.tv_preshare_award);
        mMTvAwardParent = $(R.id.ll_preshare_award_parent);
        mTvSelectNum = $(R.id.tv_preshare_select_num);
        mLlRule = $(R.id.ll_preshare_rule);
        mEtClipText = $(R.id.et_preshare_cliptext);
        mTvCopy = $(R.id.tv_preshare_copy);
        mTvMShare = $(R.id.tv_preshare_mshare);
        mRvPreShare = $(R.id.rv_preshare);

        //分享的UI
        mIvPreshareChannle = $(R.id.iv_preshare_channle);
        mIvPreshareProductImg = $(R.id.tv_preshare_product_img);
        mIvPreshareCode = $(R.id.iv_preshare_code);
        mIvPreshareProductName = $(R.id.tv_preshare_product_name);
        mIvPreshareQuanHouJia = $(R.id.tv_preshare_quanhoujia);
        mIvPreshareQuanHouJias = $(R.id.tv_dibujiage);
        mIvPreshareYuanJia = $(R.id.tv_preshare_yuanjia);
        mIvPreshareYouHuiQuan = $(R.id.tv_preshare_youhuiquan);
        mIvPreshareWenAn = $(R.id.tv_preshare_wenan);

        $(R.id.tv_reload_preshare_img).setOnClickListener(this);
        $(R.id.share_weixin).setOnClickListener(this);
        $(R.id.share_friends).setOnClickListener(this);
        $(R.id.share_qq).setOnClickListener(this);
        $(R.id.share_qq_kongjian).setOnClickListener(this);
        mLlRule.setOnClickListener(this);
        mTvCopy.setOnClickListener(this);
        mTvMShare.setOnClickListener(new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                if (mSelectCount <= 0) {
                    toast("请至少选择一张图片");
                    return;
                }
                addShare(image, null);
                //循环遍历选中的，然后保存图片到本地
                //请求后台给后台传数据smallImages
//                if (numboolean&&TextUtils.isEmpty( mProductInfoBean.productWithBLOBs.tbItemId )&&TextUtils.isEmpty( Url.get( 0 ) )&&TextUtils.isEmpty( getURLEncoderString(tbTpwd) )){


            }
        });
        $(R.id.iv_common_back).setOnClickListener(this);
    }

    @Override
    protected void initView() {
        ARouter.getInstance().inject(this);

        mProductInfoBean = JSON.parseObject(getIntent().getStringExtra("all"), ProductInfoBean.class);


        try {
            //奖励佣金预估
            mMTvAwardParent.setVisibility(TextUtils.isEmpty(mProductInfoBean.productWithBLOBs.shareIntegral) ? View.GONE : View.VISIBLE);
            //奖励佣金预估
//            mTvAward.setText("奖励佣金预估 ¥" + (TextUtils.isEmpty(mProductInfoBean.productWithBLOBs.zhuanMoney) ? "" : StringUtils.stringToStringDeleteZero(mProductInfoBean.productWithBLOBs.zhuanMoney)));
            mTvAward.setText(TextUtils.isEmpty(mProductInfoBean.productWithBLOBs.shareIntegral) ? "" : StringUtils.stringToStringDeleteZero(mProductInfoBean.productWithBLOBs.shareIntegral));
            //编辑分享文案
//            mEtClipText.setText(mProductInfoBean.productWithBLOBs.shareText);
            String shareText=mProductInfoBean.productWithBLOBs.shareText;
            shareText=shareText.replace("{","");
            shareText=shareText.replace("}","");
            mEtClipText.setText(shareText);

            //请求到制作二维码的数据之后再展示上面的选择图片
//            setShareImg(mProductInfoBean.productWithBLOBs.smallImages);

            mIvPreshareWenAn.setText(TextUtils.isEmpty(mProductInfoBean.productWithBLOBs.shareText) ? "" : mProductInfoBean.productWithBLOBs.shareText);
            mIvPreshareQuanHouJia.setText(StringUtils.stringToStringDeleteZero(mProductInfoBean.productWithBLOBs.preferentialPrice));
            mIvPreshareQuanHouJias.setText(StringUtils.stringToStringDeleteZero(mProductInfoBean.productWithBLOBs.preferentialPrice));
            mIvPreshareYuanJia.setText("原   价 ￥" + StringUtils.doubleToStringDeleteZero(mProductInfoBean.productWithBLOBs.price));
            mIvPreshareYuanJia.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            mIvPreshareYouHuiQuan.setText("" + mProductInfoBean.productWithBLOBs.couponMoney);
//            image=mProductInfoBean.productWithBLOBs.smallImages.get( 0 );
        } catch (Exception e) {

        }

    }

    private void addShare(String url, final SHARE_MEDIA media) {
        Map<String, Object> allParam = new HashMap<>();
        /*LogUtils.w( "tag1","tbItemId="+mProductInfoBean.productWithBLOBs.tbItemId+"  productId="+mProductInfoBean.productWithBLOBs.id
        +"  tbCouponId="+mProductInfoBean.productWithBLOBs.tbCouponId+"  image="+url+"  tbTpwd="+getURLEncoderString(tbTpwd)+"  shopType="+
        mProductInfoBean.productWithBLOBs.shopType+"  price="+mProductInfoBean.productWithBLOBs.price+"  couponMoney="+mProductInfoBean.productWithBLOBs.couponMoney
        +"  name="+mProductInfoBean.productWithBLOBs.name+"  productName="+mProductInfoBean.productWithBLOBs.productName+"  productText="+
        mProductInfoBean.productWithBLOBs.productText);*/
        allParam.put("tbItemId", mProductInfoBean.productWithBLOBs.tbItemId);
        if (!TextUtils.isEmpty(mProductInfoBean.productWithBLOBs.id)) {
            allParam.put("productId", mProductInfoBean.productWithBLOBs.id);
        }
        if (!TextUtils.isEmpty(mProductInfoBean.productWithBLOBs.tbCouponId)) {
            allParam.put("tbCouponId", mProductInfoBean.productWithBLOBs.tbCouponId);
        }
        allParam.put("image", url);
        allParam.put("tbTpwd", getURLEncoderString(tbTpwd));
        if (!TextUtils.isEmpty(mProductInfoBean.productWithBLOBs.shopType + "")) {
            allParam.put("shopType", mProductInfoBean.productWithBLOBs.shopType);
        }
        allParam.put("price", mProductInfoBean.productWithBLOBs.price);
        if (!TextUtils.isEmpty(mProductInfoBean.productWithBLOBs.couponMoney + "")) {
            allParam.put("couponMoney", mProductInfoBean.productWithBLOBs.couponMoney);
        }
        allParam.put("name", mProductInfoBean.productWithBLOBs.name);
        allParam.put("productName", mProductInfoBean.productWithBLOBs.productName);
        if (!TextUtils.isEmpty(mProductInfoBean.productWithBLOBs.productText)) {
            allParam.put("productText", mProductInfoBean.productWithBLOBs.productText);
        }
        NetWork.getInstance()
                .setTag(Qurl.addShare)
                .getApiService(ModuleApi.class)
                .addShare(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<AddShareBean>(this, false, false) {

                    @Override
                    public void onSuccess(AddShareBean result) {
                        if (result == null) {
                            return;
                        }
                        try {
                            if (result.getResCode().equals("success")) {
//                                showShareDialog();
                                if (media == null) {
                                    return;
                                }
                                shareImage(media);
                            }
//                            stopProgressDialog();
                        } catch (Exception e) {
                            toast("数据异常-->" + e.toString());
                            stopProgressDialog();
                        }

                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed(errorCode, moreInfo);
                        toast(moreInfo);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });
    }


    /*private void addShare(String url, final SHARE_MEDIA media){
        Map<String, Object> allParam = new HashMap<>();
        *//*LogUtils.w( "tag1","tbItemId="+mProductInfoBean.productWithBLOBs.tbItemId+"  productId="+mProductInfoBean.productWithBLOBs.id
        +"  tbCouponId="+mProductInfoBean.productWithBLOBs.tbCouponId+"  image="+url+"  tbTpwd="+getURLEncoderString(tbTpwd)+"  shopType="+
        mProductInfoBean.productWithBLOBs.shopType+"  price="+mProductInfoBean.productWithBLOBs.price+"  couponMoney="+mProductInfoBean.productWithBLOBs.couponMoney
        +"  name="+mProductInfoBean.productWithBLOBs.name+"  productName="+mProductInfoBean.productWithBLOBs.productName+"  productText="+
        mProductInfoBean.productWithBLOBs.productText);*//*
        allParam.put( "tbItemId", mProductInfoBean.productWithBLOBs.tbItemId );
        if (!TextUtils.isEmpty( mProductInfoBean.productWithBLOBs.id )){
            allParam.put( "productId",mProductInfoBean.productWithBLOBs.id );
        }
        if (!TextUtils.isEmpty( mProductInfoBean.productWithBLOBs.tbCouponId )){
            allParam.put( "tbCouponId",mProductInfoBean.productWithBLOBs.tbCouponId );
        }
        allParam.put( "image",url );
        allParam.put( "tbTpwd",getURLEncoderString(tbTpwd) );
        if (!TextUtils.isEmpty( mProductInfoBean.productWithBLOBs.shopType+"" )){
            allParam.put( "shopType",mProductInfoBean.productWithBLOBs.shopType );
        }
        allParam.put( "price",mProductInfoBean.productWithBLOBs.price );
        if (!TextUtils.isEmpty( mProductInfoBean.productWithBLOBs.couponMoney+"" )){
            allParam.put( "couponMoney",mProductInfoBean.productWithBLOBs.couponMoney );
        }
        allParam.put( "name",mProductInfoBean.productWithBLOBs.name );
        allParam.put( "productName",mProductInfoBean.productWithBLOBs.productName );
        if (!TextUtils.isEmpty( mProductInfoBean.productWithBLOBs.productText )){
            allParam.put( "productText",mProductInfoBean.productWithBLOBs.productText );
        }
        NetWork.getInstance()
                .setTag(Qurl.addShare)
                .getApiService(ModuleApi.class)
                .addShare(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<AddShareBean>(this, false, false) {

                    @Override
                    public void onSuccess(AddShareBean result) {
                        if (result == null) {
                            return;
                        }
                        try {
                            for (int i = 0; i < smallImgs.size(); i++) {
                                if (smallImgs.get(i).isSelect) {
                                    //合成一张图片，合成完之后就直接分享图片到第三方应用
                                    shareImage(media);
                                    stopProgressDialog();
                                    break;
                                }
                            }
//                            stopProgressDialog();
                        } catch (Exception e) {
                            toast("数据异常-->" + e.toString());
                            stopProgressDialog();
                        }

                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed(errorCode, moreInfo);
                        toast("errorCode="+errorCode+ "  moreInfo="+moreInfo );
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });
    }*/

    private String shareUrl;
    private String shareUrlShowLogo;
    private String shareUrlOther;
    private String shareUrlShowLogoOther;
    private boolean isContinue = false;
    private boolean isContinueOther = false;

    private void LongChangeShort(String Url, final int tags, final List<String> smallImages) {
        Map<String, String> allParam = new HashMap<>();
        allParam.put("source", "1681459862");
        allParam.put("url_long", Url);
        NetWork.getInstance()
                .setTag(Qurl.longChangeShort)
                .getApiService(ModuleApi.class)
                .getShort(Qurl.longChangeShort, allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<LongChangeShortBean>(this, true, false) {

                    @Override
                    public void onSuccess(LongChangeShortBean result) {
                        if (result == null) {
                            return;
                        }
                        if (tags == 1) {
                            shareUrl = result.url_short;
                        }
                        if (tags == 2) {
                            shareUrlShowLogo = result.url_short;
                        }
                        if (isContinue) {
                            Continue(smallImages);
                            isContinue = false;
                        } else
                            isContinue = true;


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


    /**
     * 设置分享的图片
     *
     * @param smallImages
     */
    //存放图片的集合
    private List<PreShareImageBean> smallImgs = new ArrayList<>();
    private List<PreShareImageBean> mPreShareImageBeans;
    private BaseQuickAdapter<PreShareImageBean, BaseViewHolder> mBaseQuickAdapter;

    private void setShareImg(List<String> smallImages) {
        if (smallImages == null || smallImages.size() <= 0) {
            return;
        }
        if (smallImgs == null) {
            smallImgs = new ArrayList<>();
        }
        smallImgs.clear();

        if (mPreShareImageBeans == null) {
            mPreShareImageBeans = new ArrayList<>();
        }
        mPreShareImageBeans.clear();

        String appHost = SPUtils.getInstance().getString(BaseApplication.getApplication(), "appHost", "");
        /*String shareUrl = appHost + "/appH/html/details_share1.html" + "?tbItemId="
                + (TextUtils.isEmpty(mProductInfoBean.productWithBLOBs.tbItemId) ? "" : mProductInfoBean.productWithBLOBs.tbItemId)
                + "&id=" + (TextUtils.isEmpty(mProductInfoBean.productWithBLOBs.id) ? "" : mProductInfoBean.productWithBLOBs.id)
                + "&tkl=" + getURLEncoderString(tbTpwd) + "&type=" + type;*/
        shareUrl = appHost + "/appH/html/details_share_df.html"
                + "?tkl=" + getURLEncoderString(tbTpwd);
        LongChangeShort(shareUrl, 1, smallImages);

        /*String shareUrlShowLogo = appHost + "/appH/html/details_share_df.html" + "?inviteCode="
                + UserHelper.getInstence().getUserInfo().getInvitationCode()
                + "&tkl=" + getURLEncoderString(tbTpwd) ;*/
        shareUrlShowLogo = appHost + "/appH/html/details_share_df.html" + "?tkl=" + getURLEncoderString(tbTpwd) + "&inviteCode="
                + UserHelper.getInstence().getUserInfo().getInvitationCode();
        LongChangeShort(appHost + "/appH/html/details_share_df.html" + "?tkl=" + getURLEncoderString(tbTpwd) + "&inviteCode="
                + UserHelper.getInstence().getUserInfo().getInvitationCode(), 2, smallImages);

        /*LogUtils.w("tag1", "不带inviteCode=" + appHost + "/appH/html/details_share_df.html"
                + "?tkl=" + getURLEncoderString(tbTpwd) + "   带inviteCode=" + appHost + "/appH/html/details_share_df.html" + "?inviteCode="
                + UserHelper.getInstence().getUserInfo().getInvitationCode()
                + "&tkl=" + getURLEncoderString(tbTpwd));*/
    }

    private void Continue(List<String> smallImages) {
        for (int i = 0; i < smallImages.size(); i++) {
            //第一张默认选中
            smallImgs.add(new PreShareImageBean(i == 0, i, smallImages.get(i)));
            /*if (i==0){
                image=smallImages.get( 0 );
                LogUtils.w( "tag1",image );
            }*/
            if (i > 0) {
                PreShareImageBean preShareImageBean = new PreShareImageBean();
                preShareImageBean.imgUrl = smallImages.get(i);
                preShareImageBean.productName = mProductInfoBean.productWithBLOBs.productName;
                preShareImageBean.quanhoujia = mProductInfoBean.productWithBLOBs.preferentialPrice;
                preShareImageBean.shopType = mProductInfoBean.productWithBLOBs.shopType;
                preShareImageBean.yuanjia = StringUtils.doubleToStringDeleteZero(mProductInfoBean.productWithBLOBs.price);
                preShareImageBean.qnyCodeUrl = shareUrl;
                preShareImageBean.qnyCodeUrlShowLogo = shareUrlShowLogo;
                preShareImageBean.quan = StringUtils.doubleToStringDeleteZero(mProductInfoBean.productWithBLOBs.couponMoney);
                mPreShareImageBeans.add(preShareImageBean);
            }
        }
        mSelectCount = smallImgs.size() > 0 ? 1 : 0;
//        mSelectCount = 0;
        mTvSelectNum.setText("已选" + mSelectCount + "张");
        if (mBaseQuickAdapter == null) {
            mBaseQuickAdapter = new BaseQuickAdapter<PreShareImageBean, BaseViewHolder>(R.layout.item_preshare_rv, smallImgs) {
                @Override
                protected void convert(BaseViewHolder helper, final PreShareImageBean item) {

                    try {
                        //需要分享的图片
                        final ImageView ivPreShareImg = helper.getView(R.id.iv_preshare_shareimg);
                        Glide.with(BaseApplication.getApplication()).load(item.imgUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                ivPreShareImg.setImageBitmap(resource);
                                item.mHttpBitmap = resource;//保存bitmap
                                if (item.isSelect) {
                                    setHttpToLocal(item);
                                }
                            }

                        });

                        //设置选中状态
                        ImageView IvPreShareSelect = helper.getView(R.id.iv_preshare_select);
                        IvPreShareSelect.setSelected(item.isSelect);
                        helper.addOnClickListener(R.id.iv_preshare_select);

                    } catch (Exception e) {

                    }
                }
            };
            mRvPreShare.setHasFixedSize(true);
            mRvPreShare.setNestedScrollingEnabled(false);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            mRvPreShare.setLayoutManager(linearLayoutManager);

            mRvPreShare.setAdapter(mBaseQuickAdapter);
            mBaseQuickAdapter.setOnItemClickListener(this);
            mBaseQuickAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                @Override
                public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                    int id = view.getId();
                    if (id == R.id.iv_preshare_select) {
                        PreShareImageBean item = smallImgs.get(position);
                        if (item.mHttpBitmap == null) {
                            toast("图片未加载完成，请稍后！");
                            return;
                        }
                        setHttpToLocal(item);
                        item.isSelect = !item.isSelect;
                        if (mBaseQuickAdapter != null) {
                            mBaseQuickAdapter.notifyDataSetChanged();
                        }
                        mSelectCount = 0;
                        for (PreShareImageBean shareImageBean : smallImgs) {
                            if (shareImageBean.isSelect) {
                                mSelectCount++;
                                if (shareImageBean.mHttpBitmap != null) {
                                    setHttpToLocal(shareImageBean);
                                }
                            }
                        }
                        if (position == 0) {
                            numboolean = numboolean ? false : true;
                        }
                        mTvSelectNum.setText("已选" + mSelectCount + "张");
                    }
                }
            });
        } else {
            mBaseQuickAdapter.notifyDataSetChanged();
        }
    }

    private ArrayList<String> showPic;

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        //点击图片放大图片，点击查看进入查看详情（显示查看按钮）
        if (showPic == null) {
            showPic = new ArrayList<>();
        }
        showPic.clear();
        for (int i = 0; i < smallImgs.size(); i++) {
            showPic.add(smallImgs.get(i).imgUrl);
        }

        ARouter.getInstance().build(ARouters.PATH_PREIMG)
                .withStringArrayList("urls", showPic)
                .withInt("position", position)
                .withBoolean("withDelete", false)
                .withBoolean("showLoad", true)
                .navigation();
//        if (smallImgs.get(position).mHttpBitmap == null){
//            toast("图片未加载完成，请稍后！");
//            return;
//        }
//        setSelectState(position);
    }

    private void setSelectState(int position) {
        smallImgs.get(position).isSelect = !smallImgs.get(position).isSelect;
        if (mBaseQuickAdapter != null) {
            mBaseQuickAdapter.notifyDataSetChanged();
        }
        mSelectCount = 0;
        for (PreShareImageBean shareImageBean : smallImgs) {
            if (shareImageBean.isSelect) {
                mSelectCount++;
                if (shareImageBean.mHttpBitmap != null) {
                    setHttpToLocal(shareImageBean);
                }
            }
        }
        mTvSelectNum.setText("已选" + mSelectCount + "张");
    }

    @Override
    protected void getData() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (TextUtils.isEmpty(tbTpwd) || TextUtils.equals(tbTpwd, "tkl is fail")) {
            getTaoKongLing();
        }
        String appHost = SPUtils.getInstance().getString(BaseApplication.getApplication(), "appHost");
        if (TextUtils.isEmpty(appHost)) {
            //获取分享连接图片的域名
            getShareHost();
        }
    }

    private void getShareHost() {
        NetWork.getInstance()
                .setTag(Qurl.adverInfo)
                .getApiService(RetrofitServer.class)
                .getShareProductUrl()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<GetHostUrlBean>(PreShareActivity.this, false, false) {

                    @Override
                    public void onSuccess(GetHostUrlBean result) {
                        if (result == null || result.show == null || TextUtils.isEmpty(result.show.appHost)) {
                            return;
                        }
                        SPUtils.getInstance().putString(BaseApplication.getApplication(), "appHost", result.show.appHost);
                    }
                });
    }

    private String tbTpwd = "";//淘口令
    private String tbPrivilegeUrl = "";//淘口令连接

    private void getTaoKongLing() {
        Map<String, String> allParam = new HashMap<>();
        allParam.put("tbItemId", mProductInfoBean.productWithBLOBs.tbItemId);
        if (!TextUtils.isEmpty(mProductInfoBean.productWithBLOBs.tbCouponId)) {
            allParam.put("tbCouponId", mProductInfoBean.productWithBLOBs.tbCouponId);
        }
        if (!TextUtils.isEmpty(mProductInfoBean.productWithBLOBs.id)) {
            allParam.put("id", mProductInfoBean.productWithBLOBs.id);
        }

        NetWork.getInstance()
                .setTag(Qurl.getTaoBaoUrl4_0)
                .getApiService(ModuleApi.class)
                .getTaoBaoUrl3_0(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<GetTaoBaoUrlBean>(this, true) {

                    @Override
                    public void onFailed_4011() {
                        super.onFailed_4011();
                        showShouquan();
                    }

                    @Override
                    public void onSuccess(GetTaoBaoUrlBean result) {
                        if (result == null || result.productWithBLOBs == null) {
                            return;
                        }
                        if (TextUtils.isEmpty(result.productWithBLOBs.tbPrivilegeUrl)) {
                            toast("转链接失败！");
                            return;

                        }
                        if (TextUtils.isEmpty(result.productWithBLOBs.tbTpwd)) {
                            setShareImg(mProductInfoBean.productWithBLOBs.smallImages);
                            return;
                        }
                        tbTpwd = result.productWithBLOBs.tbTpwd;
                        tbPrivilegeUrl = result.productWithBLOBs.tbPrivilegeUrl;
                        String etText = mEtClipText.getText().toString();
                        if (!TextUtils.isEmpty(etText) && !TextUtils.isEmpty(tbTpwd)) {
                            String repS = etText.replace("选择分享渠道后生成淘口令", tbTpwd);
                            mEtClipText.setText(repS);
                        }
                        createBitmap();



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

    private void getShouQuan(){

        final AlibcLogin alibcLogin = AlibcLogin.getInstance();
        if (alibcLogin.isLogin()){
            showProgressDialog();
            alibcLogin.logout(new AlibcLoginCallback() {
                @Override
                public void onSuccess(int i) {
                    alibcLogin.showLogin(new AlibcLoginCallback() {
                        @Override
                        public void onSuccess(int i) {
                            stopProgressDialog();
                            ARouter.getInstance().build(ARouters.PATH_COMMOM_WEB)
                                    .withString("loadUrl", Qurl.HOST + Qurl.shouquan)
                                    .withInt("type",4)
                                    .withString(Constant.TITLE, "授权登陆")
                                    .navigation();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            toast(s);
                            stopProgressDialog();
                        }
                    });
                }

                @Override
                public void onFailure(int i, String s) {
                    stopProgressDialog();
                }
            });

        }else {
            alibcLogin.showLogin(new AlibcLoginCallback() {
                @Override
                public void onSuccess(int i) {
                    ARouter.getInstance().build(ARouters.PATH_COMMOM_WEB)
                            .withString("loadUrl", Qurl.HOST + Qurl.shouquan)
                            .withInt("type",4)
                            .withString(Constant.TITLE, "授权登陆")
                            .navigation();
                }

                @Override
                public void onFailure(int i, String s) {
                    toast(s);
                }
            });
        }

    }

    private CommonDialog mCommonDialog;
    private void showShouquan(){
        if (mCommonDialog != null) {
            mCommonDialog.dismiss();
            mCommonDialog = null;
        }
        if (mCommonDialog == null) {
            mCommonDialog = new CommonDialog(PreShareActivity.this, R.layout.layout_dialog_shouquan);
        }
        mCommonDialog.getViewId(R.id.iv_layout_dialog_shouquan_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCommonDialog != null) {
                    mCommonDialog.dismiss();
                    mCommonDialog = null;
                }
            }
        });
        mCommonDialog.getViewId(R.id.tv_layout_dialog_go_shouquan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getShouQuan();
                if (mCommonDialog != null) {
                    mCommonDialog.dismiss();
                    mCommonDialog = null;
                }
            }
        });
        mCommonDialog.show();
    }

    private void LongChangeShortOther(String Url, final int tags) {
        Map<String, String> allParam = new HashMap<>();
        allParam.put("source", "1681459862");
        allParam.put("url_long", Url);
        NetWork.getInstance()
                .setTag(Qurl.longChangeShort)
                .getApiService(ModuleApi.class)
                .getShort(Qurl.longChangeShort, allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<LongChangeShortBean>(this, true, false) {

                    @Override
                    public void onSuccess(LongChangeShortBean result) {
                        if (result == null) {
                            return;
                        }
                        if (tags == 1) {
                            shareUrlOther = result.url_short;
                        }
                        if (tags == 2) {
                            shareUrlShowLogoOther = result.url_short;
                        }
                        if (isContinueOther) {
                            ContinueOther();
                            isContinueOther = false;
                        } else
                            isContinueOther = true;


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

    /**
     * 获取第一张生成图片
     */
    private void createBitmap() {
        String appHost = SPUtils.getInstance().getString(BaseApplication.getApplication(), "appHost", "");
        if (TextUtils.isEmpty(appHost)) {
            toast("正在重新获取分享地址，请稍后重试！");
            getShareHost();
            return;
        }
        /*String shareUrl = appHost + "/appH/html/details_share1.html" + "?tbItemId="
                + (TextUtils.isEmpty(mProductInfoBean.productWithBLOBs.tbItemId) ? "" : mProductInfoBean.productWithBLOBs.tbItemId)
                + "&id=" + (TextUtils.isEmpty(mProductInfoBean.productWithBLOBs.id) ? "" : mProductInfoBean.productWithBLOBs.id)
                + "&tkl=" + getURLEncoderString(tbTpwd) + "&type=" + type;*/
        shareUrlOther = appHost + "/appH/html/details_share_df.html"
                + "?tkl=" + getURLEncoderString(tbTpwd);
        LongChangeShortOther(appHost + "/appH/html/details_share_df.html"
                + "?tkl=" + getURLEncoderString(tbTpwd), 1);

        /*if (TextUtils.isEmpty( shareUrl )||shareUrl==null){
            shareUrl =  appHost + "/appH/html/details_share_df.html"
                    + "?tkl=" + getURLEncoderString(tbTpwd) +"&type"+ type;
        }*/
        shareUrlShowLogoOther = appHost + "/appH/html/details_share_df.html" + "?inviteCode="
                + UserHelper.getInstence().getUserInfo().getInvitationCode()
                + "&tkl=" + getURLEncoderString(tbTpwd);
        LongChangeShortOther(appHost + "/appH/html/details_share_df.html" + "?inviteCode="
                + UserHelper.getInstence().getUserInfo().getInvitationCode()
                + "&tkl=" + getURLEncoderString(tbTpwd), 2);
        /*LogUtils.w("tag1", "不带inviteCode=" + appHost + "/appH/html/details_share_df.html"
                + "?tkl=" + getURLEncoderString(tbTpwd) + "   带inviteCode=" + appHost + "/appH/html/details_share_df.html" + "?inviteCode="
                + UserHelper.getInstence().getUserInfo().getInvitationCode()
                + "&tkl=" + getURLEncoderString(tbTpwd));*/

        /*if (TextUtils.isEmpty( shareUrlShowLogo )||shareUrlShowLogo==null){
            shareUrlShowLogo = appHost + "/appH/html/details_share_df.html"+ "?tkl=" + getURLEncoderString(tbTpwd) + "&inviteCode="
                    + UserHelper.getInstence().getUserInfo().getInvitationCode()+"&type"+ type;
        }*/

//        setShareImg();
    }

    private void ContinueOther() {
        final PreShareImageBean preShareImageBean = new PreShareImageBean();
        /*LogUtils.w("tag1", "productName=" + mProductInfoBean.productWithBLOBs.productName + "    preferentialPrice=" +
                mProductInfoBean.productWithBLOBs.preferentialPrice + "      shopType=" + mProductInfoBean.productWithBLOBs.shopType + "    yuanjia=" +
                StringUtils.doubleToStringDeleteZero(mProductInfoBean.productWithBLOBs.price) + "      qnyCodeUrl=" + shareUrlOther + "   qnyCodeUrlShowLogo "
                + shareUrlShowLogoOther);*/
        preShareImageBean.productName = mProductInfoBean.productWithBLOBs.productName;
        preShareImageBean.quanhoujia = mProductInfoBean.productWithBLOBs.preferentialPrice;
        preShareImageBean.shopType = mProductInfoBean.productWithBLOBs.shopType;
        preShareImageBean.yuanjia = StringUtils.doubleToStringDeleteZero(mProductInfoBean.productWithBLOBs.price);
        preShareImageBean.qnyCodeUrl = shareUrlOther;
        preShareImageBean.qnyCodeUrlShowLogo = shareUrlShowLogoOther;

        preShareImageBean.quan = StringUtils.doubleToStringDeleteZero(mProductInfoBean.productWithBLOBs.couponMoney);
        final List<String> smallImages = mProductInfoBean.productWithBLOBs.smallImages;
        Url = smallImages;
        if (smallImages != null && smallImages.size() > 0) {
            image = smallImages.get(0);
            addShare(image, null);
            Glide.with(BaseApplication.getApplication()).load(smallImages.get(0)).asBitmap().into(new SimpleTarget<Bitmap>() {

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
                        boolean compress = resource.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                        fileOutputStream.flush();
                        fileOutputStream.close();

                        if (compress) {
                            preShareImageBean.localImageCache = shareFile.getAbsolutePath();
                        }

                        File shareFileCode = new File(newFile, FileUtils.getNameFromDate() + ".png");
                        boolean createCode = ZxingUtils.createQRImage(preShareImageBean.qnyCodeUrl, 250, 250, 0, null, shareFileCode.getAbsolutePath());
                        if (createCode) {
                            preShareImageBean.localCodeCache = shareFileCode.getAbsolutePath();
                        }

                        File shareFileCodeShowLogo = new File(newFile, FileUtils.getNameFromDate() + ".png");
                        boolean createCodeShowLogo = ZxingUtils.createQRImage(preShareImageBean.qnyCodeUrlShowLogo, 250, 250, 0, null, shareFileCodeShowLogo.getAbsolutePath());
                        if (createCodeShowLogo) {
                            preShareImageBean.localCodeCacheShowLogo = shareFileCodeShowLogo.getAbsolutePath();
                        }

                        ShareImageView shareImageView = new ShareImageView(PreShareActivity.this);
                        shareImageView.setShareImageBean(preShareImageBean, new ShareImageView.IShareImageView() {
                            @Override
                            public void shareImageView(String bitmap) {
                                if (smallImages != null && smallImages.size() > 0) {
                                    smallImages.add(0, bitmap);
                                    setShareImg(smallImages);
                                }
                            }
                        });
                    } catch (Exception e) {
                        toast("获取分享图片失败！");
                    }
                }
            });
        }
    }

    /**
     * 保存分享小图片
     */
    private void setHttpToLocal(PreShareImageBean preShareImageBean) {
        Observable.just(preShareImageBean)
                .subscribeOn(Schedulers.io())
                .map(new Function<PreShareImageBean, PreShareImageBean>() {
                    @Override
                    public PreShareImageBean apply(PreShareImageBean preShareImageBean1) throws Exception {
                        File newFile = new File(Environment.getExternalStorageDirectory() + "/" + BuildConfig.FILE_PATH);//图片路径
                        if (!newFile.exists()) {
                            newFile.mkdir();
                        }
                        String fileName = FileUtils.getNameFromDate() + ".png";//图片名
                        File imgDownFile = new File(newFile, fileName);
                        try {
                            FileOutputStream fos = new FileOutputStream(imgDownFile);
                            preShareImageBean1.mHttpBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                            preShareImageBean1.fileLocal = imgDownFile;
                            fos.flush();
                            fos.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        return preShareImageBean1;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PreShareImageBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(PreShareImageBean preShareImageBean1) {

                        /*try {
                            // 其次把文件插入到系统图库
                            MediaStore.Images.Media.insertImage(getContentResolver(),
                                    preShareImageBean1.fileLocal.getAbsolutePath(), preShareImageBean1.fileLocal.getName(), null);
                            // 最后通知图库更新
                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                                    Uri.fromFile(new File(preShareImageBean1.fileLocal.getPath()))));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }*/
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    @Override
    public void onClick(View view) {

        int id = view.getId();
        if (id == R.id.iv_common_back) {
            finish();
        } else if (id == R.id.ll_preshare_rule) {
            if (mDialogGuiZe != null) {
                mDialogGuiZe.dismiss();
                mDialogGuiZe = null;
            }

            if (mDialogGuiZe == null) {
                mDialogGuiZe = new CommonDialog(this, R.layout.dialog_preshare_guize);
                mDialogGuiZe.getViewId(R.id.tv_dialog_guize_sure).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialogGuiZe.dismiss();
                    }
                });
            }

            mDialogGuiZe.show();

        } else if (id == R.id.tv_preshare_copy) {
            if (mDialogWenAn != null) {
                mDialogWenAn.dismiss();
                mDialogWenAn = null;
            }

            if (mDialogWenAn == null) {
                final String etText = mEtClipText.getText().toString();
                mDialogWenAn = new CommonDialog(this, R.layout.dialog_preshare_wenan);
                mDialogWenAn.setTextView(R.id.tv_dialog_wenan_content, etText);
                mDialogWenAn.getViewId(R.id.tv_dialog_wenan_sure).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 获取系统剪贴板
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);


                        // 创建一个剪贴数据集，包含一个普通文本数据条目（需要复制的数据）
                        ClipData clipData = ClipData.newPlainText("app_searched", TextUtils.isEmpty(etText) ? "" : etText);

                        // 把数据集设置（复制）到剪贴板
                        clipboard.setPrimaryClip(clipData);
                        mDialogWenAn.dismiss();
                        if (!TextUtils.isEmpty(etText)) {
                            ShareUtils.shareToWChart(PreShareActivity.this, null, etText);
                        } else {
                            toast("分享数据为空！");
                        }
                    }
                });
            }
            mDialogWenAn.getViewId(R.id.iv_close_copy).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialogWenAn.dismiss();
                }
            });
            mDialogWenAn.show();
        } else if (id == R.id.tv_reload_preshare_img) {

            if (mPreShareImageBeans == null || mPreShareImageBeans.size() <= 1) {
                if (TextUtils.isEmpty(tbTpwd)) {
                    getTaoKongLing();
                    toast("正在重新获取资源图片，请稍后重试！");
                } else {
                    toast("暂无可生成的主图！");
                }
                return;
            }
            Intent intent = new Intent(PreShareActivity.this, ProductionImgActivity.class);
            intent.putExtra("mPreShareImageBeans", JSON.toJSONString(mPreShareImageBeans));
            startActivityForResult(intent, 1001);
        } else if (id == R.id.share_weixin) {
            addShare(image,SHARE_MEDIA.WEIXIN);
        } else if (id == R.id.share_friends) {
            addShare(image,SHARE_MEDIA.WEIXIN_CIRCLE);
        } else if (id == R.id.share_qq) {
            addShare(image,SHARE_MEDIA.QQ);
        } else if (id == R.id.share_qq_kongjian) {
            addShare(image,SHARE_MEDIA.QZONE);
        }
    }

    /**
     * 合成分享图片
     */
    private void shareImage(final SHARE_MEDIA media) {

//        showProgressDialog();
        ArrayList<File> uriList = new ArrayList<>();
        //需要得到分享的选中的数量
        File[] shareFile = new File[mSelectCount];
        //array为要分享图片的本地地址
        int count = 0;
        for (int i = 0; i < smallImgs.size(); i++) {
            //将所有的本地地址转换为Uri并添加至集合
            if (smallImgs.get(i).isSelect && smallImgs.get(i).fileLocal != null) {

                uriList.add(smallImgs.get(i).fileLocal);
                shareFile[count] = smallImgs.get(i).fileLocal;
                count++;
            }
        }

        String etText = mEtClipText.getText().toString();
        if (!TextUtils.isEmpty(etText)) {
            // 获取系统剪贴板
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

            // 创建一个剪贴数据集，包含一个普通文本数据条目（需要复制的数据）
            ClipData clipData = ClipData.newPlainText("app_searched", TextUtils.isEmpty(etText) ? "" : etText);

            // 把数据集设置（复制）到剪贴板
            clipboard.setPrimaryClip(clipData);
            toast("文案已复制！");
        } else {
            toast("文案数据为空，复制失败！");
        }
        if (media == null) { //保存到本地
            try {
                for (int i = 0; i < uriList.size(); i++) {
                    File file = uriList.get(i);
                    MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), null);
                    // 发送广播，通知刷新图库的显示
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getName())));
                }

                toast("图片已保存");
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (media == SHARE_MEDIA.WEIXIN) {//微信
            ShareUtils.shareToWChart(PreShareActivity.this, shareFile, "");
        } else if (media == SHARE_MEDIA.WEIXIN_CIRCLE) {//微信圈
//                                        ShareUtils.shareToWChartFs(getActivity(), shareFile, "");
            ShareUtils.umShare(PreShareActivity.this, media, uriList, new UMShareListener() {
                @Override
                public void onStart(SHARE_MEDIA share_media) {
                    Logger.e("-----", "onStart");

                }

                @Override
                public void onResult(SHARE_MEDIA share_media) {
                    Logger.e("-----", "onResult");
                }

                @Override
                public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                    Logger.e("-----", "onError");
                }

                @Override
                public void onCancel(SHARE_MEDIA share_media) {
                    Logger.e("-----", "onCancel");
                }
            });

        } else if (media == SHARE_MEDIA.QQ) {
            ShareUtils.shareToQQ(PreShareActivity.this, shareFile, "");
        } else if (media == SHARE_MEDIA.QZONE) {//qq和qq空间
            ShareUtils.umShare(PreShareActivity.this, media, uriList, new UMShareListener() {
                @Override
                public void onStart(SHARE_MEDIA share_media) {
                    Logger.e("-----", "onStart");

                }

                @Override
                public void onResult(SHARE_MEDIA share_media) {
                    Logger.e("-----", "onResult");
                }

                @Override
                public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                    Logger.e("-----", "onError");
                }

                @Override
                public void onCancel(SHARE_MEDIA share_media) {
                    Logger.e("-----", "onCancel");
                }
            });

        } else if (media == SHARE_MEDIA.SINA) {//新浪微博
            ShareUtils.shareToSinaWeiBo(PreShareActivity.this, shareFile, "");
        }
    }

    /**
     * 分享的弹出框
     */
    private CommonDialog mShareDialog;

    private void showShareDialog() {
        if (mShareDialog != null) {
            mShareDialog.dismiss();
            mShareDialog = null;
        }
        if (mShareDialog == null) {
            mShareDialog = new CommonDialog(PreShareActivity.this, R.layout.dialog_friends_share);
        }
        mShareDialog.setGravity(Gravity.BOTTOM);
        TextView tvDialogHint = (TextView) mShareDialog.getViewId(R.id.tv_dialog_hint);
        SpannableString sp = new SpannableString("小提示:微信朋友圈如无法一键分享,请先“一键存图”,在手动“发朋友圈”哦~");
        int length1 = "小提示:微信朋友圈如无法一键分享,请先".length();
        int length2 = "小提示:微信朋友圈如无法一键分享,请先“一键存图”,在手动".length();
        sp.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.c_main)), length1, length1 + 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.c_main)), length2, length2 + 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvDialogHint.setText(sp);
        mShareDialog.getViewId(R.id.share_weixin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareImage(SHARE_MEDIA.WEIXIN);
                mShareDialog.dismiss();
            }
        });
        mShareDialog.getViewId(R.id.share_friends).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (numboolean){
                    addShare(image,SHARE_MEDIA.WEIXIN_CIRCLE);
                }else {*/
                shareImage(SHARE_MEDIA.WEIXIN_CIRCLE);
                mShareDialog.dismiss();
//                }
            }
        });
        mShareDialog.getViewId(R.id.save_local).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//保存到本地
                shareImage(null);
                mShareDialog.dismiss();
            }
        });
        mShareDialog.getViewId(R.id.share_qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareImage(SHARE_MEDIA.QQ);
                mShareDialog.dismiss();
            }
        });
        mShareDialog.getViewId(R.id.share_sina).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareImage(SHARE_MEDIA.SINA);
                mShareDialog.dismiss();
            }
        });
        mShareDialog.getViewId(R.id.share_qq_kongjian).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareImage(SHARE_MEDIA.QZONE);
                mShareDialog.dismiss();
            }
        });
        mShareDialog.getViewId(R.id.tv_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShareDialog.dismiss();
            }
        });


        mShareDialog.show();
    }
   /* private void openPermission( final SHARE_MEDIA media) {
        AndPermission.with(PreShareActivity.this)
                .permission( Permission.Group.CAMERA, Permission.Group.STORAGE)
                .onGranted(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        String cfShareCopy = item.cfShareCopy;
                        if (!TextUtils.isEmpty(cfShareCopy)) {
                            // 获取系统剪贴板
                            ClipboardManager clipboard = (ClipboardManager) PreShareActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);

                            // 创建一个剪贴数据集，包含一个普通文本数据条目（需要复制的数据）
                            ClipData clipData = ClipData.newPlainText("app_friends_share_copy", TextUtils.isEmpty(cfShareCopy) ? "" : cfShareCopy);

                            // 把数据集设置（复制）到剪贴板
                            clipboard.setPrimaryClip(clipData);
                            toast("复制成功！");
                        } else {
                            toast("复制失败！");
                        }
                        getShareImages(item.id, item.cfType, item.cfNumber, media);
                    }
                })
                .onDenied(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {

                        //权限申请失败
                    }
                }).start();
    }*/

    /**
     * view 转 bitmap
     *
     * @return
     */
    private PreShareImageBean createBitmap(PreShareImageBean index) {

        // todo 设置数据
        LinearLayout view = findViewById(R.id.ll_parent_layout_preshare);
        /*private ImageView mIvPreshareChannle;
        private ImageView mIvPreshareProductImg;
        private ImageView mIvPreshareCode;
        private TextView mIvPreshareProductName;
        private TextView mIvPreshareQuanHouJia;
        private TextView mIvPreshareYouHuiQuan;
        private TextView mIvPreshareWenAn;*/
        mIvPreshareProductName.setText(mProductInfoBean.productWithBLOBs.productName);//商品标题
        mIvPreshareChannle.setImageResource(mProductInfoBean.productWithBLOBs.shopType == 1 ? R.drawable.ic_preshare_tb : R.drawable.ic_preshare_tm);
        mIvPreshareProductImg.setImageBitmap(index.mHttpBitmap);
        mIvPreshareQuanHouJia.setText(StringUtils.stringToStringDeleteZero(mProductInfoBean.productWithBLOBs.preferentialPrice));
        mIvPreshareYuanJia.setText("原   价 ￥" + StringUtils.doubleToStringDeleteZero(mProductInfoBean.productWithBLOBs.price));
        mIvPreshareYuanJia.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        mIvPreshareYouHuiQuan.setText("" + mProductInfoBean.productWithBLOBs.couponMoney);
        mIvPreshareWenAn.setText(TextUtils.isEmpty(mProductInfoBean.productWithBLOBs.shareText) ? "" : mProductInfoBean.productWithBLOBs.shareText);
        File newFile = new File(Environment.getExternalStorageDirectory() + "/" + BuildConfig.FILE_PATH);//图片路径
        if (!newFile.exists()) {
            newFile.mkdir();
        }
        String fileName = FileUtils.getNameFromDate() + ".png";//图片名
        File imgDownFile = new File(newFile, fileName);
        if (TextUtils.isEmpty(tbTpwd)) {
            toast("获取淘口令失败！");
            tbTpwd = "tkl is fail";
        }
        String appHost = SPUtils.getInstance().getString(BaseApplication.getApplication(), "appHost", "");
        if (TextUtils.isEmpty(appHost)) {
            toast("获取连接域名出错！");
        }
        String shareUrl = appHost + "/appH/html/details_share1.html" + "?tbItemId="
                + (TextUtils.isEmpty(mProductInfoBean.productWithBLOBs.tbItemId) ? "" : mProductInfoBean.productWithBLOBs.tbItemId)
                + "&id=" + (TextUtils.isEmpty(mProductInfoBean.productWithBLOBs.id) ? "" : mProductInfoBean.productWithBLOBs.id)
                + "&tkl=" + getURLEncoderString(tbTpwd) + "&type=" + type;
        if (ZxingUtils.createQRImage(shareUrl, 250, 250, 0, null, imgDownFile.getAbsolutePath())) {
            mIvPreshareCode.setImageBitmap(BitmapFactory.decodeFile(imgDownFile.getAbsolutePath()));
        }

        view.setDrawingCacheEnabled(true);
        index.mLocalBitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return index;
    }

    //不转url编码
    public String getURLEncoderString(String str) {//url编码
        /*String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = java.net.URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;*/
        return str;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            String savePath = data.getStringExtra("savePath");
            String hah = data.getStringExtra("number");
            num = Integer.parseInt(data.getStringExtra("number")) + 1;
            if (Url.size() > 0) {
                Url.get(num);
                image = Url.get(num);
            }
            if (TextUtils.isEmpty(savePath)) {
                toast("获取主图失败！");
            }
            if (mBaseQuickAdapter != null && smallImgs != null && smallImgs.size() > 0) {
                smallImgs.get(0).imgUrl = savePath;
                mBaseQuickAdapter.notifyDataSetChanged();
            }
        }
    }


}
