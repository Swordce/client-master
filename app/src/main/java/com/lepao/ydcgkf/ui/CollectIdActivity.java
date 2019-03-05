package com.lepao.ydcgkf.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.lepao.ydcgkf.R;
import com.lepao.ydcgkf.base.BaseMvpActivity;
import com.lepao.ydcgkf.content.EventMsg;
import com.lepao.ydcgkf.content.EventMsgType;
import com.lepao.ydcgkf.mvp.model.CollectModel;
import com.lepao.ydcgkf.mvp.model.CommonModel;
import com.lepao.ydcgkf.mvp.presenter.CollectIdPresenter;
import com.lepao.ydcgkf.mvp.view.ICollectIdView;
import com.lepao.ydcgkf.ocr.OCRHttpRequest;
import com.lepao.ydcgkf.ocr.bean.OCRIdCardFaceDataJson;
import com.lepao.ydcgkf.ocr.bean.OCRIdCardResultJson;
import com.lepao.ydcgkf.utils.AppManager;
import com.lepao.ydcgkf.utils.CompareRex;
import com.lepao.ydcgkf.utils.ToastUtils;
import com.lepao.ydcgkf.widgets.GlideApp;
import com.wildma.idcardcamera.camera.CameraActivity;
import com.wildma.idcardcamera.utils.FileUtils;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

import static com.lepao.ydcgkf.content.EventMsgType.OCR_RESULT_FAILED;

/**
 * created by zwj on 2018/9/7 0007
 * 身份证信息采集,开卡
 */
public class CollectIdActivity extends BaseMvpActivity<CollectIdPresenter> implements ICollectIdView {
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_gender)
    EditText etGender;
    @BindView(R.id.et_nation)
    EditText etNation;
    @BindView(R.id.et_date)
    EditText etDate;
    @BindView(R.id.et_address)
    EditText etAddress;
    @BindView(R.id.et_id_number)
    EditText etIdNumber;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.cb_argument)
    CheckBox cbArgument;
    @BindView(R.id.iv_id)
    ImageView ivId;
    private OCRIdCardFaceDataJson faceDataJson;
    private String sid;
    private ProgressDialog mProgressDialog;
    private boolean isRequesting = false;
    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventMsg msg) {
        hideLoading();
        switch (msg.getType()) {
            case EventMsgType.OCR_RESULT:
                faceDataJson = new Gson().fromJson(msg.getValue(), OCRIdCardFaceDataJson.class);
                etName.setText(faceDataJson.getName());
                etDate.setText(faceDataJson.getBirth());
                etGender.setText(faceDataJson.getSex());
                etNation.setText(faceDataJson.getNationality());
                etIdNumber.setText(faceDataJson.getNum());
                etAddress.setText(faceDataJson.getAddress());
                break;
            case OCR_RESULT_FAILED:
                ToastUtils.showShort(this, msg.getValue());
                break;
        }
    }

    @Override
    public void initView() {

    }

    @Override
    public void getData() {
        Intent intent = getIntent();
        sid = intent.getStringExtra("sid");
    }

    @Override
    public int getLayout() {
        return R.layout.activity_collect_id;
    }

    @Override
    public void setPresenter(CollectIdPresenter presenter) {
        if (presenter == null) {
            this.presenter = new CollectIdPresenter();
            this.presenter.attachView(this);
        }
    }

    @Override
    public void requestError(String msg) {
        hideLoading();
        ToastUtils.showShort(this, msg);
    }

    @OnClick({R.id.ll_finish, R.id.ll_collect_id, R.id.ll_summit_audit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_finish:
                AppManager.getAppManager().finishActivity();
                break;
            case R.id.ll_collect_id:
                takePhoto();
                break;
            case R.id.ll_summit_audit:
                if (cbArgument.isChecked()) {
                    String names = etName.getText().toString();
                    String nation = etNation.getText().toString();
                    String gender = etGender.getText().toString();
                    String outDate = etDate.getText().toString();
                    String idNumber = etIdNumber.getText().toString();
                    String phone = etPhone.getText().toString();
                    String address = etAddress.getText().toString();
                    if (!TextUtils.isEmpty(names) && !TextUtils.isEmpty(nation) && !TextUtils.isEmpty(gender)
                            && !TextUtils.isEmpty(outDate) && !TextUtils.isEmpty(idNumber) && !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(address)) {
//                        if(CompareRex.isCellPhone(phone)) {
                        if(!isRequesting) {
                            isRequesting = true;
                            showLoading("提交中...");
                            Map<String, String> map = new HashMap<>();
                            map.put("sid", sid);
                            map.put("username", names);
                            map.put("gender", gender);
                            map.put("nationality", nation);
                            map.put("birthday", outDate);
                            map.put("address", address);
                            map.put("cardno", idNumber);
                            map.put("mobile", phone);
                            presenter.addCard(map);
                        }

//                        }else {
//                            ToastUtils.showShort(this, "手机号格式不正确");
//                        }

                    } else {
                        ToastUtils.showShort(this, "请将内容填写完整");
                    }

                } else {
                    ToastUtils.showShort(this, "请勾选同意用户办卡协议");
                }
                break;
        }
    }

    private void takePhoto() {
        AndPermission.with(this)
                .runtime()
                .permission(Permission.CAMERA, Permission.READ_EXTERNAL_STORAGE)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        CameraActivity.toCameraActivity(CollectIdActivity.this, CameraActivity.TYPE_IDCARD_FRONT);
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(@NonNull List<String> permissions) {
                        ToastUtils.showShort(CollectIdActivity.this, "相机权限已禁止，将影响部分功能正常使用！");
                    }
                })
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CameraActivity.REQUEST_CODE && resultCode == CameraActivity.RESULT_CODE) {
            final String path = CameraActivity.getImagePath(data);
            Log.e("image path",path+" ");
            if (!TextUtils.isEmpty(path)) {
                GlideApp.with(this).load(new File(path)).into(ivId);
                showLoading("识别中...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        readIdCard(new File(path), OCRHttpRequest.SIDE_FACE);
                    }
                }).start();
            }
        }
    }

    private void readIdCard(File cardFile, final String side) {
        OCRHttpRequest.readIdCardImg(cardFile, side, new OCRHttpRequest.OCRCallBack<OCRIdCardResultJson>() {
            @Override
            public void onFail(String errorMessage) {
                EventBus.getDefault().post(new EventMsg(OCR_RESULT_FAILED, errorMessage));
            }

            @Override
            public void onSuccess(OCRIdCardResultJson ocrIdCardResultJson) {
                if (OCRHttpRequest.SIDE_FACE.equals(side)) {
                    EventBus.getDefault().post(new EventMsg(EventMsgType.OCR_RESULT, ocrIdCardResultJson.getOutputs().get(0).getOutputValue().getDataValue()));
                }
            }
        });
    }


    @Override
    public void collectIdResult() {
        ToastUtils.showShort(this, "");
    }

    @Override
    public void summitAddCardResult(CollectModel response) {
        hideLoading();
        if (response.getCode() == 200) {
            Intent intent = new Intent(this, ScanResultActivity.class);
            intent.putExtra(ScanResultActivity.RESULT_TYPE, ScanResultActivity.COLLECT_SUCCESS);
            intent.putExtra(ScanResultActivity.RESULT_VALUE, "已完成身份证信息采集！\n请继续完成指纹采集!");
            intent.putExtra("sid", sid);
            intent.putExtra("cardno", etIdNumber.getText().toString());
            startActivity(intent);
            AppManager.getAppManager().finishActivity();
        } else {
            Intent intent = new Intent(this, ScanResultActivity.class);
            intent.putExtra(ScanResultActivity.RESULT_TYPE, ScanResultActivity.COLLECT_FAILED);
            intent.putExtra(ScanResultActivity.RESULT_VALUE, response.getMsg());
//            intent.putExtra("sid",sid);
            startActivity(intent);
        }

    }




    void showLoading(@NonNull String message) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(message);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    void hideLoading() {
        isRequesting = false;
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }
}
