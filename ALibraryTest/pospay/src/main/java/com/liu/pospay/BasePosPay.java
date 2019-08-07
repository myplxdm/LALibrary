package com.liu.pospay;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.liu.lalibrary.AbsActivity;

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
        //中国银行总行
        i.setComponent(new ComponentName("com.yada.spos.cashierplatfrom", "com.yada.spos.cashierplatfrom.InvokeActivity"));
        if (pkg.queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY).size() > 0)
            return new BocPosPay();
        //中国银行分行
        i.setComponent(new ComponentName("com.landicorp.boc", "com.landicorp.boc.activity.TopAcitivity"));
        if (pkg.queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY).size() > 0)
            return new BocBranchPosPay();
        //银商
        if (isHasPackname(paramContext, "com.ums.tss.mastercontrol"))
        {
            return new YSPosPay();
        }
        //汇付
        if (isHasPackname(paramContext, "com.chinapnr.nl.addpay"))
        {
            return new HFPosPay();
        }
        return null;
    }

    public static boolean isHasPackname(Context context, String pn)
    {
        try {
            context.getPackageManager()
                    .getApplicationInfo(pn,
                            PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e)
        {
        }
        return false;
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

    @Override
    public void signIn(AbsActivity activity)
    {
    }

    public boolean isActivityResult()
    {
        return true;
    }
}
