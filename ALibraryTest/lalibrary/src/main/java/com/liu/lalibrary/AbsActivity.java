package com.liu.lalibrary;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.liu.lalibrary.plugins.IPlugin;
import com.liu.lalibrary.ui.view.BaseView;
import com.liu.lalibrary.ui.view.IView;
import com.liu.lalibrary.utils.PermissionsUtil;
import com.zhy.autolayout.AutoLayoutActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by liu on 2017/4/10.
 */

public abstract class AbsActivity extends AutoLayoutActivity
{
    public static final String PARAM_OBJ = "obj";
    public static final int REQ_CODE_EXIT_TO = 0xff1010;//回退时带的数据

    public interface ActivityListener
    {
        public void onAllActivityClose();
    }

    //
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    private static ArrayList<AbsActivity> actList = new ArrayList<AbsActivity>();
    public static AbsActivity curActivity;
    public static AbsActivity exitToActivity;
    public static Intent exitToData;
    public static ActivityListener listener;
    //
    protected boolean mToBack = false;
    protected boolean mMaskBack = false;
    protected long preBackTime;
    protected PermissionsUtil permissionsUtil;
    //sub view
    protected ArrayList<IView> subViews = new ArrayList<>();
    //plugins
    protected ArrayList<IPlugin> plugins = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(getRootViewId());
        addActivity(this);
        //
        onInitView();
        activeVPState(IView.VIEW_EVENT_INIT_VIEW);

        onInitData(getIntent());
        activeVPState(IView.VIEW_EVENT_INIT_DATA);
    }

    protected void addSubView(BaseView bv)
    {
        subViews.add(bv);
    }

    public void addPlugin(IPlugin plg)
    {
        plugins.add(plg);
    }

    public IPlugin getPluginByName(String name)
    {
        for (IPlugin plg : plugins)
        {
            if (plg.getName().equals(name))
            {
                return plg;
            }
        }
        return null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            if (mMaskBack) return true;
            if (mToBack)
            {
                moveTaskToBack(true);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    //isMaskBack 是否屏蔽返回键
    //isToBack 按返回键让app转入后台
    public void setBackFun(boolean isMaskBack, boolean isToBackgroud)
    {
        mMaskBack = isMaskBack;
        mToBack = isToBackgroud;
    }

    public void maskBack(boolean isMask)
    {
        mMaskBack = isMask;
    }

    public void backToBackgroud(boolean isBack)
    {
        mToBack = isBack;
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        activeVPState(IView.VIEW_EVENT_START);
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        activeVPState(IView.VIEW_EVENT_RESTART);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (exitToActivity != null && exitToActivity == this && exitToData != null)
        {
            onActivityResult(REQ_CODE_EXIT_TO, RESULT_OK, exitToData);
            exitToActivity = null;
            exitToData = null;
        }
        activeVPState(IView.VIEW_EVENT_RESUME);
        curActivity = this;
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        activeVPState(IView.VIEW_EVENT_PAUSE);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        activeVPState(IView.VIEW_EVENT_STOP);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (curActivity == this)
        {
            curActivity = null;
        }
        activeVPState(IView.VIEW_EVENT_DESTRORY);
        subViews.clear();
        for (int i = plugins.size() - 1;i > -1;i--)
        {
            plugins.get(i).stopPlugin();
        }
        plugins.clear();
        delActivity(this);
    }

    protected void activeVPState(int event)
    {
        if (subViews.size() == 0) return;
        switch (event)
        {
            case IView.VIEW_EVENT_INIT_VIEW:
                for (IView bv : subViews)
                {
                    bv.onInitView();
                }
                break;
            case IView.VIEW_EVENT_INIT_DATA:
                for (IView bv : subViews)
                {
                    bv.onInitData();
                }
                break;
            case IView.VIEW_EVENT_START:
                for (IView bv : subViews)
                {
                    bv.onStart();
                }
                for (IPlugin plg : plugins)
                {
                    plg.onStart();
                }
                break;
            case IView.VIEW_EVENT_RESUME:
                for (IView bv : subViews)
                {
                    bv.onResume();
                }
                for (IPlugin plg : plugins)
                {
                    plg.onResume();
                }
                break;
            case IView.VIEW_EVENT_RESTART:
                for (IView bv : subViews)
                {
                    bv.onRestart();
                }
                for (IPlugin plg : plugins)
                {
                    plg.onRestart();
                }
                break;
            case IView.VIEW_EVENT_PAUSE:
                for (IView bv : subViews)
                {
                    bv.onPause();
                }
                for (IPlugin plg : plugins)
                {
                    plg.onPause();
                }
                break;
            case IView.VIEW_EVENT_STOP:
                for (IView bv : subViews)
                {
                    bv.onStop();
                }
                for (IPlugin plg : plugins)
                {
                    plg.onStop();
                }
                break;
            case IView.VIEW_EVENT_DESTRORY:
                for (IView bv : subViews)
                {
                    bv.onDestroy();
                }
                for (IPlugin plg : plugins)
                {
                    plg.onDestroy();
                }
                break;
        }
    }

    public ViewGroup loadLayout(int rid, ViewGroup parent, boolean bAttRoot)
    {
        return (ViewGroup) LayoutInflater.from(this).inflate(rid, parent, bAttRoot);
    }

    public ViewGroup getRootViewGroup()
    {
        return (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
    }

    public void hideInputMethod(View view)
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void delayedFinish(View v, int time)
    {
        v.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                finish();
            }
        }, time);
    }


    public int generateViewId()
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
        {
            for (; ; )
            {
                final int result = sNextGeneratedId.get();
                // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
                int newValue = result + 1;
                if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
                if (sNextGeneratedId.compareAndSet(result, newValue))
                {
                    return result;
                }
            }
        } else
        {
            return View.generateViewId();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        for (IView bv : subViews)
        {
            bv.onActivityResult(requestCode, resultCode, data);
        }
        for (IPlugin plg : plugins)
        {
            plg.onActivityResult(requestCode, resultCode, data);
        }
    }

    /*************** Permissions *****************/

    public synchronized void checkPermissions(PermissionsUtil.PermissionCallback cb, String... permissions)
    {
        if (permissionsUtil == null)
        {
            permissionsUtil = new PermissionsUtil(this);
        }
        permissionsUtil.checkPermissions(cb, permissions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissionsUtil != null)
        {
            permissionsUtil.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /*************** abstract *****************/
    protected abstract int getRootViewId();

    protected abstract void onInitView();

    protected abstract void onInitData(Intent data);

    /*************  static fun  ****************/
    public static void addActivity(AbsActivity act)
    {
        actList.add(act);
    }

    private static void checkAllClose()
    {
        if (actList.size() == 0 && listener != null)
        {
            listener.onAllActivityClose();
        }
    }

    public static void delActivity(AbsActivity act)
    {
        if (actList.size() > 0)
        {
            int pos = actList.indexOf(act);
            if (pos != -1)
            {
                actList.remove(pos);
            }
        }
        checkAllClose();
    }

    public static void closeAll()
    {
        for (int i = actList.size() - 1; i >= 0; i--)
        {
            actList.get(i).finish();
        }
    }

    //从当前界面返回到第几个界面 a -> b -> c ,num = 1 , back to b
    public static void exitToNum(int num, Intent data)
    {
        if (num < 0) return;
        exitToActivity = actList.get(num);
        exitToData = data;
        for (int i = actList.size() - 1; i > num; i--)
        {
            actList.get(i).finish();
        }
    }

    public static void popNum(int num, Intent data)
    {
        if (num >= actList.size())return;
        AbsActivity.exitToNum(actList.size() - num - 1, data);
    }

    public static void openNewTask(AbsActivity act, Class cls)
    {
        Intent i = new Intent(act, cls);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        act.startActivity(i);
    }

    public static void show(AbsActivity act, Class cls, int reqCode)
    {
        Intent i = new Intent(act, cls);
        act.startActivityForResult(i, reqCode);
    }

    public static void show(AbsActivity act, Class cls)
    {
        Intent i = new Intent(act, cls);
        act.startActivity(i);
    }

    public static void show(AbsActivity act, Bundle data, Class cls)
    {
        Intent i = new Intent(act, cls);
        i.putExtras(data);
        act.startActivity(i);
    }

    public static void show(AbsActivity act, Serializable obj, Class cls, int reqCode)
    {
        Intent i = new Intent(act, cls);
        if (obj != null)
        {
            Bundle b = new Bundle();
            b.putSerializable(PARAM_OBJ, obj);
            i.putExtras(b);
        }
        if (reqCode < 0)
        {
            act.startActivity(i);
            return;
        }
        act.startActivityForResult(i, reqCode);
    }
}
