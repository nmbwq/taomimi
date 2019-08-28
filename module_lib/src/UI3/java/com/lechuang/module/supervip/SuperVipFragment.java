package java.com.lechuang.module.supervip;


import android.net.http.SslError;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseFragment;
import com.common.app.base.LazyBaseFragment;
import com.common.app.constants.Constant;
import com.common.app.database.manger.UserHelper;
import com.common.app.http.api.Qurl;
import com.lechuang.module.R;

/**
 * @author: zhengjr
 * @since: 2018/8/9
 * @describe:
 */

@Route(path = ARouters.PATH_SUPER_VIP)
public class SuperVipFragment extends BaseFragment {

    @Autowired
    public String loadUrl;//webview加载网址
    @Autowired(name = Constant.TITLE)
    public String title;//头部
    @Autowired(name = Constant.TYPE)
    public int type;//当type==4时，表示自己网站的h5，需要添加拦截其他的不拦截
    private WebView mWebSuperVip;
    private TextView mTvWebTile, tvPopKnow, tvPopContent1;
    private boolean isSuccess = false;//webview的加载状态
    private boolean isReLoad = true;//判断是否需要重新加载
    private PopupWindow mPopupWindow;
    private View mVsCommonWevError;
    private boolean mLoadError = false;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_super_vip;
    }

    @Override
    protected void findViews() {

        $ ( R.id.iv_common_back ).setVisibility ( View.GONE );
        //隐藏标题
        $ ( R.id.rl_common_background ).setVisibility ( View.GONE );
        $ ( R.id.tv_status_bar ).setVisibility ( View.GONE );
        mWebSuperVip = $ ( R.id.web_super_vip );

        mTvWebTile = (TextView) $ ( R.id.iv_common_title );
        mVsCommonWevError = $ ( R.id.vs_common_web_error );
        $ ( R.id.tv_common_hint ).setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                mLoadError = false;
                mVsCommonWevError.setVisibility ( View.GONE );
                mWebSuperVip.reload ();
            }
        } );
    }

    @Override
    protected void initView() {
        ARouter.getInstance ().inject ( this );
        mTvWebTile.setText ( title );
        WebSettings webSettings = mWebSuperVip.getSettings ();
        if (webSettings == null) return;
        // 支持 Js 使用
        webSettings.setJavaScriptEnabled ( true );
        // 开启DOM缓存
        webSettings.setDomStorageEnabled ( true );
        webSettings.setAllowFileAccess ( true );
        webSettings.setAppCacheEnabled ( true );
        webSettings.setUseWideViewPort ( true );
        webSettings.setLoadWithOverviewMode ( true );
        //是否支持缩放
        webSettings.setSupportZoom ( false );
        webSettings.setBuiltInZoomControls ( false );
        webSettings.setDisplayZoomControls ( false );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        mWebSuperVip.setWebChromeClient ( new WebChromeClient () {
            //获取网页的标题
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle ( view, title );
                getActivity ().setTitle ( title );
            }

            //重写alert弹框
            @Override
            public boolean onJsAlert(WebView view, String url, final String message, JsResult result) {
                getActivity ().runOnUiThread ( new Runnable () {
                    @Override
                    public void run() {
                        showPopupWindow ( message );
                    }
                } );
                result.confirm ();//这里必须调用，否则页面会阻塞造成假死
                return true;
            }

        } );
        mWebSuperVip.setWebViewClient ( new WebViewClient () {

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();// 接受所有网站的证书
            }

            //加载错误的时候会回调
            @Override
            public void onReceivedError(WebView webView, int i, String s, String s1) {
                super.onReceivedError ( webView, i, s, s1 );
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    return;
                }
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {

                if (type == 4 && url.contains ( Qurl.HOST )) {
                    return super.shouldInterceptRequest ( view, url );
                }
                return null;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //过滤自己的安全域名
                if (type == 4 && url.contains ( Qurl.HOST )) {
                    return false;
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished ( view, url );
                if (mLoadError) {
                    mVsCommonWevError.setVisibility ( View.VISIBLE );
                    mWebSuperVip.setVisibility ( View.GONE );
                } else {
                    mWebSuperVip.setVisibility ( View.VISIBLE );
                }

                //不需要设置网页的头部
//                String webTile = view.getTitle();
//                mTvWebTile.setText(TextUtils.isEmpty(webTile) ? title : webTile);

                if (!isSuccess) {
                    isReLoad = true;
                } else {
                    isReLoad = false;
                }
                isSuccess = true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError ( view, request, error );
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (request.isForMainFrame ()) {
                        mVsCommonWevError.setVisibility ( View.VISIBLE );
                        mWebSuperVip.setVisibility ( View.GONE );
                    }
                }
                mLoadError = true;
                isSuccess = false;
            }
        } );
    }

    //弹出提示
    private void showPopupWindow(String content) {
        View contentView = LayoutInflater.from ( getActivity () ).inflate ( R.layout.popupwind_xiaoxi_know, null );
        mPopupWindow = new PopupWindow ( contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true );
        tvPopKnow = (TextView) contentView.findViewById ( R.id.tv_popwindow_know );
        tvPopContent1 = (TextView) contentView.findViewById ( R.id.tv_popwindow_content );
        tvPopContent1.setText ( content );
        tvPopKnow.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss ();
            }
        } );
        mPopupWindow.setContentView ( contentView );
        mPopupWindow.showAtLocation ( contentView, Gravity.BOTTOM, 0, 0 );
    }

    @Override
    protected void getData() {
        if (isReLoad) {//没有加载成功，重新加载
            mWebSuperVip.loadUrl ( Qurl.shengji + "?userId=" + UserHelper.getInstence ().getUserInfo ().getId () );
        }
    }

    @Override
    protected void updataRequest() {
        super.updataRequest ();
        mWebSuperVip.loadUrl ( Qurl.shengji + "?userId=" + UserHelper.getInstence ().getUserInfo ().getId () );
    }

    @Override
    public void onDestroy() {
        super.onDestroy ();
        if (mWebSuperVip != null) {
            mWebSuperVip.destroy ();
        }
    }

}

