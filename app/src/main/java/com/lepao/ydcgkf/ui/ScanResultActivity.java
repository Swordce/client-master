package com.lepao.ydcgkf.ui;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lepao.ydcgkf.R;
import com.lepao.ydcgkf.base.BaseActivity;
import com.lepao.ydcgkf.base.BaseMvpActivity;
import com.lepao.ydcgkf.content.EventMsg;
import com.lepao.ydcgkf.content.EventMsgType;
import com.lepao.ydcgkf.mvp.model.LoginType2Model;
import com.lepao.ydcgkf.mvp.model.VenueModel;
import com.lepao.ydcgkf.mvp.presenter.VenuePresenter;
import com.lepao.ydcgkf.mvp.view.VenueView;
import com.lepao.ydcgkf.utils.AppManager;
import com.lepao.ydcgkf.utils.SharedPreferencesUtil;
import com.lepao.ydcgkf.utils.ToastUtils;
import com.lepao.ydcgkf.widgets.GlideApp;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * created by zwj on 2018/9/6 0006
 */
public class ScanResultActivity extends BaseMvpActivity<VenuePresenter> implements VenueView {
    @BindView(R.id.tv_scan_result)
    TextView tvScanResult;
    @BindView(R.id.iv_scan_result)
    ImageView ivScanResult;
    @BindView(R.id.tv_result_action)
    TextView tvResultAction;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    public static final String RESULT_TYPE = "type";
    public static final String RESULT_VALUE = "value";
    public static final int RESULT_SCAN_SUCCESS = 0;
    public static final int RESULT_SCAN_FAILED = 1;
    public static final int COLLECT_SUCCESS = 2;
    public static final int COLLECT_FAILED = 3;
    public static final int COLLECT_FINGER_SUCCESS = 4;
    public static final int COLLECT_FINGER_FAILED = 5;
    private int resultType;
    private String result;
    private String sid;
    private String phone;
    private String token;
    private String cardno;

    @Override
    public void initView() {
        Intent intent = getIntent();
        resultType = intent.getIntExtra(RESULT_TYPE, 0);
        result = intent.getStringExtra(RESULT_VALUE);
        sid = intent.getStringExtra("sid");
        phone = intent.getStringExtra("phone");
        cardno = intent.getStringExtra("cardno");
        tvScanResult.setText(result);
        switch (resultType) {
            case RESULT_SCAN_SUCCESS:
                GlideApp.with(this).load(R.mipmap.ic_s).into(ivScanResult);
                tvResultAction.setText("我知道了");
                break;
            case RESULT_SCAN_FAILED:
                GlideApp.with(this).load(R.mipmap.ic_f).into(ivScanResult);
                tvResultAction.setText("立即重试");
                break;
            case COLLECT_SUCCESS:
                GlideApp.with(this).load(R.mipmap.ic_s).into(ivScanResult);
                tvResultAction.setText("指纹采集");
                break;
            case COLLECT_FAILED:
                GlideApp.with(this).load(R.mipmap.ic_f).into(ivScanResult);
                tvResultAction.setText("立即重试");
                break;
        }

        LoginType2Model.ResultBean resultBean = new Gson().fromJson(SharedPreferencesUtil.getInstance().getString("loginType2_resultBean"), LoginType2Model.ResultBean.class);
        token = resultBean.getToken();
        if (resultBean.getVenueIdList().size() == 1) {
            presenter.getVenueList(resultBean.getVenueIdList().get(0) + "", token);
        } else {
            if (resultBean.getRoleType() == 1) {
                tvTitle.setText("学校场地对外开放总管理平台");
            }
            if (resultBean.getRoleType() == 2) {
                tvTitle.setText("蜀山区学校场地对外开放平台");
            }
        }
    }

    @Override
    public void getData() {

    }

    @Override
    public int getLayout() {
        return R.layout.activity_scan_result;
    }


    @OnClick({R.id.ll_finish, R.id.tv_result_action, R.id.tv_back_home_action})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_finish:
                AppManager.getAppManager().finishActivity();
                break;
            case R.id.tv_result_action:
                switch (resultType) {
                    case 0:
                        EventBus.getDefault().postSticky(new EventMsg(EventMsgType.RESTART));
                        AppManager.getAppManager().finishActivity();
                        break;
                    case 1:
                        EventBus.getDefault().postSticky(new EventMsg(EventMsgType.RESTART));
                        AppManager.getAppManager().finishActivity();
                        break;
                    case 2:
                        Intent intent = new Intent(this, NewFingerCollectActivity.class);
                        intent.putExtra("sid", sid);
                        intent.putExtra("phone", phone);
                        intent.putExtra("cardno",cardno);
                        intent.putExtra("isWalked", false);
                        startActivity(intent);
                        AppManager.getAppManager().finishActivity();
                        break;
                    case 3:
                        AppManager.getAppManager().finishActivity();
                        break;
                }
                break;
            case R.id.tv_back_home_action:
                Intent intent = new Intent(this, HomeActivity.class);
                intent.putExtra("sid", sid);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                AppManager.getAppManager().finishActivity();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void venueListResult(VenueModel model) {
        if (model.getErrcode() == 0) {
            List<VenueModel.ResultBean> bean = model.getResult();

            for (int i = 0; i < bean.size(); i++) {
                VenueModel.ResultBean resultBean = bean.get(i);
                if (i == 0) {
                    tvTitle.setText(resultBean.getName());
                }
            }
        } else {
            tvTitle.setText("学校场地对外开放管理平台");
        }
    }

    @Override
    public void setPresenter(VenuePresenter presenter) {
        if (presenter == null) {
            this.presenter = new VenuePresenter();
            this.presenter.attachView(this);
        }
    }

    @Override
    public void requestError(String msg) {

    }
}
