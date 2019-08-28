package java.com.lechuang.module.mytry.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.com.lechuang.module.bean.MyTryBean;
import java.com.lechuang.module.bean.TryDetailsBean;
import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/10/22
 * @describe:
 */

public class TryDetailsEntity implements MultiItemEntity {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_PRODUCT = 1;
    public static final int TYPE_PRODUCTTWO = 2;
    public static final int TYPE_PRODUCTTHREE = 3;
    public int itemType;

    public TryDetailsBean.ProductBean mBanner;//轮播
    public String shortRegular;//简短活动规则
    public int retype;//1.参与试用 2.兑换更多（已经参与过） 3.预计开奖 4.未中奖 5.中奖
    public String winNum;//中奖码
    public String images;//图片

    public TryDetailsEntity(int itemType) {
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
