package java.com.lechuang.module.bean;

import java.util.List;

public class LaXinMainWeekBean {
    public int btnType;
    public int count;
    public int sort;
    public String startTime;
    public String endTime;
    public String position;
    public List<RankingListBean> rankingList;

    public static class RankingListBean{
        public int sort;
        public String nickName;
        public String phone;
        public String photo;
        public String inviteCount;
    }
}
