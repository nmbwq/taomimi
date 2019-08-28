package com.common.app.arouter;

/**
 * @author: zhengjr
 * @since: 2018/8/9
 * @describe:定义路由
 */

public interface ARouters {


    /**
     *  引导页
     */
    String PATH_GUILD = "/main/GuildActivity";

    /**
     * 广告页
     */
    String PATH_ADVER = "/main/AdverActivity";

    /**
     * 主页
     */
    //主页
    String PATH_MAIN = "/main/MainActivity";

    /**
     * 首页路由
     */
    //首页
    String PATH_HOME = "/home/HomeFragment";
    String PATH_HOME_SERVICE = "/home/service";
    //首页的全部fragment
    String PATH_ALL = "/home/AllFragment";
    //首页的其他fragment
    String PATH_OTHER = "/home/OtherFragment";


    /**
     * 分类路由
     */
    //分类
    String PATH_FEN_LEI = "/fenlei/FenLeiFragment";


    /**
     * 直播路由
     */
    //直播
    String PATH_ZHI_BO = "/zhibo/ZhiBoFragment";


    /**
     * 品牌路由
     */
    //品牌
    String PATH_BRAND = "/brand/BrandFragment";
    //品牌详情
    String PATH_BRAND_INFO = "/brand/BrandInfoActivity";
    String PATH_BRAND_ACTIVITY = "/brand/BrandActivity";


    /**
     * 找券路由
     */
    //找券
    String PATH_ZHAO_QUAN = "/zhaoquan/ZhaoQuanFragment";


    /**
     * 我的路由
     */
    //我的
    String PATH_MINE = "/mine/MineFragment";


    /**
     * 搜索路由
     */
    //搜索
    String PATH_SEARCH = "/search/SearchActivity";
    //搜索结果页
    String PATH_SEARCH_RESULT = "/search/SearchResultActivity";


    /**
     * 登录注册路由
     */
    //登录路由(注册)GuideLoginActivity
    String PATH_GUIDE_LOGIN = "/login/GuideLoginActivity";
    //登录路由
    String PATH_LOGIN = "/login/LoginActivity";
    //注册
    String PATH_REGISTER = "/login/RegistActivity";
    //忘记密码
    String PATH_FIND_PSD = "/login/FindPsdActivity";
    //设置手机密码
    String PATH_SET_PSD = "/login/SetPsdActivity";
    //手机验证码登录
    String PATH_PHONE_LOGIN = "/login/PhoneLoginActivity";
    //第三方绑定
    String PATH_BOUND = "/login/BoundActivity";


    /**
     * module
     */
    //商品详情分享预览页
    String PATH_PRE_SHARE = "/modlue/PreShareActivity";
    //商品栏目列表
    String PATH_PRODUCT = "/modlue/ProductActivity";
    //商品集合列表
    String PATH_PRODUCT_JIHE = "/modlue/ProductJiHeActivity";
    //首页其他子类列表
    String PATH_OTHER_PRODUCT = "/modlue/OtherProductActivity";
    //商品集合列表6
    String PATH_PRODUCT_TYPE = "/modlue/ProductTypeActivity";
    //限时抢购
    String PATH_FLASH_SALE = "/modlue/FlashSaleActivity";
    //限时抢购第一页
    String PATH_FLASH_SALE_FRAGMENT = "/modlue/FlashSsleFragment";
    //粉丝里面的Activity
    String PATH_FENSI_A= "/modlue/FenSiActivity";
    //粉丝里面的二级Activity
    String PATH_MYFENSI= "/modlue/MyFansActivity";
    //粉丝里面的fragment
    String PATH_FENSI_F = "/modlue/FenSiFragment";
    //粉丝里面的fragment
    String PATH_FENSI_FS = "/modlue/FenSiStraightFragment";
    //粉丝里面的fragment
    String PATH_FENSI_FR = "/modlue/FenSiRecommendFragment";
    //超级入口
    String PATH_SUPERENTRANCE = "/modlue/SuperEntranceFragment";
    //超级入口第一页面
    String PATH_SUPERFIRST = "/modlue/SuperFirstFragment";
    //超级入口其他页面
    String PATH_SUPEROTHER = "/modlue/SuperOtherFragment";
    //超级入口百货第一页面
    String PATH_SUPERBAIHUOFIRST = "/modlue/SuperBaihuoFirstFragment";
    //超级入口另一个页面
    String PATH_SUPERBAIHUOOTHER = "/modlue/SuperBaihuoOtherFragment";
    //超级教程
    String PATH_SUPERTUTORIAL = "/modlue/SuperTutorialActivity";
    //APP圈，朋友圈。社交圈
    String PATH_FRIENDS = "/modlue/FriendsFragment";
    //朋友圈,内容
    String PATH_FRAGMENT_CONTENT = "/modlue/FriendsContentFragment";
    //朋友圈,内容其他
    String PATH_FRAGMENT_CONTENT_OTHER = "/modlue/FriendsContentOtherFragment";
    //超级会员
    String PATH_SUPER_VIP = "/modlue/SuperVipFragment";
    //消息
    String PATH_NEWS = "/modlue/NewsActivity";
    //消息内容
    String PATH_NEWS_CONTENT = "/modlue/NewsContentActivity";
    //视频购页面
    String PATH_VIDEO = "/modlue/VideoActivity";
    //视频购页面
    String PATH_VIDEO_FRAGMENT = "/modlue/VideoFragment";
    //设置页面
    String PATH_SET = "/modlue/SetActivity";
    //疯抢榜单
    String PATH_FENGQIANG = "/modlue/FengQiangFragment";
    //我的收益
    String PATH_MYEARNINGS = "/modlue/MyEarningsActivity";
    //我的收益记录
    String PATH_EARNINGSRECORD = "/modlue/EarningsRecordActivity";
    //我的订单
    String PATH_ORDER = "/modlue/OrderActivity";
    //修改昵称
    String PATH_NICK = "/modlue/UpdataNickActivity";
    //身份验证
    String PATH_VERIFY = "/modlue/AuthenticationActivity";
    //绑定支付宝
    String PATH_BIND_ZFB = "/modlue/BindZfbActivity";
    //绑定支付宝成功
    String PATH_COMPLETE_BIND = "/modlue/CompleteBindActivity";
    //更新手机号
    String PATH_UPDATA_PHONE = "/modlue/UpdataPhoneActivity";
    //更改密码（和找回密码不是一回事）
    String PATH_UPDATA_PSD = "/modlue/UpdataPsdActivity";
    //微信客服
    String PATH_WX_KEFU = "/modlue/WxKeFuActivity";
    //分享APP
    String PATH_SHARE_APP = "/modlue/ShareAppActivity";
    //商品详情
    String PATH_PRODUCT_INFO = "/modlue/ProductInfoActivity";
    //关于我们
    String PATH_ABOUT_US = "/modlue/AboutUsActivity";
    //提现信息
    String PATH_WITHDRAW_DEPOSI = "/modlue/WithdrawDepositActivity";
    //通用webview
    String PATH_COMMOM_WEB = "/modlue/CommonWebViewActivity";
    //超级入口webview
    String PATH_SUPER_WEB = "/modlue/CommonSuperWebViewActivity";
    //收藏
    String PATH_SHOUCANG = "/modlue/ShouCangActivity";
    //足迹
    String PATH_ZUJI = "/modlue/ZuJiActivity";
    //图片预览
    String PATH_PREIMG = "/modlue/PreviewImgActivity";
    //反馈
    String PATH_FANKUI = "/modlue/FanKuiActivity";

    //消息通知开关界面
    String PATH_NEW_NOTIFY = "/modlue/NewsNotifyActivity";
    //我的觅卡
    String PATH_MY_CARD = "/modlue/MyCardActivity";
    //我的觅卡
    String PATH_MY_CARD_MORE = "/modlue/MyCardMoreActivity";
    //我的觅卡记录
    String PATH_MY_CARD_RECORD = "/modlue/MyCardRecordActivity";
    //我的觅卡兑换
    String PATH_MY_CARD_EXCHANGE = "/modlue/MyCardExchangeActivity";
    //我的觅卡已使用
    String PATH_MY_CARD_USED = "/modlue/MyCardUsedFragment";
    //我的觅卡已失效
    String PATH_MY_CARD_FAILURE = "/modlue/MyCardFailureFragment";
    //我的觅卡已失效
    String PATH_MY_CARD_EXCHANGE_RECORD = "/modlue/MyCardExchangeRecordActivity";
    //打卡
    String PATH_SIGN_IN = "/modlue/SignInActivity";
    //挑战规则
    String PATH_SIGN_IN_CHALLENGE = "/modlue/SignInChallengeActivity";
    //打卡记录
    String PATH_SIGN_IN_RECORD = "/modlue/SignInRecordActivity";
    //试用
    String PATH_MY_TRY = "/modlue/MyTryActivity";
    //试用细则
    String PATH_TRY_RULE = "/modlue/TryRuleActivity";
    //我的试用首页
    String PATH_Mine_TRY = "/modlue/MineTryFragment";
    //我的试用内容
    String PATH_Mine_TRY_CONTENT = "/modlue/TryContentFragment";
    //参与成功
    String PATH_JOIN_SUCCESS_F = "/modlue/JoinSuccessFragment";
    //参与成功
    String PATH_JOIN_SUCCESS_A = "/modlue/JoinSuccessActivity";
    //试用详情
    String PATH_TRY_DETAILS = "/modlue/TryDetailsActivity";
    //每周的签到界面（签到赢大奖）
    String PATH_WEEK_SIGNIN = "/modlue/WeekSignInFragment";
    //每周的签到界面,child内容
    String PATH_WEEK_SIGNIN_CONTENT = "/modlue/WeekSignInVpContentFragment";
    //大转盘
    String PATH_MY_BIG_WHELL = "/modlue/MyBigWhellActivity";
    //大转盘规则
    String PATH_BIG_WHELL_RULE = "/modlue/BigWhellRuleActivity";
    //大转盘记录
    String PATH_BIG_WHELL_RECORD = "/modlue/BigWhellRecordActivity";
    //拉新列表
    String PATH_MINE_BANG_DAN="/modlue/MineBangDanActivity";
    //拉新列表的弹窗
    String PATH_MINE_BANG_DAN_INVITE="/modlue/MineBangDanInviteActivity";
    //拉新主页
    String PATH_LAXIN="/modlue/LaXinFragment";
    //宝箱
    String PATH_MY_TREASURE_BOX = "/modlue/MyTreasureBoxActivity";
    //0元购入口
    String PATH_MY_ZERO_BUY = "/modlue/MyZeroBuyActivity";
    //0元购活动规则
    String PATH_ZERO_BUY_RULE = "/modlue/ZeroBuyRuleActivity";
    //0元购详情
    String PATH_ZERO_BUY_DETAILS = "/modlue/ZeroBuyDetailsActivity";
    //我的奖品列表
    String PATH_VALUE_BANG_DAN="/modlue/ValueBangDanActivity";
    //我的奖品列表
    String PATH_VALUE_HOME="/modlue/ValueActivity";

    //0元购
    String PATH_Mine_ZERO_BUY = "/modlue/MineZeroBuyFragment";
    //0元购
    String PATH_Mine_ZERO_BUY_CONTENT = "/modlue/ZeroBuyContentFragment";
    //0元购参与结果页面
    String PATH_PARTICIPATE_SUCCESS_A = "/modlue/ParticipateSuccessActivity";
    //0元购参与结果页面
    String PATH_PARTICIPATE_SUCCESS_F = "/modlue/ParticipateSuccessFragment";
    //新人注册红包
    String PATH_REDPACKAGE = "/modlue/RedPackageActivity";
    //红包明细
    String PATH_REDPACKAGE_INFO = "/modlue/RedPackageInfoActivity";
    //天猫超市首页
    String PATH_TMALL_SUPERMARKET = "/modlue/TmallSupermarketActivity";
    //天猫超市优惠券详情
    String PATH_TMALL_SUPERMARKET_YHJ = "/modlue/TmallSupermarketYHJDetailsActivity";
    //天猫超市规则
    String PATH_TMALL_SUPERMARKET_RULE = "/modlue/TmallSupermarketRuleActivity";
    //天猫超市图片
    String PATH_TMALL_SUPERMARKET_IMAGE = "/modlue/TmallSupermarketImageActivity";
    //详情
    String PATH_UP_GRADE_DETAILS = "/modlue/UpGradeDetailsActivity";
    //详情Fragment
    String PATH_UP_GRADE_DETAILS_F = "/modlue/UpGradeDetailsFragment";
    //详情Fragment
    String PATH_UP_GRADE_DETAILS_OTHER_F = "/modlue/UpGradeDetailsOtherFragment";
    //升级运营商
    String PATH_UP_GRADE_F = "/modlue/UpGradeFragment";
    //用户地址列表
    String PATH_ADRESSLIST = "/modlue/AdressListActivity";
    //增加以及修改地址界面
    String PATH_ADDANDUPDATE = "/modlue/AddandUpdateActivity";
    //升级运营商
    String PATH_MY_ORDER = "/modlue/MyOrderActivity";
    //津贴提现
    String PATH_WITHDRAWL_ALLOWANCE = "/modlue/WithdrawalAllowanceActivity";
    //津贴提现记录
    String PATH_WITHDRAWL_ALLOWANCE_RECORD = "/modlue/WithdrawalAllowanceRecordActivity";
    //找回淘宝订单
    String PATH_FIND_TAO_ORDER = "/modlue/FindTaoOrderActivity";
}
