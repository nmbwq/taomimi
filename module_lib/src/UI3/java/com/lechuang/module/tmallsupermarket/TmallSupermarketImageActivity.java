package java.com.lechuang.module.tmallsupermarket;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.base.BaseApplication;
import com.common.app.http.NetWork;
import com.common.app.http.RxObserver;
import com.common.app.http.api.Qurl;
import com.common.app.utils.LogUtils;
import com.common.app.utils.Logger;
import com.common.app.utils.ShareUtils;
import com.common.app.view.GlideRoundTransform;
import com.lechuang.module.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.com.lechuang.module.ModuleApi;
import java.com.lechuang.module.bean.TryRuleBean;
import java.com.lechuang.module.zerobuy.MyZeroBuyActivity;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouters.PATH_TMALL_SUPERMARKET_IMAGE)
public class TmallSupermarketImageActivity extends BaseActivity implements View.OnClickListener {
    @Autowired()
    public String title;
    @Autowired()
    public int id;
    private WebView mIvContent;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_tmallsupermarket_image;
    }

    @Override
    protected void findViews() {
        $(R.id.iv_common_back).setOnClickListener(this);
        mIvContent = $(R.id.tv_content);
    }

    @Override
    protected void initView() {
        ARouter.getInstance ().inject ( this );
        ((TextView) $(R.id.iv_common_title)).setText(title);

    }

    @Override
    protected void getData() {
        updataAlipay();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.et_bind_zfb_save){
            updataAlipay();
        }else if (id== R.id.iv_common_back){
            finish();
        }
    }

    private void updataAlipay() {
        Map<String, Object> allParam = new HashMap<>();
        allParam.put("id", id);
        allParam.put("isEncode", 1);
        NetWork.getInstance()
                .setTag( Qurl.tmallSuperMarketTmallDetail)
                .getApiService(ModuleApi.class)
                .tmallSuperMarketTmallDetail(allParam)
                .subscribeOn( Schedulers.io())
                .observeOn( AndroidSchedulers.mainThread())
                .subscribe(new RxObserver<TryRuleBean> (TmallSupermarketImageActivity.this,true,false) {

                    @Override
                    public void onSuccess(TryRuleBean result) {
                        if (result==null){
                            return;
                        }
//                        try{
//                            LogUtils.w( "okhttp",result.content );
//                            String html= URLDecoder.decode( result.content,"UTF-8" );
//                            LogUtils.w( "okhttp",html );
//
//                        }catch (UnsupportedEncodingException e){
//                            e.printStackTrace();
//                        }

                    }
                });
    }
}
