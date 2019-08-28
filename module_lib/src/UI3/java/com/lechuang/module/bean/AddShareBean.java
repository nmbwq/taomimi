package java.com.lechuang.module.bean;

/**
 * Created by lianzun on 2018/12/27.
 */

public class AddShareBean {


    /**
     * data : {"resCode":"success","resMessage":"商品分享记录保存成功"}
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
     * resCode : success
     * resMessage : 商品分享记录保存成功
     */

    private String resCode;
    private String resMessage;

    public String getResCode() {
        return resCode;
    }

    public void setResCode(String resCode) {
        this.resCode = resCode;
    }

    public String getResMessage() {
        return resMessage;
    }

    public void setResMessage(String resMessage) {
        this.resMessage = resMessage;
    }

}
