package java.com.lechuang.module.bean;

/**
 * @author: zhengjr
 * @since: 2019/1/21
 * @describe:
 */

public class MyCardStratPayBean {

    public int status;
    public String errMsg;
    public ReturnMap returnMap;

    public static class ReturnMap{
        public String sign;
        public String tradNo;
        public String paymentNo;
        public String showMsg;
    }

}
