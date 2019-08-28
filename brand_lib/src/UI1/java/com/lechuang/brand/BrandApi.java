package java.com.lechuang.brand;

import com.common.app.base.bean.BaseResponseBean;
import com.common.app.http.api.Qurl;

import java.com.lechuang.brand.bean.BrandBean;
import java.com.lechuang.brand.bean.BrandInfoBean;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * @author: zhengjr
 * @since: 2018/8/24
 * @describe:
 */

public interface BrandApi {

    //首次接口
    @FormUrlEncoded
    @POST(Qurl.brandListShow)
    Observable<BaseResponseBean<BrandBean>> brandListShow(@FieldMap Map<String,Object> allParam);

    //底部刷新接口
    @FormUrlEncoded
    @POST(Qurl.brandShowAll)
    Observable<BaseResponseBean<BrandBean>> brandShowAll(@FieldMap Map<String,Object> allParam);

    //全部的首次接口
    @FormUrlEncoded
    @POST(Qurl.brandDetail)
    Observable<BaseResponseBean<BrandInfoBean>> brandDetail(@FieldMap Map<String,Object> allParam);

    //底部刷新接口
    @FormUrlEncoded
    @POST(Qurl.brandProductShow)
    Observable<BaseResponseBean<BrandInfoBean>> brandProductShow(@FieldMap Map<String,Object> allParam);
}
