package com.liu.app.pluginImpl;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.liu.app.network.LjhHttpUtils;
import com.liu.lalibrary.AbsActivity;
import com.liu.lalibrary.camera.PhotoProcActivity;
import com.liu.lalibrary.plugins.IPlugin;
import com.liu.lalibrary.plugins.IPluginEvent;
import com.liu.lalibrary.plugins.PluginBase;
import com.liu.lalibrary.utils.AppUtils;
import com.liu.lalibrary.utils.Utils;
import com.liu.lalibrary.utils.imagecache.CommonUtil;
import com.liu.lalibrary.utils.imagecache.ImageTools;

import java.io.File;
import java.util.HashMap;

/**
 * Created by liu on 2017/9/20.
 */

public class PluginFileUpload extends PluginBase
{
    public static final String NAME = "fileUpload";
    public static final String P_URL = "url";
    public static final String P_RATION = "ration";
    public static final String P_IS_CORP = "corp";
    public static final String P_WIDTH = "width";
    public static final String P_HEIGHT = "height";
    private final int REQ_PHOTO_CORP = 0xff101010;
    //
    private String upUrl;
    private float corpRation;
    private int width;
    private int height;
    private IPluginEvent event;

    public PluginFileUpload(AbsActivity activity)
    {
        super(activity);
    }

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public String getDescribe()
    {
        return NAME;
    }

    @Override
    public boolean exec(String cmd, JSONObject params, IPluginEvent event)
    {
        upUrl = params.getString(P_URL);
        width = params.getIntValue(P_WIDTH);
        height = params.getIntValue(P_HEIGHT);
        if (params.getBoolean(P_IS_CORP))
        {
            corpRation = params.getFloat(P_RATION);
        }
        IPlugin pc = getActivity().getPluginByName(PluginPhotoChoose.NAME);
        if (pc == null)return false;
        pc.exec(null, null, new IPluginEvent()
        {
            @Override
            public void pluginResult(boolean isSuccess, String path, Object param)
            {
                if (isSuccess)
                {
                    if (corpRation > 0)
                    {
                        PhotoProcActivity.show(path, corpRation, REQ_PHOTO_CORP, getActivity());
                        return;
                    }
                    uploadFile(path);
                }
            }

            @Override
            public void pluginClose(boolean isSuccess, String result)
            {

            }
        });
        return true;
    }

    private void uploadFile(String path)
    {
        Bitmap bmp = ImageTools.getPhotoFromSDCard(path, width, height);
        final ProgressDialog dlg = ProgressDialog.show(getActivity(), null, "正在上传文件...");
        final String fileName = ImageTools.savePhotoToSDCard(bmp,
                                                             CommonUtil.getRootFilePath() + AppUtils.getAppName(getActivity()),
                                                             "tmp." + Utils.getExtName(path), true);
        HashMap<String,File> files = new HashMap<>();
        files.put("upfile",new File(fileName));
        LjhHttpUtils.inst().uploadFile(upUrl, null, files, new LjhHttpUtils.IHttpRespListener()
        {
            @Override
            public void onHttpReqResult(int state, final String result)
            {
                if (state == LjhHttpUtils.HU_STATE_OK)
                {
                    getActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            dlg.dismiss();
                            if (!TextUtils.isEmpty(result))
                            {
                                event.pluginResult(true, result, null);
                                //web.loadUrl(String.format("javascript:photoup_after('%s')",result));
                                return;
                            }
                            Toast.makeText(getActivity(), "上传失败", Toast.LENGTH_LONG).show();
                        }
                    });
                }else
                {
                    getActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            dlg.dismiss();
                            Toast.makeText(getActivity(), String.format("上传失败(%s)",result), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onHttpReqProgress(float v)
            {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_PHOTO_CORP && data != null)
        {
            uploadFile(data.getStringExtra(PhotoProcActivity.RESULT_FILE_NAME));
        }
    }
}
