package com.lepao.ydcgkf.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleReadCallback;
import com.clj.fastble.callback.BleScanAndConnectCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.google.gson.Gson;
import com.lepao.ydcgkf.App;
import com.lepao.ydcgkf.R;
import com.lepao.ydcgkf.base.BaseActivity;
import com.lepao.ydcgkf.base.BaseMvpActivity;
import com.lepao.ydcgkf.content.Common;
import com.lepao.ydcgkf.content.EventMsg;
import com.lepao.ydcgkf.content.EventMsgType;
import com.lepao.ydcgkf.db.FingerDao;
import com.lepao.ydcgkf.mvp.model.CommonModel;
import com.lepao.ydcgkf.mvp.model.FingerModel;
import com.lepao.ydcgkf.mvp.presenter.FingerPresenter;
import com.lepao.ydcgkf.mvp.view.FingerView;
import com.lepao.ydcgkf.ui.reader.BluetoothReaderService;
import com.lepao.ydcgkf.ui.reader.DeviceListActivity;
import com.lepao.ydcgkf.utils.AppManager;
import com.lepao.ydcgkf.utils.CompareRex;
import com.lepao.ydcgkf.utils.RetrofitFactory;
import com.lepao.ydcgkf.utils.SharedPreferencesUtil;
import com.lepao.ydcgkf.utils.ToastUtils;
import com.marcoscg.dialogsheet.DialogSheet;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *  * 采用蓝牙连接指纹机,正在使用
 */

public class NewFingerCollectActivity extends BaseMvpActivity<FingerPresenter> implements FingerView {

    private static final String TAG = "BluetoothReader";
    private static final boolean D = true;

    private final static byte CMD_PASSWORD = 0x01;    //Password
    private final static byte CMD_ENROLHOST = 0x07;    //Enroll to Host
    private final static byte CMD_CAPTUREHOST = 0x08;    //Caputre to Host

    private final static byte CMD_GETIMAGE = 0x30;
    @BindView(R.id.tv_finger_tip)
    TextView tvFingerTip;
    @BindView(R.id.tv_state)
    TextView tvTitle;
    @BindView(R.id.et_phone)
    EditText etPhone;

    private byte mDeviceCmd = 0x00;
    private boolean mIsWork = false;
    private byte mCmdData[] = new byte[10240];
    private int mCmdSize = 0;

    private Timer mTimerTimeout = null;
    private TimerTask mTaskTimeout = null;
    private Handler mHandlerTimeout;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_WRITE = 3;
    public static final String TOAST = "toast";

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;


    private String mConnectedDeviceName = null;
    private StringBuffer mOutStringBuffer;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothReaderService mReaderService = null;

    public byte mRefData[] = new byte[512];
    public byte mMatData[] = new byte[512];
    private boolean isWalked = false;
    private String sid;
    private boolean isWalkedout;
    private ProgressDialog mProgressDialog;
    private BleDevice device;
    private UUID uuid_service;
    private UUID uuid_chara;
    private int tempCount = 1;
    private StringBuilder tempFpBuilder = new StringBuilder();
    private boolean isRequsting = false;//跳转到结果页，然后回到界面
    private String cardno;
    private DialogSheet sheet;

    private MediaPlayer mediaPlayer;
    private AssetManager assetManager;

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else {
            setupChat();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(EventMsg msg) {
        switch (msg.getType()) {
            case EventMsgType.RESTART:
//                tvTitle.setText("点击连接指纹仪");
//                setupChat();
                sendCmdType();
                tvFingerTip.setText("请将手指放到指纹采集器上");
                EventBus.getDefault().removeStickyEvent(msg);
                break;
        }
    }


    @Override
    public void initView() {
        assetManager = getAssets();
        Intent intent = getIntent();
        isWalked = intent.getBooleanExtra("isWalked", false);
        sid = intent.getStringExtra("sid");
        cardno = intent.getStringExtra("cardno");
        isWalkedout = intent.getBooleanExtra("isWalkedOut", false);
        if (!isWalked) {
            etPhone.setVisibility(View.VISIBLE);
            etPhone.setHint("请输入身份证号");
            etPhone.setText(cardno);
            tvFingerTip.setText("请将手指放到指纹采集器上，连续按两次");
        } else {
            etPhone.setVisibility(View.GONE);
            tvFingerTip.setText("请将手指放到指纹采集器上");
        }
//        scanDevice();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "请确认蓝牙功能是否正常", Toast.LENGTH_LONG).show();
            AppManager.getAppManager().finishActivity();
            return;
        }


    }

    @Override
    public void getData() {
//        Intent serverIntent = new Intent(this, DeviceListActivity.class);
//        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
//        Map<String,String> map = new HashMap<>();
//        map.put("fingerPrint","AwFgFoQA//7ABoACgAKAAoACgAKAAoACgAKAAoACgAKAAoACgALAAgAAAAAAAAAAAAAAAAAAAAAwlZqeSJZZnl2jVl41phjeLrCX/jS3Vn5HOlZeSIvbH2kO1L8WpRnfGy5YP1wwVL9UMqr/IzcW31q31P9hQdQfaKqVPG0tE7w5uqyaOT4VekK/0xo8QaqzAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=");
//        presenter.matchFP(RetrofitFactory.getRequestBody(new Gson().toJson(map)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mReaderService != null) {
            if (mReaderService.getState() == BluetoothReaderService.STATE_NONE) {
                mReaderService.start();
            }
        }
//        if(isRestart) {
//            sendCmdType();
//        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReaderService != null) mReaderService.stop();
        TimeOutStop();
        stopMediaplayer();
//        BleManager.getInstance().disconnectAllDevice();
    }

    @Override
    public int getLayout() {
        return R.layout.activity_finger_collect;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    mReaderService.connect(device);

                    SharedPreferences sp;
                    sp = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor edit = sp.edit();
                    edit.putString("device", address);
                    edit.commit();
                }
                break;
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    setupChat();
                } else {
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    AppManager.getAppManager().finishActivity();
                }
        }
    }

    private void setupChat() {
        showLoading("连接中...");
        mReaderService = new BluetoothReaderService(this, mHandler);
        SharedPreferences sp;
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        String address = sp.getString("device", "");
        if (address.length() > 2) {
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
            mReaderService.connect(device);
        } else {
            Intent serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
        }
    }

    private void SendCommand(byte cmdid, byte[] data, int size) {
        if (mIsWork) return;

        int sendsize = 9 + size;
        byte[] sendbuf = new byte[sendsize];
        sendbuf[0] = 'F';
        sendbuf[1] = 'T';
        sendbuf[2] = 0;
        sendbuf[3] = 0;
        sendbuf[4] = cmdid;
        sendbuf[5] = (byte) (size);
        sendbuf[6] = (byte) (size >> 8);
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                sendbuf[7 + i] = data[i];
            }
        }
        int sum = calcCheckSum(sendbuf, (7 + size));
        sendbuf[7 + size] = (byte) (sum);
        sendbuf[8 + size] = (byte) (sum >> 8);

        mIsWork = true;
        TimeOutStart();
        mDeviceCmd = cmdid;
        mCmdSize = 0;

        if (mReaderService != null) {
            mReaderService.write(sendbuf);
        }
    }

    private int calcCheckSum(byte[] buffer, int size) {
        int sum = 0;
        for (int i = 0; i < size; i++) {
            sum = sum + buffer[i];
        }
        return (sum & 0x00ff);
    }

    /**
     * 超时重新发送指令
     */

    @SuppressLint("HandlerLeak")
    public void TimeOutStart() {
        if (mTimerTimeout != null) {
            return;
        }
        mTimerTimeout = new Timer();
        mHandlerTimeout = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                TimeOutStop();
                Log.e("mIsWork", mIsWork + " ");
                if (mIsWork) {
                    tvFingerTip.setText("指纹采集超时，请重按手指~");
                    mIsWork = false;
                    sendCmdType();
                }
                super.handleMessage(msg);
            }
        };
        mTaskTimeout = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                mHandlerTimeout.sendMessage(message);
            }
        };
        mTimerTimeout.schedule(mTaskTimeout, 10000, 10000);
    }

    public void TimeOutStop() {
        if (mTimerTimeout != null) {
            mTimerTimeout.cancel();
            mTimerTimeout = null;
            mTaskTimeout.cancel();
            mTaskTimeout = null;
        }
    }


    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_DEVICE_NAME:
                    hideLoading();
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    sendCmdType();
                    tvTitle.setText("已连接" + mConnectedDeviceName);
                    Toast.makeText(getApplicationContext(), "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    hideLoading();
                    if (!msg.getData().getString(TOAST).contains("lost")) {
                        tvTitle.setText("点击连接指纹仪");
                        Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    ReceiveCommand(readBuf, msg.arg1);
                    break;
            }
        }
    };

    private void memcpy(byte[] dstbuf, int dstoffset, byte[] srcbuf, int srcoffset, int size) {
        for (int i = 0; i < size; i++) {
            dstbuf[dstoffset + i] = srcbuf[srcoffset + i];
        }
    }

    private void ReceiveCommand(byte[] databuf, int datasize) {
        if (mDeviceCmd != CMD_GETIMAGE) {
            memcpy(mCmdData, mCmdSize, databuf, 0, datasize);
            mCmdSize = mCmdSize + datasize;
            int totalsize = (byte) (mCmdData[5]) + ((mCmdData[6] << 8) & 0xFF00) + 9;
            if (mCmdSize >= totalsize) {
                mCmdSize = 0;
                mIsWork = false;
                if ((mCmdData[0] == 'F') && (mCmdData[1] == 'T')) {
                    switch (mCmdData[4]) {
                        case CMD_PASSWORD: {
                        }
                        break;
                        case CMD_ENROLHOST: {

                            int size = (byte) (mCmdData[5]) + ((mCmdData[6] << 8) & 0xFF00) - 1;
                            if (mCmdData[7] == 1) {
                                if (!TextUtils.isEmpty(etPhone.getText().toString())) {
//                                    if (CompareRex.isCellPhone(etPhone.getText().toString())) {
                                    if (!isRequsting) {
                                        isRequsting = true;
                                        showLoading("上传中...");
                                        memcpy(mRefData, 0, mCmdData, 8, size);
                                        tvFingerTip.setText("指纹采集成功~");
//                                            SharedPreferencesUtil.getInstance().putString(etPhone.getText().toString(), Base64.encodeToString(mRefData, Base64.NO_WRAP));
                                        Map<String, String> map = new HashMap<>();
                                        map.put("sid", sid);
                                        map.put("mobile", etPhone.getText().toString());
                                        map.put("fp", Base64.encodeToString(mRefData, Base64.NO_WRAP));
                                        presenter.saveFPByCardNO(map);

//                                            if(etPhone.getText().toString().length() > 11) {
//                                            }else {
//                                                presenter.getFingerInfo(map);
//                                            }
                                    }

//                                    } else {
//                                        sendCmdType();
//                                        ToastUtils.showShort(NewFingerCollectActivity.this, "手机号格式不正确~");
//                                    }

                                } else {
                                    sendCmdType();
                                    ToastUtils.showShort(NewFingerCollectActivity.this, "请输入身份证号");
                                }
                            } else {
                                tvFingerTip.setText("指纹采集失败，请重按手指~");
                                sendCmdType();
                            }
                        }
                        break;
                        case CMD_CAPTUREHOST: {
                            int size = (byte) (mCmdData[5]) + ((mCmdData[6] << 8) & 0xFF00) - 1;
                            if (mCmdData[7] == 1) {
                                if (!isRequsting) {
                                    tvFingerTip.setText("指纹采集成功~");
                                    isRequsting = true;
                                    showLoading("匹配中...");
                                    memcpy(mMatData, 0, mCmdData, 8, size);
                                    Map<String, String> map = new HashMap<>();
                                    map.put("fingerPrint", Base64.encodeToString(mMatData, Base64.NO_WRAP));
                                    presenter.matchFP(RetrofitFactory.getRequestBody(new Gson().toJson(map)));
                                }

                            } else {
                                tvFingerTip.setText("指纹采集失败，请重按手指~");
                                sendCmdType();
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    private void sendCmdType() {
        if (isWalked) {
            SendCommand(CMD_CAPTUREHOST, null, 0);//指纹采集
        } else {
            SendCommand(CMD_ENROLHOST, null, 0);
        }
    }

    @OnClick({R.id.ll_finish, R.id.tv_state, R.id.tv_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_finish:
                AppManager.getAppManager().finishActivity();
                break;
            case R.id.tv_state:
                setupChat();
                break;
            case R.id.tv_right:
                Intent serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                break;
        }
    }


    @Override
    public void fingerInfoResult(CommonModel model) {
        hideLoading();
        if (model.getCode() == 200) {
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
                sendCmdType();
                ToastUtils.showShort(this, model.getMsg());
            }

        }
        isRequsting = false;
    }

    @Override
    public void entranceResult(CommonModel model) {
        hideLoading();
        if (model.getCode() == 200) {
            playMp3("voice_go");
            sendCmdType();
            sheet = new DialogSheet(this)
                    .setTitle("识别结果")
                    .setMessage("验证通过，请入场")
                    .setPositiveButton("我知道了", new DialogSheet.OnPositiveClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendCmdType();
                            sheet.dismiss();
                        }
                    })
                    .setNegativeButton("回到首页", new DialogSheet.OnNegativeClickListener() {
                        @Override
                        public void onClick(View v) {
                            AppManager.getAppManager().finishActivity();
                        }
                    })
                    .setCancelable(false);
//            sheet.show();
        } else {
            playMp3("voice_refuse");
            sendCmdType();
            sheet = new DialogSheet(this)
                    .setTitle("识别结果")
                    .setMessage("验证失败，请重试")
                    .setPositiveButton("我知道了", new DialogSheet.OnPositiveClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendCmdType();
                            sheet.dismiss();
                        }
                    })
                    .setCancelable(false);
//            sheet.show();
        }
        isRequsting = false;

    }

    @Override
    public void walkedOutResult(CommonModel model) {
        hideLoading();
        if (model.getCode() == 200) {
            playMp3("voice_go");
            sendCmdType();
            sheet = new DialogSheet(this)
                    .setTitle("识别结果")
                    .setMessage("验证通过，请离场")
                    .setPositiveButton("我知道了", new DialogSheet.OnPositiveClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendCmdType();
                            sheet.dismiss();
                        }
                    })
                    .setNegativeButton("回到首页", new DialogSheet.OnNegativeClickListener() {
                        @Override
                        public void onClick(View v) {
                            AppManager.getAppManager().finishActivity();
                        }
                    })
                    .setCancelable(false);
//            sheet.show();
        } else {
            playMp3("voice_refuse");
            sendCmdType();
            sheet = new DialogSheet(this)
                    .setTitle("识别结果")
                    .setMessage("验证失败，请重试")
                    .setPositiveButton("我知道了", new DialogSheet.OnPositiveClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendCmdType();
                            sheet.dismiss();
                        }
                    })
                    .setCancelable(false);
//            sheet.show();
        }
        isRequsting = false;

    }

    @Override
    public void matchFpResult(FingerModel model) {
        if (model.getCode() == 200) {
            if (!isWalkedout) {
                presenter.entence(sid, model.getData().getSerialNumber());
            } else {
                presenter.walkedOut(sid, model.getData().getSerialNumber());
            }
        } else {
            hideLoading();
            playMp3("voice_refuse");
            ToastUtils.showShort(this, model.getMsg());
            sendCmdType();
        }
        isRequsting = false;

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
        hideLoading();
        sendCmdType();
        tvFingerTip.setText("指纹采集失败，请重按手指~");
        ToastUtils.showShort(this, msg);
        isRequsting = false;
    }


    private void playMp3(String fileName) {
        try {
            AssetFileDescriptor afd = assetManager.openFd("voice/" + fileName + ".mp3");
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopMediaplayer() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer = null;
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
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }
}
