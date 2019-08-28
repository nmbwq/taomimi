package java.com.lechuang.module.bean;

/**
 * @author: zhengjr
 * @since: 2018/8/31
 * @describe:
 */

public class KeFuBean {

    public CustomerServiceBean customerService;

    public static class CustomerServiceBean{
        public String serviceTime;
        public String wechatNumber;
        public String wechatQrcode;
        public String name;

    }
}
