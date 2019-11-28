package com.liu.app.version;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.liu.app.network.LjhHttpUtils;
import com.liu.lalibrary.AbsActivity;
import com.liu.lalibrary.log.LogUtils;
import com.liu.lalibrary.ui.AlertDialogHelper;
import com.liu.lalibrary.update.UpdateManager;
import com.liu.lalibrary.utils.AppUtils;
import com.liu.lalibrary.utils.PermissionsUtil;

/**
 * Created by liu on 2017/10/29.
 */

public class VersionManager
{
    public static final int VM_TYPE_NONE = 0;//自己不做处理
    public static final int VM_TYPE_AUTO = 1;//默认处理
    public static final int VM_TYPE_LOCK = 2;//锁定，这个用于pos这种情况，升级由第三方市场安装情况下

//    private boolean isClickUpdate;

    public interface OnVersionListener
    {
        public void onRecvVersion(VersionInfo ver);
        //
        public int onGetUpdateType(VersionInfo ver);

        public AbsActivity onVMGetActivity();
    }

    public void req(String url, final OnVersionListener listener)
    {
        LjhHttpUtils.inst().get(url, new LjhHttpUtils.IHttpRespListener()
        {
            @Override
            public void onHttpReqResult(int state, String result)
            {
                if (state == LjhHttpUtils.HU_STATE_OK && !TextUtils.isEmpty(result))
                {
                    try
                    {
                        NetResult nr = JSON.parseObject(result, NetResult.class);
                        if (nr.state == 0)
                        {
                            final VersionInfo vi = JSON.parseObject(nr.obj, VersionInfo.class);
                            final AbsActivity activity = listener.onVMGetActivity();
                            if (activity == null)return;
                            listener.onRecvVersion(vi);
                            String ver = AppUtils.getVersionName(activity);
                            if (compareVersion(vi.ver, ver) == 1)
                            {
                                if (!TextUtils.isEmpty(vi.remark) || vi.enforce)
                                {
                                    final int type = listener.onGetUpdateType(vi);
                                    if (activity == null) return;
                                    switch (type)
                                    {
                                        case VM_TYPE_NONE:
                                            break;
                                        case VM_TYPE_AUTO:
                                        case VM_TYPE_LOCK:

                                            if (TextUtils.isEmpty(vi.remark))
                                            {
                                                vi.remark = "该版本太低，需强制升级到最新版本";
                                            }
                                            activity.runOnUiThread(new Runnable()
                                            {
                                                @Override
                                                public void run()
                                                {
                                                    confirmUpdate(activity, vi.url, vi.remark, vi.enforce, type == VM_TYPE_LOCK);
                                                }
                                            });
                                            break;
                                    }
                                }
                            }
                        }
                    } catch (Exception e)
                    {
                        LogUtils.LOGE(VersionManager.class, e.getMessage());
                    }
                }
            }

            @Override
            public void onHttpReqProgress(float progress)
            {

            }
        });
    }

    /**
     * 比较版本号的大小,前者大则返回一个正数,后者大返回一个负数,相等则返回0
     * @param version1
     * @param version2
     * @return
     */
    public static int compareVersion(String version1, String version2)
    {
        if (version1 == null || version2 == null)
        {
            return 0;
        }

        String[] versionArray1 = version1.split("\\.");//注意此处为正则匹配，不能用"."；
        String[] versionArray2 = version2.split("\\.");
        int idx = 0;
        int minLength = Math.min(versionArray1.length, versionArray2.length);//取最小长度值
        int diff = 0;
        while (idx < minLength
                && (diff = versionArray1[idx].length() - versionArray2[idx].length()) == 0//先比较长度
                && (diff = versionArray1[idx].compareTo(versionArray2[idx])) == 0)
        {//再比较字符
            ++idx;
        }
        //如果已经分出大小，则直接返回，如果未分出大小，则再比较位数，有子版本的为大；
        diff = (diff != 0) ? diff : versionArray1.length - versionArray2.length;
        return diff;
    }

    public void update(final AbsActivity activity, final String url)
    {
        activity.checkPermissions(new PermissionsUtil.PermissionCallback()
        {
            @Override
            public void onPermission(boolean isOK)
            {
                if (isOK)
                {
                    new UpdateManager(activity, url);
                }
            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    public void confirmUpdate(final AbsActivity activity, final String url, String remark, final boolean enforce, final boolean lock)
    {
        AlertDialog.Builder dlg = new AlertDialog.Builder(activity);
        dlg.setTitle("新版本").setMessage(remark).setPositiveButton(lock ? "请升级" : "升级", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if (!lock)
                {
                    update(activity, url);
                }
                if (enforce)
                {
                    AlertDialogHelper.closeDlg(dialog, true);
                }
            }
        });
        if (enforce)
        {
            dlg.setCancelable(false);
        } else dlg.setNegativeButton("取消", null);
        dlg.show();
    }
}
