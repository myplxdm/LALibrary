package com.liu.lalibrary.utils.imagecache;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;

import com.liu.lalibrary.log.LogUtils;
import com.liu.lalibrary.utils.AppUtils;
import com.liu.lalibrary.utils.Utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Tools for handler picture
 *
 * @author Ryan.Tang
 */
public final class ImageTools
{
    public static final int REQ_CHOOSE_ALBUM = 0x1f1;
    public static final int REQ_TAKE_PIC = 0x1f2;
    public static final int REQ_OPEN_CORP = 0x1f3;

    public static final class ImageSize
    {
        int width;
        int height;

        public ImageSize()
        {
        }

        public ImageSize(int width, int height)
        {
            this.width = width;
            this.height = height;
        }
    }

    /**
     * Transfer drawable to bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable)
    {
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888
                : Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * Bitmap to drawable
     *
     * @param bitmap
     * @return
     */
    public static Drawable bitmapToDrawable(Resources res, Bitmap bitmap)
    {
        return new BitmapDrawable(res, bitmap);
    }

    /**
     * Input stream to bitmap
     *
     * @param inputStream
     * @return
     * @throws Exception
     */
    public static Bitmap inputStreamToBitmap(InputStream inputStream)
            throws Exception
    {
        return BitmapFactory.decodeStream(inputStream);
    }

    /**
     * Byte transfer to bitmap
     *
     * @param byteArray
     * @return
     */
    public static Bitmap byteToBitmap(byte[] byteArray)
    {
        if (byteArray.length != 0)
        {
            return BitmapFactory
                    .decodeByteArray(byteArray, 0, byteArray.length);
        } else
        {
            return null;
        }
    }

    /**
     * Byte transfer to drawable
     *
     * @param byteArray
     * @return
     */
    public static Drawable byteToDrawable(byte[] byteArray)
    {
        ByteArrayInputStream ins = null;
        if (byteArray != null)
        {
            ins = new ByteArrayInputStream(byteArray);
        }
        return Drawable.createFromStream(ins, null);
    }

    /**
     * Bitmap transfer to bytes
     *
     * @param
     * @return
     */
    public static byte[] bitmapToBytes(Bitmap bm)
    {
        byte[] bytes = null;
        if (bm != null)
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
            bytes = baos.toByteArray();
        }
        return bytes;
    }

    /**
     * Drawable transfer to bytes
     *
     * @param drawable
     * @return
     */
    public static byte[] drawableToBytes(Drawable drawable)
    {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        Bitmap bitmap = bitmapDrawable.getBitmap();
        byte[] bytes = bitmapToBytes(bitmap);
        ;
        return bytes;
    }

    /**
     * Base64 to byte[] //
     */
    // public static byte[] base64ToBytes(String base64) throws IOException {
    // byte[] bytes = Base64.decode(base64);
    // return bytes;
    // }
    //
    // /**
    // * Byte[] to base64
    // */
    // public static String bytesTobase64(byte[] bytes) {
    // String base64 = Base64.encode(bytes);
    // return base64;
    // }

    /**
     * Create reflection images
     *
     * @param bitmap
     * @return
     */
    public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap)
    {
        final int reflectionGap = 4;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, h / 2, w,
                                                     h / 2, matrix, false);

        Bitmap bitmapWithReflection = Bitmap.createBitmap(w, (h + h / 2),
                                                          Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmapWithReflection);
        canvas.drawBitmap(bitmap, 0, 0, null);
        Paint deafalutPaint = new Paint();
        canvas.drawRect(0, h, w, h + reflectionGap, deafalutPaint);

        canvas.drawBitmap(reflectionImage, 0, h + reflectionGap, null);

        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
                                                   bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
                                                   0x00ffffff, TileMode.CLAMP);
        paint.setShader(shader);
        // Set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        // Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, h, w, bitmapWithReflection.getHeight()
                + reflectionGap, paint);

        return bitmapWithReflection;
    }

    /**
     * Get rounded corner images
     *
     * @param bitmap
     * @param roundPx 5 10
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx)
    {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Bitmap output = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, w, h);
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /**
     * Resize the bitmap
     *
     * @param bitmap
     * @param width
     * @param height
     * @return
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height)
    {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        return newbmp;
    }

    public static Bitmap scaleAspect(Bitmap bmp, int w, int h)
    {
        int bw = bmp.getWidth();
        int bh = bmp.getHeight();
        if (bw <= w && bh <= h)
        {
            return bmp;
        }
        float ws = w / bw;
        float hs = h / bh;
        float scale = Math.min(ws, hs);
        return ImageTools.zoomBitmap(bmp, (int) (bw * scale), (int) (bh * scale));
    }

    /**
     * Resize the drawable
     *
     * @param drawable
     * @param w
     * @param h
     * @return
     */
    public static Drawable zoomDrawable(Resources res, Drawable drawable,
                                        int w, int h)
    {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap oldbmp = drawableToBitmap(drawable);
        Matrix matrix = new Matrix();
        float sx = ((float) w / width);
        float sy = ((float) h / height);
        matrix.postScale(sx, sy);
        Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
                                            matrix, true);
        return new BitmapDrawable(res, newbmp);
    }

    /**
     * Get images from SD card by path and the name of image
     *
     * @param photoName
     * @return
     */
    public static Bitmap getPhotoFromSDCard(String path, String photoName)
    {
        Bitmap photoBitmap = BitmapFactory.decodeFile(path + "/" + photoName);
        if (photoBitmap == null)
        {
            return null;
        } else
        {
            return photoBitmap;
        }
    }

    public static Bitmap getPhotoFromSDCard(String filepath, int width,
                                            int height)
    {
        if (width != 0 || height != 0)
        {
            return decodeFile(new File(filepath), width, height);
        } else
        {
            return BitmapFactory.decodeFile(filepath);
        }
    }

    public static void getImageSize(File f, ImageSize size)
    {
        try
        {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            size.width = o.outWidth;
            size.height = o.outHeight;
            fis.close();
        } catch (Exception e)
        {
        }
    }

    public static Bitmap decodeFile(File f, int limitWidth, int limitHeight)
    {
        FileInputStream fis = null;
        try
        {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true)
            {
                if (width_tmp / 2 < limitWidth && height_tmp / 2 < limitHeight)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }
            o.inJustDecodeBounds = false;
            o.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o);
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            try
            {
                if (fis != null)
                {
                    fis.close();
                }
            } catch (Exception e2)
            {
                e2.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Check the SD card
     *
     * @return
     */
    public static boolean checkSDCardAvailable()
    {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * Get image from SD card by path and the name of image
     *
     * @param
     * @return
     */
    public static boolean findPhotoFromSDCard(String path, String photoName)
    {
        boolean flag = false;

        if (checkSDCardAvailable())
        {
            File dir = new File(path);
            if (dir.exists())
            {
                File folders = new File(path);
                File photoFile[] = folders.listFiles();
                for (int i = 0; i < photoFile.length; i++)
                {
                    String fileName = photoFile[i].getName().split("\\.")[0];
                    if (fileName.equals(photoName))
                    {
                        flag = true;
                    }
                }
            } else
            {
                flag = false;
            }
            // File file = new File(path + "/" + photoName + ".jpg" );
            // if (file.exists()) {
            // flag = true;
            // }else {
            // flag = false;
            // }

        } else
        {
            flag = false;
        }
        return flag;
    }

    /**
     * Save image to the SD card
     *
     * @param photoBitmap
     * @param photoName
     * @param path
     */
    public static String savePhotoToSDCard(Bitmap photoBitmap, String path,
                                           String photoName, boolean checkSD)
    {
        if (checkSD && !checkSDCardAvailable())
            return null;

        File dir = new File(path);
        if (!dir.exists())
        {
            dir.mkdirs();
        }
        boolean jpg = photoName.endsWith(".jpg") || photoName.endsWith(".jpeg");
        File photoFile = new File(path, photoName);
        FileOutputStream fileOutputStream = null;
        try
        {
            fileOutputStream = new FileOutputStream(photoFile);
            if (photoBitmap != null)
            {
                if (photoBitmap.compress(jpg ? Bitmap.CompressFormat.JPEG
                                                 : Bitmap.CompressFormat.PNG, 100, fileOutputStream))
                {
                    fileOutputStream.flush();
                }
            }
        } catch (FileNotFoundException e)
        {
            photoFile.delete();
            e.printStackTrace();
        } catch (IOException e)
        {
            photoFile.delete();
            e.printStackTrace();
        } finally
        {
            try
            {
                if (fileOutputStream != null) fileOutputStream.close();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return photoFile.getAbsolutePath();
    }

    public static Bitmap photoRotation(Bitmap origin, final int orientationDegree)
    {
        if (origin == null)
        {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(orientationDegree);
        // 围绕原地进行旋转
        return Bitmap.createBitmap(origin, 0, 0, width, height, matrix, true);
//        Matrix m = new Matrix();
//        m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
//        float targetX, targetY;
//        if (orientationDegree == 90)
//        {
//            targetX = bm.getHeight();
//            targetY = 0;
//        } else
//        {
//            targetX = bm.getHeight();
//            targetY = bm.getWidth();
//        }
//
//        final float[] values = new float[9];
//        m.getValues(values);
//
//        float x1 = values[Matrix.MTRANS_X];
//        float y1 = values[Matrix.MTRANS_Y];
//
//        m.postTranslate(targetX - x1, targetY - y1);
//
//        Bitmap bm1 = Bitmap.createBitmap(bm.getHeight(), bm.getWidth(), Bitmap.Config.ARGB_8888);
//        Paint paint = new Paint();
//        Canvas canvas = new Canvas(bm1);
//        canvas.drawBitmap(bm, m, paint);
//
//        return bm1;
//		Matrix m = new Matrix();
//		m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
//		try
//		{
//			Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
//			return bm1;
//		} catch (OutOfMemoryError ex)
//		{
//		}
//		return null;
    }

    /**
     * Delete the image from SD card
     *
     * @param
     * @param path file:///sdcard/temp.jpg
     */
    public static void deleteAllPhoto(String path)
    {
        if (checkSDCardAvailable())
        {
            File folder = new File(path);
            File[] files = folder.listFiles();
            for (int i = 0; i < files.length; i++)
            {
                files[i].delete();
            }
        }
    }

    public static void deletePhotoAtPathAndName(String path, String fileName)
    {
        if (checkSDCardAvailable())
        {
            File folder = new File(path);
            File[] files = folder.listFiles();
            for (int i = 0; i < files.length; i++)
            {
                if (files[i].getName().split("\\.")[0].equals(fileName))
                {
                    files[i].delete();
                }
            }
        }
    }

    public static void takePicture(Fragment act, String path)
    {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri imageUri = Uri.fromFile(new File(path));
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        act.startActivityForResult(openCameraIntent, REQ_TAKE_PIC);
    }

    public static void takePicture(Activity act, String path)
    {
        ImageTools.takePicture(act, path, REQ_TAKE_PIC);
    }

    public static void takePicture(Activity act, String path, int reqCode)
    {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri imageUri;
        if (AppUtils.getOSVersion() >= 24)
        {
            imageUri = FileProvider.getUriForFile(act, act.getPackageName() + ".fileprovider", new File(path));
        }else
        {
            imageUri = Uri.fromFile(new File(path));
        }
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        act.startActivityForResult(openCameraIntent, reqCode);
    }

    public static void chooseAlbum(Fragment act)
    {
        Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
        openAlbumIntent.setType("image/*");
        act.startActivityForResult(openAlbumIntent, REQ_CHOOSE_ALBUM);
    }

    public static void chooseAlbum(Activity act)
    {
        ImageTools.chooseAlbum(act, REQ_CHOOSE_ALBUM);
    }

    public static void chooseAlbum(Activity act, int reqCode)
    {
        Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
        openAlbumIntent.setType("image/*.jpg;image/*.jpeg;image/*.png");
        act.startActivityForResult(openAlbumIntent, reqCode);
    }

    public static String getAlbumPath(Activity act, Intent data)
    {
        try
        {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {Media.DATA};

            Cursor cursor = act.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            if (cursor != null)
            {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                return picturePath;
            } else
            {
                return selectedImage.getPath();
            }
        } catch (Exception e)
        {
            LogUtils.LOGE(ImageTools.class, e.getMessage());
        }
        return null;
    }

    public static Bitmap getAlbumImage(Activity act, Intent data)
    {
        String file = getAlbumPath(act, data);
        if (file == null)
        {
            try
            {
                return Media.getBitmap(act.getContentResolver(), data.getData());
            } catch (Exception e)
            {
            }
            return null;
        } else
        {
            return BitmapFactory.decodeFile(file);
        }
    }

    public static Bitmap getAlbumImage(Activity act, Intent data, int maxPX)
    {
        String file = getAlbumPath(act, data);
        if (file == null)
        {
            try
            {
                Bitmap bmp = Media.getBitmap(act.getContentResolver(), data.getData());
                if (bmp != null)
                {
                    return scaleAspect(bmp, maxPX, maxPX);
                }
            } catch (Exception e)
            {
            }
            return null;
        } else
        {
            return decodeFile(new File(file), maxPX, maxPX);
        }
    }

    public static String[] getAlbumList(Activity act)
    {
        String[] proj = {Media.DATA};
        Cursor cursor = act.getContentResolver().query(
                Media.EXTERNAL_CONTENT_URI, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(Media.DATA);
        int count = cursor.getCount();
        String rets[] = null;
        if (count > 0 && cursor.moveToFirst())
        {
            rets = new String[count];
            int i = 0;
            do
            {
                rets[i++] = cursor.getString(column_index);
            } while (cursor.moveToNext());
        }
        return rets;
    }

    public static void openCrop(int aspectX, int aspectY, int outputX,
                                int outputY, boolean retData, Uri imageUrl, Uri outUrl, int reqCode, Fragment act)
    {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(imageUrl, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("return-data", retData);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outUrl);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        act.startActivityForResult(intent, reqCode);
    }

    public static void openCrop(int aspectX, int aspectY, int outputX,
                                int outputY, boolean retData, Uri imageUrl, Uri outUrl, int reqCode, Activity act)
    {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(imageUrl, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("return-data", retData);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outUrl);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        act.startActivityForResult(intent, reqCode);
    }
}
