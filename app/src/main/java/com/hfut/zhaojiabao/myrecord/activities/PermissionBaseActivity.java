package com.hfut.zhaojiabao.myrecord.activities;

import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限请求
 */
public class PermissionBaseActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 10;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                //TODO test code
                System.out.println("JayLog, permission: " + permissions[i] + " result: " + grantResults[i]);
            }
        }
    }

    /**
     * 批量请求权限
     *
     * @param permissions 需要请求的权限
     */
    public void requestPermissions(String[] permissions) {
        List<String> needGrantPermissions = getNeedGrantPermissions(permissions);

        ActivityCompat.requestPermissions(this,
                needGrantPermissions.toArray(new String[needGrantPermissions.size()]),
                REQUEST_CODE);
    }

    /**
     * 请求单个权限
     *
     * @param permission 需要请求的权限
     * @param rationale  如果需要的话,给用户为何申请该权限的解释
     */
    public void requestPermission(String permission, @NonNull IRationale rationale) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, permission)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                rationale.showRationale(permission);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{permission},
                        REQUEST_CODE);
            }
        }
    }

    /**
     * 直接请求单个权限
     *
     * @param permission 需要请求的权限
     */
    public void requestPermission(String permission) {
        ActivityCompat.requestPermissions(this,
                new String[]{permission},
                REQUEST_CODE);
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

    public interface IRationale {
        void showRationale(String permission);
    }
}
