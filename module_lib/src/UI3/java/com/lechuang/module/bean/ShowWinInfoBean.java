package java.com.lechuang.module.bean;

import java.util.List;

public class ShowWinInfoBean {
    public List<LottoWinBean> LottoWin;

    public static class LottoWinBean{
        public String awardProduct;
        public String id;
        public int isChange;
        public String nickName;
        public String wechatNumber;
        public int winCount;
        public String acquiredTime;
        public String winType;
    }
}
