package com.common.app.constants;

import com.common.BuildConfig;

/**
 * Author：CHENHAO
 * date：2018/5/3
 * desc：
 */

public interface Constant {
    String BASE_URL = BuildConfig.BASE_URL;
    int OKHTTP_TIMEOUT = 10;

    //通用字段
    String LOGIN_SUCCESS = "login_success";
    String LOGOUT_SUCCESS = "logout_success";
    String UPDATA_USERINFO = "updata_userinfo";
    String ONEKEY_HOME = "onekey_home";
    String THREEKEY_HOME = "threekey_home"; //商品订单购买支付成功以后   返回到赚钱界面
    String PAY_UPDATE = "pay_update"; //超级会员变成特级店主（支付成功以后弹窗）

    String TITLE = "title";
    String TYPE = "type";
    String CLASSTYPEID = "classTypeId";
    String SENDDATAONE = "1";
    String SENDDATAO = "0";
    String SENDDATA = "";
    String SENDDATAONE1 = "1";
    String SENDDATAO0 = "0";
    String SENDDATA2 = "";
    String HOME_ALL_BOTTOM = "home_all_bottom";
    String WEEK_SIGN_REFRESH = "week_sign_refresh";

    String ISFIRSTOPEN = "is_first_open";//判断用户第一次打开APP

    //搜索页的渠道
    String CHANNEL = "channel";
    String SEARCHTEXT = "search_text";
    String KEYWORD="keyword";
    String KEYWORDText="keywordtext";

    //跳转到栏目
    String PROGRAMAID = "programaId";

    //商品详情需要传的字段名，（跟前端对接吧，这个有点乱，都不愿意改）
    String id = "id";//商品主键id
    String t = "t";
    String zbjId = "zbjId";
    String i = "productId";//商品id
    String TBCOUPINID = "tbCouponId";
    String CUSTOM = "custom";
    String GATHERID = "gatherId";

    String TBITEMID = "tbItemId";
    String UUID = "uuid";
    String SORT = "sort";
    String DEVICETYPE = "deviceType";

    //60秒倒计时
    int TIME = 60;
    //广告图6秒倒计时
    int ADVERTISEMENT_TIME = 6;

    int LY_0 = 0;
    int LY_1 = 1;
    int LY_2 = 2;
    int LY_3 = 3;
    int LY_4 = 4;
    int LY_5 = 5;
}
