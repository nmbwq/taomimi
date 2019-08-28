package java.com.lechuang.home.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/10/22
 * @describe:
 */

public class HomeAllEntity implements MultiItemEntity {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_PRODUCT = 1;
    public int itemType;

    public List<HomeAllBean.IndexBannerListBean> indexBannerList;//顶部的banner数据
    public List<HomeAllBean.GuideBannerListBean> guideBannerList;//导航数据
    public List<HomeAllBean.ActivityImgListBean> activityImgList;//活动数据
    public List<HomeAllBean.BroadcastListBean> broadcastList;//设置textView滚动
    public List<HomeAllBean.AdvertProductListBean> advertProductList;//品牌热卖
    public List<HomeAllBean.ProgramaImgListBean> programaImgList;//设置中间栏目
    public List<HomeAllBean.RushTimeListBean> rushTimeList; //抢购时间列表
    public HomeAllBean.ProductListBean mProductListBean;//底部列表的数据
    public List<HomeAllBean.HotSaleProductListBean> hotSaleProductList;
    public List<HomeAllBean.TodayProductList> todayProductList;
    public List<HomeAllBean.BoutiqueProductListBean> boutiqueProductList;
    public List<HomeAllBean.RushProductList> rushProductList;//限时抢购
    public List<HomeAllBean.ModuleColumnHeadList> moduleColumnHeadList;//新栏目
    public List<HomeAllBean.MiddleBannerList> middleBannerList;//滑动
    public List<HomeAllBean.ModuleColumnBelowList> moduleColumnBelowList;//新格子
    public HomeAllBean.ActiveCover activeCover;//0元抢购
    public HomeAllBean.HotSaleProduct hotSaleProduct;//爆款必抢
    public HomeAllBean.CustomProductListBean customProductListBean;
    public HomeAllBean.PlacardBannerBean placardBanner;
    public String advertName;
    public String advertImg;
    public int programaClass;
    public long countDown;
    public String moduleName;

    public HomeAllEntity(int itemType) {
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
