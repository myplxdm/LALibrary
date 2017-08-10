package com.liu.lalibrary.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.liu.lalibrary.AbsActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liu on 2017/3/31.
 */

public class PermissionsUtil
{
    public interface PermissionCallback
    {
        public void onPermission(boolean isOK);
    }

    private WeakReference<AbsActivity> refActivity;
    private PermissionCallback curPermissionCB;
    private static final int PERMISSON_REQUESTCODE = 0x9FF;

    public PermissionsUtil(AbsActivity activity)
    {
        refActivity = new WeakReference<AbsActivity>(activity);
    }

    public void checkPermissions(PermissionCallback cb, String... permissions)
    {
        List<String> needRequestPermissonList = findDeniedPermissions(permissions);
        if (null != needRequestPermissonList && needRequestPermissonList.size() > 0)
        {
            curPermissionCB = cb;
            ActivityCompat.requestPermissions(refActivity.get(), needRequestPermissonList.toArray(new String[needRequestPermissonList.size()]),
                                              PERMISSON_REQUESTCODE);
        }else
        {
            cb.onPermission(true);
        }
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     * @since 2.5.0
     */
    private List<String> findDeniedPermissions(String[] permissions)
    {
        List<String> needRequestPermissonList = new ArrayList<String>();
        for (String perm : permissions)
        {
            if (ContextCompat.checkSelfPermission(refActivity.get(), perm) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.shouldShowRequestPermissionRationale(refActivity.get(), perm))
            {
                needRequestPermissonList.add(perm);
            }
        }
        return needRequestPermissonList;
    }

    /**
     * 检测是否说有的权限都已经授权
     *
     * @param grantResults
     * @return
     * @since 2.5.0
     */
    private boolean verifyPermissions(int[] grantResults)
    {
        for (int result : grantResults)
        {
            if (result != PackageManager.PERMISSION_GRANTED)
            {
                return false;
            }
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] paramArrayOfInt)
    {
        if (requestCode == PERMISSON_REQUESTCODE)
        {
            if (!verifyPermissions(paramArrayOfInt))
            {
                showMissingPermissionDialog();
            }
        }else if (curPermissionCB != null)
        {
            curPermissionCB.onPermission(true);
            curPermissionCB = null;
        }
    }

    /**
     * 显示提示信息
     *
     * @since 2.5.0
     */
    private void showMissingPermissionDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(refActivity.get());
        builder.setTitle("提示");
        builder.setMessage("当前应用缺少必要权限。\\n\\n请点击\\\"设置\\\"-\\\"权限\\\"-打开所需权限。");

        // 拒绝, 退出应用
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
                                  {
                                      @Override
                                      public void onClick(DialogInterface dialog, int which)
                                      {
                                          refActivity.get().finish();
                                      }
                                  });
        builder.setPositiveButton("设置",
                                  new DialogInterface.OnClickListener()
                                  {
                                      @Override
                                      public void onClick(DialogInterface dialog, int which)
                                      {
                                          startAppSettings();
                                      }
                                  });
        builder.setCancelable(false);
        builder.show();
    }

    /**
     * 启动应用的设置
     *
     * @since 2.5.0
     */
    private void startAppSettings()
    {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + refActivity.get().getPackageName()));
        refActivity.get().startActivity(intent);
    }

//    private static class SingletonHolder
//    {
//        private static final PermissionsUtil INSTANCE = new PermissionsUtil();
//    }
//
//    public static final PermissionsUtil getInst()
//    {
//        return SingletonHolder.INSTANCE;
//    }
//
//    //
//    private final int SDK_PERMISSION_REQUEST = 127;
//    private WeakReference<AbsActivity> refActivity;
//
//    public void initActivity(AbsActivity act)
//    {
//        refActivity = new WeakReference<AbsActivity>(act);
//    }
//
//    public void reqLocation()
//    {
//        getPersimmions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE});
//    }
//
//    @TargetApi(23)
//    public void getPersimmions(String[] permissions)
//    {
//        if (refActivity.get() == null) return;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//        {
//            ArrayList<String> permissionInfo = new ArrayList<String>();
//
//            for (String per : permissions)
//            {
//                if (refActivity.get().checkSelfPermission(per) != PackageManager.PERMISSION_GRANTED)
//                {
//                    permissionInfo.add(per);
//                }
//            }
//
//            if (permissionInfo.size() > 0)
//            {
//                refActivity.get().requestPermissions(permissionInfo.toArray(new String[permissionInfo.size()]), SDK_PERMISSION_REQUEST);
//            }
//        }
//    }
//
//    @TargetApi(23)
//    private boolean addPermission(ArrayList<String> permissionsList, String permission)
//    {
//        if (refActivity.get() == null) return false;
//        if (refActivity.get().checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
//        { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
//            if (refActivity.get().shouldShowRequestPermissionRationale(permission))
//            {
//                return true;
//            }else
//            {
//                permissionsList.add(permission);
//                return false;
//            }
//        }else
//        {
//            return true;
//        }
//    }
}
