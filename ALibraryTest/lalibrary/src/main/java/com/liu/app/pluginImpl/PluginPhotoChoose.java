package com.liu.app.pluginImpl;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;

import com.alibaba.fastjson.JSONObject;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.liu.app.DirManager;
import com.liu.lalibrary.AbsActivity;
import com.liu.lalibrary.plugins.IPluginEvent;
import com.liu.lalibrary.plugins.PluginBase;
import com.liu.lalibrary.utils.AppUtils;
import com.liu.lalibrary.utils.PermissionsUtil;
import com.liu.lalibrary.utils.imagecache.ImageTools;

/**
 * Created by liu on 2017/9/1.
 */

public class PluginPhotoChoose extends PluginBase
{
    public static final String NAME = "photo_choose";
    public static final String CHOOSE_TYPE = "ct";
    public static final int PHOTO_CHOOSE_CT_ALBUM = 0;//相册
    public static final int PHOTO_CHOOSE_CT_TAKEPIC = 1;//相机
    public static final int PHOTO_CHOOSE_CT_BOTH = 2;//
    private final String TAKE_PHOTO_NAME = "photo.jpg";
    private AlertView photoAlertView;
    private IPluginEvent event;
    private int chooseType = 0;//0 相册，1 相机 2 相册与相机

    public PluginPhotoChoose(AbsActivity activity)
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
        return "choose photo";
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
        this.event = event;
        if (params != null)
        {
            chooseType = params.getIntValue(CHOOSE_TYPE);
        }
        if (AppUtils.getOSVersion() < 23)
        {
            photoPermiss.onPermission(true);
        } else
        {
            AbsActivity act = getActivity();
            if (act != null)
            {
                act.checkPermissions(photoPermiss, Manifest.permission.CAMERA,
                                     Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                     Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
        return true;
    }

    private PermissionsUtil.PermissionCallback photoPermiss = new PermissionsUtil.PermissionCallback()
    {
        @Override
        public void onPermission(boolean b)
        {
            if (b)
            {
                if (chooseType == 2)
                {
                    if (photoAlertView == null)
                    {
                        photoAlertView = new AlertView("上传图片", null, "取消", null,
                                new String[]{"从相机打开", "从相册打开"}, wrActivity.get(),
                                AlertView.Style.ActionSheet, new OnItemClickListener()
                        {
                            @Override
                            public void onItemClick(Object o, int position)
                            {
                                if (position == 0)
                                {
                                    ImageTools.takePicture(getActivity(), DirManager.inst().getDirByType(DirManager.DIR_CACHE, TAKE_PHOTO_NAME));
                                } else if (position == 1)
                                {
                                    ImageTools.chooseAlbum(getActivity());
                                }
                            }
                        });
                    }
                    photoAlertView.show();
                }else if (chooseType == 0) //相册
                {
                    ImageTools.chooseAlbum(getActivity());
                }else //相机
                {
                    ImageTools.takePicture(getActivity(), DirManager.inst().getDirByType(DirManager.DIR_CACHE, TAKE_PHOTO_NAME));
                }
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
        {
            if (requestCode == ImageTools.REQ_TAKE_PIC)
            {
                event.pluginResult(true, DirManager.inst().getDirByType(DirManager.DIR_CACHE, TAKE_PHOTO_NAME), null);
            } else if (requestCode == ImageTools.REQ_CHOOSE_ALBUM)
            {
                String path = ImageTools.getAlbumPath(wrActivity.get(), data);
                if (path == null)
                {
                    Bitmap bmp = ImageTools.getAlbumImage(wrActivity.get(), data);
                    if (bmp != null)
                    {
                        path = ImageTools.savePhotoToSDCard(bmp, DirManager.inst().getDirByType(DirManager.DIR_CACHE),
                                                            TAKE_PHOTO_NAME, true);
                        if (path != null) event.pluginResult(true, path, null);
                    }
                    event.pluginResult(false, null, null);
                    return;
                }
                event.pluginResult(true, path, null);
            }
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        event = null;
        photoAlertView = null;
    }
}
