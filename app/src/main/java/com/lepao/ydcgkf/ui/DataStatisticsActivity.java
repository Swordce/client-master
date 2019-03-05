package com.lepao.ydcgkf.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.just.agentweb.AgentWeb;
import com.just.agentweb.DefaultWebClient;
import com.lepao.ydcgkf.R;
import com.lepao.ydcgkf.base.BaseActivity;
import com.lepao.ydcgkf.utils.AppManager;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * created by zwj on 2018/9/10 0010
 */
public class DataStatisticsActivity extends BaseActivity {

    private String url = "http://www.javaer.com.cn/lepao/h5/#/dataPlatformH5?token=";
    private AgentWeb mAgentWeb;
    private LinearLayout mLinearLayout;

    @Override
    public void initView() {
        String token = getIntent().getStringExtra("token");
        if(!TextUtils.isEmpty(token)) {
            url = url + token;
        }else {
            url = url + "lepao123456";
        }
        mLinearLayout = (LinearLayout) this.findViewById(R.id.container);
        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(mLinearLayout, new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .setMainFrameErrorView(R.layout.agentweb_error_page, -1)
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)//打开其他应用时，弹窗咨询用户是否前往其他应用
                .interceptUnkownUrl() //拦截找不到相关页面的Scheme
                .createAgentWeb()
                .ready()
                .go(url);
    }

    @Override
    public void getData() {

    }

    @Override
    public int getLayout() {
        return R.layout.activity_data_statistic;
    }


    @OnClick({R.id.ll_finish,R.id.ll_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_finish:
                AppManager.getAppManager().finishActivity();
                break;
            case R.id.ll_right:
                Intent intent = new Intent(this,DataStatisticsQRCodeActivity.class);
                intent.putExtra("url",url);
                startActivity(intent);
                break;
        }
    }
}
