package com.hfut.zhaojiabao.myrecord.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hfut.zhaojiabao.myrecord.R;
import com.hfut.zhaojiabao.myrecord.dialogs.CommonDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限请求
 * <p>
 * 使用流程:
 * 使`Activity`继承自`PermissionBaseActivity`
 * ## 请求单个权限:
 * 1.调用`checkPermissionAndRequestIfNotHaved()`,传入需要的权限.
 * 2.复写`permissionGrantedResult`,判断权限是否获取,若未获取,则弹出弹窗向用户解释为何需要该权限,用户点击确定则跳转至系统App设置界面:`startAppSettings()`,手动授予权限.
 * <p>
 * ## 请求多个权限
 * 调用`requestPermissions()`,传入需要请求的多个权限.之后的步骤和请求单个权限类似.
 */
public class PermissionBaseActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1024;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            permissionGrantedResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * 子类继承该方法处理权限请求结果
     * 若请求失败,比较好的处理方法是弹出对话框,向用户解释为何需要该权限,点击同意则进入手机设置页,由用户手动授予App权限.
     *
     * @param requestCode  请求码
     * @param permissions  权限列表
     * @param grantResults 请求结果
     */
    protected void permissionGrantedResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    }

    /**
     * 打开当前应用设置界面
     */
    protected void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    public void showRationale(String title, String content) {
        final CommonDialog dialog = new CommonDialog();
        CommonDialog.CommonBuilder builder = new CommonDialog.CommonBuilder(this);
        builder.setTitleText(title)
                .setContentText(content)
                .setLeftTextVisible(true)
                .setLeftText(R.string.cancel)
                .setLeftListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                })
                .setRightTextVisible(true)
                .setRightText(R.string.confirm)
                .setRightListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startAppSettings();
                        dialog.dismiss();
                    }
                });
        dialog.setBuilder(builder);
        dialog.show(getSupportFragmentManager(), "RationaleDialog");
    }

    /**
     * 批量请求权限
     *
     * @param permissions 需要请求的权限
     */
    public void requestPermissions(String[] permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        List<String> needGrantPermissions = getNeedGrantPermissions(permissions);
        if (needGrantPermissions.isEmpty()) {
            return;
        }
        ActivityCompat.requestPermissions(this,
                needGrantPermissions.toArray(new String[needGrantPermissions.size()]), REQUEST_CODE);
    }

    /**
     * 请求单个权限
     *
     * @param permission 需要请求的权限
     */
    public void requestPermission(String permission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        ActivityCompat.requestPermissions(this, new String[]{permission}, REQUEST_CODE);
    }

    /**
     * 检查单个权限,若没有则获取
     *
     * @return 权限已获取
     */
    public boolean checkPermissionAndRequestIfNotHaved(String permission) {
        boolean isHaved = Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || getNeedGrantPermissions(new String[]{permission}).isEmpty();
        if (!isHaved) {
            requestPermission(permission);
        }
        return isHaved;
    }

    /**
     * 获取需要授权的权限
     *
     * @param permissions 请求权限列表
     * @return 需要授权的权限
     */
    public List<String> getNeedGrantPermissions(String[] permissions) {
        List<String> needGrantPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                needGrantPermissions.add(permission);
            }
        }
        return needGrantPermissions;
    }
}
