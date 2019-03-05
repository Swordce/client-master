package com.lepao.ydcgkf;

import android.app.Application;
import android.content.Context;

import com.clj.fastble.BleManager;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.lepao.ydcgkf.content.Common;
import com.lepao.ydcgkf.utils.SharedPreferencesUtil;
import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * created by zwj on 2018/9/6 0006
 */
public class App extends Application {
    public static  Context appContext;
    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(this);
        appContext = getApplicationContext();
        SharedPreferencesUtil.init(this,"shushan",MODE_PRIVATE);
        BleManager.getInstance().init(this);
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setSplitWriteNum(20)
                .setConnectOverTime(100000)
                .setOperateTimeout(5000);
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setAutoConnect(false)      // 连接时的autoConnect参数，可选，默认false
                .setScanTimeOut(10000)              // 扫描超时时间，可选，默认10秒；小于等于0表示不限制扫描时间
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);

    }
}
