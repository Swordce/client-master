package com.lepao.ydcgkf.api;

import com.lepao.ydcgkf.mvp.model.CommonModel;
import com.lepao.ydcgkf.mvp.model.UserInfoModel;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * created by zwj on 2018/9/6 0006
 */
public interface ScanResultApi {
    @POST("entrance")
    @FormUrlEncoded
    Observable<CommonModel> postScanResult(@Field("sid") String sid,@Field("code") String code);

    @POST("qrCode")
    @FormUrlEncoded
    Observable<UserInfoModel> getUserInfo(@Field("sid") String sid, @Field("code") String code);

    @POST("walkedOut")
    @FormUrlEncoded
    Observable<CommonModel> postWalkedOut(@Field("sid") String sid,@Field("code") String code);

}
