package com.lepao.ydcgkf.mvp.presenter;

import com.lepao.ydcgkf.api.ApiService;
import com.lepao.ydcgkf.base.BasePresenter;
import com.lepao.ydcgkf.mvp.model.CommonModel;
import com.lepao.ydcgkf.mvp.model.UserInfoModel;
import com.lepao.ydcgkf.mvp.view.IScanResultView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

/**
 * created by zwj on 2018/9/6 0006
 */
public class ScanResultPresenter extends BasePresenter<IScanResultView> {

    public void postScanResult(String sid,String code) {
        Disposable disposable = ApiService.getScanResultApi().postScanResult(sid,code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<CommonModel>() {
                    @Override
                    public void accept(CommonModel bean) throws Exception {
                        baseview.entranceResult(bean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        baseview.requestError(throwable.getMessage() +" ");
                    }
                });
        addSubscription(disposable);
    }

    public void walkedOut(String sid,String code) {
        Disposable disposable = ApiService.getScanResultApi().postWalkedOut(sid,code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<CommonModel>() {
                    @Override
                    public void accept(CommonModel bean) throws Exception {
                        baseview.walkedOutResult(bean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        baseview.requestError(throwable.getMessage() +" ");
                    }
                });
        addSubscription(disposable);
    }


    public void getUserInfo(String sid,String code) {
        Disposable disposable = ApiService.getScanResultApi().getUserInfo(sid,code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<UserInfoModel>() {
                    @Override
                    public void accept(UserInfoModel bean) throws Exception {
                        baseview.userInfoResult(bean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        baseview.requestError(throwable.getMessage() +" ");
                    }
                });
        addSubscription(disposable);
    }
}
