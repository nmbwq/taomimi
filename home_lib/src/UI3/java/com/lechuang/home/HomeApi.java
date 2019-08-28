package java.com.lechuang.home;

import com.common.app.base.bean.BaseResponseBean;
import com.common.app.http.api.Qurl;

import java.com.lechuang.home.bean.FlashSaleIdBean;
import java.com.lechuang.home.bean.HomeAllBean;
import java.com.lechuang.home.bean.HomeOtherBean;
import java.com.lechuang.home.bean.OtherAllChildBean;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

/**
 * @author: zhengjr
 * @since: 2018/8/21
 * @describe:
 */

public interface HomeApi {

    //首页全部的首次接口
    @FormUrlEncoded
    @POST(Qurl.homePageShowAll)
    Observable<BaseResponseBean<HomeAllBean>> homePageShowAll(@FieldMap Map<String,String> allParam);


    //首页全部的首次接口
    @FormUrlEncoded
    @POST(Qurl.productShowAll)
    Observable<BaseResponseBean<HomeAllBean>> homeProductShowAll(@FieldMap Map<String,String> allParam);

    //首页全部的首次接口
    @FormUrlEncoded
    @POST(Qurl.programaShowAll)
//    Observable<BaseResponseBean<HomeAllBean>> homeProgramaShowAll(@HeaderMap Map<String,String> headers ,@FieldMap Map<String,String> allParam);
    Observable<BaseResponseBean<HomeAllBean>> homeProgramaShowAll(@FieldMap Map<String,String> allParam);


    //首页全部的首次接口
    @FormUrlEncoded
    @POST(Qurl.productShowAll)
    Observable<BaseResponseBean<HomeOtherBean>> productShowAll(@FieldMap Map<String,String> allParam);

    //其他页面的子选项
    @FormUrlEncoded
    @POST(Qurl.getNextTbClass)
    Observable<BaseResponseBean<OtherAllChildBean>> getNextTbClass(@FieldMap Map<String,Object> allParam);

    //限时抢购ID
    @FormUrlEncoded
    @POST(Qurl.flashSaleId)
    Observable<BaseResponseBean<FlashSaleIdBean>> getFlashSaleId(@FieldMap Map<String, Object> allParam);


}
