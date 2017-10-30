package com.liu.app.version;

import android.Manifest;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.liu.app.network.LjhHttpUtils;
import com.liu.lalibrary.AbsActivity;
import com.liu.lalibrary.log.LogUtils;
import com.liu.lalibrary.update.UpdateManager;
import com.liu.lalibrary.utils.PermissionsUtil;

/**
 * Created by liu on 2017/10/29.
 */

public class VersionManager
{
    public interface OnVersionListener
    {
        public void onRecvVersion(VersionInfo ver);
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
                        VersionInfo vi = JSON.parseObject(result, VersionInfo.class);
                        listener.onRecvVersion(vi);
                    }catch (Exception e)
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

    public void update(final AbsActivity activity, final String url)
    {
        activity.checkPermissions(new PermissionsUtil.PermissionCallback() {
            @Override
            public void onPermission(boolean isOK)
            {
                if (isOK)
                {
                    Toast.makeText(activity, "正在下载......", Toast.LENGTH_LONG).show();
                    new UpdateManager(activity, url);
                }
            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    public void confirmUpdate(final AbsActivity activity, final String url, String remark)
    {
        AlertDialog.Builder dlg = new AlertDialog.Builder(activity);
        dlg.setTitle("新版本").setMessage(remark).setPositiveButton("升级", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                update(activity, url);
            }
        }).setNegativeButton("取消", null).show();
    }
}
