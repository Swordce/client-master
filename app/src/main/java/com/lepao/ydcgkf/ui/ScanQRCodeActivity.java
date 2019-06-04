package com.lepao.ydcgkf.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lepao.ydcgkf.R;
import com.lepao.ydcgkf.base.BaseActivity;
import com.lepao.ydcgkf.base.BaseMvpActivity;
import com.lepao.ydcgkf.mvp.model.CommonModel;
import com.lepao.ydcgkf.mvp.model.UserInfoModel;
import com.lepao.ydcgkf.mvp.presenter.ScanResultPresenter;
import com.lepao.ydcgkf.mvp.view.IScanResultView;
import com.lepao.ydcgkf.utils.AppManager;
import com.lepao.ydcgkf.utils.LogUtils;
import com.lepao.ydcgkf.utils.ToastUtils;
import com.lepao.ydcgkf.widgets.popupwindow.CommonPopupWindow;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

/**
 * created by zwj on 2018/9/6 0006
 */
public class ScanQRCodeActivity extends BaseMvpActivity<ScanResultPresenter> implements QRCodeView.Delegate, IScanResultView, View.OnClickListener {

    @BindView(R.id.v_scan)
    ZXingView vScan;
    @BindView(R.id.tv_state)
    TextView tvState;
    private String sid;
    private CommonPopupWindow popupWindow;
    private boolean isWalkedOut = false;
    public static final String SCAN_TYPE = "scanType";
    public static final int SCAN_TYPE_ENTER = 0;
    public static final int SCAN_TYPE_WORKEDOUT = 1;


    @Override
    public void initView() {
        vScan.setDelegate(this);
//        vScan.startSpotAndShowRect();
    }

    @Override
    public void getData() {
        Intent intent = getIntent();
        sid = intent.getStringExtra("sid");
        int type = intent.getIntExtra(SCAN_TYPE, 0);
        if (type == 0) {
            isWalkedOut = false;
        } else {
            isWalkedOut = true;
        }
    }

    @Override
    public int getLayout() {
        return R.layout.activity_capture;
    }


    @OnClick({R.id.ll_finish, R.id.tv_state})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_finish:
                AppManager.getAppManager().finishActivity();
                break;
            case R.id.tv_state:
                popupWindow = new CommonPopupWindow.Builder(this)
                        .setView(R.layout.activity_pop)
                        .setWidthAndHeight(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT)
                        .setBackGroundLevel(0.5f)
                        .setAnimationStyle(R.style.anim_menu_pop)
                        .setViewOnclickListener(new CommonPopupWindow.ViewInterface() {
                            @Override
                            public void getChildView(final View view, int layoutResId) {
                                view.findViewById(R.id.tv_sex_man).setOnClickListener(ScanQRCodeActivity.this);
                                view.findViewById(R.id.tv_sex_woman).setOnClickListener(ScanQRCodeActivity.this);
                            }
                        })
                        .setOutsideTouchable(true)
                        .create();
//                popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.BOTTOM, 0, 0);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        vScan.startSpotAndShowRect();
    }

    @Override
    protected void onStop() {
        vScan.stopCamera(); // 关闭摄像头预览，并且隐藏扫描框
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        vScan.onDestroy(); // 销毁二维码扫描控件
        super.onDestroy();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        String[] s1 = result.split("\\?");
        String code = s1[1].substring(s1[1].indexOf("=") + 1, s1[1].length());
        if (!isWalkedOut) {
            presenter.postScanResult(sid, result);
        } else {
            presenter.walkedOut(sid, result);
        }
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        vScan.startSpotAndShowRect();
    }

    @Override
    public void setPresenter(ScanResultPresenter presenter) {
        if (presenter == null) {
            this.presenter = new ScanResultPresenter();
            this.presenter.attachView(this);
        }
    }

    @Override
    public void requestError(String msg) {
        ToastUtils.showShort(this, msg);
        vScan.startSpotAndShowRect();
    }

    @Override
    public void entranceResult(CommonModel model) {
        if (model.getCode() == 200) {
            Intent intent = new Intent(this, ScanResultActivity.class);
            intent.putExtra(ScanResultActivity.RESULT_VALUE, "验证通过，请进场");
            intent.putExtra(ScanResultActivity.RESULT_TYPE, ScanResultActivity.RESULT_SCAN_SUCCESS);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, ScanResultActivity.class);
            intent.putExtra(ScanResultActivity.RESULT_VALUE, "验证失败，请重试");
            intent.putExtra(ScanResultActivity.RESULT_TYPE, ScanResultActivity.RESULT_SCAN_FAILED);
            startActivity(intent);
        }
    }

    @Override
    public void walkedOutResult(CommonModel model) {
        if (model.getCode() == 200) {
            Intent intent = new Intent(this, ScanResultActivity.class);
            intent.putExtra(ScanResultActivity.RESULT_TYPE, ScanResultActivity.RESULT_SCAN_SUCCESS);
            intent.putExtra(ScanResultActivity.RESULT_VALUE, "验证通过，请离场");
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, ScanResultActivity.class);
            intent.putExtra(ScanResultActivity.RESULT_VALUE, "验证失败，请重试");
            intent.putExtra(ScanResultActivity.RESULT_TYPE, ScanResultActivity.RESULT_SCAN_FAILED);
            startActivity(intent);
        }
    }

    @Override
    public void userInfoResult(UserInfoModel model) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_sex_man:
                isWalkedOut = false;
                tvState.setText("入场");
                break;
            case R.id.tv_sex_woman:
                isWalkedOut = true;
                tvState.setText("出场");
                break;
        }
        popupWindow.dismiss();
    }
}
