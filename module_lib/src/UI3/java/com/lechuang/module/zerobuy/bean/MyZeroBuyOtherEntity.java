package java.com.lechuang.module.zerobuy.bean;

import com.common.app.base.bean.BaseItemEntity;

import java.com.lechuang.module.bean.MyZeroBuyBean;
import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/10/22
 * @describe:
 */

public class MyZeroBuyOtherEntity extends BaseItemEntity {

    public static final int TYPE_PRODUCT = 1;

    public MyZeroBuyBean.ProListBean mProductListBean;//底部列表的数据//底部数据
    public List<MyZeroBuyBean.UseListBean> UseList;//使用列表
    public String imgUrl;//图片

    public MyZeroBuyOtherEntity(int itemType) {
        super(itemType);
    }
}
