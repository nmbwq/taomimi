package java.com.lechuang.module.mytry.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.com.lechuang.module.bean.MyTryBean;
import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/10/22
 * @describe:
 */

public class MyTryAllEntity implements MultiItemEntity {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_PRODUCT = 1;
    public int itemType;

    public MyTryBean.ProListBean mProductListBean;//底部列表的数据//底部数据
    public List<MyTryBean.UseListBean> UseList;//使用列表
    public String imgUrl;//图片

    public MyTryAllEntity(int itemType) {
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
