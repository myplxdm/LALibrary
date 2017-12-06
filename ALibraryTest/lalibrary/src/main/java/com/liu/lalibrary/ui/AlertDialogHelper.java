package com.liu.lalibrary.ui;

import android.content.DialogInterface;

import java.lang.reflect.Field;

/**
 * Created by liu on 2017/12/5.
 */

public class AlertDialogHelper
{
    public static void closeDlg(DialogInterface dlg, boolean isMaskClose)
    {
        try
        {
            Field field = dlg.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dlg, !isMaskClose);
            dlg.dismiss();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
