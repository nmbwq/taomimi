package java.com.lechuang.module;

import com.common.app.base.bean.BaseResponseBean;
import com.common.app.http.api.Qurl;

import java.com.lechuang.module.bean.*;

import com.common.app.http.bean.ShareAppBean;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * @author: zhengjr
 * @since: 2018/8/23
 * @describe:
 */

public interface ModuleApi {

    //粉丝接口
    @FormUrlEncoded
    @POST(Qurl.MyTeam)
    Observable<BaseResponseBean<FenSiBean>> myTeam(@FieldMap Map<String, String> allParam);

    //二级粉丝接口
    @FormUrlEncoded
    @POST(Qurl.MyFansTeam)
    Observable<BaseResponseBean<FenSiBean>> myFansTeam(@FieldMap Map<String, String> allParam);

    //消息接口
    @FormUrlEncoded
    @POST(Qurl.allNews)
    Observable<BaseResponseBean<NewsBean>> allNews(@FieldMap Map<String, String> allParam);

    //产品接口，二级
    @FormUrlEncoded
    @POST(Qurl.programaShowAll)
    Observable<BaseResponseBean<ProductBean>> programaShowAll(@FieldMap Map<String, String> allParam);

    //产品接口，二级
    @FormUrlEncoded
    @POST()
    Observable<BaseResponseBean<ProductBean>> programaShowAllUrl(@Url String url, @FieldMap Map<String, String> allParam);

    //搜索热搜接口
    @POST(Qurl.HotSearchWord)
    Observable<BaseResponseBean<SearchHotBean>> hotSearchWord();

    //搜索结果接口
    @FormUrlEncoded
    @POST(Qurl.productShowAll)
    Observable<BaseResponseBean<SearchResultBean>> productShowAll_search_result(@FieldMap Map<String, Object> allParam);

    //搜索结果接口
    @FormUrlEncoded
    @POST()
    Observable<BaseResponseBean<SearchResultBean>> productShowAll_search_resultUrl(@Url String url,@FieldMap Map<String,String> allParam);


    //登出
    @GET(Qurl.logout)
    Observable<BaseResponseBean<String>> logout();

    //忘记密码
    @FormUrlEncoded
    @POST(Qurl.updateAppUserPassword)
    Observable<BaseResponseBean<String>> updateAppUserPassword(@FieldMap Map<String, String> allParam);

    //修改手机号
    @FormUrlEncoded
    @POST(Qurl.changeUserPhone)
    Observable<BaseResponseBean<String>> changeUserPhone(@FieldMap Map<String, String> allParam);

    //验证身份
    @FormUrlEncoded
    @POST(Qurl.authentication)
    Observable<BaseResponseBean<String>> authentication(@FieldMap Map<String, String> allParam);

    //忘记密码发送验证码
    @FormUrlEncoded
    @POST(Qurl.sendVerifiCode)
    Observable<BaseResponseBean<String>> sendVerifiCode(@FieldMap Map<String, String> allParam);

    //修改昵称
    @FormUrlEncoded
    @POST(Qurl.updateAppUser)
    Observable<BaseResponseBean<String>> updateAppUser(@FieldMap Map<String, String> allParam);

    //更新用户头像
    @FormUrlEncoded
    @POST(Qurl.updateAppUser)
    Observable<BaseResponseBean<String>> updateAppUserPhono(@FieldMap Map<String, Object> allParam);


    //获取微信客服
    @POST(Qurl.serviceCode)
    Observable<BaseResponseBean<KeFuBean>> serviceCode();

    //获取淘宝链接
    @FormUrlEncoded
    @POST(Qurl.getTaoBaoUrl4_0)//以后统一改地址，不要改这个函数名
    Observable<BaseResponseBean<GetTaoBaoUrlBean>> getTaoBaoUrl(@FieldMap Map<String, String> allParam);
    //获取淘宝链接
    @FormUrlEncoded
    @POST(Qurl.getTaoBaoUrl4_0)//以后统一改地址，不要改这个函数名
    Observable<BaseResponseBean<GetTaoBaoUrlBean>> getTaoBaoUrl2_0(@FieldMap Map<String, String> allParam);
    @FormUrlEncoded
    @POST(Qurl.getTaoBaoUrl4_0)//以后统一改地址，不要改这个函数名
    Observable<BaseResponseBean<GetTaoBaoUrlBean>> getTaoBaoUrl3_0(@FieldMap Map<String, String> allParam);

    //获取商品详情
    @FormUrlEncoded
    @POST(Qurl.zhuanProductDetail1)
    Observable<BaseResponseBean<ProductInfoBean>> zhuanProductDetail(@FieldMap Map<String, Object> allParam);

    //订单接口
    @FormUrlEncoded
    @POST(Qurl.myOrder)
    Observable<BaseResponseBean<OrderBean>> MyOrderShowAll(@Field("status") int status, @Field("page") int page, @Field("time") String time, @Field("imgType") int imgType);

    //订单搜索接口
    @FormUrlEncoded
    @POST(Qurl.myOrder)
    Observable<BaseResponseBean<OrderBean>> MyOrderShowSearch(@Field("imgType") int imgType, @Field("orderNum") String orderNum);

    //我的收益
    @FormUrlEncoded
    @POST(Qurl.myEarnings)
    Observable<BaseResponseBean<MyEarningsBean>> MyEarnings(@FieldMap Map<String, String> allParam);

    //我的收益记录
    @FormUrlEncoded
    @POST(Qurl.earningsRecord)
    Observable<BaseResponseBean<EarningsRecordBean>> EarningsRecord(@FieldMap Map<String, String> allParam);
//    Observable<BaseResponseBean<EarningsRecordBean>> EarningsRecord();
//    Observable<BaseResponseBean<EarningsRecordBean>> EarningsRecord(@Field ( "page" ) int page);

    //获取当前用户信息
    @POST(Qurl.userInfo)
    Observable<BaseResponseBean<SetUserBean>> userInfo();

    //获取当前用户信息
    @POST(Qurl.getShareImages)
    Observable<BaseResponseBean<ShareAppBean>> getShareImages();

    //获取分享图片地址
    @POST(Qurl.getShareImagesCode)
    Observable<BaseResponseBean<ShareAppBean>> getShareImagesCode();

    //获取收藏
    @FormUrlEncoded
    @POST(Qurl.shoucang)
    Observable<BaseResponseBean<ShouCangBean>> shouCang(@FieldMap Map<String, String> allParam);

    //删除收藏
    @FormUrlEncoded
    @POST(Qurl.deleteShoucang)
    Observable<BaseResponseBean<ShouCangBean>> deleteShouCang(@FieldMap Map<String, String> allParam);

    //获取足迹
    @FormUrlEncoded
    @POST(Qurl.getZuji)
    Observable<BaseResponseBean<ZuJiBean>> getZuji(@FieldMap Map<String, String> allParam);

    //删除足迹
    @POST(Qurl.deleteZuji)
    Observable<BaseResponseBean<ZuJiBean>> deleteZuji();

    //获取提现信息
    @POST(Qurl.userWithdraw)
    Observable<BaseResponseBean<WithdrawDepositBean>> getUserWithdraw();

    //获取提现信息
    @FormUrlEncoded
    @POST(Qurl.sendWithdraw)
    Observable<BaseResponseBean<String>> getSendWithdraw(@FieldMap Map<String, String> allParam);
    //朋友圈左边接口
    @FormUrlEncoded
    @POST(Qurl.friendsLeft)
    Observable<BaseResponseBean<FriendsBean>> friendsLeft(@FieldMap Map<String, String> allParam);

    //朋友圈右边接口
    @FormUrlEncoded
    @POST(Qurl.friendsRight)
    Observable<BaseResponseBean<FriendsBean>> friendsRight(@FieldMap Map<String, String> allParam);

    //朋友圈分享图片 todo
    @Deprecated
    @FormUrlEncoded
    @POST(Qurl.friendsShareImages)
    Observable<BaseResponseBean<FriendsShareImagesBean>> friendsShareImages(@FieldMap Map<String, Object> allParam);

    @FormUrlEncoded
    @POST(Qurl.friendsShareImages2)
    Observable<BaseResponseBean<FriendsShareImagesBean>> friendsShareImages2(@FieldMap Map<String, Object> allParam);
    //获取超级信息
    @POST(Qurl.superEntrance)
    Observable<BaseResponseBean<SuperEntranceBean>> superEntrance();
    //意见反馈
    @FormUrlEncoded
    @POST(Qurl.feedback)
    Observable<BaseResponseBean<String>> feedback(@FieldMap Map<String, Object> allParam);
    //加入分享
    @FormUrlEncoded
    @POST(Qurl.addShare)
    Observable<BaseResponseBean<AddShareBean>> addShare(@FieldMap Map<String, Object> allParam);

    //长连接转短链接
    @FormUrlEncoded
    @POST()
    Observable<BaseResponseBean<LongChangeShortBean>> getShort(@Url String url,@FieldMap Map<String,String> allParam);
    //限时抢购头部时间导航栏
    @POST(Qurl.titleAndTime)
    Observable<BaseResponseBean<FlashSaleTitleAndTimeBean>> getTitleAndTime();
    //限时抢购列表
    @FormUrlEncoded
    @POST(Qurl.flashSaleDetails)
    Observable<BaseResponseBean<FlashSaleDetailsBean>> flashSaleDetailsBean(@FieldMap Map<String, Object> allParam);
    //限时抢购ID
    @FormUrlEncoded
    @POST(Qurl.flashSaleId)
    Observable<BaseResponseBean<FlashSaleIdBean>> getFlashSaleId(@FieldMap Map<String, Object> allParam);
    //我的觅卡列表
    @POST(Qurl.myCard)
    Observable<BaseResponseBean<MyCardBean>> myCard();
    //我的觅卡兑换
    @FormUrlEncoded
    @POST(Qurl.myCardExchange)
    Observable<BaseResponseBean<MyCardExchangeBean>> myCardExchange(@FieldMap Map<String, Object> allParam);
    //我的觅卡兑换记录
    @FormUrlEncoded
    @POST(Qurl.myCardExchangeRecord)
    Observable<BaseResponseBean<MyCardExchangeRecordBean>> myCardExchangeRecord(@FieldMap Map<String, Object> allParam);
    //我的觅卡使用记录
    @FormUrlEncoded
    @POST(Qurl.myCardUsed)
    Observable<BaseResponseBean<MyCardUsedRecordBean>> myCardUsedRecord(@FieldMap Map<String, Object> allParam);
    //我的觅卡使用记录
    @FormUrlEncoded
    @POST(Qurl.myCardDisabled)
    Observable<BaseResponseBean<MyCardFailureRecordBean>> myCardDisabledRecord(@FieldMap Map<String, Object> allParam);

    //我的觅卡使用记录
    @FormUrlEncoded
    @POST(Qurl.myCardMore)
    Observable<BaseResponseBean<MyCardMoreBean>> myCardMore(@FieldMap Map<String, Object> allParam);
    //今日打卡状态
    @POST(Qurl.myTodayPunchInSituation)
    Observable<BaseResponseBean<MySigninTodayBean>> myTodayPunchInSituation();
    //参与打卡状态
    @POST(Qurl.myParticipateState)
    Observable<BaseResponseBean<MyParticipateStateBean>> myParticipateState();
    //打卡
    @POST(Qurl.userPunchIn)
    Observable<BaseResponseBean<MyCardExchangeBean>> myPunchIn();
    //获取用户战绩汇总
    @POST(Qurl.userPunchInSituation)
    Observable<BaseResponseBean<MyUserPunchInSituationBean>> userPunchInSituation();
    //获取用户战绩
    @FormUrlEncoded
    @POST(Qurl.userPunchInHistory)
    Observable<BaseResponseBean<MySigninUserPunchInHistoryBean>> userPunchInHistory(@FieldMap Map<String, Object> allParam);

    //我的觅卡获取支付信息
    @POST(Qurl.message)
    Observable<BaseResponseBean<MyCardMessageBeab>> message();
    //我的觅卡开始支付
    @FormUrlEncoded
    @POST(Qurl.startPay)
    Observable<BaseResponseBean<MyCardStratPayBean>> startPay(@FieldMap Map<String, Object> allParam);
    //我的觅卡开始支付
    @FormUrlEncoded
    @POST(Qurl.notify)
    Observable<BaseResponseBean<MyCardNotifyBean>> notify(@FieldMap Map<String, Object> allParam);

    //试用入口
    @FormUrlEncoded
    @POST(Qurl.myTryAll)
    Observable<BaseResponseBean<MyTryBean>> myTryAll(@FieldMap Map<String, String> allParam);

    //我的试用黄卡个数
    @POST(Qurl.haveCardsNums)
    Observable<BaseResponseBean<ShowInMyCardBean>> haveCardsNums();

    //我的试用（全部，参与中，开奖中，已中奖）接口
    @FormUrlEncoded
    @POST(Qurl.showIngMy)
    Observable<BaseResponseBean<ShowInMyBean>> showIngMy(@FieldMap Map<String, Object> allParam);

    //参与成功
    @FormUrlEncoded
    @POST(Qurl.joinSuccess)
    Observable<BaseResponseBean<JoinSuccessBean>> joinSuccess(@FieldMap Map<String, Object> allParam);


    //免费试用活动规则
    @POST(Qurl.tryRule)
    Observable<BaseResponseBean<TryRuleBean>> getTryRule();
    //试用详情
    @FormUrlEncoded
    @POST(Qurl.tryDetails)
    Observable<BaseResponseBean<TryDetailsBean>> tryDetails(@FieldMap Map<String, Object> allParam);

    //试用分享
    @FormUrlEncoded
    @POST(Qurl.tryAllShareApp)
    Observable<BaseResponseBean<ZeroBuyShareAppBean>> tryAllShareApp(@FieldMap Map<String, Object> allParam);

    //签到赢大奖
    @POST(Qurl.signShowsInfo)
    Observable<BaseResponseBean<WeekSignInBean>> signShowsInfo();

    //去签到
    @POST(Qurl.signRun)
    Observable<BaseResponseBean<WeekSiginRunBean>> signRun();

    //去签到
    @POST(Qurl.getReward)
    Observable<BaseResponseBean<WeekSiginRunBean>> getReward();

    //每一期的参与签到
    @FormUrlEncoded
    @POST(Qurl.participate)
    Observable<BaseResponseBean<String>> participate(@FieldMap Map<String, Object> allParam);
    //用户邀请榜单列表
    @FormUrlEncoded
    @POST(Qurl.userRankingList)
    Observable<BaseResponseBean<MineBangDanBean>> userRankingList(@FieldMap Map<String, Object> allParam);

    //大转盘样式
    @POST(Qurl.dialShow)
    Observable<BaseResponseBean<BigWhellShowBean>> getDialShow();
    //大转盘抽奖
    @FormUrlEncoded
    @POST(Qurl.dialRun)
    Observable<BaseResponseBean<BigWhellRunBean>> getDialRun(@FieldMap Map<String, Object> allParam);
    //大转盘抽中奖品列表
    @FormUrlEncoded
    @POST(Qurl.bigWhellLuckInventory)
    Observable<BaseResponseBean<BigWhellLuckInventoryBean>> getBigWhellInventory(@FieldMap Map<String, Object> allParam);

    //邀请用户详情
    @FormUrlEncoded
    @POST(Qurl.inviteUserList)
    Observable<BaseResponseBean<InviteUserListBean>> inviteUserList(@FieldMap Map<String, Object> allParam);

    //拉新首页
    @POST(Qurl.getActiveInfo)
    Observable<BaseResponseBean<LaiXinMainBean>> getActiveInfo();

    //拉新主页的排名
    @FormUrlEncoded
    @POST(Qurl.rankingList)
    Observable<BaseResponseBean<LaXinMainWeekBean>> rankingList(@FieldMap Map<String, Object> allParam);

    //拉新接口领取奖品
    @POST(Qurl.acceptAward)
    Observable<BaseResponseBean<AcceptAwardBean>> acceptAward();

    //0元购入口
    @FormUrlEncoded
    @POST(Qurl.myZeroBuy)
    Observable<BaseResponseBean<MyZeroBuyBean>> getMyZeroBuy(@FieldMap Map<String, Object> allParam);
    //0元购活动规则
    @POST(Qurl.myZeroBuyRule)
    Observable<BaseResponseBean<TryRuleBean>> getZeroBuyRule();
    //0元抢购详情
    @FormUrlEncoded
    @POST(Qurl.myZeroBuyDetails)
    Observable<BaseResponseBean<ZeroBuyDetailsBean>> myZeroBuyDetailsDetails(@FieldMap Map<String, Object> allParam);
    //0元抢购绿卡个数
    @POST(Qurl.zeroBuyHaveCardsNums)
    Observable<BaseResponseBean<ShowInMyCardBean>> zeroBuyHaveCardsNums();

    //0元购（全部，参与中，开奖中，已中奖）接口
    @FormUrlEncoded
    @POST(Qurl.zeroBuyShowIngMy)
    Observable<BaseResponseBean<ShowInMyBean>> zeroBuyShowIngMy(@FieldMap Map<String, Object> allParam);
    //神秘宝箱获取活动信息
    @POST(Qurl.lottoyShow)
    Observable<BaseResponseBean<LottoyShowBean>> lottoyShow();
    //抽中奖品的列表
    @FormUrlEncoded
    @POST(Qurl.showWinInfo)
    Observable<BaseResponseBean<ShowWinInfoBean>> showWinInfo(@FieldMap Map<String, Object> allParam);

    //开启宝箱
    @FormUrlEncoded
    @POST(Qurl.lotRun)
    Observable<BaseResponseBean<LotRunBean>> lotRun(@FieldMap Map<String, Object> allParam);


    //0元抢购绿卡个数
    @FormUrlEncoded
    @POST(Qurl.zeroBuyMyRanking)
    Observable<BaseResponseBean<MyRankingBean>> zeroBuyHaveCardsNums(@FieldMap Map<String, Object> allParam);
    //参与成功
    @FormUrlEncoded
    @POST(Qurl.zeroBuyJoinSuccess)
    Observable<BaseResponseBean<ZeroBuyJoinSuccessBean>> zeroBuyJoinSuccess(@FieldMap Map<String, Object> allParam);
    //试用和0元购分享
    @FormUrlEncoded
    @POST(Qurl.zeroBuyShareApp)
    Observable<BaseResponseBean<ZeroBuyShareAppBean>> zeroBuyShareApp(@FieldMap Map<String, Object> allParam);

    //参与成功
    @FormUrlEncoded
    @POST()
    Observable<BaseResponseBean<String>> authorToken(@Url String url,@FieldMap Map<String, Object> allParam);

    //红包状态
    @POST(Qurl.getActiveStatus)
    Observable<BaseResponseBean<RedPackageStateBean>> getActiveStatus();
    //红包信息
    @POST(Qurl.redPackageInfo)
    Observable<BaseResponseBean<RedPackageBean>> redPackage();

    //参与成功
    @FormUrlEncoded
    @POST(Qurl.transferAccounts)
    Observable<BaseResponseBean<String>> transferAccounts(@FieldMap Map<String, Object> allParam);

    //淘宝福利入口
//    @FormUrlEncoded
//    @POST(Qurl.tryDetails)
//    Observable<BaseResponseBean<TmallSupermarketBean>> tmallSupermarket(@FieldMap Map<String, Object> allParam);
    //淘宝福利入口
    @FormUrlEncoded
    @POST(Qurl.tmallSupermarket)
    Observable<BaseResponseBean<TmallSupermarketBean>> getTmallSupermarket(@FieldMap Map<String, Object> allParam);
    //淘宝福利活动说明
    @POST(Qurl.tmallSuperMarketRule)
    Observable<BaseResponseBean<TryRuleBean>> tmallSuperMarketRule();
    //淘宝福利活动说明
    @FormUrlEncoded
    @POST(Qurl.tmallSuperMarketTmallDetail)
    Observable<BaseResponseBean<TryRuleBean>> tmallSuperMarketTmallDetail(@FieldMap Map<String, Object> allParam);
    //淘宝福利活动说明
    @FormUrlEncoded
    @POST(Qurl.tmallSuperMarketTmallList)
    Observable<BaseResponseBean<TmallSupermarketListBean>> tmallSuperMarketTmallList(@FieldMap Map<String, Object> allParam);
    //设置红包的发放金额
    @FormUrlEncoded
    @POST(Qurl.redPacketFit)
    Observable<BaseResponseBean<String>> redPacketFit(@FieldMap Map<String, Object> allParam);

    //设置红包的发放金额
    @FormUrlEncoded
    @POST(Qurl.redPacketInfo)
    Observable<BaseResponseBean<RedPackageInfoBean>> redPacketInfo(@FieldMap Map<String, Object> allParam);

    //设置红包开关
    @FormUrlEncoded
    @POST(Qurl.operateSwitch)
    Observable<BaseResponseBean<String>> operateSwitch(@FieldMap Map<String, Object> allParam);

    //支付可用余额
    @POST(Qurl.rechargeInfo)
    Observable<BaseResponseBean<RechargeInfoBean>> rechargeInfo();

    //我的觅卡开始支付
    @FormUrlEncoded
    @POST(Qurl.operateRecharge)
    Observable<BaseResponseBean<MyCardStratPayBean>> operateRecharge(@FieldMap Map<String, Object> allParam);

    //淘宝授权
    @POST(Qurl.shouquan)
    Observable<BaseResponseBean<ShouQuanBean>> shouquan();

    //新人红包接口回调
    @FormUrlEncoded
    @POST(Qurl.queryAlipayBill)
    Observable<BaseResponseBean<MyCardNotifyBean>> queryAlipayBill(@FieldMap Map<String, Object> allParam);
    //会员升级2.0
    @FormUrlEncoded
    @POST(Qurl.getAppraise2)
    Observable<BaseResponseBean<UpGradeBean>> getAppraise2(@FieldMap Map<String, Object> allParam);
    //398详情
    @POST(Qurl.getProDetail)
    Observable<BaseResponseBean<UpGradeDetailsBean>> getProDetail();
    //我的地址列表
    @FormUrlEncoded
    @POST(Qurl.adressList)
    Observable<BaseResponseBean<AddListBean>> adressList(@FieldMap Map<String, Object> allParam);


    //我的地址列表
    @FormUrlEncoded
    @POST(Qurl.editAddress)
    Observable<BaseResponseBean<String>> editAddress(@FieldMap Map<String, Object> allParam);
    //刪除地址
    @FormUrlEncoded
    @POST(Qurl.delAddress)
    Observable<BaseResponseBean<String>> delAddress(@FieldMap Map<String, Object> allParam);


    //购买订单
    @FormUrlEncoded
    @POST(Qurl.getMyOrder)
    Observable<BaseResponseBean<MyOrderBean>> getMyOrder(@FieldMap Map<String, Object> allParam);

    //订单支付(开启运营商)
    @FormUrlEncoded
    @POST(Qurl.purchase)
    Observable<BaseResponseBean<PurchaseBean>> purchase(@FieldMap Map<String, Object> allParam);
    //支付查询（(开启运营商)）
    @FormUrlEncoded
    @POST(Qurl.queryAlipay)
    Observable<BaseResponseBean<QueryAlipayBean>> queryAlipay(@FieldMap Map<String, Object> allParam);
    //购买订单
    @FormUrlEncoded
    @POST(Qurl.withdrawAllowance)
    Observable<BaseResponseBean<String>> withdrawAllowance(@FieldMap Map<String, Object> allParam);
    //管理津贴提现记录
    @FormUrlEncoded
    @POST(Qurl.userAllowanceList)
    Observable<BaseResponseBean<UserAllowanceListBean>> userAllowanceList(@FieldMap Map<String, Object> allParam);
    //订单找回
    @FormUrlEncoded
    @POST(Qurl.findOrder)
    Observable<BaseResponseBean<FindTaoOrderBean>> findOrder(@FieldMap Map<String, Object> allParam);
    //获取管理津贴信息
    @POST(Qurl.userAllowance)
    Observable<BaseResponseBean<IncomeOverviewBean>> userAllowance();
}
