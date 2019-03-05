package com.lepao.ydcgkf.api;

import com.lepao.ydcgkf.content.Common;
import com.lepao.ydcgkf.mvp.model.CommonModel;
import com.lepao.ydcgkf.mvp.model.LoginType2Model;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface LoginApi {
    @POST("login")
    @FormUrlEncoded
    Observable<CommonModel> login(@Field("username") String userName, @Field("password") String password);

    /**
     * 同步登录接口
     * @param userName
     * @param password
     * @return
     */
    @POST("login")
    @FormUrlEncoded
    Call<CommonModel> login1(@Field("username") String userName, @Field("password") String password);

    @POST(Common.BASE_URL2 + "users/login")
    @FormUrlEncoded
    Observable<LoginType2Model> loginType2(@Field("username") String userName, @Field("password") String password);

}
