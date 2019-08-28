package java.com.lechuang.brand.bean;

import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/9/13
 * @describe:
 */

public class BrandInfoBean {
    public String brandImg;
    public String brandTitle;

    public List<IndexBannerListBean> indexBannerList;
    public List<ProductListBean> productList;

    public static class ProductListBean {
        /**
         * commission : 20.5
         * couponMoney : 5
         * disposeImg : http://img.taoyouji666.com/1534888973_4876897.jpg
         * id : r0lk7faw
         * img : https://img.alicdn.com/imgextra/i3/372438979/TB2dlVMbpkoBKNjSZFkXXb4tFXa_!!372438979.jpg
         * imgs : https://img.alicdn.com/imgextra/i3/372438979/TB2dlVMbpkoBKNjSZFkXXb4tFXa_!!372438979.jpg_400x400.jpg
         * name : 宁安堡宁夏枸杞子中宁苟杞子正宗特级500g克枸杞茶小袋男肾红枸杞
         * nowNumber : 795650
         * preferentialPrice : 24.9
         * price : 29.9
         * priceText : 【原价29.90元】券后【24.90元】包邮
         * productName : 独立小包50*10g！正宗宁夏中宁红枸杞
         * shopType : 1
         * tbCouponId : e83fa6fc54ea4eeaac932d4f53dc430a
         * tbItemId : 21194243684
         * zhuanMoney : 2.61
         */

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
        public String upZhuanMoney;
    }

    public static class IndexBannerListBean {
        /**
         * id : b8qp
         * img : http://img.taoyouji666.com/c77550cee81f880ca0bf3d7805e069cd
         * link : https://item.taobao.com/item.htm?id=560805497872
         * type : 4
         */

        public String id;
        public String img;
        public String link;
        public double type;
        public String pname;
        public String programaId;
    }
}
