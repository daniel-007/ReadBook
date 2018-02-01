/**
 * Copyright 2016 JustWayward Team
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xyu.reader.ui.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.xyu.reader.R;
import com.xyu.reader.utils.PermissionsChecker;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {

    @Bind(R.id.tvSkip)
    TextView tvSkip;

    private boolean flag = false;
    private Runnable runnable;

    //权限相关
    private PermissionsChecker checker;
    private int requestPermissions_RetryTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

//对于6.0以上系统,在启动时请求权限
        if (Build.VERSION.SDK_INT >= 23) {
            checker = new PermissionsChecker(this);
            checker.checkPermissions(PermissionsChecker.PERMISSIONS);
            if (!checker.lacksPermissions(PermissionsChecker.PERMISSIONS)) {
                Init();
            }
        } else {
            Init();
        }
    }

    private void Init() {
        runnable = new Runnable() {
            @Override
            public void run() {
                goHome();
            }
        };

        tvSkip.postDelayed(runnable, 2000);

        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goHome();
            }
        });
    }

    private synchronized void goHome() {
        if (!flag) {
            flag = true;
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        flag = true;
        tvSkip.removeCallbacks(runnable);
        ButterKnife.unbind(this);
    }
    /**
     * 请求权限检查完后回调的结果
     *
     * @param requestCode  .
     * @param permissions  所请求的权限
     * @param grantResults .
     */
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                requestCode != PermissionsChecker.PERMISSION_REQUEST_CODE)
            return;

        int i = 0;
        for (int len = permissions.length; i < len; i++) {
            String permission = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                boolean showRationale = shouldShowRequestPermissionRationale(permission);
                if (!showRationale) {
                    // 用户点击不再提醒,弹出权限框,引导其手动开启权限
//                    checker.showMissingPermissionDialog();
                    Toast.makeText(this, "请赐予我权限:" + permission, Toast.LENGTH_LONG).show();
                    checker.startAppSettings();
//                    finish();
                    break;
                } else {
                    // 用户点击取消,继续提示
                    checker.checkPermissions(PermissionsChecker.PERMISSIONS);
                    break;
                }
            }
        }

        if (i == permissions.length && !checker.lacksPermissions(PermissionsChecker.PERMISSIONS)) {
            Init();
//            Toast.makeText(this, "onRequestPermissionsResult:success", Toast.LENGTH_LONG).show();
        }

    }

    /**
     * 从设置跳回的时候,检查是否获取到权限
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PermissionsChecker.PERMISSION_REQUEST_CODE_FROMSETTINGS) {
            requestPermissions_RetryTime++;
            checker.checkPermissions(PermissionsChecker.PERMISSIONS);
            if (requestPermissions_RetryTime > 1) {
                Toast.makeText(this, "请重新启动应用,并赐予权限!", Toast.LENGTH_LONG).show();
                finish();
            }
            if (!checker.lacksPermissions(PermissionsChecker.PERMISSIONS)) {
                Init();
//                Toast.makeText(this, "onActivityResult:success", Toast.LENGTH_LONG).show();
            }
        }
    }
}
