package com.mango.permission;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description TODO(申请Activity)
 * @author cxy
 * @Date 2018/11/29 15:22
 *
 *  <!-- Dangerous Permissions start -->
 *  <!--PHONE-->
 *  <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
 *  <uses-permission android:name="android.permission.CALL_PHONE"/>
 *  <uses-permission android:name="android.permission.READ_CALL_LOG"/>
 *  <uses-permission android:name="android.permission.ADD_VOICEMAIL"/>
 *  <uses-permission android:name="android.permission.WRITE_CALL_LOG"/>
 *  <uses-permission android:name="android.permission.USE_SIP"/>
 *  <uses-permission android:name="android.permission.PROCESS_OUTGOING_CALLS"/>
 *  <!--CALENDAR-->
 *  <uses-permission android:name="android.permission.READ_CALENDAR"/>
 *  <uses-permission android:name="android.permission.WRITE_CALENDAR"/>
 *  <!--CAMERA-->
 *  <uses-permission android:name="android.permission.CAMERA"/>
 *  <!--CONTACTS-->
 *  <uses-permission android:name="android.permission.READ_CONTACTS"/>
 *  <uses-permission android:name="android.permission.WRITE_CONTACTS"/>
 *  <uses-permission android:name="android.permission.GET_ACCOUNTS"/>
 * <!--LOCATION-->
 *  <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
 *  <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
 *  <!--MICROPHONE-->
 *  <uses-permission android:name="android.permission.RECORD_AUDIO"/>
 *  <!--SENSORS-->
 *  <uses-permission android:name="android.permission.BODY_SENSORS"/>
 *  <!--SMS-->
 *  <uses-permission android:name="android.permission.SEND_SMS"/>
 *  <uses-permission android:name="android.permission.RECEIVE_SMS"/>
 *  <uses-permission android:name="android.permission.READ_SMS"/>
 *  <uses-permission android:name="android.permission.RECEIVE_WAP_PUSH"/>
 *  <uses-permission android:name="android.permission.RECEIVE_MMS"/>
 *  <!--STORAGE-->
 *  <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
 *  <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
 *  <!-- Dangerous Permissions end -->
 *
 */
public class MPermissionActivity extends AppCompatActivity{

    private String TAG = MPermissionActivity.class.getSimpleName();

    public static int PERMISSION_APPLY_SINGLE = 1;
    public static int PERMISSION_APPLY_MULTI = 2;
    private static final int REQUEST_CODE_SINGLE = 3;
    private static final int REQUEST_CODE_MULTI = 4;
    private static final int REQUEST_SETTING = 5;

    private String[] permission = null;
    private int mApplyType;
    private String mAppName;

    private String mTitle;
    private String mContent;
    private String mCancelTxt;
    private String mPosTxt;
    private boolean mIsShowAlarm;

    private MPermissionListener mListener;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mListener = MPermission.Build(this).mListener;
        Intent intent = getIntent();
        permission = intent.getStringArrayExtra("permission");
        mApplyType = intent.getIntExtra("type",1);
        mTitle = intent.getStringExtra("title");
        mContent = intent.getStringExtra("content");
        mIsShowAlarm = intent.getBooleanExtra("show",false);
        mAppName = getApplicationInfo().loadLabel(getPackageManager()) +"";

        if (TextUtils.isEmpty(mTitle))
            mTitle = getString(R.string.title);
        if (TextUtils.isEmpty(mContent))
            mContent = getString(R.string.content_first_half) + "\n\n" + getString(R.string.content_second_half,mAppName);
        mCancelTxt = getString(R.string.negative);
        mPosTxt = getString(R.string.positive);

        if (mApplyType == PERMISSION_APPLY_SINGLE)
            ActivityCompat.requestPermissions(this,permission,REQUEST_CODE_SINGLE);
        else
            ActivityCompat.requestPermissions(this,permission,REQUEST_CODE_MULTI);

    }

    /**
     * 权限被拒绝了，那就推荐用户到设置界面去添加
     * @param permissions 被拒绝的权限
     */
    private void showAlertDialog(String... permissions) {

        if (!mIsShowAlarm || permissions == null || permissions.length == 0 || TextUtils.isEmpty(permissions[0])) {
            putRefusePermission();
            finish();
            return;
        }

        permission = permissions;
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(mTitle)
                .setMessage(mContent)
                .setCancelable(false)
                .setNegativeButton(mCancelTxt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        putRefusePermission();
                        finish();
                    }
                })
                .setPositiveButton(mPosTxt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri uri = Uri.parse("package:"+MPermissionActivity.this.getPackageName());
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,uri);
                        startActivityForResult(intent,REQUEST_SETTING);
                        dialog.dismiss();
                    }
                }).create();
        alertDialog.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_SINGLE) {
            if (ContextCompat.checkSelfPermission(this,permissions[0]) == PackageManager.PERMISSION_GRANTED) {
                onGranted(permissions);
                finish();
            } else {
                showAlertDialog(permissions);
            }
        } else {

            StringBuffer granted = new StringBuffer();
            StringBuffer refused = new StringBuffer();
            int size = permissions == null ? 0 : permissions.length;
            for (int i=0; i<size; i++) {
                String permission = permissions[i];
                if (ContextCompat.checkSelfPermission(this,permissions[i]) == PackageManager.PERMISSION_GRANTED) {
                    granted.append(permission + "-");
                } else {
                    refused.append(permission + "-");
                }
            }

            String[] str = granted.toString().split("-");
            onGranted(str);

            String[] str2 = refused.toString().split("-");
            if (str2.length == 0)
                finish();
            else
                showAlertDialog(str2);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SETTING) putRefusePermission();
        finish();
    }

    private void putRefusePermission(){
        Map<String,Boolean> map = new HashMap<>();
        int size = permission == null ? 0 : permission.length;
        for (int i=0; i<size; i++) {
            String per = permission[i];
            if (ContextCompat.checkSelfPermission(this,per) != PackageManager.PERMISSION_GRANTED) {
                boolean isRequestAgain = ActivityCompat.shouldShowRequestPermissionRationale(this,per);
                map.put(per,isRequestAgain);
            }
        }
        onRefuse(map);
    }

    private void onGranted(String... permissions){
        if (mListener != null) mListener.onGranted(permissions);
    }

    private void onRefuse(Map<String,Boolean> map){
        if (mListener != null) mListener.onRefuse(map);
    }

}
