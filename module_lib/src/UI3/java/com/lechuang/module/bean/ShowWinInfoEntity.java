package java.com.lechuang.module.bean;

import com.common.app.base.bean.BaseItemEntity;

public class ShowWinInfoEntity extends BaseItemEntity {

    public static final int TYPE_NONE = 0;//默认的布局类型
    public String awardProduct;
    public String id;
    public int isChange;
    public String nickName;
    public String wechatNumber;
    public int winCount;
    public String acquiredTime;
    public String winType;

    public ShowWinInfoEntity(int itemType) {
        super(itemType);
    }
}
