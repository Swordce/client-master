package com.lepao.ydcgkf.mvp.presenter;

import com.lepao.ydcgkf.api.ApiService;
import com.lepao.ydcgkf.base.BasePresenter;
import com.lepao.ydcgkf.base.BaseView;
import com.lepao.ydcgkf.mvp.model.LoginType2Model;
import com.lepao.ydcgkf.mvp.model.VenueModel;
import com.lepao.ydcgkf.mvp.view.VenueView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class VenuePresenter extends BasePresenter<VenueView> {

    public void getVenueList(String idList,String token) {
        Disposable disposable = ApiService.getVenueApi().getVenueList(idList,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<VenueModel>() {
                    @Override
                    public void accept(VenueModel bean) throws Exception {
                        baseview.venueListResult(bean);
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
