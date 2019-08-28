package java.com.lechuang.module.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author: zhengjr
 * @since: 2019/1/21
 * @describe:
 */

public class AddListBean implements Serializable{



        private List<AddressListBean> addressList;

        public List<AddressListBean> getAddressList() {
            return addressList;
        }

        public void setAddressList(List<AddressListBean> addressList) {
            this.addressList = addressList;
        }

        public static class AddressListBean implements Serializable{
            /**
             * areaAddress : 浙江省杭州市江干区
             * city : 杭州市
             * county : 江干区
             * createTime : 2019-03-22 10:43:16
             * detailAddress : 公元时代1001
             * id : b8qp
             * isDefault : 1
             * province : 浙江省
             * receiverName : 淘觅觅
             * receiverPhone : 18888888888
             */

            private String areaAddress;
            private String city;
            private String county;
            private String createTime;
            private String detailAddress;
            private String id;
            private String intId;
            private int isDefault;
            private String province;
            private String receiverName;
            private String receiverPhone;

            public String getIntId() {
                return intId;
            }

            public void setIntId(String intId) {
                this.intId = intId;
            }

            public String getAreaAddress() {
                return areaAddress;
            }

            public void setAreaAddress(String areaAddress) {
                this.areaAddress = areaAddress;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getCounty() {
                return county;
            }

            public void setCounty(String county) {
                this.county = county;
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

            public int getIsDefault() {
                return isDefault;
            }

            public void setIsDefault(int isDefault) {
                this.isDefault = isDefault;
            }

            public String getProvince() {
                return province;
            }

            public void setProvince(String province) {
                this.province = province;
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
