package java.com.lechuang.module.bean;

import com.common.app.base.bean.BaseItemEntity;

public class RedPackInfoEntity extends BaseItemEntity {

    public static final int TYPE_HEADER = 0;


    public RedPackInfoEntity(int itemType) {
        super(itemType);
    }

    public double provide;
    public double restore;

    public String createTime;
    public String expiredTime;
    public String id;
    public int intId;
    public int isStatus;
    public double money;
    public String nickName;
    public String phone;
    public String photo;
    public int type;
    public int upUserId;
    public int userId;
}
