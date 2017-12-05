package com.liu.app.version;

import android.Manifest;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.liu.app.network.LjhHttpUtils;
import com.liu.app.network.NetResult;
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
        //如果remark不为空，或需要强制升级就会回调这个方法，如果返回activity就由该类处理，返回null由应用自行处理
        public AbsActivity onConfirmUpdate(VersionInfo ver);
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
                            listener.onRecvVersion(vi);
                            if (!TextUtils.isEmpty(vi.remark) || vi.enforce)
                            {
                                final AbsActivity activity = listener.onConfirmUpdate(vi);
                                if (activity != null)
                                {
                                    activity.runOnUiThread(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            if (TextUtils.isEmpty(vi.remark))
                                            {
                                                vi.remark = "该版本太低，需强制升级到最新版本";
                                            }
                                            confirmUpdate(activity, vi.url, vi.remark, vi.enforce);
                                        }
                                    });
                                }
                            }
                        }
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

    public void confirmUpdate(final AbsActivity activity, final String url, String remark, boolean enforce)
    {
        AlertDialog.Builder dlg = new AlertDialog.Builder(activity);
        dlg.setTitle("新版本").setMessage(remark).setPositiveButton("升级", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                update(activity, url);
            }
        });
        if (!enforce) dlg.setNegativeButton("取消", null).show();
        else dlg.show();
    }
}
