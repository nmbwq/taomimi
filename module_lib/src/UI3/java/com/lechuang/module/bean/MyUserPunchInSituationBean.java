package java.com.lechuang.module.bean;

import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/8/31
 * @describe:
 */

public class MyUserPunchInSituationBean {

    /**
     * data : {"allAvgMonetExplain":"明早瓜分金额(元)","allAvgMoney":6,"joinCount":2,"userHeaders":[{"id":"b8qp","img":"http://img.taoyouji666.com/8B5F022D9BC03C49257FD3A325294244","nickName":"总公司","punchInTime":"06:25:33"},{"id":"1g8to","img":"http://thirdwx.qlogo.cn/mmopen/vi_32/eqW8c5NXuCjuzHSHibgrobibOL4FUk4qjOMm7mibZ7wYiclhuvCKjFpIQG1pWw30XYc3VkwT5EdCycicYRLo1ydCeAQ/132","nickName":"玄鸟-瑞","punchInTime":"06:25:33"}]}
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
         * allAvgMonetExplain : 明早瓜分金额(元)
         * allAvgMoney : 6.0
         * joinCount : 2
         * userHeaders : [{"id":"b8qp","img":"http://img.taoyouji666.com/8B5F022D9BC03C49257FD3A325294244","nickName":"总公司"},{"id":"1g8to","img":"http://thirdwx.qlogo.cn/mmopen/vi_32/eqW8c5NXuCjuzHSHibgrobibOL4FUk4qjOMm7mibZ7wYiclhuvCKjFpIQG1pWw30XYc3VkwT5EdCycicYRLo1ydCeAQ/132","nickName":"玄鸟-瑞","punchInTime":"06:25:33"}]
         */

        public double income;
        public int joinDays;
        public double payment;
        public int punchInDays;


}
