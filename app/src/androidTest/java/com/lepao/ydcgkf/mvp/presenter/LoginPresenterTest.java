package com.lepao.ydcgkf.mvp.presenter;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.util.Log;

import com.lepao.ydcgkf.api.ApiService;
import com.lepao.ydcgkf.mvp.model.CommonModel;

import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

import static org.junit.Assert.*;

public class LoginPresenterTest {

    @Test
    public void login() throws IOException {
        Call<CommonModel> rs = ApiService.getLoginApi().login1("蜀山区宁溪小学","123456");
        String sid = rs.execute().body().getData();

        Map<String, String> map = new HashMap<>();
        map.put("sid", sid);
        map.put("mobile","321380199808127586" );
        map.put("fp", "123123");

        Call<CommonModel> fp = ApiService.getFingerApi().saveFPByCardNO1(map);
        Log.e("JUNIT",fp.execute().body().getData());
    }
}