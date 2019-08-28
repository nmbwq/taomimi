package java.com.lechuang.home.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/10/22
 * @describe:
 */

public class OtherAllEntity implements MultiItemEntity {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_SHAIXUAN = 1;
    public static final int TYPE_PRODUCT = 2;
    public int itemType;

    public List<OtherAllChildBean.ClassTypeListBean> classTypeList;

    public HomeOtherBean.ProductListBean mProductListBean;

    public OtherAllEntity(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return this.itemType;
    }
}
