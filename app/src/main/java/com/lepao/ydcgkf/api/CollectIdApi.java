package com.lepao.ydcgkf.api;

import com.lepao.ydcgkf.mvp.model.CollectModel;
import com.lepao.ydcgkf.mvp.model.CommonModel;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * created by zwj on 2018/9/7 0007
 */
public interface CollectIdApi {
    @POST("http://dm-51.data.aliyun.com/rest/160601/ocr/ocr_idcard.json")
    Observable<String> getIdInfo(@Body RequestBody body,@Header("Authorization") String header);
    @POST("addCard")
    @FormUrlEncoded
    Observable<CollectModel> addCard(@FieldMap Map<String,String> fieldMap);
}
