package java.com.lechuang.module.bean;

/**
 * @author: zhengjr
 * @since: 2018/8/31
 * @describe:
 */

public class MySigninTodayBean {

    /**
     * data : {"firstPunchInUser":{"id":"b8qp","img":"http://img.taoyouji666.com/8B5F022D9BC03C49257FD3A325294244","nickName":"总公司"},"isAllowClock":0,"isJoin":1,"isJoinTomorrow":0,"isPunchIn":3,"punchCardCount":2,"punchInStr":"打卡超时,参与明天打卡","unPunchCardCount":1}
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
         * firstPunchInUser : {"id":"b8qp","img":"http://img.taoyouji666.com/8B5F022D9BC03C49257FD3A325294244","nickName":"总公司"}
         * isAllowClock : 0
         * isJoin : 1
         * isJoinTomorrow : 0
         * isPunchIn : 3
         * punchCardCount : 2
         * punchInStr : 打卡超时,参与明天打卡
         * unPunchCardCount : 1
         */

        public FirstPunchInUserBean firstPunchInUser;
        public StaminaUserBean staminaUser;
        public int isAllowClock;
        public int isJoin;
        public int isJoinTomorrow;
        public int isPunchIn;
        public int punchCardCount;
        public String punchInStr;
        public int unPunchCardCount;
        public String endTimestamp;
        public String failedMoney;

    public StaminaUserBean getStaminaUser() {
        return staminaUser;
    }

    public void setStaminaUser(StaminaUserBean staminaUser) {
        this.staminaUser = staminaUser;
    }

    public int show;

        public FirstPunchInUserBean getFirstPunchInUser() {
            return firstPunchInUser;
        }

        public void setFirstPunchInUser(FirstPunchInUserBean firstPunchInUser) {
            this.firstPunchInUser = firstPunchInUser;
        }

        public int getIsAllowClock() {
            return isAllowClock;
        }

        public void setIsAllowClock(int isAllowClock) {
            this.isAllowClock = isAllowClock;
        }

        public int getIsJoin() {
            return isJoin;
        }

        public void setIsJoin(int isJoin) {
            this.isJoin = isJoin;
        }

        public int getIsJoinTomorrow() {
            return isJoinTomorrow;
        }

        public void setIsJoinTomorrow(int isJoinTomorrow) {
            this.isJoinTomorrow = isJoinTomorrow;
        }

        public int getIsPunchIn() {
            return isPunchIn;
        }

        public void setIsPunchIn(int isPunchIn) {
            this.isPunchIn = isPunchIn;
        }

        public int getPunchCardCount() {
            return punchCardCount;
        }

        public void setPunchCardCount(int punchCardCount) {
            this.punchCardCount = punchCardCount;
        }

        public String getPunchInStr() {
            return punchInStr;
        }

        public void setPunchInStr(String punchInStr) {
            this.punchInStr = punchInStr;
        }

        public int getUnPunchCardCount() {
            return unPunchCardCount;
        }

        public void setUnPunchCardCount(int unPunchCardCount) {
            this.unPunchCardCount = unPunchCardCount;
        }

    public static class StaminaUserBean {
        /**
         * durationDay : 7
         * nickName : 总公司
         * phone : 18888888888
         */

        public int durationDay;
        public String nickName;
        public String phone;
        public String img;

        public int getDurationDay() {
            return durationDay;
        }

        public void setDurationDay(int durationDay) {
            this.durationDay = durationDay;
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
    }

        public static class FirstPunchInUserBean {
            /**
             * id : b8qp
             * img : http://img.taoyouji666.com/8B5F022D9BC03C49257FD3A325294244
             * nickName : 总公司
             */

            public String id;
            public String img;
            public String nickName;
            public String punchInTime;

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

            public String getNickName() {
                return nickName;
            }

            public void setNickName(String nickName) {
                this.nickName = nickName;
            }

    }
}
