package java.com.lechuang.module.bean;


import com.common.app.base.bean.BaseItemEntity;

public class MineBangDanEntity extends BaseItemEntity {

    public MineBangDanEntity(int itemType) {
        super(itemType);
    }

    public int followerCount;
    public int position;
    public String positionStr;
    public int status;
    public int isAward;
    public String startTime;
    public String endTime;
    public String caption;
    public String id;

}
