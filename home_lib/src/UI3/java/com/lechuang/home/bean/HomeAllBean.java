package java.com.lechuang.home.bean;

import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/8/21
 * @describe:
 */

public class HomeAllBean {

    public String advertName;
    public String advertImg;
    public int programaClass;
    public long countDown;
    public int guideColumn;
    public List<ActivityImgListBean> activityImgList;
    public List<AdvertProductListBean> advertProductList;
    public List<BroadcastListBean> broadcastList;
    public List<GuideBannerListBean> guideBannerList;//
    public List<IndexBannerListBean> indexBannerList;//首页顶部轮播图
    public List<RushTimeListBean> rushTimeList;
    public List<ProductListBean> productList;
    public List<ProgramaImgListBean> programaImgList;//
    public List<HotSaleProductListBean> hotSaleProductList;
    public List<BoutiqueProductListBean> boutiqueProductList;
    public List<HomeAllBean.TodayProductList> todayProductList;
    public List<RushProductList> rushProductList;
    public List<ModuleColumnHeadList> moduleColumnHeadList;//新栏目
    public List<MiddleBannerList> middleBannerList;//新滑动
    public List<ModuleColumnBelowList> moduleColumnBelowList;//新格子
    public HotSaleProduct hotSaleProduct;//爆款必抢
    public ActiveCover activeCover;//0元夺宝

    public List<CustomProductListBean> customProductList;
    public PlacardBannerBean placardBanner;
    public PlacardBannerBean firstPlacard;

    public static class RushProductList {
        public String d_id;
        public String id;
        public String d_title;
        public double quan_jine;
        public String jiage;
        public String miaoshu;
        public String new_words;
        public String paiqi;
        public String pic;
        public String quan_num;
        public String quan_time;
        public String title;
        public String xiaoliang;
        public String yuanjia;
    }

    public static class ActivityImgListBean {
        /**
         * activeImage : qn|taoyouji2|ecbfff37e046c3f37ea226563e08d52b
         * activeUrl : https://detail.tmall.com/item.htm?https://detail.tmall.com/item.htm?id=551622026686
         * id : b8qp
         * img : http://img.taoyouji666.com/ecbfff37e046c3f37ea226563e08d52b
         * type : 1
         */

        public String activeImage;
        public String activeUrl;
        public String id;
        public String activeName ;

        public String rootId;
        public String rootName;
        public int status;
        public String img;
        public String link;
        public int type;
        public String mustParam;
        public String attachParam;
        public int linkAllows;
        public int obJump;
        public String commandCopy;//跳转的路径
    }

    public static class AdvertProductListBean {
        /**
         * commission : 20.5
         * couponMoney : 10
         * disposeImg : http://img.taoyouji666.com/1534275979_4363178.jpg
         * id : tka4foi688j
         * img : https://img.alicdn.com/bao/uploaded/i2/3162510293/TB1_DklAeOSBuNjy0FdXXbDnVXa_!!0-item_pic.jpg
         * imgs : https://img.alicdn.com/bao/uploaded/i2/3162510293/TB1_DklAeOSBuNjy0FdXXbDnVXa_!!0-item_pic.jpg_400x400.jpg
         * name : 防辐射眼镜正圆形复古近视眼镜框架圆眼镜男女款潮平光镜成品配镜
         * preferentialPrice : 48
         * price : 58
         * priceText : 【原价58.00元】券后【48.00元】包邮
         * productName : 复古圆框眼镜,版潮眼睛框镜架
         * shopType : 1
         * tbCouponId : c0f9bac1811449fcbee455f5cdbb665f
         * tbItemId : 549553268062
         * zhuanMoney : 5.04
         */

        public double commission;
        public double couponMoney;
        public String disposeImg;
        public String id;
        public String img;
        public String imgs;
        public String name;
        public String preferentialPrice;
        public double price;
        public String priceText;
        public String productName;
        public double shopType;
        public String tbCouponId;
        public String tbItemId;
        public String zhuanMoney;
    }

    public static class BroadcastListBean {
        /**
         * award : 0.18元
         * event : 13分钟前获得分享佣金
         * id : n3ga
         * nickName : 姐的拽◣你不配学
         */

        public String award;
        public String event;
        public String id;
        public String nickName;
    }
    public static class ModuleColumnHeadList {
        /**
         * award : 0.18元
         * event : 13分钟前获得分享佣金
         * id : n3ga
         * nickName : 姐的拽◣你不配学
         */

        public String pname;
        public String img;
        public String id;
        public String programaId;
        public int module;
        public int type;
        public String activeUrl;
        public String attachParam;
    }
    public static class MiddleBannerList {
        /**
         * award : 0.18元
         * event : 13分钟前获得分享佣金
         * id : n3ga
         * nickName : 姐的拽◣你不配学
         */

        public String name;
        public String rootName;
        public String img;
        public String id;
        public String link;
        public String mustParam;
        public String programaId;
        public int module;
        public int type;
        public String activeUrl;
        public String attachParam;
        public String commandCopy;
    }
    public static class ModuleColumnBelowList {
        /**
         * award : 0.18元
         * event : 13分钟前获得分享佣金
         * id : n3ga
         * nickName : 姐的拽◣你不配学
         */

        public String name;
        public String rootName;
        public String img;
        public String id;
        public String link;
        public String mustParam;
        public String programaId;
        public int module;
        public int type;
        public String activeUrl;
        public String attachParam;
        public String commandCopy;
    }

    public static class HotSaleProduct{
        public double commission;
        public double couponMoney;
        public String id;
        public String disposeImg;
        public String img;
        public String imgs;
        public String name;
        public int nowNumber;
        public String preferentialPrice;
        public double price;
        public String productName;
        public String priceText;
        public int shopType;
        public String tbCouponId;
        public String tbItemId;
        public String classTypeId;
        public String zhuanMoney;
        public String upZhuanMoney;
    }

    public static class ActiveCover{
        public String name;
        public String coverImg;
        public String coverName;
        public String coverPrice;
        public int coverActiveNumber;
    }

    public static class GuideBannerListBean {
        /**
         * id : b8qp
         * img : http://img.taoyouji666.com/79c8e8c70d2c4ff2eac191c36dbbc2e9
         * rootId : 1
         * rootName : 京东好货
         * status : 0
         */

        public String id;
        public String rootId;
        public String rootName;
        public int status;
        public String img;
        public String link;
        public int type;
        public String mustParam;
        public String attachParam;
        public String commandCopy;
        public String name;//标题名
        public int linkAllows;
        public int obJump;
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
        public String slipImg;
        public String link;
        public String name;//栏目备注
        public int type;
        public String mustParam;
        public String attachParam;
        public String customParam;
        public String rootName;
        public String commandCopy;
        public int linkAllows;
        public int obJump;
    }

    public static class RushTimeListBean{
        public int status;
        public String time;
        public String retime;
        public String statusStr;

    }

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
        public String videoUrl;
        public String isVideo;
    }

    public static class ProgramaImgListBean {
        /**
         * id : 1k6x1
         * img : http://img.taoyouji666.com/b853e527bf2f3f2544cefdda178f4b30
         * pname : 品质优选
         * programaId : 2
         */

        public String id;
        public String img;
        public String pname;
        public String programaId;
        public String attachParam;//活动类型 brand 品牌闪购； activeType1 免费试用; activeType2 签到活动等等
        public String activeUrl;//活动链接地址（type=2时使用）需要拼接
        public String type;//活动跳转方式 1原生页面; 2 H5网页；
        public int module;//模块类型 1 商品栏目; 2 营销活动;
    }

    public static class HotSaleProductListBean{
        public double commission;
        public double couponMoney;
        public String id;
        public String disposeImg;
        public String img;
        public String imgs;
        public String name;
        public int nowNumber;
        public String preferentialPrice;
        public double price;
        public String productName;
        public String priceText;
        public int shopType;
        public String tbCouponId;
        public String tbItemId;
        public String classTypeId;
        public String zhuanMoney;
        public String upZhuanMoney;
    }

    public static class BoutiqueProductListBean{
        public double commission;
        public double couponMoney;
        public String id;
        public String img;
        public String name;
        public int nowNumber;
        public String preferentialPrice;
        public double price;
        public String productName;
        public int shopType;
        public String tbCouponId;
        public String tbItemId;
    }

    public static class CustomProductListBean{
        public double commission;
        public double couponMoney;
        public String id;
        public String img;
        public String imgs  ;
        public String name;
        public int nowNumber;
        public String preferentialPrice;
        public double price;
        public String productName;
        public int shopType;
        public String tbCouponId;
        public String tbItemId;
        public String zhuanMoney;
    }

    public static class PlacardBannerBean{

        public String id;
        public String img;
        public String link;
        public String mustParam;
        public String name;
        public String rootName;
        public String programaId;
        public String attachParam;
        public int type;
        public int linkAllows;
        public int obJump;
        public String commandCopy;//跳转的路径

    }

    public static class TodayProductList{
        public double commission;
        public double couponMoney;
        public String id;
        public String img;
        public String name;
        public int nowNumber;
        public String preferentialPrice;
        public double price;
        public String productName;
        public int shopType;
        public String tbCouponId;
        public String tbItemId;
    }
}
