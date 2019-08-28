package java.com.lechuang.module.bean;

import java.io.Serializable;
import java.util.List;

public class ZeroBuyJoinSuccessBean implements Serializable {

    public List<ProListBean> proList;
    public int num;
    public int uaCodeNum;
    public String code;
    public String keyNumSt;

    public static class ProListBean implements Serializable{
        public long countDown;
        public String id;
        public String name;
        public int needNum;
        public int realNum;
        public String showImg;
        public List<String> showImgList;
    }
}
