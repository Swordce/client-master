package com.lepao.ydcgkf.mvp.view;

import com.lepao.ydcgkf.base.BaseView;
import com.lepao.ydcgkf.mvp.model.CommonModel;
import com.lepao.ydcgkf.mvp.model.LoginType2Model;
import com.lepao.ydcgkf.mvp.presenter.LoginPresenter;

/**
 * created by zwj on 2018/9/6 0006
 */
public interface ILoginView extends BaseView<LoginPresenter> {
    void loginResult(CommonModel model);
    void loginType2Result(LoginType2Model model);
}
