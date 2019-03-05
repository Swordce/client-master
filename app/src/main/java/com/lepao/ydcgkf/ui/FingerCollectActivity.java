package com.lepao.ydcgkf.ui;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.lepao.ydcgkf.R;
import com.lepao.ydcgkf.base.BaseMvpActivity;
import com.lepao.ydcgkf.base.BasePresenter;
import com.lepao.ydcgkf.db.FingerDao;
import com.lepao.ydcgkf.mvp.model.CommonModel;
import com.lepao.ydcgkf.mvp.model.FingerModel;
import com.lepao.ydcgkf.mvp.presenter.FingerPresenter;
import com.lepao.ydcgkf.mvp.view.FingerView;
import com.lepao.ydcgkf.utils.AppManager;
import com.lepao.ydcgkf.utils.ToastUtils;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.zkteco.android.biometric.ZKSensorHelper;
import com.zkteco.android.biometric.core.device.ParameterHelper;
import com.zkteco.android.biometric.core.device.TransportType;
import com.zkteco.android.biometric.core.device.usb.ZKUSBHOSTAPIService;
import com.zkteco.android.biometric.core.utils.LogHelper;
import com.zkteco.android.biometric.core.utils.ToolUtils;
import com.zkteco.android.biometric.module.fingerprintreader.FingerprintCaptureListener;
import com.zkteco.android.biometric.module.fingerprintreader.FingerprintSensor;
import com.zkteco.android.biometric.module.fingerprintreader.FingprintFactory;
import com.zkteco.android.biometric.module.fingerprintreader.ZKFingerService;
import com.zkteco.android.biometric.module.fingerprintreader.ZKIDFprService;
import com.zkteco.android.biometric.module.fingerprintreader.exception.FingerprintException;
import com.zkteco.zkfinger.ZKFingerLibSetting;

import java.sql.Blob;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * created by zwj on 2018/9/9 0009
 * 采用usb连接指纹机,已弃用
 */
public class FingerCollectActivity extends BaseMvpActivity<FingerPresenter> implements FingerView, FingerprintCaptureListener {
    @BindView(R.id.iv_finger_capture)
    ImageView ivFingerCapture;
    @BindView(R.id.tv_finger_tip)
    TextView tvFingerTip;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.tv_state)
    TextView tvState;

    private static final int VID = 6997;
    private static final int PID = 288;
    private boolean bstart = false;
    private boolean isRegister = false;
    private int uid = 1;
    private byte[][] regtemparray = new byte[3][2048];  //register template buffer array
    private int enrollidx = 0;
    private byte[] lastRegTemp = new byte[2048];
    private FingerprintSensor fingerprintSensor = null;

    private final String ACTION_USB_PERMISSION = "com.zkteco.silkiddemo.USB_PERMISSION";
    private String strBase64;
    private String sid;
    private String phone;
    private boolean isWalkedout = false;
    private ProgressDialog mProgressDialog;

    private BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        LogHelper.i("have permission!");
                    } else {
                        LogHelper.e("not permission!");
                    }
                }
            }
        }
    };

    @Override
    public void initView() {
        Intent intent = getIntent();
        isRegister = intent.getBooleanExtra("isRegister", false);
        sid = intent.getStringExtra("sid");
        phone = intent.getStringExtra("phone");
        isWalkedout = intent.getBooleanExtra("isWalkedOut", false);
        if (!TextUtils.isEmpty(phone)) {
            etPhone.setText(phone);
        }

        if (isRegister) {
            etPhone.setVisibility(View.VISIBLE);
            tvState.setText("指纹录入");
        } else {
            etPhone.setVisibility(View.GONE);
            tvState.setText("");
        }

        initDevice();
    }

    @Override
    public void getData() {

    }

    @Override
    public int getLayout() {
        return R.layout.activity_finger_collect;
    }


    @Override
    public void setPresenter(FingerPresenter presenter) {
        if (presenter == null) {
            this.presenter = new FingerPresenter();
            this.presenter.attachView(this);
        }
    }

    @Override
    public void requestError(String msg) {
        Log.e("fingerServices",msg +"  request");
        hideLoading();
    }


    @OnClick({R.id.ll_finish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_finish:
                AppManager.getAppManager().finishActivity();
                break;
        }
    }

    private void initDevice() {
        UsbManager musbManager = (UsbManager) this.getSystemService(Context.USB_SERVICE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
        Context context = this.getApplicationContext();
        context.registerReceiver(mUsbReceiver, filter);
        int size = musbManager.getDeviceList().size();
        if (size > 0) {
            for (UsbDevice device : musbManager.getDeviceList().values()) {
                if (device.getVendorId() == VID && device.getProductId() == PID) {
                    if (!musbManager.hasPermission(device)) {
                        Intent intent = new Intent(ACTION_USB_PERMISSION);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
                        musbManager.requestPermission(device, pendingIntent);
                        startFingerprintSensor();
                    }
                    startFingerprintSensor();
                    showLoading("初始化指纹设备...");
                    startCollect();
                    List<FingerDao> daoList = new Select().from(FingerDao.class).queryList();
                    if (daoList.size() > 0) {
                        for (FingerDao dao : daoList) {
                            byte[] bytes = Base64.decode(dao.fingerTemplate, Base64.DEFAULT);
                            String fingerId = dao.fingerId;
                            ZKFingerService.save(bytes, fingerId);
                        }
                    }
                    hideLoading();
                }
            }
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("温馨提示")
                    .setMessage("未连接到指纹设备")
                    .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            AppManager.getAppManager().finishActivity();
                        }
                    })
                    .setCancelable(false)
                    .show();
        }

    }

    private void startFingerprintSensor() {
        LogHelper.setLevel(Log.WARN);
        Map fingerprintParams = new HashMap();
        fingerprintParams.put(ParameterHelper.PARAM_KEY_VID, VID);
        fingerprintParams.put(ParameterHelper.PARAM_KEY_PID, PID);
        fingerprintSensor = FingprintFactory.createFingerprintSensor(this, TransportType.USB, fingerprintParams);
    }

    private void stopFingerCollect() {
        if (fingerprintSensor != null) {
            try {
                if (bstart) {
                    fingerprintSensor.stopCapture(0);
                    bstart = false;
                    fingerprintSensor.close(0);
                }
            } catch (FingerprintException e) {
                Log.e("FingerCollect", e.getMessage());
            }
        }

    }

    private void startCollect() {
        if (bstart) return;
        if (fingerprintSensor != null) {
            try {
                fingerprintSensor.open(0);
                fingerprintSensor.setFingerprintCaptureListener(0, this);
                fingerprintSensor.startCapture(0);
                bstart = true;
            } catch (FingerprintException e) {
                Log.e("FingerCollect", e.getMessage());
            }
        }

    }

    @Override
    protected void onDestroy() {
        stopFingerCollect();
        super.onDestroy();
    }

    @Override
    public void captureOK(final byte[] fpImage) {
        final int width = fingerprintSensor.getImageWidth();
        final int height = fingerprintSensor.getImageHeight();
        if (enrollidx < 4) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (null != fpImage) {
                        ToolUtils.outputHexString(fpImage);
                        Bitmap bitmapFp = ToolUtils.renderCroppedGreyScaleBitmap(fpImage, width, height);
                        ivFingerCapture.setImageBitmap(bitmapFp);
                    }
                }
            });
        }
    }

    @Override
    public void captureError(FingerprintException e) {
//        Log.e("fingerServices","error msg = " +  e.getMessage());
    }

    @Override
    public void extractOK(byte[] fpTemplate) {
        final byte[] tmpBuffer = fpTemplate;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isRegister) {
                    if (!TextUtils.isEmpty(etPhone.getText().toString())) {
                        byte[] bufids = new byte[256];
                        int ret = ZKFingerService.identify(tmpBuffer, bufids, 55, 1);
                        if (ret > 0) {
//                        isRegister = false;
                            enrollidx = 0;
                            ToastUtils.showShort(FingerCollectActivity.this, "您的指纹已采集,轻勿重复录入");
                            return;
                        }

                        if (enrollidx > 0 && ZKFingerService.verify(regtemparray[enrollidx - 1], tmpBuffer) <= 0) {
//                            tvFingerTip.setText("需使用同一个手指采集3次~");
                            return;
                        }
                        if(enrollidx < regtemparray.length) {
                            System.arraycopy(tmpBuffer, 0, regtemparray[enrollidx], 0, 2048);
                        }
                        enrollidx++;
                        Log.e("fingerServices", enrollidx + "");
                        if (enrollidx == 3) {
                            try{
                                byte[] regTemp = new byte[2048];
                                ret = ZKFingerService.merge(regtemparray[0], regtemparray[1], regtemparray[2], regTemp);
                                Log.e("fingerServices", "ret = " + ret);
                                Log.e("fingerServices", "le = " + regtemparray[0].length +" " + regtemparray[1].length +" " + regtemparray[2].length);
                                if (ret > 0) {
                                    showLoading("录入中...");
                                    System.arraycopy(regTemp, 0, lastRegTemp, 0, ret);
                                    strBase64 = Base64.encodeToString(regTemp, 0, ret, Base64.NO_WRAP);
                                    Map<String, String> map = new HashMap<>();
                                    map.put("sid", sid);
                                    map.put("mobile", etPhone.getText().toString());
                                    map.put("fp", strBase64);
                                    presenter.saveFPByCardNO(map);
                                }
                            }catch (Exception e) {
                                Log.e("fingerServices",e.getMessage() +" ");
                            }
                        }else {
                            if(enrollidx >= 4) {
                                enrollidx = 0;
                                tvFingerTip.setText("采集失败,请重按手指~");
                            }else {
                                tvFingerTip.setText("你需要按" + (3-enrollidx)+"次手指");
                            }
                        }
//                        isRegister = false;
                    }else {
                        ToastUtils.showShort(FingerCollectActivity.this,"请输入手机号码");
                    }

                } else {//查询
                    Log.e("fingerServices", "开始查询");
                    byte[] bufids = new byte[256];
                    int ret = ZKFingerService.identify(tmpBuffer, bufids, 55, 1);
                    Log.e("fingerServices", "查询结果=" + ret);
                    if (ret > 0) {
                        String strRes[] = new String(bufids).split("\t");
                        Log.e("fingerServices", "id=" + strRes[0]);
                        if (!isWalkedout) {
                            presenter.entence(sid, strRes[0]);
                        } else {
                            presenter.walkedOut(sid, strRes[0]);
                        }
                    } else {
                        new AlertDialog.Builder(FingerCollectActivity.this)
                                .setTitle("温馨提示")
                                .setMessage("未匹配到您的指纹信息,请重试!")
                                .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                                .setCancelable(false)
                                .show();
                    }
                }
            }
        });
    }

    @Override
    public void extractError(int i) {
        Log.e("fingerServices", "errcode=" + i + " ");
    }

    @Override
    public void fingerInfoResult(CommonModel model) {
        hideLoading();
        if (model.getCode() == 200) {
            FingerDao dao = new FingerDao();
            dao.fingerId = model.getData();
            dao.fingerTemplate = strBase64;
            dao.insert();
            Intent intent = new Intent(this, ScanResultActivity.class);
            startActivity(intent);
            AppManager.getAppManager().finishActivity();
        } else {
            if (model.getMsg().contains("存在")) {
                Intent intent = new Intent(this, CollectIdActivity.class);
                intent.putExtra("sid", sid);
                startActivity(intent);
                AppManager.getAppManager().finishActivity();
            } else {
                enrollidx = 0;
                isRegister = true;
                ToastUtils.showShort(this, model.getMsg());
            }

        }
    }

    @Override
    public void entranceResult(CommonModel model) {
        if (model.getCode() == 200) {
            Intent intent = new Intent(this, ScanResultActivity.class);
            intent.putExtra(ScanResultActivity.RESULT_VALUE, "验证通过，请进场");
            intent.putExtra(ScanResultActivity.RESULT_TYPE, ScanResultActivity.RESULT_SCAN_SUCCESS);
            startActivity(intent);
            AppManager.getAppManager().finishActivity();
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
            AppManager.getAppManager().finishActivity();
        } else {
            Intent intent = new Intent(this, ScanResultActivity.class);
            intent.putExtra(ScanResultActivity.RESULT_VALUE, "验证失败，请重试");
            intent.putExtra(ScanResultActivity.RESULT_TYPE, ScanResultActivity.RESULT_SCAN_FAILED);
            startActivity(intent);
        }
    }

    @Override
    public void matchFpResult(FingerModel model) {

    }

    void showLoading(@NonNull String message) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(message);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    void hideLoading() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }
}
