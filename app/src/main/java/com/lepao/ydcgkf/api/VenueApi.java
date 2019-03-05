package com.lepao.ydcgkf.api;

import com.lepao.ydcgkf.content.Common;
import com.lepao.ydcgkf.mvp.model.VenueModel;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface VenueApi {
    @POST(Common.BASE_URL2 + "venue/getIdList")
    @FormUrlEncoded
    Observable<VenueModel> getVenueList(@Field("idList") String idList,@Field("token") String token);

}
