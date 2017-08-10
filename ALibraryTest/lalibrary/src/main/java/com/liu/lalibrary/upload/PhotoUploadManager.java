package com.liu.lalibrary.upload;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.widget.Toast;

import com.liu.lalibrary.AbsActivity;
import com.liu.lalibrary.camera.PhotoProcActivity;
import com.liu.lalibrary.utils.AppUtils;
import com.liu.lalibrary.utils.PermissionsUtil;
import com.liu.lalibrary.utils.imagecache.CommonUtil;
import com.liu.lalibrary.utils.imagecache.ImageTools;

import org.apache.http.entity.mime.content.FileBody;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by liu on 16/6/3.
 */
public class PhotoUploadManager implements UploadFileManager.UploadProgressEvent
{
    public interface PhotoUploadCompletListener
    {
        public void onPhotoUploadComplet(String ret);
        public void onAddUpdateParam(HttpMultipartEntity entity);
    }

    public static final int				    UP_STATE_START		= 1;
    public static final int				    UP_STATE_PROG		= 2;
    public static final int				    UP_STATE_COMP		= 3;
    public static final int				    UP_STATE_ALL_COMP	= 4;
    public static final int				    UP_STATE_ERR		= 5;
    private final String                    TEMP_BMP_NAME       = "temp.jpg";


    private AbsActivity                     act_own;
    private String                          savePath;
    private PhotoUploadCompletListener      listener;
    private MessageProc					    msg_proc;
    private boolean                         isEditable;
    private String                          uploadUrl;
    public float                           ration;

    public PhotoUploadManager(AbsActivity act, float ration, PhotoUploadCompletListener listener,
                              boolean isEditbale, String uploadUrl)
    {
        act_own = act;
        this.savePath = CommonUtil.getRootFilePath() + AppUtils.getAppName(act) + File.separator;
        this.listener = listener;
        this.isEditable = isEditable;
        this.ration = ration;
        this.uploadUrl = uploadUrl;

        msg_proc = new MessageProc(act_own, listener);
    }

    public void attchListener()
    {
        UploadFileManager.getInst().addEventListener(this);
    }

    public void cancelListener()
    {
        UploadFileManager.getInst().removeEventListener(this);
    }

    public void chooseAlbum(final AbsActivity act)
    {
        if (Build.VERSION.SDK_INT >= 23)
        {
            act.checkPermissions(new PermissionsUtil.PermissionCallback()
            {
                @Override
                public void onPermission(boolean b)
                {
                    ImageTools.chooseAlbum(act);
                }
            }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }else
        {
            ImageTools.chooseAlbum(act);
        }
    }

    public void takePic(AbsActivity act)
    {
        ImageTools.takePicture(act, savePath);
    }

    private void uploadFile(final String file)
    {

        UploadInfo ui = new UploadInfo(uploadUrl)
        {
            @Override
            public void createEntity(HttpMultipartEntity entity)
            {
                listener.onAddUpdateParam(entity);
                entity.addPart("upfile", new FileBody(new File(file)));
            }
        };
        UploadFileManager.getInst().add(ui);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == ImageTools.REQ_CHOOSE_ALBUM)
        {
            String file = ImageTools.getAlbumPath(act_own, data);
            Bitmap bmp = null;
            if (file == null)
            {
                try
                {
                    bmp = MediaStore.Images.Media.getBitmap(act_own.getContentResolver(), data.getData());
                    if (bmp != null)
                    {
                        ImageTools.savePhotoToSDCard(bmp, savePath, TEMP_BMP_NAME, true);
                        file = savePath + TEMP_BMP_NAME;
                    }
                }catch (Exception e)
                {
                }
            }
            if (file != null)
            {
                if (isEditable)
                {
                    PhotoProcActivity.show(file, ration, ImageTools.REQ_OPEN_CORP, act_own);
                }else
                {
                    uploadFile(file);
                }
            }
        }else if (requestCode == ImageTools.REQ_OPEN_CORP)
        {
            uploadFile(savePath + TEMP_BMP_NAME);
        }
    }

    static class MessageProc extends Handler
    {
        WeakReference<Activity> mRef;
        WeakReference<PhotoUploadCompletListener>	mlRef;
        ProgressDialog pd;

        public MessageProc(Activity wa, PhotoUploadCompletListener l)
        {
            mRef = new WeakReference<Activity>(wa);
            mlRef = new WeakReference<PhotoUploadCompletListener>(l);
        }

        @Override
        public void handleMessage(Message msg)
        {
            final Activity wa = mRef.get();
            final PhotoUploadCompletListener pucl = mlRef.get();
            if (wa != null)
            {
                switch (msg.what)
                {
                    case UP_STATE_START:
                        pd = new ProgressDialog(wa);
                        pd.setTitle("上传文件");
                        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        pd.setCancelable(true);
                        pd.setMax(100);
                        pd.show();
                        break;
                    case UP_STATE_PROG:
                        pd.setProgress(msg.arg1);
                        break;
                    case UP_STATE_ALL_COMP:
                        if (pd != null)
                        {
                            pd.dismiss();
                        }
                        pd = null;
                        pucl.onPhotoUploadComplet(((UploadInfo)msg.obj).retString);
                        break;
                    case UP_STATE_ERR:
                        if (pd != null)
                        {
                            pd.dismiss();
                        }
                        pd = null;
                        Toast.makeText(wa, "上传出错", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        }
    }

    /*************************************************************************/
    /*******               UploadProgressEvent                    ************/
    /*************************************************************************/

    @Override
    public void onStartUpload(UploadInfo ui)
    {
        Message msg = Message.obtain();
        msg.what = UP_STATE_START;
        msg_proc.sendMessage(msg);
    }

    @Override
    public void onProgress(UploadInfo ui)
    {
        Message msg = Message.obtain();
        msg.what = UP_STATE_PROG;
        msg.arg1 = (int) ((ui.progress * 1f / ui.totalSize) * 100);
        msg_proc.sendMessage(msg);
    }

    @Override
    public void onUploadComplet(UploadInfo ui)
    {
        Message msg = Message.obtain();
        msg.what = UP_STATE_ALL_COMP;
        msg.obj = ui;
        msg_proc.sendMessage(msg);
    }

    @Override
    public void onUploadAllComplet()
    {
    }

    @Override
    public void onUploadError(UploadInfo ui)
    {
        Message msg = Message.obtain();
        msg.what = UP_STATE_ERR;
        msg_proc.sendMessage(msg);
    }
}
