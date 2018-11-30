package com.mango.permission;

import java.util.Map;

/**
 * @Description TODO()
 * @author cxy
 * @Date 2018/11/29 15:32
 */
public interface MPermissionListener {

    /**
     * 拒绝申请
     * @param isSelectedBox 拒绝的同时是否勾选了不再提示
     */
    void onRefuse(Map<String,Boolean> isSelectedBox);

    /**
     * 同意申请
     * @param permission 被同意的权限
     */
    void onGranted(String... permission);

    /**
     * 无需申请
     */
    void isFine();

}
