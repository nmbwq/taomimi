package java.com.lechuang.module.upgrade.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.com.lechuang.module.bean.MyOrderBean;
import java.com.lechuang.module.bean.MyTryBean;
import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/10/22
 * @describe:
 */

public class MyOrderEntity implements MultiItemEntity {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_PRODUCT = 1;
    public int itemType;

    public MyOrderBean.UserAddressBean userAddress;//底部列表的数据//底部数据
    public MyOrderBean.ProductBean product;//底部列表的数据//底部数据
    public List<MyOrderBean> UseList;//使用列表
    public String imgUrl;//图片

    public MyOrderEntity(int itemType) {
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
