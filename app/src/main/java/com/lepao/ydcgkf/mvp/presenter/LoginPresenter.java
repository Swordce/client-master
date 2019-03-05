package com.lepao.ydcgkf.mvp.presenter;

import com.lepao.ydcgkf.api.ApiService;
import com.lepao.ydcgkf.base.BasePresenter;
import com.lepao.ydcgkf.mvp.model.CommonModel;
import com.lepao.ydcgkf.mvp.model.LoginType2Model;
import com.lepao.ydcgkf.mvp.view.ILoginView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * created by zwj on 2018/9/6 0006
 */
public class LoginPresenter extends BasePresenter<ILoginView> {

    public void login(String username,String pwd) {
        Disposable disposable = ApiService.getLoginApi().login(username,pwd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<CommonModel>() {
                    @Override
                    public void accept(CommonModel bean) throws Exception {
                        baseview.loginResult(bean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        baseview.requestError(throwable.getMessage());
                    }
                });
        addSubscription(disposable);
    }

    public void loginType2(String username,String pwd) {
        Disposable disposable = ApiService.getLoginApi().loginType2(username,pwd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<LoginType2Model>() {
                    @Override
                    public void accept(LoginType2Model bean) throws Exception {
                        baseview.loginType2Result(bean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        baseview.requestError(throwable.getMessage());
                    }
                });
        addSubscription(disposable);
    }
}
