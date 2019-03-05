package com.lepao.ydcgkf.mvp.presenter;

import android.util.Log;

import com.lepao.ydcgkf.api.ApiService;
import com.lepao.ydcgkf.base.BasePresenter;
import com.lepao.ydcgkf.mvp.model.CommonModel;
import com.lepao.ydcgkf.mvp.model.FingerModel;
import com.lepao.ydcgkf.mvp.view.FingerView;

import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

/**
 * created by zwj on 2018/9/10 0010
 */
public class FingerPresenter extends BasePresenter<FingerView> {

    public void matchFP(RequestBody body) {
        Disposable disposable = ApiService.getFingerApi().matchFinger(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<FingerModel>() {
                    @Override
                    public void accept(FingerModel bean) throws Exception {
                        baseview.matchFpResult(bean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        baseview.requestError(throwable.getMessage());
                    }
                });
        addSubscription(disposable);
    }

    public void getFingerInfo(Map<String,String> map) {
        Disposable disposable = ApiService.getFingerApi().getFingerInfo(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<CommonModel>() {
                    @Override
                    public void accept(CommonModel bean) throws Exception {
                        baseview.fingerInfoResult(bean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        baseview.requestError(throwable.getMessage());
                    }
                });
        addSubscription(disposable);
    }

    public void saveFPByCardNO(Map<String,String> map) {
        Disposable disposable = ApiService.getFingerApi().saveFPByCardNO(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<CommonModel>() {
                    @Override
                    public void accept(CommonModel bean) throws Exception {
                        baseview.fingerInfoResult(bean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        baseview.requestError(throwable.getMessage());
                    }
                });
        addSubscription(disposable);
    }


    public void entence(String sid,String code) {
        Disposable disposable = ApiService.getFingerApi().postScanResult(sid,code)
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
        Disposable disposable = ApiService.getFingerApi().postWalkedOut(sid,code)
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
}
