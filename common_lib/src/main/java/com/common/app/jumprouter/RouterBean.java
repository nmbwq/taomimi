package com.common.app.jumprouter;

import com.common.app.base.FragmentActivity;

/**
 * @author: zhengjr
 * @since: 2018/9/4
 * @describe:
 */

public class RouterBean {


    //携带的非必要参数
    public String i;
    public String id;
    public String t;
    public String zbjId;
    public String channel;//搜索渠道
    public String searchtxt;//搜索内容
    public String pname;//栏目标题(只有在首页的栏目用的是他)
    public String programaId;//栏目类别
    public String rootName;//name
    public String pathUrl;//跳转的路径
    public String commandCopy;//跳转的路径
    public String customParam;//自定义参数(根据登录用户获取) 当type=3时使用
    public int typeBrand;//品牌详情参数
    public int linkAllows;
    public int obJump;



    //首页活动的跳转参数
    public String activeImage;
    public String activeName;
    public String activeUrl;



    //公共参数
    public String img;//图片地址
    public String link;//如果是链接，则是链接地址， 否则，为接口地址
    public String mustParam;//必要参数，当link是接口地址时使用
    public String attachParam;//附加参数（预留字段）
    public String tbCouponId;
    public android.support.v4.app.FragmentActivity thisActivity;


    public int type;
    public String tbItemId;
    // 1 淘宝网页(跳转淘宝APP)；
    // 2 淘宝网页(打开淘宝链接)；
    // 3 第三方链接；
    // 4 内部网页；

    // 5 商品栏目；

    // 6 商品分类；

    // 7 首页商品位置；

    // 8 商品集合页；

    // 9 商品详情；

    // 10 收藏；
    // 11 足迹；
    // 12 教程；
    // 13 帮助；
    // 14 客服；
    // 15 收益；
    // 16 提现；
    // 17 分享；
    // 18 我的团队；
    // 19:订单明细；

    // 20：京东；
    // 21：拼多多；
    // 22：飞猪







}
