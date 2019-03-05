package com.lepao.ydcgkf.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.jaeger.library.StatusBarUtil;
import com.lepao.ydcgkf.R;
import com.lepao.ydcgkf.utils.AppManager;

import butterknife.ButterKnife;


public abstract class BaseActivity extends AppCompatActivity {
    protected String TAG = getClass().getSimpleName();
    protected int total;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        setContentView(getLayout());
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.white),120);
        ButterKnife.bind(this);
        initView();
        getData();
    }

    /**
     * 初始化布局
     */
    public abstract void initView();

    /**
     * 获取数据
     */
    public abstract void getData();

    /**
     * 设置布局文件id
     *
     * @return
     */
    public abstract int getLayout();

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AppManager.getAppManager().finishActivity();
        }
        return super.onKeyDown(keyCode, event);
    }

}
