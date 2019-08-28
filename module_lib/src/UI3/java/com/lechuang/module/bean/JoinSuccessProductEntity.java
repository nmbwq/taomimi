package java.com.lechuang.module.bean;

import com.common.app.base.bean.BaseItemEntity;

public class JoinSuccessProductEntity extends BaseItemEntity {

    public static final int TYPE_PRODUCT = 1;//底部产品

    public JoinSuccessBean.ProListBean mProListBean;

    public JoinSuccessProductEntity(int itemType) {
        super(itemType);
    }
}
