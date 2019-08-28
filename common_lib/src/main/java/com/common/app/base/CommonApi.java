package com.common.app.base;

import com.common.app.base.bean.BaseResponseBean;
import com.common.app.base.bean.UrlToTitleBean;
import com.common.app.http.api.Qurl;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * @author: zhengjr
 * @since: 2018/8/27
 * @describe:
 */

public interface CommonApi {

    //main 更新APP
    @FormUrlEncoded
    @POST(Qurl.urlToTitle)
    Observable<BaseResponseBean<UrlToTitleBean>> getTitle(@FieldMap Map<String, String> allParam);

    /*@POST(Qurl.guide)
    Observable<BaseResponseBean<GuideBean>> guide();

    @POST(Qurl.adverInfo)
    Observable<BaseResponseBean<AdverBean>> adverInfo();

    @FormUrlEncoded
    @POST(Qurl.queryShowHide)
    Observable<BaseResponseBean<QueryShowHideBean>> queryShowHide(@FieldMap Map<String, Object> allParam);*/
}
