package java.com.lechuang.module.bean;

import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/8/23
 * @describe:
 */

public class TmallSupermarketBean {
    public List<BannerListBean> bannerList;
    public List<ListBean> list;

    public List<BannerListBean> getBannerList() {
        return bannerList;
    }

    public void setBannerList(List<BannerListBean> bannerList) {
        this.bannerList = bannerList;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class BannerListBean {
        /**
         * id : mu22
         * img : http://img.taoyouji666.com/37d34554212143539fcdaa0fcb96a770
         * intId : 1194
         * linkAllows : 1
         * slipImg :
         * type : 8
         */

        public String id;
        public String img;
        public int intId;
        public int linkAllows;
        public String slipImg;
        public int type;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public int getIntId() {
            return intId;
        }

        public void setIntId(int intId) {
            this.intId = intId;
        }

        public int getLinkAllows() {
            return linkAllows;
        }

        public void setLinkAllows(int linkAllows) {
            this.linkAllows = linkAllows;
        }

        public String getSlipImg() {
            return slipImg;
        }

        public void setSlipImg(String slipImg) {
            this.slipImg = slipImg;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }

    public static class ListBean {
        /**
         * id : 1
         * img : qn|taoyouji2|c7c30fcd7c8342e88e75f076b0676a8e
         * name : 天猫超市券
         * secondList : [{"id":7,"img":"qn|taoyouji2|8f779dc9f1aa4d08b577e36e9838070f","name":"测试二级","status":1,"superId":1},{"id":8,"img":"qn|taoyouji2|aa47b8ac80754ccaa003754efd46f847","name":"测试二级2","status":1,"superId":1}]
         * status : 1
         * superId : 0
         */

        //天猫超市
        public String descriptionWords;
        public String showImg;
        public String title;

        //好劵专区
        public int ids;
        public String id;
        public String img;
        public String name;
        public int status;
        public int superId;
        public List<SecondListBean> secondList;

        //漏洞单
        public double commission;//佣金
        public double couponMoney;//劵金额
        public int nowNumber;//销量
        public String preferentialPrice;//劵后价
        public String productName;//商品名
        public String productText;//利益说明
        public String productUrl;//商品链接
        public String zhuanMoney;//分享赚金额
        public String tbCouponId;;//优惠券ID
        public String tbItemId;;//商品ID
        public double price;//原价




        public static class SecondListBean {
            /**
             * id : 7
             * img : qn|taoyouji2|8f779dc9f1aa4d08b577e36e9838070f
             * name : 测试二级
             * status : 1
             * superId : 1
             */

            public int id;
            public String img;
            public String name;
            public int status;
            public int superId;
        }
    }





}
