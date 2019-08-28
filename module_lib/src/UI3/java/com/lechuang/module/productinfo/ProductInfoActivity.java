package java.com.lechuang.module.productinfo;


import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

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
import com.common.app.view.CommonDialog;
import com.lechuang.module.R;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.GetTaoBaoUrlBean;
import java.com.lechuang.module.bean.ProductInfoBean;
import java.com.lechuang.module.bean.ShouQuanBean;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_PRODUCT_INFO)
public class ProductInfoActivity extends BaseActivity implements View.OnClickListener {

    @Autowired(name = Constant.TBCOUPINID)
    public String tbCouponId;//优惠券ID
    @Autowired(name = Constant.TYPE)
    public String type;
    @Autowired(name = Constant.id)
    public String id;
    @Autowired(name = Constant.TBITEMID)
    public String tbItemId;
    @Autowired(name = Constant.UUID)
    public String uuid;
    @Autowired(name = Constant.SORT)
    public String sort;


    private WebView mWebProductInfo;
    private ProgressBar mPbProductInfo;
    private String loadUrl;//webview加载网址
    private View mVsCommonWevError;
    private boolean mLoadError = false;
    private Timer timer = new Timer();
    private int currentProgress = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_product_info;
    }

    @Override
    protected void findViews() {

        mWebProductInfo = $(R.id.web_product_info);
        mVsCommonWevError = $(R.id.vs_common_web_error);
        mPbProductInfo = $(R.id.pb_product_info);
        $(R.id.iv_common_circle_back).setVisibility(View.GONE);
        $(R.id.tv_common_hint).setOnClickListener(this);
    }

    @Override
    protected void initView() {

        EventBus.getDefault().register(this);
        ARouter.getInstance().inject(this);

        WebSettings webSettings = mWebProductInfo.getSettings();
        if (webSettings == null) return;
        //清空缓存
        mWebProductInfo.clearCache(true);
        // 支持 Js 使用
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBlockNetworkImage(false);
        // 开启DOM缓存
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        //是否支持缩放
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        mWebProductInfo.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                if (newProgress == 100) {
                    // 网页加载完成
                    stopTimeTask();
                    mPbProductInfo.setVisibility(View.GONE);
                } else {
                    // 加载中
                    if (newProgress > currentProgress) {
                        mPbProductInfo.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                        mPbProductInfo.setProgress(newProgress);
                        currentProgress = newProgress;
                    }
                }

            }
        });
        mWebProductInfo.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();// 接受所有网站的证书
            }


            //在开始加载网页时会回调
            @Override
            public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
                super.onPageStarted(webView, s, bitmap);
                startTimeTask();
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
                        mWebProductInfo.setVisibility(View.GONE);
                        mLoadError = true;
                    }
                }

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                stopTimeTask();
                if (mLoadError) {
                    mVsCommonWevError.setVisibility(View.VISIBLE);
                    mWebProductInfo.setVisibility(View.GONE);
                } else {
                    mWebProductInfo.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (url.startsWith("appfun") || url.contains(Qurl.HOST)) {
                    return super.shouldInterceptRequest(view, url);
                }
                return null;
            }


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {


                if (url.equals("appfun:product:detail") && UserHelper.getInstence().isLogin()) {
                    //跳转淘宝立即领券
                    getTaoBaoUrl();

                } else if (url.equals("appfun:product:share") && UserHelper.getInstence().isLogin()) {

                    AndPermission.with(ProductInfoActivity.this)
                            .permission(Permission.Group.STORAGE)
                            .onGranted(new Action() {
                                @Override
                                public void onAction(List<String> permissions) {
                                    //这里需要读写的权限
                                    //去分享页
                                    //获取产品数据
                                    getProductData();


                                }
                            })
                            .onDenied(new Action() {
                                @Override
                                public void onAction(@NonNull List<String> permissions) {
                                    if (AndPermission.hasAlwaysDeniedPermission(ProductInfoActivity.this, permissions)) {
                                        //这个里面提示的是一直不过的权限
                                    }
                                }
                            })
                            .start();


                } else if (url.equals("appfun:productdetail:pop")) {
                    finish();
                } else if (url.equals("appfun:jump:home")) {
                    EventBus.getDefault().post(Constant.ONEKEY_HOME);
                } else if (url.equals("appfun:jump:superMember")){
                    if (UserHelper.getInstence().isLogin()){
                        String loadUrl = UserHelper.getInstence().getUserInfo().getId();
                        ARouter.getInstance().build(ARouters.PATH_COMMOM_WEB)
                                .withString("loadUrl", Qurl.shengji + (TextUtils.isEmpty(loadUrl) ? "" : ("?userId=" + loadUrl)))
                                .withString(Constant.TITLE, "赚钱")
                                .withInt(Constant.TYPE, 4)
                                .navigation();
                    }else {
                        ARouter.getInstance().build(ARouters.PATH_LOGIN).navigation();
                    }

                }else if (url.contains("appfun:login")) {
                    //未登录，去登录
                    if (!UserHelper.getInstence().isLogin())
                        ARouter.getInstance().build(ARouters.PATH_LOGIN).navigation();
                    else
                        toast(url);
                } else if (url.contains("rule.html")) {
                    return false;
                } else {
                    //未登录，去登录
                    if (!UserHelper.getInstence().isLogin())
                        ARouter.getInstance().build(ARouters.PATH_LOGIN).navigation();
                    else
                        toast(url);
                }
                return true;
            }
        });
    }

    /**
     * 启动定时器
     */
    private void startTimeTask() {
        stopTimeTask();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        if (currentProgress < 90) {
                            currentProgress += 1;
                            mPbProductInfo.setProgress(currentProgress);
                        } else {
                            stopTimeTask();
                        }
                    }
                });
            }
        }, 0, 50);
    }

    /**
     * 关闭定时器
     */
    private void stopTimeTask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * 获取淘宝链接
     */
    private void getTaoBaoUrl() {


        Map<String, String> allParam = new HashMap<>();
        allParam.put("tbItemId", tbItemId);
        if (!TextUtils.isEmpty(tbCouponId)) {
            allParam.put("tbCouponId", tbCouponId);
        }

        if (!TextUtils.isEmpty(id) && !TextUtils.equals("null", id)) {
            allParam.put("id", id);
        }

        NetWork.getInstance()
                .setTag(Qurl.getTaoBaoUrl4_0)
                .getApiService(ModuleApi.class)
                .getTaoBaoUrl2_0(allParam)
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
                        AlibcTrade.show(ProductInfoActivity.this, alibcBasePage, alibcShowParams, taoke, exParams, new AlibcTradeCallback() {
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
            mCommonDialog = new CommonDialog(ProductInfoActivity.this, R.layout.layout_dialog_shouquan);
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
    protected void getData() {
        loadUrl = Qurl.productDetails
                + "deviceType=1"
                + "&type=" + type
                + vertifyIsEmpty("userId", UserHelper.getInstence().getUserInfo().getId())
                + vertifyIsEmpty("id", id)
                + vertifyIsEmpty("uuid", uuid)
                + vertifyIsEmpty("sort", sort)
                + vertifyIsEmpty("tbItemId", tbItemId);

        mWebProductInfo.loadUrl(loadUrl);
    }

    /**
     * 获取产品数据
     */
    private void getProductData() {
        Map<String, Object> allParam = new HashMap<>();
//        if (!TextUtils.isEmpty(id)) {
//            allParam.put("id", id);
//        }
//        allParam.put("productId", i);
//        if (TextUtils.isEmpty(t)){
//            t = "1";
//        }
//        allParam.put("userId", UserHelper.getInstence().getUserInfo().getId());
//        allParam.put("type", t);
//        if (!TextUtils.isEmpty(tbCouponId)){
//            allParam.put("tbCouponId", tbCouponId);
//        }


        if (!TextUtils.isEmpty(id)) {
            allParam.put("id", id);
        }
        if (!TextUtils.isEmpty(type)) {
            allParam.put("type", Integer.valueOf(type));
        }
        if (!TextUtils.isEmpty(tbItemId)) {
            allParam.put("tbItemId", tbItemId);
        }
        if (!TextUtils.isEmpty(uuid)) {
            allParam.put("uuid", uuid);
        }
        String userId = UserHelper.getInstence().getUserInfo().getId();
        if (!TextUtils.isEmpty(userId)) {
            allParam.put("userId", userId);
        }
        allParam.put("deviceType", 1);

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
                            return;
                        }
                        //请求成功，直接跳转到分享
                        ARouter.getInstance().build(ARouters.PATH_PRE_SHARE)
                                .withString("all", JSON.toJSONString(result))
                                .withInt("type", Integer.valueOf(type))
                                .navigation();
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
    protected void onResume() {
        super.onResume();

    }

    private String vertifyIsEmpty(String key, String value) {
        if (!TextUtils.isEmpty(value) && !value.equalsIgnoreCase("null")) {
            return "&" + key + "=" + value;
        }
        return "";
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_common_circle_back) {
//            finish();
            if (mWebProductInfo != null) {
                if (mWebProductInfo.canGoBack()) {
                    mWebProductInfo.goBack();
                } else {
                    onBackPressed();
                }
            }
        } else if (id == R.id.tv_common_hint) {
            mLoadError = false;
            mVsCommonWevError.setVisibility(View.GONE);
            mWebProductInfo.reload();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebProductInfo.canGoBack()) {
            mWebProductInfo.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mWebProductInfo != null) {
            mWebProductInfo.clearCache(true);
            mWebProductInfo.destroy();
            mWebProductInfo = null;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeNav(String message) {
        if (message.equalsIgnoreCase(Constant.LOGIN_SUCCESS) || message.equalsIgnoreCase(Constant.LOGOUT_SUCCESS)) {
            getData();
        }
    }
}
