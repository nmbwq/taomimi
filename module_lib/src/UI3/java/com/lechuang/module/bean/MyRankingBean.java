package java.com.lechuang.module.bean;

import java.util.List;

public class MyRankingBean {

    /**
     * data : {"list":[{"appUserId":21424,"nickName":"鸿雁","phone":"18637337420","sum":1,"photo":"http://img.taoyouji666.com/D9E91F3C84F91F5C5BC0557364699005"},{"appUserId":21864,"nickName":"南客","phone":"15658132130","photo":"http://img.taoyouji666.com/D9E91F3C84F91F5C5BC0557364699005","sum":2},{"appUserId":21424,"nickName":"鸿雁","phone":"18637337420","sum":1}]}
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
         * appUserId : 21424
         * nickName : 鸿雁
         * phone : 18637337420
         * sum : 1
         * photo : http://img.taoyouji666.com/D9E91F3C84F91F5C5BC0557364699005
         */

        public int appUserId;
        public int ranking;
        public String nickName;
        public String phone;
        public int sum;
        public String photo;

        public int getAppUserId() {
            return appUserId;
        }

        public void setAppUserId(int appUserId) {
            this.appUserId = appUserId;
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

        public int getSum() {
            return sum;
        }

        public void setSum(int sum) {
            this.sum = sum;
        }

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }

    }
}
