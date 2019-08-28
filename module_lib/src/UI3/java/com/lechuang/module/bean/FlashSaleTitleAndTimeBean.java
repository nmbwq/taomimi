package java.com.lechuang.module.bean;

import java.util.List;

/**
 * Created by lianzun on 2018/9/20.
 */

public class FlashSaleTitleAndTimeBean {


    /**
     * data : {"list":[{"status":2,"statusStr":"抢购中","time":"08:00","timeNum":"0208"},{"status":2,"statusStr":"抢购中","time":"10:00","timeNum":"0210"},{"status":2,"statusStr":"抢购中","time":"13:00","timeNum":"0213"},{"status":2,"statusStr":"抢购中","time":"15:00","timeNum":"0215"},{"status":1,"statusStr":"抢购进行中","time":"17:00","timeNum":"0217"},{"status":0,"statusStr":"即将开场","time":"19:00","timeNum":"0219"},{"status":0,"statusStr":"即将开场","time":"00:00","timeNum":"0300"}]}
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

    public List<ListBean> list;

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            /**
             * status : 2
             * statusStr : 抢购中
             * time : 08:00
             * timeNum : 0208
             */

            public int status;
            public String statusStr;
            public String time;
            public String timeNum;

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public String getStatusStr() {
                return statusStr;
            }

            public void setStatusStr(String statusStr) {
                this.statusStr = statusStr;
            }

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }

            public String getTimeNum() {
                return timeNum;
            }

            public void setTimeNum(String timeNum) {
                this.timeNum = timeNum;
            }
        }

}
