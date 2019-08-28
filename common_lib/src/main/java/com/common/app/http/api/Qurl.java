package com.common.app.http.api;

import com.common.BuildConfig;

/**
 * @author: zhengjr
 * @since: 2018/6/22
 * @describe:
 */

public interface Qurl {

    String HOST = BuildConfig.BASE_URL;

    String WEB = "appH/html/";


    //首次打开app图片
    String guide = "indexShow/start_img";

    //进入app时的广告图，每次都会请求的数据加载
    String adverInfo = "showAdviertisement/advertisingImg";


    //上架前显示隐藏接口
    String queryShowHide = "indexShow/queryShowHide";

    //APP更新
    String appUpdata = "user/appUsers/get_appVersion";
    //获取活动红包
    @Deprecated
    String getHuoDongRed = "activity_desc/getActivContent";
    String getHuoDongRed2_0 = "activity_desc/getActiveFrame";

    /**
     * 通用接口
     */
    //获取分享商品的域名
    String getShareProductUrl="indexShow/queryShowHide";
    //通过商品id获取商品的详细信息
    String MyTeam = "PartnerCenter/MyTeam";
    //二级粉丝
    String MyFansTeam = "PartnerCenter/NextTeam";
    //消息
    String allNews = "wjf/notice/all_news";
    //获取列表参数
    String productShowAll = "indexShow/productShowAll2.0";
    //搜索热搜接口
    String HotSearchWord = "indexShow/HotSearchWord";
    //登录用户信息
    String userInfo = "user/appUsers/userInfo";
    String userInfo2 = "user/appUsers/userInfo2.0";//用户信息
    String incomeOverview = "user/appUsers/incomeOverview";//收益信息
    String userAllowance = "userAllowance";//管理津贴信息
    //活动
    String active = "user/appUsers/active_custom";
    //分享APP
    @Deprecated
    String getShareImages = "user/appUsers/get_share_images_new";
    String getShareImagesCode = "user/appUsers/get_shareImage_code";
    //常见问题
    String problem = HOST + WEB + "problem.html";
    //新手指引
    String newpeople = HOST + WEB + "new_user_guide.html";
    //官方公告
    String gonggao = HOST + WEB + "gonggao.html";
    //超级会员
    String supermember = HOST + "appH/html/supermember.html";
    //升级会员
    String shengji = HOST + "appH/html/vip_up.html";
    //管理中心
    String guanli = HOST + "/appH/html/op_system_index.html";
    String guanli1 = HOST + "#/login";
    //栏目接口
    String programaShowAll = "indexShow/programaShowAll3.0";
    //咚咚抢
    String ddq = HOST + WEB + "ddq.html";



    /**
     * 登录接口
     */
    //协议
    String xiexi = HOST + WEB + "statement.html";
    //注册发送验证码
    String registVerifiCode = "retrieve/registVerifiCode";
    //注册
    String register = "retrieve/register";
    //登录
    String login = "retrieve/login";
    //设置登录密码 登录
    String improveUserInfo = "improveUserInfo";
    //手机号登录
    String identifyUserSMS = "identifyUserSMS";
    //登出
    String logout = "logout";
    //忘记密码
    String sureUserPassword = "retrieve/sureUserPassword";
    //忘记密码时发送验证码
    String sendVerifiCode = "user/appUsers/sendVerifiCode";
    //微信授权登录请求用户access_token
    String accessToken = "https://api.weixin.qq.com/sns/oauth2/access_token";
    //微信授权登录请求用户信息
    String userinfo = "https://api.weixin.qq.com/sns/userinfo";
    //微信授权验证
    String verify = "verifyThirdCheckOpenId";
    //微信授权验证发送验证码
    @Deprecated
    String registVerifiCodeWeixinOrQQ = "registVerifiCodeWeixinOrQQ";
    String thirdVerifiCode = "thirdVerifiCode";
    //登录后直接绑定接口
    String userBindingWeChatOrQQ = "userBindingWeChatOrQQ";
    //微信授权绑定
    String thirdCheckVerifiCode = "thirdCheckVerifiCode";




    /**
     * 首页接口
     */

    //首页全部的首次数据
//    String homePageShowAll = "indexShow/homePageShowAll3.0";
    String homePageShowAll = "indexShow/homePageShowAll4.0";

    //其他页面的子选项
    String getNextTbClass =  "indexShow/getNextTbClass";


    /**
     * 品牌闪购接口
     */
    String brandListShow = "brandShow/brandListShow";
    //底部刷新数据
    String brandShowAll = "brandShow/brandShowAll";
    //详情第一次接口
    String brandDetail = "brandShow/brandDetail";
    //详情刷新接口
    String brandProductShow = "brandShow/brandProductShow";

    /**
     * 商品详情
     */
    String productDetails = HOST + WEB + "details_new1.html?";
    /**
     * 我的订单接口
     */
    String myOrder = "/oreder/details?";

    /**
     * module接口
     */
    //修改密码
    String updateAppUserPassword = "user/appUsers/updateAppUserPassword";
    //修改手机号
    String changeUserPhone = "retrieve/changeUserPhone";
    //验证身份
    String authentication = " /retrieve/getVerifiCode";
    //修改昵称
    String updateAppUser = "user/appUsers/updateAppUser";
    //上传图片
    String fileUpload = "api/fileUploadImage/upload";
    //获取微信客服
    String serviceCode = "user/appUsers/serviceCode";
    @Deprecated//获取商品转链接，旧接口
    String getTaoBaoUrl = "indexShow/get_tbPrivilegeUrl_tbTpwd";
    @Deprecated//获取商品转链接，旧接口
    String getTaoBaoUrl2_0 = "indexShow/get_tbPrivilegeUrl_tbTpwd2.0";
    @Deprecated
    String getTaoBaoUrl3_0 = "indexShow/get_tbPrivilegeUrl_tbTpwd3.0";
    String getTaoBaoUrl4_0 = "indexShow/get_tbPrivilegeUrl_tbTpwd4.0";
    //链接转标题
    String urlToTitle = "/productCollections/tbpwd_Product";
    //获取商品数据
    @Deprecated//旧接口
    String zhuanProductDetail = "zhuanPage/zhuanProductDetail";
    String zhuanProductDetail1 = "zhuanPage/ProductDetailon";
    //我的收益
    String myEarnings = "/wjf/userIntegral/myincome";
    //我的收益记录
    String earningsRecord = "/cash/points/recordShowList";

    //我的收藏
    String shoucang = "productCollections/getCoullect";
    //删除收藏
    String deleteShoucang = "productCollections/dels_coullect";
    //超级入口
    String superEntrance = "/entrance/seEntrance";
    //我的足迹
    String getZuji = "productCollections/getMyFootprint";
    //删除我的足迹
    String deleteZuji = "productCollections/delete_all_product_foot";
    //提现信息
    String userWithdraw = "userWithdraw";
    //提现接口
    String sendWithdraw = "sendWithdraw";
    //朋友圈左边数据
    String friendsLeft = "circle/productCircleShow_1.0";
    //朋友圈右边数据
    String friendsRight = "circle/posterCircleShow_1.0";
    //获取朋友圈的接口
    @Deprecated
    String friendsShareImages = "circle/shareProductFriendCircle_1.0";
    String friendsShareImages2 = "circle/shareProductFriendCircle_2.0";
    //意见反馈
    String feedback = "user/appUsers/feedback";
//    String LongChangeShort = "short_url/shorten.json";
    //长连接转短链接
    String longChangeShort = "https://api.weibo.com/2/short_url/shorten.json";
    //分享前先提交数据至后台
    String addShare = "productShare/addShare";
    //限时抢购头部时间导航栏
    String titleAndTime = "api/xsqg/top_index";
    //限时抢购列表
    String flashSaleDetails="api/xsqg/index";
    //限时抢购详情id
    String flashSaleId="api/xsqg/index_id";
    //我的觅卡
    String myCard="api/mi_card/miCardOverview";
    //我的觅卡
    String myCardExchange="api/mi_secret/exchange";
    //我的觅卡使用记录
    String myCardUsed="api/mi_card/miCardUseHistory";
    //我的觅卡失效记录
    String myCardDisabled="api/mi_card/miCardDisabledHistory";
    //我的觅卡兑换记录
    String myCardExchangeRecord="api/mi_card/exchange_history";
    //我的觅卡类型详情
    String myCardMore="api/mi_card/miCardTypeDetail";
    //今日打卡状态
    String myTodayPunchInSituation="api/punchIn/todayPunchInSituation";
    //参与打卡状态
    String myParticipateState="api/punchIn/tomorrowJoinSituation";
    //获取用户战绩
    String userPunchInHistory="api/punchIn/userPunchInHistory";
    //获取用户战绩汇总
    String userPunchInSituation="api/punchIn/userPunchInSituation";
    //打卡
    String userPunchIn="api/punchIn/punchIn";
    //我的觅卡获取支付信息
    String message="api/punchIn/join/message";
    //开始支付
    String startPay="api/punchIn/join";
    //支付成功后通知后台
    String notify="api/punchIn/join/ali/notify";
    //免费试用
    String myTryAll="free_product_ing/show_ing_all";
    //我的试用黄卡个数
    String haveCardsNums="free_product_ing/have_cards_num";
    //我的试用（全部，参与中，开奖中，已中奖）接口
    String showIngMy="free_product_ing/show_ing_my";
    //免费试用活动规则
    String tryRule="free_product_ing/regular";
    //参与成功
    String joinSuccess = "free_product_ing/participate_trial";
    //试用详情
    String tryDetails="free_product_ing/show_ing_one";
    //试用分享
    String tryAllShareApp="user/appUsers/get_activShare_code";
    //签到赢大奖
    String signShowsInfo = "sign/activity/showsInfo";
    //去签到
    String signRun = "sign/activity/sign_run";
    //领取奖励
    String getReward = "sign/activity/get_reward";
    //参与签到期
    String participate = "sign/activity/participate";
    //用户邀请榜单列表
    String userRankingList = "inviteActive/userRankingList";
    //大转盘内容
    String dialShow = "Lottoy_action/show";
    //大转盘抽奖
    String dialRun = "Lottoy_action/lot_run";
    //邀请用户详情
    String inviteUserList = "inviteActive/inviteUserList";
    //大转盘抽中奖品列表
    String bigWhellLuckInventory = "Lottoy_action/show_win_info";
    //拉新主页
    String getActiveInfo = "inviteActive/getActiveInfo";
    //拉新主页的排名
    String rankingList = "inviteActive/rankingList";
    //拉新接口领取奖品
    String acceptAward = "inviteActive/acceptAward";
    //0元购
    String myZeroBuy = "zero_product_ing/show_ing_all";
    //0元购活动规则
    String myZeroBuyRule = "zero_product_ing/regular";
    //0元抢购详情
    String myZeroBuyDetails="zero_product_ing/show_ing_one";
    //0元抢购绿卡个数
    String zeroBuyHaveCardsNums="zero_product_ing/have_cards_num";
    //神秘宝箱获取活动信息
    String lottoyShow = "Lottoy_action_box/show";
    //抽中奖品的列表
    String showWinInfo = "Lottoy_action_box/show_win_info";
    //开启宝箱
    String lotRun = "Lottoy_action_box/lot_run";
    //地址列表
    String adressList = "userAddress/getAddressList";

    //修改以及添加地址
    String editAddress = "userAddress/editAddress";
    //删除地址
    String delAddress = "userAddress/delAddress";

    //开启运营商 订单支付
    String purchase = "make_money/purchase";
    //开启运营商 支付查询
    String queryAlipay = "make_money/queryAlipayBill";



    //0元抢购（全部，参与中，开奖中，已中奖）接口
    String zeroBuyShowIngMy="zero_product_ing/show_ing_my";
    //0元购我的排名
    String zeroBuyMyRanking="zero_product_ing/ranking";
    //0元购参与成功
    String zeroBuyJoinSuccess="zero_product_ing/participate_trial";
    //0元购参与成功
    String authorToken = "taobao_author/author";
    //0元购分享
    String zeroBuyShareApp="user/appUsers/get_activShare_code";
    //获取红包状态
    String getActiveStatus  = "redPacketActive/getActiveStatus";
    //红包明细
    String redPackageInfo = "redPacketActive/getActiveInfo";
    //淘宝福利入口
    String tmallSupermarket="taobao_welfare/index";
    //淘宝福利说明
    String tmallSuperMarketRule="taobao_welfare/show_explain";
    //淘宝福利天猫详情
    String tmallSuperMarketTmallDetail="taobao_welfare/tmall_detail";
    //淘宝福利天猫详情
    String tmallSuperMarketTmallList="taobao_welfare/show_list";
    //余额提现
    String transferAccounts = "redPacketActive/transferAccounts";
    //设置红包的发放金额
    String redPacketFit = "redPacketActive/redPacketFit";
    //用户红包明细
    String redPacketInfo = "redPacketActive/receiveRedPacketDetail";
    //红包开关
    String operateSwitch = "redPacketActive/operateSwitch";
    //充值界面余额
    String rechargeInfo = "redPacketActive/rechargeInfo";
    //红包金额充值
    String operateRecharge="redPacketActive/operateRecharge";
    //淘宝授权
    String shouquan = "taobao_author/channel";
    //淘宝授权重定向标记
    String author = "taobao_author/author";
    //登录弹出框
    String popWindow = "activity_desc/getMiCardPeck";
    //新人红包接口回调
    String queryAlipayBill="redPacketActive/queryAlipayBill";
    //会员升级2.0
    String getAppraise2="vip_up/get_appraise2.0";
    //398详情
    String getProDetail="make_money/pro_detail";
    //购买订单
    String getMyOrder="make_money/order";
    //管理津贴提现
    String withdrawAllowance="withdrawAllowance";
    //管理津贴提现
    String userAllowanceList="userAllowanceList";
    //订单找回
    String findOrder="back/orders/query_add";
}
