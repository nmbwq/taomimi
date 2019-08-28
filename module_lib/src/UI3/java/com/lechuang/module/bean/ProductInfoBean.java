package java.com.lechuang.module.bean;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.service.SerializationService;
import com.alibaba.fastjson.JSON;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/8/31
 * @describe:
 */

public class ProductInfoBean{


    /**
     * productWithBLOBs : {"appraiseCount":10,"classTypeId":7,"commission":31,"commissionType":5,"couponCount":10000,"couponEndTime":"1536336000000","couponEndTimeStr":"2018-09-08 00:00:00","couponMoney":10,"couponStartTime":"1534435200000","couponStartTimeStr":"2018-08-17 00:00:00","couponTimeStr":"2018-09-08 00:00:00到期","createTime":1534644305000,"disposeImg":"http://img.taoyouji666.com/1534644227_4839712.jpg","enshrineCount":8,"errorMessage":"site_id或adzone_id与登录账号不匹配","id":"1n4jib6op44u","img":"http://img.alicdn.com/imgextra/i2/1603013295/TB1HAlMkeuSBuNjSsziXXbq8pXa_!!0-item_pic.jpg","imgs":"http://img.alicdn.com/imgextra/i2/1603013295/TB1HAlMkeuSBuNjSsziXXbq8pXa_!!0-item_pic.jpg_400x400.jpg","individualScore":5,"isCollection":0,"isDataoke":0,"isJhs":0,"isStatus":0,"isTaoyouji":0,"isTqq":0,"isYishoudan":1,"maxOnlineCount":0,"name":"女夏两穿简约平跟平底一字带凉鞋","nowCount":802,"nowNumber":17,"nowOnlineCount":-1,"onedayNum":3375,"preferentialPrice":"19.9","price":29.9,"priceText":"【原价29.90元】券后【19.90元】包邮","prodSha":0,"productName":"女夏两穿简约平跟平底一字带凉鞋","productText":"两穿百搭简约软底凉鞋，露趾平底，一字带设计，百搭时尚，散步约会，各种场合适用，女神夏季首选【超值实惠】","productUrl":"https://detail.tmall.com/item.htm?id=567171276109","shareIntegral":"合伙人分享该商品成交后每单可获1.91元,非合伙人无奖励","shareText":"女夏两穿简约平跟平底一字带凉鞋\n[在售价] 29.9\n[券后价] 19.9\n[下单链接] {选择分享渠道后生成淘口令}\n打开[手机淘宝]即可查看","shopType":2,"smallImages":["http://img.alicdn.com/imgextra/i2/1603013295/TB1HAlMkeuSBuNjSsziXXbq8pXa_!!0-item_pic.jpg","http://img.alicdn.com/imgextra/i2/1603013295/TB2FZ3Qn5CYBuNkSnaVXXcMsVXa_!!1603013295.jpg","http://img.alicdn.com/imgextra/i2/1603013295/TB2lTdZkhGYBuNjy0FnXXX5lpXa_!!1603013295.jpg","http://img.alicdn.com/imgextra/i2/1603013295/TB2YKpSkb1YBuNjSszhXXcUsFXa_!!1603013295.jpg","http://img.alicdn.com/imgextra/i1/1603013295/TB14_Brkb5YBuNjSspoXXbeNFXa_!!0-item_pic.jpg"],"startCount":206,"startNumber":17,"tbCouponId":"0ba5d18f59704327abeeae187e52a607","tbCreateTime":1535713854977,"tbItemId":"567171276109","tbPrivilegeUrl":"https://uland.taobao.com/coupon/edetail?activityId=0ba5d18f59704327abeeae187e52a607&pid=mm_25394709_39288194_146350778&itemId=567171276109&src=lc_tczs","tbSellerId":"1603013295","tbShopId":"102441013","twohourNum":9677}
     * shop : {}
     */

    public ProductWithBLOBsBean productWithBLOBs;
    public ShopBean shop;

    public static class ProductWithBLOBsBean {
        /**
         * appraiseCount : 10
         * classTypeId : 7
         * commission : 31.0
         * commissionType : 5
         * couponCount : 10000
         * couponEndTime : 1536336000000
         * couponEndTimeStr : 2018-09-08 00:00:00
         * couponMoney : 10
         * couponStartTime : 1534435200000
         * couponStartTimeStr : 2018-08-17 00:00:00
         * couponTimeStr : 2018-09-08 00:00:00到期
         * createTime : 1534644305000
         * disposeImg : http://img.taoyouji666.com/1534644227_4839712.jpg
         * enshrineCount : 8
         * errorMessage : site_id或adzone_id与登录账号不匹配
         * id : 1n4jib6op44u
         * img : http://img.alicdn.com/imgextra/i2/1603013295/TB1HAlMkeuSBuNjSsziXXbq8pXa_!!0-item_pic.jpg
         * imgs : http://img.alicdn.com/imgextra/i2/1603013295/TB1HAlMkeuSBuNjSsziXXbq8pXa_!!0-item_pic.jpg_400x400.jpg
         * individualScore : 5.0
         * isCollection : 0
         * isDataoke : 0
         * isJhs : 0
         * isStatus : 0
         * isTaoyouji : 0
         * isTqq : 0
         * isYishoudan : 1
         * maxOnlineCount : 0
         * name : 女夏两穿简约平跟平底一字带凉鞋
         * nowCount : 802
         * nowNumber : 17
         * nowOnlineCount : -1
         * onedayNum : 3375
         * preferentialPrice : 19.9
         * price : 29.9
         * priceText : 【原价29.90元】券后【19.90元】包邮
         * prodSha : 0
         * productName : 女夏两穿简约平跟平底一字带凉鞋
         * productText : 两穿百搭简约软底凉鞋，露趾平底，一字带设计，百搭时尚，散步约会，各种场合适用，女神夏季首选【超值实惠】
         * productUrl : https://detail.tmall.com/item.htm?id=567171276109
         * shareIntegral : 合伙人分享该商品成交后每单可获1.91元,非合伙人无奖励
         * shareText : 女夏两穿简约平跟平底一字带凉鞋
         [在售价] 29.9
         [券后价] 19.9
         [下单链接] {选择分享渠道后生成淘口令}
         打开[手机淘宝]即可查看
         * shopType : 2
         * smallImages : ["http://img.alicdn.com/imgextra/i2/1603013295/TB1HAlMkeuSBuNjSsziXXbq8pXa_!!0-item_pic.jpg","http://img.alicdn.com/imgextra/i2/1603013295/TB2FZ3Qn5CYBuNkSnaVXXcMsVXa_!!1603013295.jpg","http://img.alicdn.com/imgextra/i2/1603013295/TB2lTdZkhGYBuNjy0FnXXX5lpXa_!!1603013295.jpg","http://img.alicdn.com/imgextra/i2/1603013295/TB2YKpSkb1YBuNjSszhXXcUsFXa_!!1603013295.jpg","http://img.alicdn.com/imgextra/i1/1603013295/TB14_Brkb5YBuNjSspoXXbeNFXa_!!0-item_pic.jpg"]
         * startCount : 206
         * startNumber : 17
         * tbCouponId : 0ba5d18f59704327abeeae187e52a607
         * tbCreateTime : 1535713854977
         * tbItemId : 567171276109
         * tbPrivilegeUrl : https://uland.taobao.com/coupon/edetail?activityId=0ba5d18f59704327abeeae187e52a607&pid=mm_25394709_39288194_146350778&itemId=567171276109&src=lc_tczs
         * tbSellerId : 1603013295
         * tbShopId : 102441013
         * twohourNum : 9677
         */

        public int appraiseCount;
        public int classTypeId;
        public double commission;
        public int commissionType;
        public int couponCount;
        public String couponEndTime;
        public String couponEndTimeStr;
        public int couponMoney;
        public String couponStartTime;
        public String couponStartTimeStr;
        public String couponTimeStr;
        public long createTime;
        public String disposeImg;
        public int enshrineCount;
        public String errorMessage;
        public String id;
        public String img;
        public String imgs;
        public double individualScore;
        public int isCollection;
        public int isDataoke;
        public int isJhs;
        public int isStatus;
        public int isTaoyouji;
        public int isTqq;
        public int isYishoudan;
        public int maxOnlineCount;
        public String name;
        public int nowCount;
        public int nowNumber;
        public int nowOnlineCount;
        public int onedayNum;
        public String preferentialPrice;
        public double price;
        public String priceText;
        public int prodSha;
        public String productName;
        public String productText;
        public String productUrl;
        public String shareIntegral;
        public String shareText;
        public int shopType;
        public int startCount;
        public int startNumber;
        public String tbCouponId;
        public long tbCreateTime;
        public String tbItemId;
        public String tbPrivilegeUrl;
        public String tbSellerId;
        public String tbShopId;
        public int twohourNum;
        public String zhuanMoney;
        public List<String> smallImages;
    }

    public static class ShopBean {
    }
}
