package java.com.lechuang.home.bean;

/**
 * Created by lianzun on 2018/12/27.
 */

public class FlashSaleIdBean {
    public String error;
    public int errorCode;
    public String status;
    public ProdBean prod;

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

    public static class ProdBean {
        public String quan_id;//劵id
        public String goodsID;//商品id传给webview
    }
}
