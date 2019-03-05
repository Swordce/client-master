package com.lepao.ydcgkf.mvp.view;

import com.lepao.ydcgkf.base.BaseView;
import com.lepao.ydcgkf.mvp.model.VenueModel;
import com.lepao.ydcgkf.mvp.presenter.VenuePresenter;

public interface VenueView extends BaseView<VenuePresenter> {
    void venueListResult(VenueModel model);
}
