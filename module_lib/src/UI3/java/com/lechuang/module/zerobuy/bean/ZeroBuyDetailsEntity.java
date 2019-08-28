package java.com.lechuang.module.zerobuy.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.com.lechuang.module.bean.MyZeroBuyBean;
import java.com.lechuang.module.bean.TryDetailsBean;
import java.com.lechuang.module.bean.ZeroBuyDetailsBean;

/**
 * @author: zhengjr
 * @since: 2018/10/22
 * @describe:
 */

public class ZeroBuyDetailsEntity implements MultiItemEntity {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_PRODUCT = 1;
    public int itemType;

    public ZeroBuyDetailsBean.ProductBean mBanner;//轮播
    public String shortRegular;//简短活动规则
    public int retype;//1.参与试用 2.兑换更多（已经参与过） 3.预计开奖 4.未中奖 5.中奖
    public String winNum;//中奖码
    public String images;//图片
    public String sum;//兑换码数量
    public String keyNumStr;//兑换码详情
    public ZeroBuyDetailsBean.MustWinUserBean mustWinUser;//人气用户数据
    public ZeroBuyDetailsBean.LuckUserBean luckUser;//中奖用户数据


    public ZeroBuyDetailsEntity(int itemType) {
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
