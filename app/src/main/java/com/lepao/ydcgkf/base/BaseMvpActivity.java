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

/**
 * Activity的基类
 * Created by wangwenzhang on 2017/11/9.
 */

public abstract class BaseMvpActivity<P extends BasePresenter>extends AppCompatActivity implements BaseView<P> {
    protected P presenter;
    protected String TAG=getClass().getSimpleName();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        AppManager.getAppManager().addActivity(this);
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.white),120);
        ButterKnife.bind(this);
        setPresenter(presenter);
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
     * @return
     */
    public abstract int getLayout();

    /**
     * 布局销毁 调用presenter置空view，防止内存溢出
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter!=null){
            presenter.detachView();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AppManager.getAppManager().finishActivity();
        }
        return super.onKeyDown(keyCode, event);
    }
}
