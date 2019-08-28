package java.com.lechuang.module.bean;

import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/9/8
 * @describe:
 */

public class ZuJiBean {
    public String infoData;

    public List<ShouCangBeans> productList;

    public static class ShouCangBeans{

        public boolean isSelect = false;//用户判断用户是否选中，默认就是未选中

        public double commission;
        public double couponMoney;
        public String disposeImg;
        public String id;
        public String img;
        public String imgs;
        public String name;
        public int nowNumber;
        public String preferentialPrice;
        public double price;
        public String priceText;
        public String productName;
        public int shopType;
        public String tbCouponId;
        public String tbItemId;
        public String zhuanMoney;
        public String productId;
        public int isStatus;
        public String videoUrl;
        public String isVideo;

    }
}
