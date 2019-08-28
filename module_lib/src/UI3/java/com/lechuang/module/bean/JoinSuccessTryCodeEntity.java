package java.com.lechuang.module.bean;

import com.common.app.base.bean.BaseItemEntity;

public class JoinSuccessTryCodeEntity extends BaseItemEntity {

    public static final int TYPE_TRYCODE_STATE = 0;//试用码状态

    public int num;
    public int uaCodeNum;
    public String code;
    public String keyNumSt;

    public JoinSuccessTryCodeEntity(int itemType) {
        super(itemType);
    }
}
