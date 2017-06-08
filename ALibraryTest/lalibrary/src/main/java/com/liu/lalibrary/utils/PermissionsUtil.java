package com.liu.lalibrary.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;

import com.liu.lalibrary.AbsActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by liu on 2017/3/31.
 */

public class PermissionsUtil
{
    private static class SingletonHolder
    {
        private static final PermissionsUtil INSTANCE = new PermissionsUtil();
    }

    public static final PermissionsUtil getInst()
    {
        return SingletonHolder.INSTANCE;
    }

    //
    private final int SDK_PERMISSION_REQUEST = 127;
    private WeakReference<AbsActivity> refActivity;

    public void initActivity(AbsActivity act)
    {
        refActivity = new WeakReference<AbsActivity>(act);
    }

    public void reqLocation()
    {
        getPersimmions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE});
    }

    @TargetApi(23)
    public void getPersimmions(String[] permissions)
    {
        if (refActivity.get() == null) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            ArrayList<String> permissionInfo = new ArrayList<String>();

            for (String per : permissions)
            {
                if (refActivity.get().checkSelfPermission(per) != PackageManager.PERMISSION_GRANTED)
                {
                    permissionInfo.add(per);
                }
            }
//            if (addPermission(permissionInfo, Manifest.permission.WRITE_EXTERNAL_STORAGE))
//            {
//            }
//            // 读取电话状态权限
//            if (addPermission(permissionInfo, Manifest.permission.READ_PHONE_STATE))
//            {
//            }
//            // 定位精确位置
//            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
//            {
//                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
//            }
//            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
//            {
//                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
//            }
//            /*
//			 * 读写权限和电话状态权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
//			 */
//            // 读写权限
//            if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE))
//            {
//                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
//            }
//            // 读取电话状态权限
//            if (addPermission(permissions, Manifest.permission.READ_PHONE_STATE))
//            {
//                permissionInfo += "Manifest.permission.READ_PHONE_STATE Deny \n";
//            }

            if (permissionInfo.size() > 0)
            {
                refActivity.get().requestPermissions(permissionInfo.toArray(new String[permissionInfo.size()]), SDK_PERMISSION_REQUEST);
            }
        }
    }

    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission)
    {
        if (refActivity.get() == null) return false;
        if (refActivity.get().checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
        { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
            if (refActivity.get().shouldShowRequestPermissionRationale(permission))
            {
                return true;
            }else
            {
                permissionsList.add(permission);
                return false;
            }
        }else
        {
            return true;
        }
    }
}
