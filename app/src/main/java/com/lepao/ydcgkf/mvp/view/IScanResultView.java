package com.lepao.ydcgkf.mvp.view;

import com.lepao.ydcgkf.base.BaseView;
import com.lepao.ydcgkf.mvp.model.CommonModel;
import com.lepao.ydcgkf.mvp.model.UserInfoModel;
import com.lepao.ydcgkf.mvp.presenter.ScanResultPresenter;

/**
 * created by zwj on 2018/9/6 0006
 */
public interface IScanResultView extends BaseView<ScanResultPresenter>{
    void entranceResult(CommonModel model);
    void walkedOutResult(CommonModel model);
    void userInfoResult(UserInfoModel model);
}
