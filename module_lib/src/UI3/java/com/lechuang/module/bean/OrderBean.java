package java.com.lechuang.module.bean;

import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/8/21
 * @describe:
 */

public class OrderBean {



        /**
         * counts : 3
         * list : [{"awardDetails":"5734.4000","commissionPrice":"0","counts":3,"createTime":"2018-08-21 11:12:14","createTimeStr":"2018-08-21 11:12:14","goodsMsg":"Supecare/舒宁电动儿童牙刷声波青蛙自动卡通软毛宝宝婴幼儿牙刷","id":"70zt5xic","itemId":"573919668357","orderNum":"204491456498900779","orderStatus":"订单付款","orderType":"天猫","payPrice":"14.9","phone":"15862100000","relation":"0"},{"awardDetails":"7680.0000","commissionPrice":"0","createTime":"2018-08-21 10:26:32","createTimeStr":"2018-08-21 10:26:32","goodsMsg":"夏季透气莫代尔男士内裤男平角裤冰丝纯棉质大码四角裤个性青年潮","id":"fv257s","itemId":"572338599776","orderNum":"204493447881834159","orderStatus":"订单付款","orderType":"淘宝","payPrice":"19.9","phone":"15862100000","relation":"0"},{"awardDetails":"3911.6800","commissionPrice":"0","createTime":"2018-08-21 10:18:47","createTimeStr":"2018-08-21 10:18:47","goodsMsg":"口算题卡一年级上册下册口算心算速算天天练人教版 一年级数学口算题卡计算能手20-50-100以内加减法数学思维训练题","id":"1rkunwfd","itemId":"550365731459","orderNum":"204362098633834159","orderStatus":"订单付款","orderType":"天猫","payPrice":"5.1","phone":"15862100000","relation":"0"}]
         */

        private int counts;
        private List<ListBean> list;

        public int getCounts() {
            return counts;
        }

        public void setCounts(int counts) {
            this.counts = counts;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            /**
             * awardDetails : 5734.4000
             * commissionPrice : 0
             * counts : 3
             * createTime : 2018-08-21 11:12:14
             * createTimeStr : 2018-08-21 11:12:14
             * goodsMsg : Supecare/舒宁电动儿童牙刷声波青蛙自动卡通软毛宝宝婴幼儿牙刷
             * id : 70zt5xic
             * itemId : 573919668357
             * orderNum : 204491456498900779
             * orderStatus : 订单付款
             * orderType : 天猫
             * payPrice : 14.9
             * phone : 15862100000
             * relation : 0
             */

            public String awardDetails;
            public String commissionPrice;
            public int counts;
            public String createTime;
            public String createTimeStr;
            public String goodsMsg;
            public String id;
            public String itemId;
            public String orderNum;
            public String orderStatus;
            public String orderType;
            public String payPrice;
            public String phone;
            public String relation;
            public String img;

            public String getJsTime() {
                return jsTime;
            }

            public void setJsTime(String jsTime) {
                this.jsTime = jsTime;
            }

            public String jsTime;

            public String getImg() {
                return img;
            }

            public void setImg(String img) {
                this.img = img;
            }



            public String getAwardDetails() {
                return awardDetails;
            }

            public void setAwardDetails(String awardDetails) {
                this.awardDetails = awardDetails;
            }

            public String getCommissionPrice() {
                return commissionPrice;
            }

            public void setCommissionPrice(String commissionPrice) {
                this.commissionPrice = commissionPrice;
            }

            public int getCounts() {
                return counts;
            }

            public void setCounts(int counts) {
                this.counts = counts;
            }

            public String getCreateTime() {
                return createTime;
            }

            public void setCreateTime(String createTime) {
                this.createTime = createTime;
            }

            public String getCreateTimeStr() {
                return createTimeStr;
            }

            public void setCreateTimeStr(String createTimeStr) {
                this.createTimeStr = createTimeStr;
            }

            public String getGoodsMsg() {
                return goodsMsg;
            }

            public void setGoodsMsg(String goodsMsg) {
                this.goodsMsg = goodsMsg;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getItemId() {
                return itemId;
            }

            public void setItemId(String itemId) {
                this.itemId = itemId;
            }

            public String getOrderNum() {
                return orderNum;
            }

            public void setOrderNum(String orderNum) {
                this.orderNum = orderNum;
            }

            public String getOrderStatus() {
                return orderStatus;
            }

            public void setOrderStatus(String orderStatus) {
                this.orderStatus = orderStatus;
            }

            public String getOrderType() {
                return orderType;
            }

            public void setOrderType(String orderType) {
                this.orderType = orderType;
            }

            public String getPayPrice() {
                return payPrice;
            }

            public void setPayPrice(String payPrice) {
                this.payPrice = payPrice;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public String getRelation() {
                return relation;
            }

            public void setRelation(String relation) {
                this.relation = relation;
            }
        }

}
