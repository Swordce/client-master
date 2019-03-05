package com.lepao.ydcgkf.mvp.view;

import com.lepao.ydcgkf.base.BaseView;
import com.lepao.ydcgkf.mvp.model.CommonModel;
import com.lepao.ydcgkf.mvp.model.FingerModel;
import com.lepao.ydcgkf.mvp.presenter.FingerPresenter;

/**
 * created by zwj on 2018/9/10 0010
 */
public interface FingerView extends BaseView<FingerPresenter>{
    void fingerInfoResult(CommonModel model);
    void entranceResult(CommonModel model);
    void walkedOutResult(CommonModel model);
    void matchFpResult(FingerModel model);
}
