package java.com.lechuang.home.bean;

import com.wikikii.bannerlib.banner.LoopLayout;
import com.wikikii.bannerlib.banner.view.BannerBgContainer;

import java.io.Serializable;

/**
 * @author: zhengjr
 * @since: 2018/12/25
 * @describe:
 */
public class ContainerView{
    public BannerBgContainer mBannerBgContainer;
    public ContainerView(BannerBgContainer bannerBgContainer) {
        mBannerBgContainer = bannerBgContainer;
    }


    /*public ContainerView() {
    }

    public ContainerView(BannerBgContainer bannerBgContainer) {
        mBannerBgContainer = bannerBgContainer;
    }

    @Override
    public <T> T json2Object(String input, Class<T> clazz) {
        return JSON.parseObject(input,clazz);
    }

    @Override
    public String object2Json(Object instance) {
        return JSON.toJSONString(instance);
    }

    @Override
    public <T> T parseObject(String input, Type clazz) {
        return JSON.parseObject(input,clazz);
    }

    @Override
    public void init(Context context) {

    }*/
}
