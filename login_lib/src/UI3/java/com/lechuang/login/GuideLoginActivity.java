package java.com.lechuang.login;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.common.BuildConfig;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseOtherActivity;
import com.common.app.constants.Constant;
import com.common.app.database.manger.UserHelper;
import com.common.app.utils.ActivityStackManager;
import com.lechuang.login.R;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @author: zhengjr
 * @since: 2018/11/20
 * @describe:
 */
@Route(path = ARouters.PATH_GUIDE_LOGIN)
public class GuideLoginActivity extends BaseOtherActivity implements View.OnClickListener {

    @Autowired
    public boolean mReStartApp = false;//用于判断用户是否token过期，而且不重新登录。


    @Override
    protected int getLayoutId() {
        return R.layout.activity_guide_login;
    }

    @Override
    protected void findViews() {
        $(R.id.tv_other_login).setOnClickListener(this);
        $(R.id.iv_weixin_login).setOnClickListener(this);
        $(R.id.iv_common_close).setOnClickListener(this);

    }

    @Override
    protected void initView() {
        ARouter.getInstance().inject(this);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void getData() {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_other_login){
            ARouter.getInstance().build(ARouters.PATH_LOGIN).withBoolean("mReStartApp",mReStartApp).navigation();
        }else if (id == R.id.iv_weixin_login){
            //通过WXAPIFactory工厂获取IWXApI的示例
            IWXAPI api = WXAPIFactory.createWXAPI(this, BuildConfig.WXAPPID, true);
            //将应用的appid注册到微信
            api.registerApp(BuildConfig.WXAPPID);
            SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";//
            req.state = "app_wechat";
            api.sendReq(req);
        }else if (id == R.id.iv_common_close){
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mReStartApp) {
            //清空用户列表，发出退出成功，清空栈的信息直到最后一条
            UserHelper.getInstence().deleteAllUserInfo();
            EventBus.getDefault().post(Constant.LOGOUT_SUCCESS);
            ActivityStackManager.getInstance().finishActivityToLast();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeNav(String message) {
        if (message.equalsIgnoreCase(Constant.LOGIN_SUCCESS)) {
            finish();
        }
    }
}
