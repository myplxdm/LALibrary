package com.liu.app.pluginImpl;

import com.alibaba.fastjson.JSONObject;
import com.liu.app.version.VersionInfo;
import com.liu.app.version.VersionManager;
import com.liu.lalibrary.AbsActivity;
import com.liu.lalibrary.plugins.IPluginEvent;
import com.liu.lalibrary.plugins.PluginBase;
import com.liu.lalibrary.utils.JsonHelper;

/**
 * Created by liu on 2019/1/15.
 */

public class PluginVersion extends PluginBase
{
    public static final String NAME = PluginVersion.class.getSimpleName();
    public static final String CMD_CHECK = "check";
    public static final String CMD_UPDATE = "update";
    public static final String P_URL = "url";
    public static final String P_UPDATE_TYPE = "update_type";
    private VersionManager vm;
    private VersionInfo vi;

    public PluginVersion(AbsActivity activity)
    {
        super(activity);
        vm = new VersionManager();
    }

    @Override
    public String getDescribe()
    {
        return NAME;
    }

    @Override
    public boolean exec(String cmd, final JSONObject params, final IPluginEvent event)
    {
        if (cmd == null || params == null)return false;
        if (cmd.equals(CMD_CHECK))
        {
            vm.req(params.getString(P_URL), new VersionManager.OnVersionListener()
            {
                @Override
                public void onRecvVersion(VersionInfo ver)
                {
                    PluginVersion.this.vi = ver;
                    event.pluginResult(true, CMD_CHECK, ver);
                }

                @Override
                public int onGetUpdateType(VersionInfo ver)
                {
                    return JsonHelper.getInt(params, P_UPDATE_TYPE, VersionManager.VM_TYPE_AUTO);
                }

                @Override
                public AbsActivity onVMGetActivity()
                {
                    return getActivity();
                }
            });
            return true;
        }else if (cmd.equals(CMD_UPDATE))
        {
            if (vi != null)
            {
                vm.update(getActivity(), vi.url);
                return true;
            }
        }
        return false;
    }
}
