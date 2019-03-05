package com.lepao.ydcgkf.mvp.presenter;

import com.lepao.ydcgkf.api.ApiService;
import com.lepao.ydcgkf.base.BasePresenter;
import com.lepao.ydcgkf.mvp.model.CollectModel;
import com.lepao.ydcgkf.mvp.model.CommonModel;
import com.lepao.ydcgkf.mvp.view.ICollectIdView;

import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

/**
 * created by zwj on 2018/9/7 0007
 */
public class CollectIdPresenter extends BasePresenter<ICollectIdView> {

    public void collect(RequestBody body,String header) {
        Disposable disposable = ApiService.getCollectIdApi().getIdInfo(body,header)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String bean) throws Exception {
                        baseview.collectIdResult();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        baseview.requestError(throwable.getMessage());
                    }
                });
        addSubscription(disposable);
    }

    public void addCard(Map<String,String> fieldMap) {
        Disposable disposable = ApiService.getCollectIdApi().addCard(fieldMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<CollectModel>() {
                    @Override
                    public void accept(CollectModel bean) throws Exception {
                        baseview.summitAddCardResult(bean);
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
