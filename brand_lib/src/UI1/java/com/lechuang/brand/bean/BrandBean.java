package java.com.lechuang.brand.bean;

import java.util.List;

import retrofit2.http.PUT;

/**
 * @author: zhengjr
 * @since: 2018/8/24
 * @describe:
 */

public class BrandBean {

    public String advertName;

    public List<BrandBannerListBean> brandBannerList;
    public List<ProductBannerListBean> productBannerList;
    public List<AdvertProductListBean> advertProductList;
    public List<BrandSellerListBean> brandSellerList;
    public TodayHotSaleBean todayHotSale;

    public static class BrandBannerListBean {
        /**
         * attachParam :
         * id : b8qx
         * img : http://img.taoyouji666.com/10eb0b0f3cbb5748237e3a5a44b0e619
         * link :
         * mustParam :
         * name :
         * pname :
         * programType : 0
         * programaId : 0
         * rootName :
         * type : 0
         */

        public String attachParam;
        public String id;
        public String img;
        public String link;
        public String mustParam;
        public String name;
        public String pname;
        public int programType;
        public int programaId;
        public String rootName;
        public int type;
        public int brandId;
        public String commandCopy;
        public int linkAllows;
        public int obJump;
    }

    public static class ProductBannerListBean{

        public String attachParam;
        public int brandId;
        public String id;
        public String img;
        public String link;
        public String mustParam;
        public String name;
        public String pname;
        public int programType;
        public int programaId;
        public String rootName;
        public int type;
        public String commandCopy;
        public int linkAllows;
        public int obJump;
    }

    public static class AdvertProductListBean{
        public int count;
        public int isStatus;
        public int nowNumber;
        public int orderNum;
        public String priceText;
        public String productUrl;
        public int startNumber;
        public String tbSellerId;
        public String upZhuanMoney;
        public String zhuanMoney;


        public String disposeImg;
        public double commission;
        public double couponMoney;
        public int classTypeId;
        public String id;
        public String img;
        public String imgs;
        public String name;
        public String preferentialPrice;
        public double price;
        public String productName;
        public int shopType;
        public String tbCouponId;
        public String tbItemId;
    }

    public static class BrandSellerListBean {
        /**
         * id : 1vfgm
         * slogan : 女装潮流
         * title : dmestyle旗舰店
         * type : 2
         * brandProduct : [{"commission":30.1,"couponMoney":25,"id":"59fcskyr","img":"https://gd2.alicdn.com/imgextra/i2/171274984/TB2Y3zDdUOWBKNjSZKzXXXfWFXa_!!171274984.jpg","imgs":"https://gd2.alicdn.com/imgextra/i2/171274984/TB2Y3zDdUOWBKNjSZKzXXXfWFXa_!!171274984.jpg_400x400.jpg","name":"小红书泰国官方正品妆蕾RAY蚕丝面膜补水保湿修复金银色泰版新版","preferentialPrice":"44","price":69,"productName":"小红书泰国官方正品妆蕾RAY蚕丝面膜","shopType":1,"tbCouponId":"149b881e4a2742cd8bef58014b5b1f81","tbItemId":"567547083549"},{"commission":25,"couponMoney":300,"id":"aiuaonwu","img":"https://img.alicdn.com/imgextra/i3/826963318/O1CN011aNgbAL8O7F2Pnc_!!826963318.jpg","imgs":"https://img.alicdn.com/imgextra/i3/826963318/O1CN011aNgbAL8O7F2Pnc_!!826963318.jpg_400x400.jpg","name":"医美面膜红蓝光美容仪术后修复彩光面罩家用医用光子嫩肤仪面膜机","preferentialPrice":"99","price":399,"productName":"医美面膜红蓝光美容仪","shopType":2,"tbCouponId":"cd82ef436e3a4dbd96952b8e63e56273","tbItemId":"575662281462"},{"commission":30,"couponMoney":20,"id":"cqw5c","img":"https://img.alicdn.com/imgextra/i3/3012913363/TB2qPrIX7fb_uJkHFrdXXX2IVXa_!!3012913363.jpg","imgs":"https://img.alicdn.com/imgextra/i3/3012913363/TB2qPrIX7fb_uJkHFrdXXX2IVXa_!!3012913363.jpg_400x400.jpg","name":"竹炭去黑头面膜撕拉式鼻贴膜收缩毛孔套装男女通用祛吸黑头神器","preferentialPrice":"19.9","price":39.9,"productName":"【买一送一】竹炭撕拉式去黑头面膜","shopType":2,"tbCouponId":"baa5b1cd8caa45c6be3c8284043a1500","tbItemId":"552754286100"}]
         */

        public String id;
        public String slogan;
        public String title;
        public String outImg;
        public int type;
        public List<BrandProductBean> brandProduct;

        public static class BrandProductBean {
            /**
             * commission : 30.1
             * couponMoney : 25
             * id : 59fcskyr
             * img : https://gd2.alicdn.com/imgextra/i2/171274984/TB2Y3zDdUOWBKNjSZKzXXXfWFXa_!!171274984.jpg
             * imgs : https://gd2.alicdn.com/imgextra/i2/171274984/TB2Y3zDdUOWBKNjSZKzXXXfWFXa_!!171274984.jpg_400x400.jpg
             * name : 小红书泰国官方正品妆蕾RAY蚕丝面膜补水保湿修复金银色泰版新版
             * preferentialPrice : 44
             * price : 69
             * productName : 小红书泰国官方正品妆蕾RAY蚕丝面膜
             * shopType : 1
             * tbCouponId : 149b881e4a2742cd8bef58014b5b1f81
             * tbItemId : 567547083549
             */

            public double commission;
            public int couponMoney;
            public String id;
            public String img;
            public String imgs;
            public String name;
            public String preferentialPrice;
            public double price;
            public String productName;
            public int shopType;
            public String tbCouponId;
            public String tbItemId;
        }
    }

    public static class TodayHotSaleBean{
        public String attachParam;
        public int brandId;
        public String id;
        public String img;//图片地址
        public String link;//链接地址
        public String mustParam;//必要参数，
        public String name;
        public String pname;
        public int programType;
        public int programaId;
        public String rootName;
        public int type;
        public String brandName;//店铺名称
        public String slogan;//宣传标语
        public String commandCopy;
        public int linkAllows;
        public int obJump;
    }
}
