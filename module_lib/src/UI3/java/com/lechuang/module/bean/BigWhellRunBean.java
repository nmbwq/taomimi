package java.com.lechuang.module.bean;

import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/8/23
 * @describe:
 */

public class BigWhellRunBean {
    /**
     * data : {"list":[{"activityId":1,"awardCount":99999,"disabled":0,"id":"b8qp","img":"http://img.taoyouji666.com/ac6b448bf5d44dbf92e538d9f5e86e2a","intId":1,"micardId":1,"name":"红卡","number":1,"oddsProbability":0.8,"sort":8,"type":1,"win":0},{"activityId":1,"awardCount":100,"disabled":0,"id":"mhaa","img":"http://img.taoyouji666.com/ac6b448bf5d44dbf92e538d9f5e86e2a","intId":2,"name":"草鞋","number":1,"oddsProbability":0.05,"sort":7,"type":2,"win":0},{"activityId":1,"awardCount":99,"disabled":0,"id":"xptv","img":"http://img.taoyouji666.com/ac6b448bf5d44dbf92e538d9f5e86e2a","intId":3,"name":"夹板鞋","number":1,"oddsProbability":0.04,"sort":6,"type":2,"win":0},{"activityId":1,"awardCount":50,"disabled":0,"id":"18ydg","img":"http://img.taoyouji666.com/ac6b448bf5d44dbf92e538d9f5e86e2a","intId":4,"name":"高跟鞋","number":1,"oddsProbability":0.03,"sort":5,"type":2,"win":0},{"activityId":1,"awardCount":30,"disabled":0,"id":"1k6x1","img":"http://img.taoyouji666.com/ac6b448bf5d44dbf92e538d9f5e86e2a","intId":5,"name":"板鞋","number":1,"oddsProbability":0.02,"sort":4,"type":2,"win":0},{"activityId":1,"awardCount":20,"disabled":0,"id":"1vfgm","img":"http://img.taoyouji666.com/ac6b448bf5d44dbf92e538d9f5e86e2a","intId":6,"name":"拖鞋","number":1,"oddsProbability":0.01,"sort":3,"type":2,"win":0},{"activityId":1,"awardCount":2,"disabled":0,"id":"26o07","img":"http://img.taoyouji666.com/ac6b448bf5d44dbf92e538d9f5e86e2a","intId":7,"name":"ipad","number":1,"oddsProbability":0,"sort":2,"type":2,"win":0},{"activityId":1,"awardCount":1,"disabled":0,"id":"7c","img":"http://img.taoyouji666.com/ac6b448bf5d44dbf92e538d9f5e86e2a","intId":8,"name":"肾x","number":1,"oddsProbability":-1,"sort":1,"type":2,"win":0}],"nickName":"112121","number":8864,"shuffling":["*仔抽中了1个红卡。","*锐抽中了1个红卡。"]}
     * error : Success
     * errorCode : 200
     * status : SUCESS
     */

    private String error;
    private int errorCode;
    private String status;


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
     * list : [{"activityId":1,"awardCount":99999,"disabled":0,"id":"b8qp","img":"http://img.taoyouji666.com/ac6b448bf5d44dbf92e538d9f5e86e2a","intId":1,"micardId":1,"name":"红卡","number":1,"oddsProbability":0.8,"sort":8,"type":1,"win":0},{"activityId":1,"awardCount":100,"disabled":0,"id":"mhaa","img":"http://img.taoyouji666.com/ac6b448bf5d44dbf92e538d9f5e86e2a","intId":2,"name":"草鞋","number":1,"oddsProbability":0.05,"sort":7,"type":2,"win":0},{"activityId":1,"awardCount":99,"disabled":0,"id":"xptv","img":"http://img.taoyouji666.com/ac6b448bf5d44dbf92e538d9f5e86e2a","intId":3,"name":"夹板鞋","number":1,"oddsProbability":0.04,"sort":6,"type":2,"win":0},{"activityId":1,"awardCount":50,"disabled":0,"id":"18ydg","img":"http://img.taoyouji666.com/ac6b448bf5d44dbf92e538d9f5e86e2a","intId":4,"name":"高跟鞋","number":1,"oddsProbability":0.03,"sort":5,"type":2,"win":0},{"activityId":1,"awardCount":30,"disabled":0,"id":"1k6x1","img":"http://img.taoyouji666.com/ac6b448bf5d44dbf92e538d9f5e86e2a","intId":5,"name":"板鞋","number":1,"oddsProbability":0.02,"sort":4,"type":2,"win":0},{"activityId":1,"awardCount":20,"disabled":0,"id":"1vfgm","img":"http://img.taoyouji666.com/ac6b448bf5d44dbf92e538d9f5e86e2a","intId":6,"name":"拖鞋","number":1,"oddsProbability":0.01,"sort":3,"type":2,"win":0},{"activityId":1,"awardCount":2,"disabled":0,"id":"26o07","img":"http://img.taoyouji666.com/ac6b448bf5d44dbf92e538d9f5e86e2a","intId":7,"name":"ipad","number":1,"oddsProbability":0,"sort":2,"type":2,"win":0},{"activityId":1,"awardCount":1,"disabled":0,"id":"7c","img":"http://img.taoyouji666.com/ac6b448bf5d44dbf92e538d9f5e86e2a","intId":8,"name":"肾x","number":1,"oddsProbability":-1,"sort":1,"type":2,"win":0}]
     * nickName : 112121
     * number : 8864
     * shuffling : ["*仔抽中了1个红卡。","*锐抽中了1个红卡。"]
     */

    public String nickName;
    public int number;
    public int type;
    public String wechatCare;
    public String prize;
    public String img;
    public List<BigWhellShowBean.ListBean> list;
    public List<String> shuffling;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public List<BigWhellShowBean.ListBean> getList() {
        return list;
    }

    public void setList(List<BigWhellShowBean.ListBean> list) {
        this.list = list;
    }

    public List<String> getShuffling() {
        return shuffling;
    }

    public void setShuffling(List<String> shuffling) {
        this.shuffling = shuffling;
    }

    public static class ListBean {
        /**
         * activityId : 1
         * awardCount : 99999
         * disabled : 0
         * id : b8qp
         * img : http://img.taoyouji666.com/ac6b448bf5d44dbf92e538d9f5e86e2a
         * intId : 1
         * micardId : 1
         * name : 红卡
         * number : 1
         * oddsProbability : 0.8
         * sort : 8
         * type : 1
         * win : 0
         */

        public int activityId;
        public int awardCount;
        public int disabled;
        public String id;
        public String img;
        public int intId;
        public int micardId;
        public String name;
        public int number;
        public double oddsProbability;
        public int sort;
        public int type;
        public int win;

        public int getActivityId() {
            return activityId;
        }

        public void setActivityId(int activityId) {
            this.activityId = activityId;
        }

        public int getAwardCount() {
            return awardCount;
        }

        public void setAwardCount(int awardCount) {
            this.awardCount = awardCount;
        }

        public int getDisabled() {
            return disabled;
        }

        public void setDisabled(int disabled) {
            this.disabled = disabled;
        }

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

        public int getMicardId() {
            return micardId;
        }

        public void setMicardId(int micardId) {
            this.micardId = micardId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public double getOddsProbability() {
            return oddsProbability;
        }

        public void setOddsProbability(double oddsProbability) {
            this.oddsProbability = oddsProbability;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getWin() {
            return win;
        }

        public void setWin(int win) {
            this.win = win;
        }
    }
}
