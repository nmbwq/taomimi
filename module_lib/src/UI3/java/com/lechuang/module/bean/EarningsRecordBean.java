package java.com.lechuang.module.bean;

import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/8/23
 * @describe:
 */

public class EarningsRecordBean {

    /**
     * data : {"list":[{"alipayNumber":"vhfg","cashMoney":"1000","cashPoints":1000,"counts":7,"createTime":1536054283000,"id":"g0w","integralTime":1533052800000,"nickName":"顺乛◡乛","phone":"13111111117","rmUserId":84,"status":0,"type":"6","userAwardId":7865712,"userId":1002},{"alipayNumber":"vhfg","cashMoney":"1000","cashPoints":1000,"createTime":1536054269000,"id":"273tr","integralTime":1533052800000,"nickName":"顺乛◡乛","phone":"13111111117","rmUserId":84,"status":0,"type":"6","userAwardId":7865711,"userId":1002},{"alipayNumber":"vhfg","cashMoney":"2183","cashPoints":2183,"createTime":1535969619000,"id":"74","integralTime":1533052800000,"nickName":"","phone":"13111111117","rmUserId":84,"status":0,"type":"6","userAwardId":7864635,"userId":1002},{"alipayNumber":"vhfg","cashMoney":"2183","cashPoints":2183,"createTime":1535969619000,"id":"y5nf","integralTime":1533052800000,"nickName":"","phone":"13111111117","rmUserId":84,"status":0,"type":"6","userAwardId":7864635,"userId":1002},{"alipayNumber":"vhfg","cashMoney":"2183","cashPoints":2183,"createTime":1535969619000,"id":"19e70","integralTime":1533052800000,"nickName":"","phone":"13111111117","rmUserId":84,"status":0,"type":"6","userAwardId":7864635,"userId":1002},{"alipayNumber":"vhfg","cashMoney":"2183","cashPoints":2183,"createTime":1535969619000,"id":"1kmql","integralTime":1533052800000,"nickName":"","phone":"13111111117","rmUserId":84,"status":1,"type":"6","userAwardId":7864635,"userId":1002},{"alipayNumber":"vhfg","cashMoney":"2183","cashPoints":2183,"createTime":1535969619000,"id":"1vva6","integralTime":1533052800000,"nickName":"","phone":"13111111117","rmUserId":84,"status":1,"type":"6","userAwardId":7864635,"userId":1002}]}
     * error : Success
     * errorCode : 200
     */

    public String error;
    public int errorCode;


    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public List<ListBean> list;

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            /**
             * alipayNumber : vhfg
             * cashMoney : 1000
             * cashPoints : 1000
             * counts : 7
             * createTime : 1536054283000
             * id : g0w
             * integralTime : 1533052800000
             * nickName : 顺乛◡乛
             * phone : 13111111117
             * rmUserId : 84
             * status : 0
             * type : 6
             * userAwardId : 7865712
             * userId : 1002
             */

            public String alipayNumber;
            public String cashMoney;
            public int cashPoints;
            public int counts;
            public long createTime;
            public String id;
            public long integralTime;
            public String nickName;
            public String phone;
            public int rmUserId;
            public int status;
            public String type;
            public int userAwardId;
            public int userId;
            public String payNum;

            public String getPayNum() {
                return payNum;
            }

            public void setPayNum(String payNum) {
                this.payNum = payNum;
            }




            public String getAlipayNumber() {
                return alipayNumber;
            }

            public void setAlipayNumber(String alipayNumber) {
                this.alipayNumber = alipayNumber;
            }

            public String getCashMoney() {
                return cashMoney;
            }

            public void setCashMoney(String cashMoney) {
                this.cashMoney = cashMoney;
            }

            public int getCashPoints() {
                return cashPoints;
            }

            public void setCashPoints(int cashPoints) {
                this.cashPoints = cashPoints;
            }

            public int getCounts() {
                return counts;
            }

            public void setCounts(int counts) {
                this.counts = counts;
            }

            public long getCreateTime() {
                return createTime;
            }

            public void setCreateTime(long createTime) {
                this.createTime = createTime;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public long getIntegralTime() {
                return integralTime;
            }

            public void setIntegralTime(long integralTime) {
                this.integralTime = integralTime;
            }

            public String getNickName() {
                return nickName;
            }

            public void setNickName(String nickName) {
                this.nickName = nickName;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public int getRmUserId() {
                return rmUserId;
            }

            public void setRmUserId(int rmUserId) {
                this.rmUserId = rmUserId;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public int getUserAwardId() {
                return userAwardId;
            }

            public void setUserAwardId(int userAwardId) {
                this.userAwardId = userAwardId;
            }

            public int getUserId() {
                return userId;
            }

            public void setUserId(int userId) {
                this.userId = userId;
            }
        }

}
