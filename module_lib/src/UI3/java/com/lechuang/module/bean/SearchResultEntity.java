package java.com.lechuang.module.bean;


import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * @author: zhengjr
 * @since: 2018/8/29
 * @describe:
 */

public class SearchResultEntity implements MultiItemEntity {

    public static final int TYPE1 = 0;
    public static final int TYPE2 = 1;
    public static final int TYPE_PRODUCT = 2;
    public int itemType;

    public SearchResultBean.ProductListBean mProductListBean;

    public SearchResultEntity(int itemType) {
        this.itemType = itemType;
    }

    public void setItemType(int itemType){
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return this.itemType;
    }

}
