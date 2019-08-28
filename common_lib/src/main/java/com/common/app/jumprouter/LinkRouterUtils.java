package com.common.app.jumprouter;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.baichuan.android.trade.AlibcTrade;
import com.alibaba.baichuan.android.trade.callback.AlibcTradeCallback;
import com.alibaba.baichuan.android.trade.model.AlibcShowParams;
import com.alibaba.baichuan.android.trade.model.OpenType;
import com.alibaba.baichuan.android.trade.page.AlibcBasePage;
import com.alibaba.baichuan.android.trade.page.AlibcPage;
import com.alibaba.baichuan.trade.biz.context.AlibcTradeResult;
import com.alibaba.baichuan.trade.biz.core.taoke.AlibcTaokeParams;
import com.common.BuildConfig;
import com.common.app.arouter.ARouters;
import com.common.app.base.FragmentActivity;
import com.common.app.constants.Constant;
import com.common.app.database.manger.UserHelper;
import com.common.app.http.api.Qurl;
import com.common.app.utils.StringUtils;
import com.common.app.utils.Utils;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * @author: zhengjr
 * @since: 2018/9/4
 * @describe:
 */

public class LinkRouterUtils implements IBuilder {


    private static class LazySingle {
        public static LinkRouterUtils mLinkRouterUtils = new LinkRouterUtils();
    }

    private LinkRouterUtils() {
    }

    public static LinkRouterUtils getInstance() {
        return LazySingle.mLinkRouterUtils;
    }


    @Override
    public void startOutsideH5Builder(Context activity, String url) {
        AlibcShowParams alibcShowParams = new AlibcShowParams(OpenType.Native, false);
        AlibcTaokeParams taoke = new AlibcTaokeParams();
        Map exParams = new HashMap<>();
        exParams.put("isv_code", "appisvcode");
        exParams.put("alibaba", "阿里巴巴");//自定义参数部分，可任意增删改
        taoke.setPid(BuildConfig.ALI_PID);
        AlibcBasePage alibcBasePage = new AlibcPage(url);//实例化URL打开page
        try {
            AlibcTrade.show((Activity) activity, alibcBasePage, alibcShowParams, taoke, exParams, new AlibcTradeCallback() {
                @Override
                public void onTradeSuccess(AlibcTradeResult alibcTradeResult) {
                }

                @Override
                public void onFailure(int i, String s) {
                    Utils.toast(s);
                }
            });
        } catch (Exception e) {

        }

    }

    @Override
    public void startInsideH5Builder(String url, String title, RouterBean routerBean) {
        if (routerBean.linkAllows == 1) {
            ARouter.getInstance().build(ARouters.PATH_COMMOM_WEB)
                    .withString("loadUrl", url)
                    .withString(Constant.TITLE, title)
//                .withString("userId", TextUtils.isEmpty(UserHelper.getInstence().getUserInfo().getId()) ? "" : UserHelper.getInstence().getUserInfo().getId())
                    .navigation();
        } else {
            ARouter.getInstance().build(ARouters.PATH_LOGIN).navigation();
        }

    }

    @Override
    public void startAppParmaBuilder() {

    }

    @Override
    public void startAppNoParmaBuilder() {

    }

    @Override
    public void startProductInfo(String i, String id, String t, String zbjId) {
        ARouter.getInstance()
                .build(ARouters.PATH_PRODUCT_INFO)
                .withString(Constant.i, i)
                .withString(Constant.id, id)
                .withString(Constant.t, t)
                .navigation();
    }

    @Override
    public void gotoTop() {

    }

    public void setRouterBean(Context activity, RouterBean routerBean) {

        if (routerBean.type == 0) {

        } else if (routerBean.type == 1) {//
            // 1 淘宝网页(手淘)；
            if (routerBean.linkAllows == 1) {
                startOutsideH5Builder(activity, routerBean.link);
            } else {
                ARouter.getInstance().build(ARouters.PATH_LOGIN).navigation();
            }


        } else if (routerBean.type == 2) {//
            // 2 淘宝网页(打开淘宝链接)；
            startInsideH5Builder(routerBean.link, "", routerBean);

        } else if (routerBean.type == 3) {
            if (routerBean.customParam!=null){
                if (!UserHelper.getInstence().isLogin()){
                    ARouter.getInstance().build(ARouters.PATH_LOGIN).navigation();
                    return;
                }
                String userId=null;
                String phone=null;
                List idList = Arrays.asList(routerBean.customParam.split(","));
                for (int i=0;i<idList.size();i++){
                    if (idList.get(i).equals("userId")){
                        userId=UserHelper.getInstence().getUserInfo().getId();
                    }
                    if (idList.get(i).equals("phone")){
                        phone=UserHelper.getInstence().getUserInfo().getPhone();
                    }
                }
                String newLink=routerBean.link;
                if (routerBean.link.contains("?")){
                    if (userId!=null){
                        newLink=newLink+"&userId="+userId;
                    }
                    if (phone!=null){
                        newLink=newLink+"&phone="+phone;
                    }
                }else {
                    if (userId!=null&&phone!=null){
                        newLink=newLink+"?userId="+userId+"&phone="+phone;
                    }else if (userId!=null){
                        newLink=newLink+"?userId="+userId;
                    }else if (phone!=null){
                        newLink=newLink+"?phone="+phone;
                    }
                }
                startInsideH5Builder(newLink, "", routerBean);
            }else {
                // 3 第三方链接；
                startInsideH5Builder(routerBean.link, "", routerBean);
            }


        } else if (routerBean.type == 4) {
            // 4 内部网页；
            startInsideH5Builder(routerBean.link, "", routerBean);

        } else if (routerBean.type == 5) {
            //5 商品栏目；

            String rootName = routerBean.rootName;
            if (TextUtils.isEmpty(rootName)) {
                rootName = StringUtils.getUrlString(routerBean.mustParam, "name");
            }

            String link = routerBean.link;
            if (TextUtils.isEmpty(link)) {
                Utils.toast("link参数不正确！");
            }

            String programaId = "";
            if (TextUtils.isEmpty(programaId)) {
                programaId = StringUtils.getUrlString(routerBean.mustParam, "programaId");
            }

            String classTypeId = "";
            if (TextUtils.isEmpty(programaId)) {
                classTypeId = StringUtils.getUrlString(routerBean.mustParam, "classTypeId");
            }

            String type = "";
            if (TextUtils.isEmpty(type)) {
                type = StringUtils.getUrlString(routerBean.mustParam, "type");
            }

            String custom = StringUtils.getUrlString(routerBean.mustParam, "custom");

            String gatherId = StringUtils.getUrlString(routerBean.mustParam, "gatherId");

            ARouter.getInstance().build(ARouters.PATH_PRODUCT)
                    .withString("link", link)
                    .withString(Constant.PROGRAMAID, programaId)
                    .withString("classTypeId", classTypeId)
                    .withString("type", type)
                    .withString(Constant.TITLE, rootName)
                    .withString(Constant.CUSTOM, custom)
                    .withString(Constant.GATHERID, gatherId)
                    .navigation();

        } else if (routerBean.type == 6) {
            // 6 商品分类；

            String rootName = routerBean.rootName;
            if (TextUtils.isEmpty(rootName)) {
                rootName = StringUtils.getUrlString(routerBean.mustParam, "name");
            }

            String link = routerBean.link;
            if (TextUtils.isEmpty(link)) {
                Utils.toast("link参数不正确！");
            }

            String programaId = "";
            if (TextUtils.isEmpty(programaId)) {
                programaId = StringUtils.getUrlString(routerBean.mustParam, "programaId");
            }

            String classTypeId = "";
            if (TextUtils.isEmpty(programaId)) {
                classTypeId = StringUtils.getUrlString(routerBean.mustParam, "classTypeId");
            }

            String type = "";
            if (TextUtils.isEmpty(type)) {
                type = StringUtils.getUrlString(routerBean.mustParam, "type");
            }

            String custom = StringUtils.getUrlString(routerBean.mustParam, "custom");

            String gatherId = StringUtils.getUrlString(routerBean.mustParam, "gatherId");

            ARouter.getInstance().build(ARouters.PATH_PRODUCT_TYPE)
                    .withString(Constant.TITLE, rootName)
                    .withString("link", link)
                    .withString(Constant.PROGRAMAID, programaId)
                    .withString("classTypeId", classTypeId)
                    .withString("type", type)
                    .withString(Constant.CUSTOM, custom)
                    .withString(Constant.GATHERID, gatherId)
                    .navigation();

        } else if (routerBean.type == 7) {
            // 7 首页商品位置；

        } else if (routerBean.type == 8) {
            // 8 商品集合页；(自定义商品列表和数据源)

            String rootName = routerBean.rootName;
            if (TextUtils.isEmpty(rootName)) {
                rootName = StringUtils.getUrlString(routerBean.mustParam, "name");
            }

            String link = routerBean.link;
            if (TextUtils.isEmpty(link)) {
                Utils.toast("link参数不正确！");
            }

            String programaId = "";
            if (TextUtils.isEmpty(programaId)) {
                programaId = StringUtils.getUrlString(routerBean.mustParam, "programaId");
            }

            String classTypeId = "";
            if (TextUtils.isEmpty(programaId)) {
                classTypeId = StringUtils.getUrlString(routerBean.mustParam, "classTypeId");
            }

            String type = "";
            if (TextUtils.isEmpty(type)) {
                type = StringUtils.getUrlString(routerBean.mustParam, "type");
            }

            String custom = StringUtils.getUrlString(routerBean.mustParam, "custom");

            String gatherId = StringUtils.getUrlString(routerBean.mustParam, "gatherId");

            ARouter.getInstance().build(ARouters.PATH_PRODUCT_JIHE)
                    .withString(Constant.TITLE, rootName)
                    .withString("link", link)
                    .withString(Constant.PROGRAMAID, programaId)
                    .withString("classTypeId", classTypeId)
                    .withString("type", type)
                    .withString(Constant.CUSTOM, custom)
                    .withString(Constant.GATHERID, gatherId)
                    .navigation();


        } else if (routerBean.type == 9) {
            // 9 商品详情；

            String id = StringUtils.getUrlString(routerBean.mustParam, "id");
            String tbItemId = StringUtils.getUrlString(routerBean.mustParam, "tbItemId");
            String tbCouponId = StringUtils.getUrlString(routerBean.mustParam, "tbCouponId");
            String type_info = StringUtils.getUrlString(routerBean.mustParam, "type");
            if (TextUtils.isEmpty(type_info)) {
                type_info = StringUtils.getUrlString(routerBean.mustParam, "t");
            }

            if (TextUtils.isEmpty(type_info)) {
                type_info = "1";
            }
            String uuid = StringUtils.getUrlString(routerBean.mustParam, "uuid");
            String sort = StringUtils.getUrlString(routerBean.mustParam, "sort");


            ARouter.getInstance()
                    .build(ARouters.PATH_PRODUCT_INFO)
                    .withString(Constant.id, id)
                    .withString(Constant.TBITEMID, tbItemId)
                    .withString(Constant.TBCOUPINID, tbCouponId)
                    .withString(Constant.TYPE, type_info)
                    .withString(Constant.UUID, uuid)
                    .withString(Constant.SORT, sort)
                    .navigation();

        } else if (routerBean.type == 10) {//10 收藏；
            if (UserHelper.getInstence().isLogin()) {
                ARouter.getInstance().build(ARouters.PATH_SHOUCANG).navigation();
            } else {
                ARouter.getInstance().build(ARouters.PATH_LOGIN).navigation();
            }

        } else if (routerBean.type == 11) {//11 足迹
            if (UserHelper.getInstence().isLogin()) {
                ARouter.getInstance().build(ARouters.PATH_ZUJI).navigation();
            } else {
                ARouter.getInstance().build(ARouters.PATH_LOGIN).navigation();
            }

        } else if (routerBean.type == 12) {//12 教程（新手指引）
            if (UserHelper.getInstence().isLogin()) {
                String rootName = routerBean.rootName;
                if (TextUtils.isEmpty(rootName)) {
                    rootName = StringUtils.getUrlString(routerBean.mustParam, "name");
                }
                ARouter.getInstance().build(ARouters.PATH_COMMOM_WEB)
                        .withString("loadUrl", Qurl.newpeople)
                        .withString(Constant.TITLE, rootName)
                        .withInt(Constant.TYPE, 4)
                        .navigation();
            } else {
                ARouter.getInstance().build(ARouters.PATH_LOGIN).navigation();
            }


        } else if (routerBean.type == 13) {//帮助（常见问题）
            if (UserHelper.getInstence().isLogin()) {
                String rootName = routerBean.rootName;
                if (TextUtils.isEmpty(rootName)) {
                    rootName = StringUtils.getUrlString(routerBean.mustParam, "name");
                }

                ARouter.getInstance().build(ARouters.PATH_COMMOM_WEB)
                        .withString("loadUrl", Qurl.problem)
                        .withString(Constant.TITLE, rootName)
                        .withInt(Constant.TYPE, 4)
                        .navigation();
            } else {
                ARouter.getInstance().build(ARouters.PATH_LOGIN).navigation();
            }


        } else if (routerBean.type == 14) {//14微信客服；
            if (UserHelper.getInstence().isLogin()) {
                //这里需要读写的权限
                ARouter.getInstance().build(ARouters.PATH_WX_KEFU).navigation();

            } else {
                ARouter.getInstance().build(ARouters.PATH_LOGIN).navigation();
            }


        } else if (routerBean.type == 15) {//15我的收益；
            if (UserHelper.getInstence().isLogin()) {
                ARouter.getInstance().build(ARouters.PATH_MYEARNINGS).navigation();
            } else {
                ARouter.getInstance().build(ARouters.PATH_LOGIN).navigation();
            }

        } else if (routerBean.type == 16) {//16 提现；
            if (UserHelper.getInstence().isLogin()) {
                ARouter.getInstance().build(ARouters.PATH_WITHDRAW_DEPOSI).navigation();
            } else {
                ARouter.getInstance().build(ARouters.PATH_LOGIN).navigation();
            }


        } else if (routerBean.type == 17) {//17 APP分享；
            if (UserHelper.getInstence().isLogin()) {
                ARouter.getInstance().build(ARouters.PATH_SHARE_APP).navigation();
            } else {
                ARouter.getInstance().build(ARouters.PATH_LOGIN).navigation();
            }


        } else if (routerBean.type == 18) {//18 我的团队；
            if (UserHelper.getInstence().isLogin()) {
                ARouter.getInstance().build(ARouters.PATH_FENSI_A).navigation();
            } else {
                ARouter.getInstance().build(ARouters.PATH_LOGIN).navigation();
            }
        } else if (routerBean.type == 19) {//19 订单明细；
            if (UserHelper.getInstence().isLogin()) {
                ARouter.getInstance().build(ARouters.PATH_ORDER).navigation();
            } else {
                ARouter.getInstance().build(ARouters.PATH_LOGIN).navigation();
            }


        } else if (routerBean.type == 20) {
            // 20：京东；


        } else if (routerBean.type == 21) {
            // 21：拼多多；


        } else if (routerBean.type == 22) {
            // 22：飞猪

        } else if (routerBean.type == 23) {// 23： 官方公告

            if (UserHelper.getInstence().isLogin()) {
                String rootName = routerBean.rootName;
                if (TextUtils.isEmpty(rootName)) {
                    rootName = StringUtils.getUrlString(routerBean.mustParam, "name");
                }
                ARouter.getInstance().build(ARouters.PATH_COMMOM_WEB)
                        .withString("loadUrl", Qurl.gonggao)
                        .withString(Constant.TITLE, rootName)
                        .withInt(Constant.TYPE, 4)
                        .navigation();
            } else {
                ARouter.getInstance().build(ARouters.PATH_LOGIN).navigation();
            }


        } else if (routerBean.type == 24) {//品牌详情
            //品牌详情
            String typeBrand = routerBean.typeBrand + "";
            String id = StringUtils.getUrlString(routerBean.mustParam, "id");
            String pname = routerBean.pname + "";

            if (TextUtils.isEmpty(typeBrand) || TextUtils.equals("null", typeBrand)) {
                typeBrand = StringUtils.getUrlString(routerBean.mustParam, "type");
            }

            if (TextUtils.isEmpty(pname) || TextUtils.equals("null", pname)) {
                typeBrand = StringUtils.getUrlString(routerBean.mustParam, "name");
            }

            ARouter.getInstance().build(ARouters.PATH_BRAND_INFO)
                    .withString(Constant.id, id)
                    .withString(Constant.TYPE, typeBrand)
                    .withString(Constant.TITLE, pname)
                    .navigation();
        } else if (routerBean.type == 25) {//自定义跳转。对应的是活动红包跳后台传的是5（淘宝活动链接（手淘）），自定义为25

            AlibcShowParams alibcShowParams = new AlibcShowParams(OpenType.Native, false);
            AlibcTaokeParams taoke = new AlibcTaokeParams();
            Map exParams = new HashMap<>();
            exParams.put("isv_code", "appisvcode");
            exParams.put("alibaba", "阿里巴巴");//自定义参数部分，可任意增删改
            taoke.setPid(BuildConfig.ALI_PID);
            AlibcBasePage alibcBasePage = new AlibcPage(routerBean.activeUrl);//实例化URL打开page
            try {
                AlibcTrade.show((Activity) activity, alibcBasePage, alibcShowParams, taoke, exParams, new AlibcTradeCallback() {
                    @Override
                    public void onTradeSuccess(AlibcTradeResult alibcTradeResult) {
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Utils.toast(s);

                    }
                });
            } catch (Exception e) {

            }


        } else if (routerBean.type == 26) {//自定义跳转。对应的是活动红包跳后台传的是6（淘宝活动链接（app）），自定义为26

        } else if (routerBean.type == 27) {//自定义跳转。对应的是活动红包跳后台传的是7（活动商品），自定义为27

        } else if (routerBean.type == 30) {//管理中心
            if (UserHelper.getInstence().isLogin()) {
                ARouter.getInstance().build(ARouters.PATH_COMMOM_WEB)
                        .withString("loadUrl", Qurl.guanli1 + "?phone=" + UserHelper.getInstence().getUserInfo().getPhone())
                        .withInt(Constant.TYPE, 4)
                        .navigation();
            } else {
                ARouter.getInstance().build(ARouters.PATH_LOGIN).navigation();
            }

        } else if (routerBean.type == 31) {//限时抢购
            ARouter.getInstance().build(ARouters.PATH_FLASH_SALE).navigation();
//            ARouter.getInstance().build(ARouters.PATH_COMMOM_WEB)
//                    .withString("loadUrl", Qurl.guanli1 + "?phone=" + UserHelper.getInstence().getUserInfo().getPhone())
//                    .withString(Constant.TITLE, "管理中心")
//                    .withInt(Constant.TYPE, 4)
//                    .navigation();
        } else if (routerBean.type == 32) {//吱口令

            String inviteCode = routerBean.commandCopy.toString();
            if (!TextUtils.isEmpty(inviteCode)) {
                ClipData clipData = ClipData.newPlainText("app_inviteCode", inviteCode);
                ((ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(clipData);
            }
            ARouter.getInstance().build(ARouters.PATH_COMMOM_WEB)
                    .withString("loadUrl", routerBean.link)
                    .withInt(Constant.TYPE, 4)
                    .navigation();

        } else if (routerBean.type == 33) {//品牌闪购

            ARouter.getInstance().build(ARouters.PATH_BRAND_ACTIVITY)
                    .navigation();
        } else if (routerBean.type == 34) {//收益消息（这里只用于推送，不用于banner图那些地方的跳转，后期要是给你扯皮叼死他，）

            if (UserHelper.getInstence().isLogin()) {
                ARouter.getInstance().build(ARouters.PATH_NEWS)
                        .withBoolean("mSetDefState", false)
                        .navigation();
            } else {
                ARouter.getInstance().build(ARouters.PATH_LOGIN).navigation();
            }

        } else if (routerBean.type == 35) {//其他消息（这里只用于推送，不用于banner图那些地方的跳转，后期要是给你扯皮叼死他，）

            if (UserHelper.getInstence().isLogin()) {
                ARouter.getInstance().build(ARouters.PATH_NEWS)
                        .withBoolean("mSetDefState", true)
                        .navigation();
            } else {
                ARouter.getInstance().build(ARouters.PATH_LOGIN).navigation();
            }

        }else if(routerBean.type ==36){
            if (routerBean.attachParam.equals( "activeType=10" )){//品牌闪购
                ARouter.getInstance().build(ARouters.PATH_BRAND_ACTIVITY).navigation();
                return;
            }
            if (!UserHelper.getInstence().isLogin()){
                ARouter.getInstance().build(ARouters.PATH_LOGIN).navigation();
                return;
            }
            //活动类型 activeType=1 免费试用; activeType=2 签到活动; activeType=3 早起打卡;
            // activeType=4 幸运转盘; activeType=5 神秘宝箱; activeType=6 0元抢购; activeType=7 拉新榜单;
            // activeType=8 红包活动; activeType=9 淘宝福利(漏洞单); activeType=10 品牌
            if (routerBean.attachParam.equals( "activeType=1" )){//免费试用
                ARouter.getInstance().build(ARouters.PATH_MY_TRY).navigation();
            }else if (routerBean.attachParam.equals( "activeType=2" )){//签到活动
                Fragment mineTryFragment = (Fragment) ARouter.getInstance().build(ARouters.PATH_WEEK_SIGNIN).navigation();
                FragmentActivity.start(routerBean.thisActivity, mineTryFragment.getClass());
            }else if (routerBean.attachParam.equals( "activeType=3" )){//早起打卡
                ARouter.getInstance().build(ARouters.PATH_SIGN_IN).navigation();
            }else if (routerBean.attachParam.equals( "activeType=4" )){//幸运转盘
                ARouter.getInstance().build(ARouters.PATH_MY_BIG_WHELL).navigation();
            }else if (routerBean.attachParam.equals( "activeType=5" )){//神秘宝箱
                ARouter.getInstance().build(ARouters.PATH_MY_TREASURE_BOX).navigation();
            }else if (routerBean.attachParam.equals( "activeType=6" )){//0元购
                 ARouter.getInstance().build(ARouters.PATH_MY_ZERO_BUY).navigation();
            }else if (routerBean.attachParam.equals( "activeType=7" )){//拉新
                Fragment mineTryFragment = (Fragment) ARouter.getInstance().build(ARouters.PATH_LAXIN).navigation();
                FragmentActivity.start(routerBean.thisActivity, mineTryFragment.getClass());
            }else if (routerBean.attachParam.equals( "activeType=8" )){//红包
                ARouter.getInstance().build(ARouters.PATH_REDPACKAGE).navigation();
            }else if (routerBean.attachParam.equals( "activeType=9" )){//淘宝福利(漏洞单)
                ARouter.getInstance().build(ARouters.PATH_TMALL_SUPERMARKET).navigation();
            }

        } else if (routerBean.type == 1000) {//自定义参数，打开支付宝
            try {
                PackageManager packageManager
                        = activity.getApplicationContext().getPackageManager();
                Intent intent = packageManager.
                        getLaunchIntentForPackage("com.eg.android.AlipayGphone");
                activity.startActivity(intent);
            } catch (Exception e) {
                ARouter.getInstance().build(ARouters.PATH_COMMOM_WEB)
                        .withString("loadUrl", "https://ds.alipay.com/?from=mobileweb")
                        .withString(Constant.TITLE, "点击打开支付宝")
                        .withInt(Constant.TYPE, 4)
                        .navigation();
            }
        } else {
            Utils.toast(routerBean.type + "非法路由跳转！");
        }
    }
}
