package java.com.lechuang.module.view;


import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.baichuan.android.trade.AlibcTrade;
import com.alibaba.baichuan.android.trade.callback.AlibcTradeCallback;
import com.alibaba.baichuan.android.trade.model.AlibcShowParams;
import com.alibaba.baichuan.android.trade.model.OpenType;
import com.alibaba.baichuan.android.trade.page.AlibcBasePage;
import com.alibaba.baichuan.android.trade.page.AlibcPage;
import com.alibaba.baichuan.trade.biz.context.AlibcTradeResult;
import com.alibaba.baichuan.trade.biz.core.taoke.AlibcTaokeParams;
import com.alibaba.baichuan.trade.biz.login.AlibcLogin;
import com.alibaba.baichuan.trade.biz.login.AlibcLoginCallback;
import com.alibaba.fastjson.JSON;
import com.common.BuildConfig;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.constants.Constant;
import com.common.app.database.manger.UserHelper;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.utils.ADFilterTool;
import com.common.app.utils.StringUtils;
import com.common.app.utils.Utils;
import com.common.app.view.CommonDialog;
import com.lechuang.module.R;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.GetTaoBaoUrlBean;
import java.com.lechuang.module.bean.ProductInfoBean;
import java.com.lechuang.module.productinfo.ProductInfoActivity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_SUPER_WEB)
public class CommonSuperWebViewActivity extends BaseActivity implements View.OnClickListener {


    @Autowired
    public String loadUrl;//webview加载网址
    @Autowired(name = Constant.TITLE)
    public String title;//webview加载网址
    @Autowired(name = Constant.TYPE)
    public int type;//当type==4时，表示自己网站的h5，需要添加拦截其他的不拦截
    private WebView mWebCommon;
    private TextView mTvCommonTile;
    private ImageView mIvCommonBack, mIvCommonRight;
    private boolean isShowButton = false;
    private RelativeLayout mRlSuperwebBottomSearch;
    private LinearLayout mLlSuperwebBottomQuanParent;
    private TextView mTvSuperwebTopHint;
    private TextView mTvSuperOneKeySearch;
    private TextView mTvSuperWebShreZhuan;
    private TextView mTvSuperWebGobuyOrQuan;
    private TextView mTvSuperWebQuan;
    private String tbItemId = "";//商品id，从地址里面截取的
    private View mVsCommonWevError;
    private View mWebParent;
    private boolean mLoadError = false;
    private ProgressBar mPbProductInfo;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_common_super_web_view;
    }

    @Override
    protected void findViews() {
        mWebCommon = $(R.id.web_common);
        mTvCommonTile = $(R.id.iv_common_title);
        mIvCommonBack = $(R.id.iv_common_back);
        mIvCommonRight = $(R.id.iv_common_right);

        mPbProductInfo = $(R.id.pb_product_info);

        mTvSuperOneKeySearch = $(R.id.tv_super_onekey_search);
        mTvSuperWebShreZhuan = $(R.id.tv_super_web_share_zhuan);
        mTvSuperWebGobuyOrQuan = $(R.id.tv_super_web_gobuy_or_quan);
        mTvSuperWebQuan = $(R.id.tv_super_web_quan);

        $(R.id.iv_common_close).setVisibility(View.VISIBLE);
        mRlSuperwebBottomSearch = $(R.id.rl_superweb_bottom_search);
        mLlSuperwebBottomQuanParent = $(R.id.tv_super_quan_parent);
        mTvSuperwebTopHint = $(R.id.tv_superweb_top_hint);
        mIvCommonBack.setOnClickListener(this);

        mVsCommonWevError = $(R.id.vs_common_web_error);
        mWebParent = $(R.id.fl_web_parent);
        $(R.id.tv_common_hint).setOnClickListener(this);

        mTvSuperOneKeySearch.setOnClickListener(this);
        mTvSuperWebShreZhuan.setOnClickListener(this);
        mTvSuperWebGobuyOrQuan.setOnClickListener(this);
        mTvSuperWebQuan.setOnClickListener(this);
        mIvCommonRight.setOnClickListener(this);
        $(R.id.iv_common_close).setOnClickListener(this);
    }

    @Override
    protected void initView() {
        ARouter.getInstance().inject(this);
        mTvCommonTile.setText("");

        mTvSuperwebTopHint.setVisibility(View.GONE);
        mRlSuperwebBottomSearch.setVisibility(View.GONE);
        mLlSuperwebBottomQuanParent.setVisibility(View.GONE);
        mTvSuperOneKeySearch.setVisibility(View.VISIBLE);
        mTvSuperWebShreZhuan.setVisibility(View.GONE);
        mTvSuperWebGobuyOrQuan.setVisibility(View.GONE);
        mTvSuperWebQuan.setVisibility(View.GONE);
        mIvCommonRight.setVisibility(View.VISIBLE);
        mIvCommonRight.setImageResource(R.drawable.ic_super_web_refresh);


        WebSettings webSettings = mWebCommon.getSettings();
        if (webSettings == null) return;
        // 支持 Js 使用
        webSettings.setJavaScriptEnabled(true);
        String ua = webSettings.getUserAgentString();
        webSettings.setUserAgentString(ua+";tae_sdk_3.1.1.210 AliApp(BC/3.1.1.210) AliBaichuan(2014_0_24890486@baichuan_android_3.1.1.210/1.0.0) AliApp(TUnionSDK/0.3.2)");
        // 开启DOM缓存
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        //是否支持缩放
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        mWebCommon.addJavascriptInterface(new Object(){
            @JavascriptInterface
            public void interactAnd(String msg){
                finish();
            }
        },"interactFinish");
        mWebCommon.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO Auto-generated method stub
                if (newProgress == 100) {
                    // 网页加载完成
                    mPbProductInfo.setVisibility(View.GONE);//加载完网页进度条消失
                } else {
                    // 加载中
                    mPbProductInfo.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    mPbProductInfo.setProgress(newProgress);//设置进度值
                }

            }
        });
        mWebCommon.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();// 接受所有网站的证书
            }

            //在开始加载网页时会回调
            @Override
            public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
                super.onPageStarted(webView, s, bitmap);
                tbItemId = "";
                mProductInfoBean = null;
            }


            //加载错误的时候会回调
            @Override
            public void onReceivedError(WebView webView, int i, String s, String s1) {
                super.onReceivedError(webView, i, s, s1);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    return;
                }
            }

            //加载错误的时候会回调
            @Override
            public void onReceivedError(WebView webView, WebResourceRequest webResourceRequest, WebResourceError webResourceError) {
                super.onReceivedError(webView, webResourceRequest, webResourceError);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (webResourceRequest.isForMainFrame()) {
                        mVsCommonWevError.setVisibility(View.VISIBLE);
                        mWebParent.setVisibility(View.GONE);
                        mLoadError = true;
                    }
                }

            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (!ADFilterTool.hasAd(CommonSuperWebViewActivity.this, url.toLowerCase())) {
                    return super.shouldInterceptRequest(view, url);
                } else {
                    return new WebResourceResponse(null, null, null);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains(Qurl.author)){
                    if (!url.contains("redirect_uri")){
                        isNeedClear = true;
                    }
                    view.loadUrl(url);
                    return true;
                }
                if (url.contains("/logout.htm")){
                    AlibcLogin alibcLogin = AlibcLogin.getInstance();
                    alibcLogin.logout(new AlibcLoginCallback() {
                        @Override
                        public void onSuccess(int i) {

                        }

                        @Override
                        public void onFailure(int i, String s) {

                        }
                    });
                    view.loadUrl(url);
                    return true;
                }
                if (url.contains("appfun:jump:shareApp")){
                    AndPermission.with(CommonSuperWebViewActivity.this)
                            .permission(Permission.Group.STORAGE)
                            .onGranted(new Action() {
                                @Override
                                public void onAction(List<String> permissions) {
                                    //这里需要读写的权限
                                    ARouter.getInstance().build(ARouters.PATH_SHARE_APP).navigation();
                                }
                            })
                            .onDenied(new Action() {
                                @Override
                                public void onAction(@NonNull List<String> permissions) {
                                    if (AndPermission.hasAlwaysDeniedPermission(CommonSuperWebViewActivity.this, permissions)) {
                                        //这个里面提示的是一直不过的权限
                                    }
                                }
                            })
                            .start();
                    return true;
                }
                if (url.startsWith("http")) {
                    if (StringUtils.isTbaoOrTianMaoRex(url) || StringUtils.isProductInfoRex(url)) {
                        tbItemId = StringUtils.getProductId(url);
                        mProductInfoBean = null;
                        mRlSuperwebBottomSearch.setVisibility(View.VISIBLE);
                        mTvSuperwebTopHint.setVisibility(View.VISIBLE);
                        mTvSuperOneKeySearch.setVisibility(View.VISIBLE);
                        mLlSuperwebBottomQuanParent.setVisibility(View.GONE);
                    } else {
                        tbItemId = "";
                        mProductInfoBean = null;
                        mRlSuperwebBottomSearch.setVisibility(View.GONE);
                        mTvSuperwebTopHint.setVisibility(View.GONE);
                        mTvSuperOneKeySearch.setVisibility(View.GONE);
                        mLlSuperwebBottomQuanParent.setVisibility(View.GONE);
                    }
                }

                return false;
            }

            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                super.doUpdateVisitedHistory(view, url, isReload);
                if (isNeedClear){
                    isNeedClear = false;
                    view.clearHistory();
                    CookieSyncManager.createInstance(getApplicationContext());  //Create a singleton CookieSyncManager within a context
                    CookieManager cookieManager = CookieManager.getInstance(); // the singleton CookieManager instance
                    cookieManager.removeAllCookie();// Removes all cookies.
                    AlibcLogin alibcLogin = AlibcLogin.getInstance();
                    alibcLogin.logout(new AlibcLoginCallback() {
                        @Override
                        public void onSuccess(int i) {

                        }

                        @Override
                        public void onFailure(int i, String s) {

                        }
                    });
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (mLoadError) {
                    mVsCommonWevError.setVisibility(View.VISIBLE);
                    mWebParent.setVisibility(View.GONE);
                } else {
                    mWebParent.setVisibility(View.VISIBLE);
                }
                if (url.startsWith("http")) {
                    if (StringUtils.isTbaoOrTianMaoRex(url) || StringUtils.isProductInfoRex(url)) {
                        tbItemId = StringUtils.getProductId(url);
                        mRlSuperwebBottomSearch.setVisibility(View.VISIBLE);
                        mTvSuperwebTopHint.setVisibility(View.VISIBLE);
                    } else {
                        mRlSuperwebBottomSearch.setVisibility(View.GONE);
                        mTvSuperwebTopHint.setVisibility(View.GONE);
                    }
                }
                String webTile = view.getTitle();
//                mTvCommonTile.setText(TextUtils.isEmpty(webTile) ? title : webTile);
                mTvCommonTile.setText(!TextUtils.isEmpty(title) ? title : webTile);

            }
        });
    }

    private boolean isNeedClear = false;

    @Override
    protected void getData() {
        mWebCommon.loadUrl(loadUrl);
    }

    /**
     * 初始化请求之后的状态
     */
    private void initViewData() {
        tbItemId = "";
        mProductInfoBean = null;
        if (mRlSuperwebBottomSearch != null)
            mRlSuperwebBottomSearch.setVisibility(View.GONE);
        if (mTvSuperwebTopHint != null)
            mTvSuperwebTopHint.setVisibility(View.GONE);
        if (mLlSuperwebBottomQuanParent != null)
            mLlSuperwebBottomQuanParent.setVisibility(View.GONE);
        if (mTvSuperOneKeySearch != null)
            mTvSuperOneKeySearch.setVisibility(View.VISIBLE);
        if (mTvSuperWebShreZhuan != null)
            mTvSuperWebShreZhuan.setVisibility(View.GONE);
        if (mTvSuperWebGobuyOrQuan != null)
            mTvSuperWebGobuyOrQuan.setVisibility(View.GONE);
        if (mTvSuperWebQuan != null)
            mTvSuperWebQuan.setVisibility(View.GONE);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_common_back) {
            if (mWebCommon.canGoBack()) {
                mWebCommon.goBack();
            } else {
                onBackPressed();
            }
            initViewData();
        } else if (id == R.id.iv_common_right && mWebCommon != null) {
            mWebCommon.reload();
        } else if (id == R.id.iv_common_close) {
            finish();
        } else if (id == R.id.tv_super_onekey_search) {//一键搜券
            onKeySearch();

        } else if (id == R.id.tv_super_web_share_zhuan) {//分享赚，佣金
            if (mProductInfoBean == null) {
                toast("参数为空,点击右上角刷新试试！");
                return;
            }
            //请求成功，直接跳转到分享
            ARouter.getInstance().build(ARouters.PATH_PRE_SHARE)
                    .withString("all", JSON.toJSONString(mProductInfoBean))
                    .navigation();
        } else if (id == R.id.tv_super_web_gobuy_or_quan) {//领券，或者购买
//            getTaoBaoUrl();
            String url = mWebCommon.getUrl();
            AlibcShowParams alibcShowParams = new AlibcShowParams(OpenType.Native, false);
            AlibcTaokeParams taoke = new AlibcTaokeParams();
            Map exParams = new HashMap<>();
            exParams.put("isv_code", "appisvcode");
            exParams.put("alibaba", "阿里巴巴");//自定义参数部分，可任意增删改
            taoke.setPid(BuildConfig.ALI_PID);
            AlibcBasePage alibcBasePage = new AlibcPage(url);//实例化URL打开page
            AlibcTrade.show(CommonSuperWebViewActivity.this, alibcBasePage, alibcShowParams, taoke, exParams, new AlibcTradeCallback() {
                @Override
                public void onTradeSuccess(AlibcTradeResult alibcTradeResult) {
                }

                @Override
                public void onFailure(int i, String s) {
                    toast(s);

                }
            });
        } else if (id == R.id.tv_super_web_quan) {//券

            String s = mTvSuperWebQuan.getText().toString();
            if (!TextUtils.equals("去购买", s)) {
                getTaoBaoUrl();
            } else {
                String url = mWebCommon.getUrl();
                AlibcShowParams alibcShowParams = new AlibcShowParams(OpenType.Native, false);
                AlibcTaokeParams taoke = new AlibcTaokeParams();
                Map exParams = new HashMap<>();
                exParams.put("isv_code", "appisvcode");
                exParams.put("alibaba", "阿里巴巴");//自定义参数部分，可任意增删改
                taoke.setPid(BuildConfig.ALI_PID);
                AlibcBasePage alibcBasePage = new AlibcPage(url);//实例化URL打开page
                AlibcTrade.show(CommonSuperWebViewActivity.this, alibcBasePage, alibcShowParams, taoke, exParams, new AlibcTradeCallback() {
                    @Override
                    public void onTradeSuccess(AlibcTradeResult alibcTradeResult) {
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        toast(s);

                    }
                });
            }
        } else if (id == R.id.tv_common_hint) {
            mLoadError = false;
            mVsCommonWevError.setVisibility(View.GONE);
            mWebCommon.reload();
        }

    }

    /**
     * 一键搜券
     */
    private ProductInfoBean mProductInfoBean;

    private void onKeySearch() {
        mProductInfoBean = null;
        Map<String, Object> allParam = new HashMap<>();
        if (TextUtils.isEmpty(tbItemId)) {
            toast("参数为空,点击右上角刷新试试！");
            return;
        }
        allParam.put("tbItemId", tbItemId);
        allParam.put("type", 3);
        allParam.put("deviceType", 1);
        String userId = UserHelper.getInstence().getUserInfo().getId();
        if (!TextUtils.isEmpty(userId)) {
            allParam.put("userId", userId);
        }

        NetWork.getInstance()
                .setTag(Qurl.zhuanProductDetail1)
                .getApiService(ModuleApi.class)
                .zhuanProductDetail(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<ProductInfoBean>(this, true, true) {

                    @Override
                    public void onSuccess(ProductInfoBean result) {
                        if (result == null) {
                            mLlSuperwebBottomQuanParent.setVisibility(View.GONE);
                            return;
                        }
                        mProductInfoBean = result;
                        mLlSuperwebBottomQuanParent.setVisibility(View.VISIBLE);
                        //请求成功
                        String zhuanMoney = result.productWithBLOBs.zhuanMoney;//赚，
                        int couponMoney = result.productWithBLOBs.couponMoney;//券

                        if (!TextUtils.isEmpty(zhuanMoney) && couponMoney > 0) {//有佣金，有券（分享赚，去领券）
                            mTvSuperOneKeySearch.setVisibility(View.GONE);
                            mTvSuperWebGobuyOrQuan.setVisibility(View.GONE);
                            mTvSuperWebShreZhuan.setVisibility(View.VISIBLE);
                            mTvSuperWebQuan.setVisibility(View.VISIBLE);
                            mTvSuperWebShreZhuan.setText("分享赚" + StringUtils.stringToStringDeleteZero(zhuanMoney));
                            mTvSuperWebQuan.setText(StringUtils.intToStringDeleteZero(couponMoney) + "元优惠券");
                        } else if (!TextUtils.isEmpty(zhuanMoney) && couponMoney <= 0) {//有佣金，无券（分享赚，去购买）
                            mTvSuperOneKeySearch.setVisibility(View.GONE);
                            mTvSuperWebGobuyOrQuan.setVisibility(View.GONE);
                            mTvSuperWebShreZhuan.setVisibility(View.VISIBLE);
                            mTvSuperWebQuan.setVisibility(View.VISIBLE);
                            mTvSuperWebShreZhuan.setText("分享赚" + StringUtils.stringToStringDeleteZero(zhuanMoney));
                            mTvSuperWebQuan.setText("去购买");
                        } else if (couponMoney > 0) {//无佣金，有券（分享赚隐藏，去领券）
                            mTvSuperOneKeySearch.setVisibility(View.GONE);
                            mTvSuperWebGobuyOrQuan.setVisibility(View.VISIBLE);
                            mTvSuperWebShreZhuan.setVisibility(View.GONE);
                            mTvSuperWebQuan.setVisibility(View.GONE);
                            mTvSuperWebGobuyOrQuan.setText("领" + couponMoney + "元券");
                        } else {//无佣金，无券（分享赚隐藏，去购买）
                            mTvSuperOneKeySearch.setVisibility(View.GONE);
                            mTvSuperWebGobuyOrQuan.setVisibility(View.VISIBLE);
                            mTvSuperWebShreZhuan.setVisibility(View.GONE);
                            mTvSuperWebQuan.setVisibility(View.GONE);
                            mTvSuperWebGobuyOrQuan.setText("去购买");
                            Utils.toast("没有查到优惠券信息");
                        }

                    }

                    @Override
                    public void onFailed(int errorCode, String moreInfo) {
                        super.onFailed(errorCode, moreInfo);
                        mLlSuperwebBottomQuanParent.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mLlSuperwebBottomQuanParent.setVisibility(View.GONE);
                    }
                });
    }

    /**
     * 获取淘宝链接
     */
    private void getTaoBaoUrl() {

        if (TextUtils.isEmpty(mProductInfoBean.productWithBLOBs.tbItemId)) {
            toast("参数错误！");
            return;
        }

        Map<String, String> allParam = new HashMap<>();
        allParam.put("tbItemId", mProductInfoBean.productWithBLOBs.tbItemId);
        if (!TextUtils.isEmpty(mProductInfoBean.productWithBLOBs.tbCouponId)) {
            allParam.put("tbCouponId", mProductInfoBean.productWithBLOBs.tbCouponId);
        }

        NetWork.getInstance()
                .setTag(Qurl.getTaoBaoUrl4_0)
                .getApiService(ModuleApi.class)
                .getTaoBaoUrl(allParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<GetTaoBaoUrlBean>(this, true, R.drawable.img_jump_taobao, "努力跳转淘宝中") {

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


                        AlibcShowParams alibcShowParams = new AlibcShowParams(OpenType.Native, false);
                        AlibcTaokeParams taoke = new AlibcTaokeParams();
                        Map exParams = new HashMap<>();
                        exParams.put("isv_code", "appisvcode");
                        exParams.put("alibaba", "阿里巴巴");//自定义参数部分，可任意增删改
                        taoke.setPid(BuildConfig.ALI_PID);
                        AlibcBasePage alibcBasePage = new AlibcPage(result.productWithBLOBs.tbPrivilegeUrl);//实例化URL打开page
                        AlibcTrade.show(CommonSuperWebViewActivity.this, alibcBasePage, alibcShowParams, taoke, exParams, new AlibcTradeCallback() {
                            @Override
                            public void onTradeSuccess(AlibcTradeResult alibcTradeResult) {
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                toast(s);

                            }
                        });
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
            mCommonDialog = new CommonDialog(CommonSuperWebViewActivity.this, R.layout.layout_dialog_shouquan);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebCommon != null) {
            mWebCommon.destroy();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebCommon.canGoBack()) {
            mWebCommon.goBack();
            tbItemId = "";
            mProductInfoBean = null;
            mRlSuperwebBottomSearch.setVisibility(View.GONE);
            mTvSuperwebTopHint.setVisibility(View.GONE);
            mLlSuperwebBottomQuanParent.setVisibility(View.GONE);
            mTvSuperOneKeySearch.setVisibility(View.VISIBLE);
            mTvSuperWebShreZhuan.setVisibility(View.GONE);
            mTvSuperWebGobuyOrQuan.setVisibility(View.GONE);
            mTvSuperWebQuan.setVisibility(View.GONE);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
