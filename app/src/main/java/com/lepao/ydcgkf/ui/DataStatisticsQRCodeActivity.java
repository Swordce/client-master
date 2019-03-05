package com.lepao.ydcgkf.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.widget.ImageView;

import com.lepao.ydcgkf.R;
import com.lepao.ydcgkf.base.BaseActivity;
import com.lepao.ydcgkf.utils.AppManager;
import com.lepao.ydcgkf.widgets.GlideApp;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;

public class DataStatisticsQRCodeActivity extends BaseActivity {
    @BindView(R.id.iv_qrcode)
    ImageView ivQrcode;

    @Override
    public void initView() {
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        Bitmap logoBitmap = BitmapFactory.decodeResource(this.getResources(), R.mipmap.lepao);
        Bitmap bitmap = QRCodeEncoder.syncEncodeQRCode(url, BGAQRCodeUtil.dp2px(this, 150), Color.parseColor("#000000"));
        GlideApp.with(this).load(bitmap).into(ivQrcode);
    }

    @Override
    public void getData() {

    }

    @Override
    public int getLayout() {
        return R.layout.activity_qrcode;
    }

    @OnClick(R.id.ll_finish)
    public void onViewClicked() {
        AppManager.getAppManager().finishActivity();
    }
}
