package com.liu.app.pluginImpl;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.alibaba.fastjson.JSONObject;
import com.liu.lalibrary.AbsActivity;
import com.liu.lalibrary.plugins.IPluginEvent;
import com.liu.lalibrary.plugins.PluginBase;
import com.liu.lalibrary.utils.PermissionsUtil;

/**
 * Created by liu on 2017/11/24.
 */

public class PluginContacts extends PluginBase
{
    public static final String NAME = PluginContacts.class.getSimpleName();
    public static final String CMD_OPEN_CONTACTS_VIEW = "openContactsView";
    private final int REQ_CODE_CONTACTS = 0xff2;
    private IPluginEvent event;

    public PluginContacts(AbsActivity activity)
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
        if (cmd.equals(CMD_OPEN_CONTACTS_VIEW))
        {
            this.event = event;
            getActivity().checkPermissions(new PermissionsUtil.PermissionCallback()
            {
                @Override
                public void onPermission(boolean isOK)
                {
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    getActivity().startActivityForResult(intent, REQ_CODE_CONTACTS);
                }
            }, Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS);

        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_CONTACTS && data != null)
        {
            Cursor cursor = null;
            Cursor phone = null;
            try
            {
                String[] projections = {ContactsContract.Contacts._ID, ContactsContract.Contacts.HAS_PHONE_NUMBER};
                cursor = getActivity().getContentResolver().query(data.getData(), projections, null, null, null);
                if ((cursor == null) || (!cursor.moveToFirst()))
                {
                    return;
                }
                int _id = cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID);
                String id = cursor.getString(_id);
                int has_ = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER);
                int hasPhoneNumber = cursor.getInt(has_);
                StringBuilder phoneNumbers = new StringBuilder();
                String name = "";
                if (hasPhoneNumber > 0)
                {
                    phone = getActivity().getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="
                                    + id, null, null);
                    while (phone.moveToNext())
                    {
                        int index = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        String number = phone.getString(index);
                        phoneNumbers.append(number);
                        phoneNumbers.append(",");
                        name = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    }

                    event.pluginResult(true, name + "-" + phoneNumbers.toString(), null);
                    return;
                }
                event.pluginResult(false, null, null);
            } catch (Exception e)
            {
                e.printStackTrace();
            } finally
            {
                if (cursor != null) cursor.close();
                if (phone != null) phone.close();
            }
        }
    }
}
