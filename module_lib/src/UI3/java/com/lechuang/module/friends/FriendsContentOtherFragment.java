package java.com.lechuang.module.friends;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseApplication;
import com.common.app.base.LazyBaseFragment;
import com.common.app.constants.Constant;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.http.bean.FriendsLoadBean;
import com.common.app.jumprouter.LinkRouterUtils;
import com.common.app.jumprouter.RouterBean;
import com.common.app.utils.LoadImage;
import com.common.app.utils.LogUtils;
import com.common.app.utils.Logger;
import com.common.app.utils.ShareUtils;
import com.common.app.utils.StringUtils;
import com.common.app.utils.Utils;
import com.common.app.view.CommonDialog;
import com.common.app.view.GlideRoundTransform;
import com.lechuang.module.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.FriendsBean;
import java.com.lechuang.module.bean.FriendsShareImagesBean;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author: zhengjr
 * @since: 2018/9/10
 * @describe:
 */

@Route(path = ARouters.PATH_FRAGMENT_CONTENT_OTHER)
public class FriendsContentOtherFragment extends LazyBaseFragment implements  OnRefreshLoadMoreListener {

    @Autowired(name = Constant.TYPE)
    public String type;
    @Autowired()
    public String loadUrl;//webview加载网址
    private WebView mWebView;
    private SmartRefreshLayout mSmartEarning;
    private ClassicsHeader mSmartEarningHeader;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_firends_content_other;
    }

    @Override
    protected void findViews() {
        mWebView = $(R.id.web_common);
        mSmartEarning = $ ( R.id.smart_earnings );
        mSmartEarningHeader = $ ( R.id.smart_earnings_header );
    }

    @Override
    protected void initView() {
        ARouter.getInstance().inject(this);
        mSmartEarning.setOnRefreshLoadMoreListener ( this );
        mSmartEarning.setEnableLoadMore ( false );
        WebSettings webSettings = mWebView.getSettings();
        if (webSettings == null) return;
        // 支持 Js 使用
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        final String ua = webSettings.getUserAgentString();
        webSettings.setUserAgentString(ua+";tae_sdk_3.1.1.210 AliApp(BC/3.1.1.210) AliBaichuan(2014_0_24890486@baichuan_android_3.1.1.210/1.0.0) AliApp(TUnionSDK/0.3.2)");
        // 开启DOM缓存
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(false);
        webSettings.setAppCacheEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        //是否支持缩放
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);
        mWebView.loadUrl( loadUrl );
        mWebView.setWebViewClient(new WebViewClient());
    }

    @Override
    protected void getData() {
    }


    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {

    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        mWebView.loadUrl( loadUrl );
        mSmartEarning.finishRefresh ( true );
    }
}
