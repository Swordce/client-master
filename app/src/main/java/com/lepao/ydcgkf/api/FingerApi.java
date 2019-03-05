package com.lepao.ydcgkf.api;

import com.lepao.ydcgkf.mvp.model.CommonModel;
import com.lepao.ydcgkf.mvp.model.FingerModel;
import com.lepao.ydcgkf.mvp.model.UserInfoModel;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * created by zwj on 2018/9/10 0010
 */
public interface FingerApi {

    @POST("fingerprint")
    @FormUrlEncoded
    Observable<CommonModel> getFingerInfo(@FieldMap Map<String,String> map);

    /**
     * 通过身份证号码提交指纹信息
     * @param map
     * @return
     */
    @POST("fpcardno")
    @FormUrlEncoded
    Observable<CommonModel> saveFPByCardNO(@FieldMap Map<String,String> map);

    @POST("fpcardno")
    @FormUrlEncoded
    Call<CommonModel> saveFPByCardNO1(@FieldMap Map<String,String> map);

    @POST("entrance")
    @FormUrlEncoded
    Observable<CommonModel> postScanResult(@Field("sid") String sid, @Field("fpsn") String code);

    @POST("walkedOut")
    @FormUrlEncoded
    Observable<CommonModel> postWalkedOut(@Field("sid") String sid,@Field("fpsn") String code);

    @POST("http://47.92.223.1:8081/shushan/finger/match")
    Observable<FingerModel> matchFinger(@Body RequestBody fp);

}
