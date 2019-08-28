package java.com.lechuang.module.other.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.com.lechuang.module.bean.FindTaoOrderBean;
import java.com.lechuang.module.bean.ZeroBuyDetailsBean;

/**
 * @author: zhengjr
 * @since: 2018/10/22
 * @describe:
 */

public class FindTaoOrderEntity implements MultiItemEntity {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_PRODUCT = 1;
    public int itemType;

    public String head;
    public FindTaoOrderBean.ListBean listBean;//内容


    public FindTaoOrderEntity(int itemType) {
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
