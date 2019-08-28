package java.com.lechuang.module.tmallsupermarket.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.com.lechuang.module.bean.TmallSupermarketBean;
import java.com.lechuang.module.bean.TryDetailsBean;
import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/10/22
 * @describe:
 */

public class TmallSupermarketEntity implements MultiItemEntity {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_PRODUCT = 1;
    public static final int TYPE_PRODUCTTWO = 2;
    public static final int TYPE_PRODUCTTHREE = 3;
    public int itemType;

    public String shortRegular;//简短活动规则
    public int retype;//1.参与试用 2.兑换更多（已经参与过） 3.预计开奖 4.未中奖 5.中奖
    public String winNum;//中奖码
    public String images;//图片

    //好劵专区
    public TmallSupermarketBean mBanners;//轮播
    public TmallSupermarketBean.ListBean haojuan;
    public String id;//主键
    public int ids;//主键
    public String itemName;//优惠券名字
    public String img;//图片
    public List<TmallSupermarketBean.ListBean.SecondListBean> secondListBean;//条目
    public int itemNum;
    //天猫超市 主键 id 图片 img
    public TmallSupermarketBean.ListBean TianMao;
    public String title;//标题
    public String descriptionWords;//文字说明
    //漏洞单
    public TmallSupermarketBean.ListBean loopholeList;//佣金
//    public int commission;//佣金
//    public int couponMoney;//劵金额
//    public int nowNumber;//销量
//    public int realityPrice;//劵后价
//    public String productName;//商品名
//    public String productText;//利益说明
//    public String productUrl;//商品链接
//    public String zhuanMoney;//分享赚金额
//    public int price;//原价
    //第一个
    public int idOne;
    public String imgOne;
    public String nameOne;
    public int statusOne;
    public int superIdOne;
    //第二个
    public int idTwo;
    public String imgTwo;
    public String nameTwo;
    public int statusTwo;
    public int superIdTwo;

    //第三个
    public int idThree;
    public String imgThree;
    public String nameThree;
    public int statusThree;
    public int superIdThree;
    //第四个
    public int idFour;
    public String imgFour;
    public String nameFour;
    public int statusFour;
    public int superIdFour;
    public TmallSupermarketEntity(int itemType) {
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
