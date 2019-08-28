package java.com.lechuang.main;

import com.common.app.base.bean.BaseResponseBean;
import com.common.app.http.api.Qurl;

import java.com.lechuang.main.bean.AdverBean;
import java.com.lechuang.main.bean.AppUpdataBean;
import java.com.lechuang.main.bean.GuideBean;
import java.com.lechuang.main.bean.MainHintBean;
import java.com.lechuang.main.bean.QueryShowHideBean;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * @author: zhengjr
 * @since: 2018/8/27
 * @describe:
 */

public interface MainApi {

    //main 更新APP
    @FormUrlEncoded
    @POST()
    Observable<BaseResponseBean<AppUpdataBean>> listShowAll(@FieldMap Map<String,String> allParam);

    @POST(Qurl.guide)
    Observable<BaseResponseBean<GuideBean>> guide();

    @POST(Qurl.adverInfo)
    Observable<BaseResponseBean<AdverBean>> adverInfo();

    @FormUrlEncoded
    @POST(Qurl.queryShowHide)
    Observable<BaseResponseBean<QueryShowHideBean>> queryShowHide(@FieldMap Map<String,Object> allParam);
}
