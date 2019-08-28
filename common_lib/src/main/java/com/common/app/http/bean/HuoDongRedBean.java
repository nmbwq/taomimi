package com.common.app.http.bean;

/**
 * @author: zhengjr
 * @since: 2018/10/30
 * @describe:
 */

public class HuoDongRedBean {
    public ActivityDescBean activityDesc;

    public ICardPeck miCardPeck;

    public static class ActivityDescBean{
        public String activeImage;
        public String activeName;
        public String activeUrl;
        public String id;
        public int type;

        public String img;
        public String slipImg;
        public String link;
        public String mustParam;
        public String attachParam;
        public String rootName;
        public String commandCopy;
        public int linkAllows;
        public int obJump;
    }

    public static class ICardPeck{
        public String activeImage;
        public String activeName;
        public String linkAllows;
        public int type;

    }
}
