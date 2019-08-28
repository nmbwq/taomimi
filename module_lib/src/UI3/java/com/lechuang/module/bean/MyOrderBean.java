package java.com.lechuang.module.bean;

/**
 * @author: zhengjr
 * @since: 2018/6/28
 * @describe:
 */

public class MyOrderBean {

        /**
         * product : {"bannerId":1259,"detailImg":"qn|taoyouji2|64206d9b31134929a71e626ebcc12184","id":1,"name":"套餐一","num":1,"price":99,"showImg":"http://img.taoyouji666.com/64206d9b31134929a71e626ebcc12184"}
         * totalMoney : 99
         * userAddress : {"appUserId":1,"areaAddress":"浙江省杭州市江干区","createTime":1553222596000,"detailAddress":"公元时代1001","id":"b8qp","intId":1,"isDefault":"1","receiverName":"淘觅觅","receiverPhone":"18888888888"}
         */

        public ProductBean product;
    public double totalMoney;
    public UserAddressBean userAddress;

        public ProductBean getProduct() {
            return product;
        }

        public void setProduct(ProductBean product) {
            this.product = product;
        }




        public UserAddressBean getUserAddress() {
            return userAddress;
        }

        public void setUserAddress(UserAddressBean userAddress) {
            this.userAddress = userAddress;
        }

        public static class ProductBean {
            /**
             * bannerId : 1259
             * detailImg : qn|taoyouji2|64206d9b31134929a71e626ebcc12184
             * id : 1
             * name : 套餐一
             * num : 1
             * price : 99
             * showImg : http://img.taoyouji666.com/64206d9b31134929a71e626ebcc12184
             */

            public int bannerId;
            public String detailImg;
            public int id;
            public String name;
            public int num;
            public double price;
            public String showImg="";
            public String proWords;

            public int getBannerId() {
                return bannerId;
            }

            public void setBannerId(int bannerId) {
                this.bannerId = bannerId;
            }

            public String getDetailImg() {
                return detailImg;
            }

            public void setDetailImg(String detailImg) {
                this.detailImg = detailImg;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getNum() {
                return num;
            }

            public void setNum(int num) {
                this.num = num;
            }

            public double getPrice() {
                return price;
            }

            public void setPrice(double price) {
                this.price = price;
            }

            public String getShowImg() {
                return showImg;
            }

            public void setShowImg(String showImg) {
                this.showImg = showImg;
            }
        }

        public static class UserAddressBean {
            /**
             * appUserId : 1
             * areaAddress : 浙江省杭州市江干区
             * createTime : 1553222596000
             * detailAddress : 公元时代1001
             * id : b8qp
             * intId : 1
             * isDefault : 1
             * receiverName : 淘觅觅
             * receiverPhone : 18888888888
             */

            public int appUserId;
            public String areaAddress;
            public String createTime;
            public String detailAddress;
            public String id;
            public int intId;
            public String isDefault;
            public String receiverName;
            public String receiverPhone;

            public int getAppUserId() {
                return appUserId;
            }

            public void setAppUserId(int appUserId) {
                this.appUserId = appUserId;
            }

            public String getAreaAddress() {
                return areaAddress;
            }

            public void setAreaAddress(String areaAddress) {
                this.areaAddress = areaAddress;
            }

            public String getCreateTime() {
                return createTime;
            }

            public void setCreateTime(String createTime) {
                this.createTime = createTime;
            }

            public String getDetailAddress() {
                return detailAddress;
            }

            public void setDetailAddress(String detailAddress) {
                this.detailAddress = detailAddress;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public int getIntId() {
                return intId;
            }

            public void setIntId(int intId) {
                this.intId = intId;
            }

            public String getIsDefault() {
                return isDefault;
            }

            public void setIsDefault(String isDefault) {
                this.isDefault = isDefault;
            }

            public String getReceiverName() {
                return receiverName;
            }

            public void setReceiverName(String receiverName) {
                this.receiverName = receiverName;
            }

            public String getReceiverPhone() {
                return receiverPhone;
            }

            public void setReceiverPhone(String receiverPhone) {
                this.receiverPhone = receiverPhone;
            }
        }
}
