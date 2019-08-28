package java.com.lechuang.main;


import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.callback.NavigationCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.base.BaseApplication;
import com.common.app.base.BaseOtherActivity;
import com.common.app.constants.Constant;
import com.common.app.jumprouter.LinkRouterUtils;
import com.common.app.jumprouter.RouterBean;
import com.lechuang.main.R;

import java.com.lechuang.main.bean.AdverBean;
import java.util.Timer;
import java.util.TimerTask;

@Route(path = ARouters.PATH_ADVER)
public class AdverActivity extends BaseOtherActivity implements View.OnClickListener {


    private TextView mTvAdverTimer;
    private int count = 5;
    private Timer mTimer;
    private AdverBean.AdvertisingImgBean mMAdverStringBean;
    private ImageView mIvAdver;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_adver;

    }

    @Override
    protected void findViews() {
        count = 5;
        mIvAdver = $(R.id.iv_adver);
        mTvAdverTimer = findViewById(R.id.tv_adver_timer);
        mTvAdverTimer.setOnClickListener(this);
        findViewById(R.id.ll_adver_timer).setOnClickListener(this);
        mIvAdver.setOnClickListener(this);
    }

    @Override
    protected void initView() {
        mMAdverStringBean = (AdverBean.AdvertisingImgBean) getIntent().getSerializableExtra("mAdverStringBean");
        Glide.with(BaseApplication.getApplication()).load(mMAdverStringBean.adImage).into(mIvAdver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (count >= 0) {
                            mTvAdverTimer.setText(count + "s");
                        }
                        count--;
                        if (count < 1) {
                            startActivity();
                        }
                    }
                });
            }
        }, 0, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    @Override
    protected void getData() {

    }

    private void startActivity() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
//        ARouter.getInstance().build(ARouters.PATH_MAIN).navigation(AdverActivity.this, new NavigationCallback() {
//            @Override
//            public void onFound(Postcard postcard) {
//
//            }
//
//            @Override
//            public void onLost(Postcard postcard) {
//
//            }
//
//            @Override
//            public void onArrival(Postcard postcard) {
//                finish();
//            }
//
//            @Override
//            public void onInterrupt(Postcard postcard) {
//
//            }
//        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ll_adver_timer) {
            startActivity();
        } else if (id == R.id.iv_adver)  {

            if (mTimer != null) {
                mTimer.cancel();
                mTimer = null;
            }
            Intent intent = new Intent(AdverActivity.this,MainActivity.class);
            startActivity(intent);

            RouterBean routerBean = new RouterBean();
            routerBean.img = mMAdverStringBean.img;
            routerBean.link = mMAdverStringBean.link;
            routerBean.type = mMAdverStringBean.type;
            routerBean.mustParam = mMAdverStringBean.mustParam;
            routerBean.attachParam = mMAdverStringBean.attachParam;
            routerBean.rootName = mMAdverStringBean.rootName;
            routerBean.obJump = mMAdverStringBean.obJump;
            routerBean.linkAllows = mMAdverStringBean.linkAllows;
            routerBean.commandCopy = mMAdverStringBean.commandCopy;
            LinkRouterUtils.getInstance().setRouterBean(AdverActivity.this, routerBean);

            finish();

            /*if (mMAdverStringBean.type == 0) {
                ARouter.getInstance().build(ARouters.PATH_COMMOM_WEB)
                        .withString("loadUrl", mMAdverStringBean.adUrl)
                        .withInt(Constant.TYPE, 4)
                        .navigation(this, new NavigationCallback() {
                            @Override
                            public void onFound(Postcard postcard) {

                            }

                            @Override
                            public void onLost(Postcard postcard) {

                            }

                            @Override
                            public void onArrival(Postcard postcard) {
                                finish();
                            }

                            @Override
                            public void onInterrupt(Postcard postcard) {

                            }
                        });
            } else if (mMAdverStringBean.type == 1) {
//                RouterBean routerBean = new RouterBean();
//                routerBean.type = 9;
//                routerBean.t = "1";
//                routerBean.id = mMAdverStringBean.id;
//                routerBean.i = mMAdverStringBean.tbItemId;
//                LinkRouterUtils.getInstance().setRouterBean(AdverActivity.this, routerBean);

                ARouter.getInstance()
                        .build(ARouters.PATH_PRODUCT_INFO)
                        .withString(Constant.i,mMAdverStringBean.tbItemId)
                        .withString(Constant.id,mMAdverStringBean.id)
                        .withString(Constant.t,"1")
                        .withString(Constant.TYPE,mMAdverStringBean.alipayCouponId)
                        .navigation(this, new NavigationCallback() {
                            @Override
                            public void onFound(Postcard postcard) {

                            }

                            @Override
                            public void onLost(Postcard postcard) {

                            }

                            @Override
                            public void onArrival(Postcard postcard) {
                                finish();
                            }

                            @Override
                            public void onInterrupt(Postcard postcard) {

                            }
                        });
            }*/

        }
    }
}
