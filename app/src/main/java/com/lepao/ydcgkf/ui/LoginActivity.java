package com.lepao.ydcgkf.ui;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.gson.Gson;
import com.lepao.ydcgkf.R;
import com.lepao.ydcgkf.base.BaseMvpActivity;
import com.lepao.ydcgkf.content.EventMsg;
import com.lepao.ydcgkf.mvp.model.CommonModel;
import com.lepao.ydcgkf.mvp.model.LoginType2Model;
import com.lepao.ydcgkf.mvp.presenter.LoginPresenter;
import com.lepao.ydcgkf.mvp.view.ILoginView;
import com.lepao.ydcgkf.utils.AppManager;
import com.lepao.ydcgkf.utils.SharedPreferencesUtil;
import com.lepao.ydcgkf.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * created by zwj on 2018/9/6 0006
 */
public class LoginActivity extends BaseMvpActivity<LoginPresenter> implements ILoginView {
    @BindView(R.id.et_login_name)
    EditText etLoginName;
    @BindView(R.id.et_login_pwd)
    EditText etLoginPwd;
    @BindView(R.id.cb_remember_pwd)
    CheckBox cbRemberPwd;
    private String sid = "";
    private boolean isLogin = false;
    private boolean isLoginType2 = false;

    @Override
    public void initView() {
        String account = SharedPreferencesUtil.getInstance().getString("account");
        String pwd = SharedPreferencesUtil.getInstance().getString("password");
        etLoginName.setText(account);
        etLoginPwd.setText(pwd);
    }

    @Override
    public void getData() {

    }

    @Override
    public int getLayout() {
        return R.layout.activity_login;
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventMsg msg) {
        if(msg.getValue().equals("loginSuccess")) {
            if(isLoginType2 && isLogin) {
                Intent intent = new Intent(this, HomeActivity.class);
                intent.putExtra("sid", sid);
                startActivity(intent);
                AppManager.getAppManager().finishActivity();
            }
        }
    }

    @OnClick(R.id.ll_btn_login)
    public void onViewClicked() {
        String username = etLoginName.getText().toString();
        String pwd = etLoginPwd.getText().toString();
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)) {
            presenter.loginType2(username,pwd);
            presenter.login(username, pwd);
        } else {
            ToastUtils.showShort(this, "请输入登录信息");
        }
    }

    @Override
    public void setPresenter(LoginPresenter presenter) {
        if (presenter == null) {
            this.presenter = new LoginPresenter();
            this.presenter.attachView(this);
        }
    }

    @Override
    public void requestError(String msg) {
        ToastUtils.showShort(this, msg);
    }

    @Override
    public void loginResult(CommonModel model) {
        if (model.getCode() == 200) {
            if(cbRemberPwd.isChecked()) {
                SharedPreferencesUtil.getInstance().putString("account", etLoginName.getText().toString());
                SharedPreferencesUtil.getInstance().putString("password", etLoginPwd.getText().toString());
            }
            sid = model.getData();
            isLogin = true;
            EventBus.getDefault().post(new EventMsg("loginSuccess"));
        } else {
            ToastUtils.showShort(this, model.getMsg());
        }
    }

    @Override
    public void loginType2Result(LoginType2Model model) {
        if(model.getErrcode() == 0) {
            isLoginType2 = true;
            LoginType2Model.ResultBean bean = model.getResult();
            SharedPreferencesUtil.getInstance().putString("loginType2_resultBean",new Gson().toJson(bean));
            EventBus.getDefault().post(new EventMsg("loginSuccess"));
        }
    }
}
