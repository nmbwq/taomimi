package java.com.lechuang.module.bean;

import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/8/23
 * @describe:
 */

public class BigWhellLuckInventoryBean {

    /**
     * data : {"LottoWin":[{"acquiredTime":"2019-02-20 09:31:37","activityId":1,"awardProduct":"夹板鞋","disabledTime":1553650297000,"id":"bbx5","intId":281,"isChange":0,"nNnStr":"*雁抽中了夹板鞋x1。","name":"夹板鞋","nickName":"*雁","wechatNumber":"taomimi01","winCount":1,"winRemark":"抽奖活动获得","winType":2,"winTypeId":3},{"acquiredTime":"2019-02-22 11:33:08","activityId":1,"awardProduct":"红卡","disabledTime":1553830388000,"id":"pi8","intId":2080,"isChange":0,"nNnStr":"*雁抽中了黄卡x1。","name":"黄卡","nickName":"*雁","wechatNumber":"taomimi01","winCount":1,"winRemark":"抽奖活动获得","winType":1,"winTypeId":1},{"acquiredTime":"2019-02-22 11:33:23","activityId":1,"awardProduct":"红卡","disabledTime":1553830403000,"id":"by1t","intId":2081,"isChange":0,"nNnStr":"*雁抽中了黄卡x1。","name":"黄卡","nickName":"*雁","wechatNumber":"taomimi01","winCount":1,"winRemark":"抽奖活动获得","winType":1,"winTypeId":1},{"acquiredTime":"2019-02-22 13:35:34","activityId":1,"awardProduct":"红卡","disabledTime":1553837734000,"id":"n6le","intId":2082,"isChange":0,"nNnStr":"*雁抽中了黄卡x1。","name":"黄卡","nickName":"*雁","wechatNumber":"taomimi01","winCount":1,"winRemark":"抽奖活动获得","winType":1,"winTypeId":1}],"nickName":"鸿雁"}
     * error : Success
     * errorCode : 200
     * status : SUCESS
     */

    public String error;
    public int errorCode;
    public String status;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

        /**
         * LottoWin : [{"acquiredTime":"2019-02-20 09:31:37","activityId":1,"awardProduct":"夹板鞋","disabledTime":1553650297000,"id":"bbx5","intId":281,"isChange":0,"nNnStr":"*雁抽中了夹板鞋x1。","name":"夹板鞋","nickName":"*雁","wechatNumber":"taomimi01","winCount":1,"winRemark":"抽奖活动获得","winType":2,"winTypeId":3},{"acquiredTime":"2019-02-22 11:33:08","activityId":1,"awardProduct":"红卡","disabledTime":1553830388000,"id":"pi8","intId":2080,"isChange":0,"nNnStr":"*雁抽中了黄卡x1。","name":"黄卡","nickName":"*雁","wechatNumber":"taomimi01","winCount":1,"winRemark":"抽奖活动获得","winType":1,"winTypeId":1},{"acquiredTime":"2019-02-22 11:33:23","activityId":1,"awardProduct":"红卡","disabledTime":1553830403000,"id":"by1t","intId":2081,"isChange":0,"nNnStr":"*雁抽中了黄卡x1。","name":"黄卡","nickName":"*雁","wechatNumber":"taomimi01","winCount":1,"winRemark":"抽奖活动获得","winType":1,"winTypeId":1},{"acquiredTime":"2019-02-22 13:35:34","activityId":1,"awardProduct":"红卡","disabledTime":1553837734000,"id":"n6le","intId":2082,"isChange":0,"nNnStr":"*雁抽中了黄卡x1。","name":"黄卡","nickName":"*雁","wechatNumber":"taomimi01","winCount":1,"winRemark":"抽奖活动获得","winType":1,"winTypeId":1}]
         * nickName : 鸿雁
         */

        private String nickName;
        private List<LottoWinBean> LottoWin;

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public List<LottoWinBean> getLottoWin() {
            return LottoWin;
        }

        public void setLottoWin(List<LottoWinBean> LottoWin) {
            this.LottoWin = LottoWin;
        }

        public static class LottoWinBean {
            /**
             * acquiredTime : 2019-02-20 09:31:37
             * activityId : 1
             * awardProduct : 夹板鞋
             * disabledTime : 1553650297000
             * id : bbx5
             * intId : 281
             * isChange : 0
             * nNnStr : *雁抽中了夹板鞋x1。
             * name : 夹板鞋
             * nickName : *雁
             * wechatNumber : taomimi01
             * winCount : 1
             * winRemark : 抽奖活动获得
             * winType : 2
             * winTypeId : 3
             */

            public String acquiredTime;
            public int activityId;
            public String awardProduct;
            public long disabledTime;
            public String id;
            public int intId;
            public int isChange;
            public String nNnStr;
            public String name;
            public String nickName;
            public String wechatNumber;
            public int winCount;
            public String winRemark;
            public int winType;
            public int winTypeId;

            public String getAcquiredTime() {
                return acquiredTime;
            }

            public void setAcquiredTime(String acquiredTime) {
                this.acquiredTime = acquiredTime;
            }

            public int getActivityId() {
                return activityId;
            }

            public void setActivityId(int activityId) {
                this.activityId = activityId;
            }

            public String getAwardProduct() {
                return awardProduct;
            }

            public void setAwardProduct(String awardProduct) {
                this.awardProduct = awardProduct;
            }

            public long getDisabledTime() {
                return disabledTime;
            }

            public void setDisabledTime(long disabledTime) {
                this.disabledTime = disabledTime;
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

            public int getIsChange() {
                return isChange;
            }

            public void setIsChange(int isChange) {
                this.isChange = isChange;
            }

            public String getNNnStr() {
                return nNnStr;
            }

            public void setNNnStr(String nNnStr) {
                this.nNnStr = nNnStr;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getNickName() {
                return nickName;
            }

            public void setNickName(String nickName) {
                this.nickName = nickName;
            }

            public String getWechatNumber() {
                return wechatNumber;
            }

            public void setWechatNumber(String wechatNumber) {
                this.wechatNumber = wechatNumber;
            }

            public int getWinCount() {
                return winCount;
            }

            public void setWinCount(int winCount) {
                this.winCount = winCount;
            }

            public String getWinRemark() {
                return winRemark;
            }

            public void setWinRemark(String winRemark) {
                this.winRemark = winRemark;
            }

            public int getWinType() {
                return winType;
            }

            public void setWinType(int winType) {
                this.winType = winType;
            }

            public int getWinTypeId() {
                return winTypeId;
            }

            public void setWinTypeId(int winTypeId) {
                this.winTypeId = winTypeId;
            }
        }

}
