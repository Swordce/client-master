package com.lepao.ydcgkf.mvp.view;

import com.lepao.ydcgkf.base.BaseView;
import com.lepao.ydcgkf.mvp.model.CollectModel;
import com.lepao.ydcgkf.mvp.model.CommonModel;
import com.lepao.ydcgkf.mvp.presenter.CollectIdPresenter;

/**
 * created by zwj on 2018/9/7 0007
 */
public interface ICollectIdView extends BaseView<CollectIdPresenter> {
    void collectIdResult();
    void summitAddCardResult(CollectModel response);
}
