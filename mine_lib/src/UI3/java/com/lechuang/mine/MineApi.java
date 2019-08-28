package java.com.lechuang.mine;

import com.common.app.base.bean.BaseResponseBean;
import com.common.app.http.api.Qurl;
import com.common.app.http.bean.ShareAppBean;

import java.com.lechuang.mine.bean.MineHuoDongBean;
import java.com.lechuang.mine.bean.MineUserBean;

import io.reactivex.Observable;
import retrofit2.http.POST;

/**
 * @author: zhengjr
 * @since: 2018/8/31
 * @describe:
 */

public interface MineApi {

    //获取当前用户信息
    @POST(Qurl.userInfo2)
    Observable<BaseResponseBean<MineUserBean>> userInfo();
    //获取收益信息
    @POST(Qurl.incomeOverview)
    Observable<BaseResponseBean<MineUserBean>> incomeOverview();
    //获取管理津贴信息
    @POST(Qurl.userAllowance)
    Observable<BaseResponseBean<MineUserBean>> userAllowance();

    //获取当前用户信息
    @POST(Qurl.active)
    Observable<BaseResponseBean<MineHuoDongBean>> getHuoDong();

    //获取分享图片
    @Deprecated
    @POST(Qurl.getShareImages)
    Observable<BaseResponseBean<ShareAppBean>> getShareImages();
}
