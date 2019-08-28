package java.com.lechuang.mine;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import com.ali.auth.third.ui.context.CallbackContext;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.baichuan.android.trade.AlibcTrade;
import com.alibaba.baichuan.android.trade.callback.AlibcTradeCallback;
import com.alibaba.baichuan.android.trade.model.AlibcShowParams;
import com.alibaba.baichuan.android.trade.model.OpenType;
import com.alibaba.baichuan.android.trade.page.AlibcMyCartsPage;
import com.alibaba.baichuan.trade.biz.AlibcConstants;
import com.alibaba.baichuan.trade.biz.context.AlibcTradeResult;
import com.alibaba.baichuan.trade.biz.core.taoke.AlibcTaokeParams;
import com.alibaba.baichuan.trade.biz.login.AlibcLogin;
import com.alibaba.baichuan.trade.biz.login.AlibcLoginCallback;
import com.common.BuildConfig;
import com.common.app.arouter.ARouters;
import com.common.app.base.BaseActivity;
import com.common.app.base.BaseOtherActivity;
import com.common.app.base.FragmentActivity;
import com.common.app.constants.Constant;
import com.common.app.utils.LogUtils;
import com.lechuang.mine.R;

import java.util.HashMap;
import java.util.Map;

public class ActivityTestActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected int getLayoutId() {
        return R.layout.test_activity;
    }

    @Override
    protected void findViews() {

        $(R.id.iv_common_back).setOnClickListener(this);
        $(R.id.tv_daka).setOnClickListener(this);
        $(R.id.tv_gou).setOnClickListener(this);
        $(R.id.tv_shengmi_baoxiang).setOnClickListener(this);
        $(R.id.tv_qiandao).setOnClickListener(this);
        $(R.id.tv_dazhuanpan).setOnClickListener(this);
        $(R.id.tv_laxin).setOnClickListener(this);
        $(R.id.tv_mianfei).setOnClickListener(this);
        $(R.id.tv_shouquan).setOnClickListener(this);
        $(R.id.tv_logout).setOnClickListener(this);
        $(R.id.tv_mika).setOnClickListener(this);
        $(R.id.tv_tianmao).setOnClickListener(this);
        $(R.id.tv_open_cardshop).setOnClickListener(this);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void getData() {

    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        if (id == R.id.iv_common_back){
            finish();
        }else if (id==R.id.tv_daka){
            ARouter.getInstance().build(ARouters.PATH_SIGN_IN).navigation();
        }else if (id == R.id.tv_gou){
            ARouter.getInstance().build(ARouters.PATH_MY_ZERO_BUY).navigation();
        }else if (id == R.id.tv_shengmi_baoxiang){
            ARouter.getInstance().build(ARouters.PATH_MY_TREASURE_BOX).navigation();
        }else if (id == R.id.tv_qiandao){
            Fragment mineTryFragment = (Fragment) ARouter.getInstance().build(ARouters.PATH_WEEK_SIGNIN).navigation();
            FragmentActivity.start(this, mineTryFragment.getClass());
        }else if (id == R.id.tv_dazhuanpan){
            ARouter.getInstance().build(ARouters.PATH_MY_BIG_WHELL).navigation();
        }else if (id == R.id.tv_laxin){
            Fragment mineTryFragment = (Fragment) ARouter.getInstance().build(ARouters.PATH_LAXIN).navigation();
            FragmentActivity.start(this, mineTryFragment.getClass());
        }else if (id == R.id.tv_mianfei){
            ARouter.getInstance().build(ARouters.PATH_MY_TRY).navigation();
        }else if (id == R.id.tv_shouquan){

            final String url = "https://oauth.taobao.com/authorize?response_type=code&client_id=25264288&redirect_uri=http://mm2nff.natappfree.cc/taobao_author/author?ckToken=08B99362C142DBAFC963A0FADE68CAF0&state=1212&view=wap ";

            AlibcLogin alibcLogin = AlibcLogin.getInstance();
            alibcLogin.showLogin(new AlibcLoginCallback() {
                @Override
                public void onSuccess(int i) {
                    ARouter.getInstance().build(ARouters.PATH_COMMOM_WEB)
                            .withString("loadUrl", url)
                            .withString(Constant.TITLE, "授权登陆")
                            .navigation();
                }

                @Override
                public void onFailure(int i, String s) {

                }
            });

        }else if (id == R.id.tv_logout){
            AlibcLogin alibcLogin = AlibcLogin.getInstance();
            alibcLogin.logout(new AlibcLoginCallback() {
                @Override
                public void onSuccess(int i) {
                    toast("退出成功！");
                }

                @Override
                public void onFailure(int i, String s) {

                }
            });
        }else if (id == R.id.tv_mika){
            ARouter.getInstance().build(ARouters.PATH_MY_CARD).navigation();
        }else if (id == R.id.tv_tianmao){
            ARouter.getInstance().build(ARouters.PATH_TMALL_SUPERMARKET).navigation();
        }else if (id == R.id.tv_open_cardshop){

            //提供给三方传递配置参数
            Map<String, String> exParams = new HashMap<>();
            exParams.put(AlibcConstants.ISV_CODE, "appisvcode");
            AlibcMyCartsPage alibcMyCartsPage = new AlibcMyCartsPage();

            AlibcTaokeParams taoke = new AlibcTaokeParams();
            taoke.setPid(BuildConfig.ALI_PID);

            AlibcShowParams alibcShowParams = new AlibcShowParams(OpenType.H5, true);

            AlibcTrade.show(ActivityTestActivity.this, alibcMyCartsPage, alibcShowParams, taoke, exParams, new AlibcTradeCallback() {
                @Override
                public void onTradeSuccess(AlibcTradeResult alibcTradeResult) {
                    LogUtils.w(alibcTradeResult.toString());
                }

                @Override
                public void onFailure(int i, String s) {
                    toast(s);
                }
            });
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        CallbackContext.onActivityResult(this, requestCode, resultCode, data);
    }
}
