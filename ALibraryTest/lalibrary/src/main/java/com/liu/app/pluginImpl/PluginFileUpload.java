package com.liu.app.pluginImpl;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.liu.app.DirManager;
import com.liu.app.network.LjhHttpUtils;
import com.liu.lalibrary.AbsActivity;
import com.liu.lalibrary.camera.PhotoProcActivity;
import com.liu.lalibrary.plugins.IPlugin;
import com.liu.lalibrary.plugins.IPluginEvent;
import com.liu.lalibrary.plugins.PluginBase;
import com.liu.lalibrary.utils.AppUtils;
import com.liu.lalibrary.utils.JsonHelper;
import com.liu.lalibrary.utils.Utils;
import com.liu.lalibrary.utils.imagecache.CommonUtil;
import com.liu.lalibrary.utils.imagecache.FileHelper;
import com.liu.lalibrary.utils.imagecache.ImageTools;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;
import com.yalantis.ucrop.model.AspectRatio;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by liu on 2017/9/20.
 */

public class PluginFileUpload extends PluginBase
{
    public static final String NAME = PluginFileUpload.class.getSimpleName();
    public static final String P_URL = "url";
    public static final String P_ASPECTX = "aspectX";//宽比例
    public static final String P_ASPECTY = "aspectY";//高比例，1:1;16:9
    public static final String P_FILE_KEY = "fileKey";
    public static final String P_MAX_WH_PX = "maxWHPX";
    public static final String P_UP_KEYS = "upkeys";
    public static final String P_UP_VALUES = "upvalues";
    //
    private String upUrl;
    //private float corpRation;
    private int aspectX = 1;
    private int aspectY = 1;
    private int maxWHPX = 0;
    private IPluginEvent event;
    private String upKeys;
    private String upValues;
    private String fileKey;

    public PluginFileUpload(AbsActivity activity)
    {
        super(activity);
    }

    @Override
    public String getDescribe()
    {
        return NAME;
    }

    @Override
    public void stopPlugin()
    {
        super.stopPlugin();
        event = null;
    }

    @Override
    public boolean exec(String cmd, JSONObject params, IPluginEvent event)
    {
        boolean isCrop = false;
        upUrl = params.getString(P_URL);
        fileKey = params.getString(P_FILE_KEY);
        if (TextUtils.isEmpty(upUrl) || TextUtils.isEmpty(fileKey))return false;
        maxWHPX = params.getIntValue(P_MAX_WH_PX);
        this.event = event;
        aspectX = JsonHelper.getInt(params, P_ASPECTX, 0);
        aspectY = JsonHelper.getInt(params, P_ASPECTY, 0);
        if (params.containsKey(P_UP_KEYS) && params.containsKey(P_UP_VALUES))
        {
            upKeys = params.getString(P_UP_KEYS);
            upValues = params.getString(P_UP_VALUES);
        }
        IPlugin pc = getActivity().getPluginByName(PluginPhotoChoose.NAME);
        if (pc == null)return false;
        pc.exec(null, params, new IPluginEvent()
        {
            @Override
            public void pluginResult(boolean isSuccess, String path, Object param)
            {
                if (isSuccess)
                {
                    if (aspectX > 0 && aspectY > 0)
                    {
                        openCrop(path);
                        return;
                    }
                    path = FileHelper.getUriPath(getActivity(), Uri.parse(path));
                    if (maxWHPX > 0)
                    {

                        ImageTools.savePhotoToSDCard(ImageTools.getPhotoFromSDCard(path, maxWHPX, maxWHPX),
                                path, true);
                    }
                    uploadFile(path, fileKey);
                }
            }

            @Override
            public void pluginClose(boolean isSuccess, String result)
            {

            }
        });
        return true;
    }

    private void openCrop(String srcImagePath)
    {
        File file = new File(DirManager.inst().getDirByType(DirManager.DIR_CACHE, "crop.jpg"));

        UCrop.Options options = new UCrop.Options();

        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.NONE, UCropActivity.ALL);
        options.setFreeStyleCropEnabled(true);
        if (maxWHPX > 0) options.setMaxBitmapSize(maxWHPX);
//       options.setAspectRatioOptions(1,
//                new AspectRatio("WOW", 1, 2),
//                new AspectRatio("MUCH", 3, 4),
//                new AspectRatio("SO", 16, 9),
//                new AspectRatio("ASPECT", 1, 1));

        UCrop.of(Uri.parse(srcImagePath), Uri.fromFile(file))
                .withOptions(options)
                .withAspectRatio(aspectX, aspectY)
                .start(getActivity());
//        Uri uri;
//        File file = new File(DirManager.inst().getDirByType(DirManager.DIR_CACHE, "crop.jpg"));
//        if (file.exists()) file.delete();
//        try
//        {
//            file.createNewFile();
//        } catch (IOException e) {}
//        ImageTools.openCrop(aspectX, aspectY, width, height, false, Uri.parse(srcImagePath),
//                Uri.fromFile(file), ImageTools.REQ_OPEN_CORP, getActivity());
//        PhotoProcActivity.show(srcImagePath, 1.0f * aspectX / aspectY, width, height, ImageTools.REQ_OPEN_CORP, getActivity());
    }

    public static JSONObject packetParam(String url, int aspectX, int aspectY, int maxWHPX,
                                         String fileKey,
                                         String httpKeys, String httpValues, int chooseType)
    {
        JSONObject json = new JSONObject();
        json.put(P_URL, url);
        json.put(P_ASPECTX, aspectX);
        json.put(P_ASPECTY, aspectY);
        json.put(P_FILE_KEY, fileKey);
        json.put(P_MAX_WH_PX, maxWHPX);
        if (!TextUtils.isEmpty(httpKeys)) json.put(P_UP_KEYS, httpKeys);
        if (!TextUtils.isEmpty(httpValues)) json.put(P_UP_VALUES, httpValues);
        json.put(PluginPhotoChoose.CHOOSE_TYPE, chooseType);
        return json;
    }

    public static JSONObject packetParam(String url, int aspectX, int aspectY, int maxWHPX,
                                         String fileKey,
                                         String httpKeys, String httpValues)
    {
        return packetParam(url, aspectX, aspectY, maxWHPX, fileKey, httpKeys, httpValues, PluginPhotoChoose.PHOTO_CHOOSE_CT_ALBUM);
    }

    private void uploadFile(String path, String fileKey)
    {
        if (TextUtils.isEmpty(path)) return;
        //Bitmap bmp = ImageTools.getPhotoFromSDCard(path, width, height);
        final ProgressDialog dlg = ProgressDialog.show(getActivity(), null, "正在上传文件...");
        //final String fileName = ImageTools.savePhotoToSDCard(bmp,
//                                                             CommonUtil.getRootFilePath() + AppUtils.getAppName(getActivity()),
//                                                             "tmp." + Utils.getExtName(path), true);
        HashMap<String,File> files = new HashMap<>();
        files.put(fileKey,new File(path));
        HashMap<String,String> params = null;
        if (upKeys != null && upValues != null)
        {
            params = new HashMap<>();
            String[] ks = upKeys.split(",");
            String[] vs = upValues.split(",");
            for (int i = 0;i < ks.length;i++)
            {
                params.put(ks[i],vs[i]);
            }
        }
        LjhHttpUtils.inst().uploadFile(upUrl, params, files, new LjhHttpUtils.IHttpRespListener()
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
                                return;
                            }
                            event.pluginResult(false, result, null);
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
//        if (requestCode == ImageTools.REQ_OPEN_CORP && data != null)
//        {
//            uploadFile(data.getStringExtra(PhotoProcActivity.RESULT_FILE_NAME));
//        }
        if (requestCode == UCrop.REQUEST_CROP && data != null && event != null)
        {
            final Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null)
            {
                uploadFile(resultUri.getPath(), fileKey);
            }
        }
    }


}
