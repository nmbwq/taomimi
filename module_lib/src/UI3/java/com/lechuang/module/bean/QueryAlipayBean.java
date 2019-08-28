package java.com.lechuang.module.bean;

/**
 * @author: zhengjr
 * @since: 2018/6/28
 * @describe:
 */

public class QueryAlipayBean {

        public String errorMsg;
    public int status;

    public QueryAlipayBean() {
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
