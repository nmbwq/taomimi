package java.com.lechuang.module.bean;

import com.common.app.base.bean.BaseItemEntity;

public class ZeroBuyJoinSuccessProductEntity extends BaseItemEntity {

    public static final int TYPE_PRODUCT = 1;//底部产品

    public ZeroBuyJoinSuccessBean.ProListBean mProListBean;

    public ZeroBuyJoinSuccessProductEntity(int itemType) {
        super(itemType);
    }
}
