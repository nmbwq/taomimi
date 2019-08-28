package java.com.lechuang.module.bean;

public class ShouQuanBean {


    /**
     * score : null
     * level : null
     * status : {"errorCode":303,"error":"SafeToken Can not be empty"}
     * moreInfo : 请先登录！
     * acquireScore : null
     * nextScore : null
     * data : null
     * error : SafeToken Can not be empty
     * errorCode : 303
     */

    public Object score;
    public Object level;
    public StatusBean status;
    public String moreInfo;
    public Object acquireScore;
    public Object nextScore;
    public Object data;
    public String error;
    public int errorCode;


    public static class StatusBean {
        /**
         * errorCode : 303
         * error : SafeToken Can not be empty
         */

        public int errorCode;
        public String error;

    }
}
