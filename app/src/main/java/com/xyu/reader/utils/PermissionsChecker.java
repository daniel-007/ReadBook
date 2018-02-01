package com.xyu.reader.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * @author xiongyu16609
 */
public class PermissionsChecker {
    public static final int PERMISSION_REQUEST_CODE = 600;
    public static final int PERMISSION_REQUEST_CODE_FROMSETTINGS = 601;
    public static final String[] PERMISSIONS = {
            Manifest.permission.READ_PHONE_STATE
            , Manifest.permission.WRITE_EXTERNAL_STORAGE
            , Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public PermissionsChecker(Activity context) {
        this.context = context;
    }

    private Activity context;

    /**
     * 检查权限是否已请求到 (6.0)
     */
    public void checkPermissions(String... permissions) {
        // 版本兼容
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                // 判断缺失哪些必要权限
                && lacksPermissions(permissions)) {
            // 如果缺失,则申请
            requestPermissions(permissions);
        }
    }

    /**
     * 判断是否缺失权限集合中的权限
     */
    public boolean lacksPermissions(String... permissions) {
        for (String permission : permissions) {
            if (lacksPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否缺少某个权限
     */
    private boolean lacksPermission(String permission) {
        return ContextCompat.checkSelfPermission(context, permission) ==
                PackageManager.PERMISSION_DENIED;
    }

    /**
     * 请求权限
     */
    public void requestPermissions(String... permissions) {
        ActivityCompat.requestPermissions(context, permissions, PERMISSION_REQUEST_CODE);
    }

    /**
     * 启动应用的设置,进入手动配置权限页面
     */
    public void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivityForResult(intent, PERMISSION_REQUEST_CODE_FROMSETTINGS);
    }

}
