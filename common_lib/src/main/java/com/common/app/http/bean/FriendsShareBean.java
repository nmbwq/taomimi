package com.common.app.http.bean;

/**
 * @author: zhengjr
 * @since: 2018/12/11
 * @describe:
 */

public class FriendsShareBean {

    public String cfProductTitle;//商品标题
    public int shopType;//商品标题
    public String cfCouponAfterPrice;//商品券后价，店铺价
    public String cfPrice;//商品原价
    public String couponPrice;//券金额
    public String preShareImgLoadUrl;//商品图本地保存的地址，传递bitmap比较消耗内存，改为传本地地址比较方便
    public String preShareCodeLoadUrl;//二维码保存的地址，传递bitmap比较消耗内存，改为传本地地址比较方便


}
