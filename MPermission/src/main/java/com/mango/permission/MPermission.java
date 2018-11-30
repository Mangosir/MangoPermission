package com.mango.permission;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import java.lang.ref.WeakReference;

/**
 * @Description TODO(申请代理类)
 * @author cxy
 * @Date 2018/11/29 15:29
 */
public class MPermission {

    private String TAG = MPermission.class.getSimpleName();

    private static MPermission instance;

    private WeakReference<Context> mContext;

    public MPermissionListener mListener;
    private String[] mPermissions;
    private String mTitle;
    private String mContent;
    private boolean mIsShowAlarm;

    private int mApplyType;

    private MPermission(Context context) {
        mContext = new WeakReference<>(context);
    }

    public static MPermission Build(Context context){
        if (instance == null) {
            instance = new MPermission(context);
        }
        return instance;
    }


    /**
     * 检测权限是否已经申请
     * @param permission
     * @return
     */
    public boolean isPermissionGranted(String permission){
        return ContextCompat.checkSelfPermission(mContext.get(),permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 重置一些变量
     * @return
     */
    public MPermission resetState(){
        mTitle = null;
        mContent = null;
        mIsShowAlarm = false;
        mPermissions = null;
        mListener = null;
        return this;
    }

    /**
     * 添加弹窗的标题和内容
     * @param title
     * @param content
     * @return
     */
    public MPermission addDialogString(String title, String content){
        mTitle = title;
        mContent = content;
        return this;
    }

    /**
     * 当用户拒绝申请后是否需要弹框提醒
     * @param isAlarm
     * @return
     */
    public MPermission shouldAlarm(boolean isAlarm){
        mIsShowAlarm = isAlarm;
        return this;
    }


    /**
     * 添加要注册的权限
     * @param permission
     * @return
     */
    public MPermission addPermission(String... permission){
        mPermissions = permission;
        return this;
    }

    /**
     * 添加监听
     * @param listener
     * @return
     */
    public MPermission addListener(MPermissionListener listener){
        mListener = listener;
        return this;
    }

    /**
     * 提交权限申请
     */
    public void apply(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M ) {
            if (mListener != null) mListener.isFine();
            return;
        }

        if (mPermissions == null || mPermissions.length == 0) {
            if (mListener != null) mListener.isFine();
            return;
        }

        String[] filter ;

        if (mPermissions.length == 1) {
            if (isPermissionGranted(mPermissions[0])) {
                if (mListener != null) mListener.isFine();
                return;
            }
            filter = mPermissions;
        } else {
            StringBuffer sb = new StringBuffer();
            for (int i=0; i<mPermissions.length; i++) {
                String per = mPermissions[i];
                if (!isPermissionGranted(per)) {
                    sb.append(per+"-");
                }
            }
            if (TextUtils.isEmpty(sb.toString()) || sb.length() < 7) {
                if (mListener != null) mListener.isFine();
                return;
            }
            filter = sb.toString().split("-");
        }

        if (filter.length == 1) {
            mApplyType = MPermissionActivity.PERMISSION_APPLY_SINGLE;
        } else {
            mApplyType = MPermissionActivity.PERMISSION_APPLY_MULTI;
        }

        startActivity(filter);
    }


    private void startActivity(String... permission){
        Intent intent = new Intent(mContext.get(),MPermissionActivity.class);
        intent.putExtra("permission",permission);
        intent.putExtra("type",mApplyType);
        intent.putExtra("title",mTitle);
        intent.putExtra("content",mContent);
        intent.putExtra("show",mIsShowAlarm);
        mContext.get().startActivity(intent);
    }

}
