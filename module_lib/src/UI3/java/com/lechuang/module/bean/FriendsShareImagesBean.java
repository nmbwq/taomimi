package java.com.lechuang.module.bean;

import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/9/13
 * @describe:
 */

public class FriendsShareImagesBean {


    /*1.0接口 start */
    public ShareImagesBean1 circleFriend;

    public static class ShareImagesBean1{

        public String cfComments;
        public long cfCreateTime;
        public String cfMasterName;
        public String cfMasterImg;
        public String cfShareCopy;
        public String cfType;
        public List<String> detailImages;
    }
    /*1.0接口 end */


    /*2.0接口 start */
    public List<ShareImagesBean2> circleList;
    public String cfType;
    public int entireCount;
    public int outCount;


    public static class ShareImagesBean2{

        public String cfShareCopy;//分享文案
        public List<String> detailImages;

        public String cfProductTitle;//商品标题
        public double cfCouponAfterPrice;//商品券后价，店铺价
        public double cfPrice;//商品原价
        public double couponPrice;//券金额
        public int shopType;//券金额
        public String qrCodeUrl;//二维码保存的地址，传递bitmap比较消耗内存，改为传本地地址比较方便
    }
    /*2.0接口 end */

}
