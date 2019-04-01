package com.liu.pospay;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by liu on 2018/8/22.
 */

public abstract class BasePosPay implements IPosPay
{
    public String curNo;
    public int payType;
    public int respTransType;

    public static BasePosPay getPoy(Context paramContext)
    {
        PackageManager pkg = paramContext.getPackageManager();
        Intent i = new Intent("android.intent.action.VIEW");
        i.setComponent(new ComponentName("com.yada.spos.cashierplatfrom", "com.yada.spos.cashierplatfrom.InvokeActivity"));
        if (pkg.queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY).size() > 0)
            return new BocPosPay();
        i.setComponent(new ComponentName("com.landicorp.boc", "com.landicorp.boc.activity.TopAcitivity"));
        if (pkg.queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY).size() > 0)
            return new BocBranchPosPay();
        i.setComponent(null);
        i.setData(Uri.parse("payment//com.pnr.pospp/paymentVoid"));
        if (pkg.queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY).size() > 0)
            return new HFPosPay();
        if (isHasPackname(paramContext, "com.ums.tss.mastercontrol"))
        {
            return new YSPosPay();
        }
        return null;
    }

    public static boolean isPos()
    {
        return android.os.Build.MODEL.indexOf("APOS") != -1;
    }

    public static boolean isHasPackname(Context context, String pn)
    {
        try
        {
            context.getPackageManager()
                    .getApplicationInfo(pn,
                            PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e)
        {
        }
        return false;
    }

    public static boolean isPosFZMarket(Context context)
    {
        return isHasPackname(context, "com.yada.spos.cashierplatfrom");
    }

    protected void insertKV(Intent paramIntent, String[] keys, String[] vals)
    {
        if ((keys != null) && (vals != null) && (keys.length == vals.length))
            for (int i = 0; i < keys.length; i++)
                paramIntent.putExtra(keys[i], vals[i]);
    }

    protected String getCurDateStr()
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

    protected String getCurMDDateStr()
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMdd");
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

    public boolean isActivityResult()
    {
        return true;
    }
}
