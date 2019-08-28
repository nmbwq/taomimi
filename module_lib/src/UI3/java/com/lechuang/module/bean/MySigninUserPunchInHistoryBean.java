package java.com.lechuang.module.bean;

import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/8/31
 * @describe:
 */

public class MySigninUserPunchInHistoryBean {

    /**
     * data : {"count":3,"list":[{"activityTime":"2019-01-19","income":0,"payment":1,"status":0,"statusStr":"待打卡"},{"activityTime":"2019-01-18","income":3.19,"payment":3,"status":1,"statusStr":"收入3.19"},{"activityTime":"2019-01-16","income":1.32,"payment":1,"status":1,"statusStr":"收入1.32"}]}
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
         * count : 3
         * list : [{"activityTime":"2019-01-19","income":0,"payment":1,"status":0,"statusStr":"待打卡"},{"activityTime":"2019-01-18","income":3.19,"payment":3,"status":1,"statusStr":"收入3.19"},{"activityTime":"2019-01-16","income":1.32,"payment":1,"status":1,"statusStr":"收入1.32"}]
         */

        public int count;
        public List<ListBean> list;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            /**
             * activityTime : 2019-01-19
             * income : 0.0
             * payment : 1.0
             * status : 0
             * statusStr : 待打卡
             */

            public String activityTime;
            public double income;
            public double payment;
            public int status;
            public String statusStr;

            public String getActivityTime() {
                return activityTime;
            }

            public void setActivityTime(String activityTime) {
                this.activityTime = activityTime;
            }

            public double getIncome() {
                return income;
            }

            public void setIncome(double income) {
                this.income = income;
            }

            public double getPayment() {
                return payment;
            }

            public void setPayment(double payment) {
                this.payment = payment;
            }

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
        }

}
