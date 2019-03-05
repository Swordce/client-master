package com.lepao.ydcgkf.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lepao.ydcgkf.R;
import com.lepao.ydcgkf.base.BaseMvpActivity;
import com.lepao.ydcgkf.mvp.model.LoginType2Model;
import com.lepao.ydcgkf.mvp.model.VenueModel;
import com.lepao.ydcgkf.mvp.presenter.VenuePresenter;
import com.lepao.ydcgkf.mvp.view.VenueView;
import com.lepao.ydcgkf.utils.AppManager;
import com.lepao.ydcgkf.utils.SharedPreferencesUtil;
import com.lepao.ydcgkf.utils.ToastUtils;
import com.pgyersdk.update.DownloadFileListener;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;
import com.pgyersdk.update.javabean.AppBean;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * created by zwj on 2018/9/6 0006
 */
public class HomeActivity extends BaseMvpActivity<VenuePresenter> implements VenueView {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    private String sid;
    private boolean isArmSupport = false;
    private long mExitTime;
    private String token = "";
    private ProgressDialog mProgressDialog;

    @Override
    public void initView() {

    }

    @Override
    public void getData() {
        Intent intent = getIntent();
        sid = intent.getStringExtra("sid");
        LoginType2Model.ResultBean resultBean = new Gson().fromJson(SharedPreferencesUtil.getInstance().getString("loginType2_resultBean"), LoginType2Model.ResultBean.class);
        token = resultBean.getToken();
        if (resultBean.getVenueIdList().size() == 1) {
            presenter.getVenueList(resultBean.getVenueIdList().get(0) + "", token);
        } else {
            if (resultBean.getRoleType() == 1) {
                tvTitle.setText("学校场地对外开放总管理平台");
            } if (resultBean.getRoleType() == 2) {
                tvTitle.setText("蜀山区学校场地对外开放平台");
            }
        }

        new PgyUpdateManager.Builder()
                .setForced(true)                //设置是否强制更新,非自定义回调更新接口此方法有用
                .setUserCanRetry(false)         //失败后是否提示重新下载，非自定义下载 apk 回调此方法有用
                .setDeleteHistroyApk(true)     // 检查更新前是否删除本地历史 Apk， 默认为true
                .setUpdateManagerListener(new UpdateManagerListener() {
                    @Override
                    public void onNoUpdateAvailable() {
                        //没有更新是回调此方法
                        Log.d("pgyer", "there is no new version");
                    }
                    @Override
                    public void onUpdateAvailable(AppBean appBean) {
                        //有更新回调此方法
                        Log.d("pgyer", "there is new version can update"
                                + "new versionCode is " + appBean.getVersionCode());
                        //调用以下方法，DownloadFileListener 才有效；
                        //如果完全使用自己的下载方法，不需要设置DownloadFileListener
                        showLoading("版本更新中,请稍后...");
                        PgyUpdateManager.downLoadApk(appBean.getDownloadURL());
                    }

                    @Override
                    public void checkUpdateFailed(Exception e) {
                        //更新检测失败回调
                        //更新拒绝（应用被下架，过期，不在安装有效期，下载次数用尽）以及无网络情况会调用此接口
                        Log.e("pgyer", "check update failed ", e);
                    }
                })
                //注意 ：
                //下载方法调用 PgyUpdateManager.downLoadApk(appBean.getDownloadURL()); 此回调才有效
                //此方法是方便用户自己实现下载进度和状态的 UI 提供的回调
                //想要使用蒲公英的默认下载进度的UI则不设置此方法
                .setDownloadFileListener(new DownloadFileListener() {
                    @Override
                    public void downloadFailed() {
                        //下载失败
                        Log.e("pgyer", "download apk failed");
                        hideLoading();
                        ToastUtils.showShort(HomeActivity.this,"版本更新失败");
                    }

                    @Override
                    public void downloadSuccessful(Uri uri) {
                        Log.e("pgyer", "download apk failed");
                        hideLoading();
                        // 使用蒲公英提供的安装方法提示用户 安装apk
                        PgyUpdateManager.installApk(uri);
                    }

                    @Override
                    public void onProgressUpdate(Integer... integers) {
                        Log.e("pgyer", "update download apk progress" + integers);

                    }})
                .register();
    }

    @Override
    public int getLayout() {
        return R.layout.activity_home;
    }


    @OnClick({R.id.ll_qrcode_enter, R.id.ll_qrcode_walkedout, R.id.ll_finger_enter, R.id.ll_finger_walkedout, R.id.ll_id_collect, R.id.ll_finger_reset, R.id.ll_data_statics})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.ll_qrcode_enter:
                toScanQRCodeActivity(0);
                break;
            case R.id.ll_qrcode_walkedout:
                toScanQRCodeActivity(1);
                break;
            case R.id.ll_finger_enter:
                Intent fingerIntent = new Intent(this, NewFingerCollectActivity.class);
                fingerIntent.putExtra("sid", sid);
//                fingerIntent.putExtra("isRegister",false);
                fingerIntent.putExtra("isWalkedOut", false);
                fingerIntent.putExtra("isWalked", true);
                startActivity(fingerIntent);
                break;
            case R.id.ll_finger_walkedout:
                Intent walkedout = new Intent(this, NewFingerCollectActivity.class);
                walkedout.putExtra("sid", sid);
//                walkedout.putExtra("isRegister",false);
                walkedout.putExtra("isWalkedOut", true);
                walkedout.putExtra("isWalked", true);
                startActivity(walkedout);
                break;
            case R.id.ll_id_collect:
                Intent intent = new Intent(HomeActivity.this, CollectIdActivity.class);
                intent.putExtra("sid", sid);
                startActivity(intent);
                break;
            case R.id.ll_finger_reset:
                AndPermission.with(this)
                        .runtime()
                        .permission(Permission.WRITE_EXTERNAL_STORAGE)
                        .onGranted(new Action<List<String>>() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onAction(List<String> permissions) {
//                                Intent reset = new Intent(HomeActivity.this,FingerCollectActivity.class);
//                                reset.putExtra("sid",sid);
//                                reset.putExtra("isRegister",true);
//                                startActivity(reset);
                                String[] armebi = Build.SUPPORTED_ABIS;
                                for (int i = 0; i < armebi.length; i++) {
                                    if (armebi[i].equals("armeabi-v7a") || armebi[i].equals("armeabi")) {
                                        isArmSupport = true;
                                    }
                                    Log.e("arema",armebi[i]);
                                }
                                if (isArmSupport) {
                                    Intent intent1 = new Intent(HomeActivity.this, NewFingerCollectActivity.class);
                                    intent1.putExtra("sid", sid);
                                    intent1.putExtra("isWalked", false);
                                    startActivity(intent1);
                                } else {
                                    new AlertDialog.Builder(HomeActivity.this)
                                            .setTitle("温馨提示")
                                            .setMessage("当前设备不支持蓝牙指纹机")
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
                        })
                        .onDenied(new Action<List<String>>() {
                            @Override
                            public void onAction(@NonNull List<String> permissions) {
                                ToastUtils.showShort(HomeActivity.this, "相机权限已禁止，将影响部分功能正常使用！");
                            }
                        })
                        .start();
                break;
            case R.id.ll_data_statics:
                Intent intent1 = new Intent(this, DataStatisticsActivity.class);
                intent1.putExtra("token", token);
                startActivity(intent1);
                break;
        }

    }

    private void toScanQRCodeActivity(final int type) {
        AndPermission.with(this)
                .runtime()
                .permission(Permission.CAMERA, Permission.READ_EXTERNAL_STORAGE)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        Intent intent = new Intent(HomeActivity.this, ScanQRCodeActivity.class);
                        intent.putExtra("sid", sid);
                        if (type == 0) {
                            intent.putExtra(ScanQRCodeActivity.SCAN_TYPE, ScanQRCodeActivity.SCAN_TYPE_ENTER);
                        } else {
                            intent.putExtra(ScanQRCodeActivity.SCAN_TYPE, ScanQRCodeActivity.SCAN_TYPE_WORKEDOUT);
                        }
                        startActivity(intent);
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(@NonNull List<String> permissions) {
                        ToastUtils.showShort(HomeActivity.this, "相机权限已禁止，将影响部分功能正常使用！");
                    }
                })
                .start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                ToastUtils.showLong(this, "再按一次退出程序");
                mExitTime = System.currentTimeMillis();
            } else {
                AppManager.getAppManager().finishAllActivity();
                AppManager.getAppManager().AppExit(this);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
