package com.lepao.ydcgkf.api;

import com.lepao.ydcgkf.utils.RetrofitFactory;

import retrofit2.Retrofit;

public class ApiService {
    private static LoginApi loginApi;
    private static ScanResultApi scanResultApi;
    private static CollectIdApi collectIdApi;
    private static FingerApi fingerApi;
    private static VenueApi venueApi;

    public static VenueApi getVenueApi() {
        if (venueApi == null) {
            Retrofit retrofit = RetrofitFactory.getRetrofit();
            venueApi = retrofit.create(VenueApi.class);
        }
        return venueApi;
    }

    public static FingerApi getFingerApi() {
        if (fingerApi == null) {
            Retrofit retrofit = RetrofitFactory.getRetrofit();
            fingerApi = retrofit.create(FingerApi.class);
        }
        return fingerApi;
    }

    public static CollectIdApi getCollectIdApi() {
        if (collectIdApi == null) {
            Retrofit retrofit = RetrofitFactory.getRetrofit();
            collectIdApi = retrofit.create(CollectIdApi.class);
        }
        return collectIdApi;
    }


    public static ScanResultApi getScanResultApi() {
        if (scanResultApi == null) {
            Retrofit retrofit = RetrofitFactory.getRetrofit();
            scanResultApi = retrofit.create(ScanResultApi.class);
        }
        return scanResultApi;
    }


    public static LoginApi getLoginApi() {
        if (loginApi == null) {
            Retrofit retrofit = RetrofitFactory.getRetrofit();
            loginApi = retrofit.create(LoginApi.class);
        }
        return loginApi;
    }


}
