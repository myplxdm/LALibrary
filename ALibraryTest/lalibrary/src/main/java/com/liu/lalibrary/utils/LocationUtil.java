package com.liu.lalibrary.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.liu.app.network.LjhHttpUtils;
import com.liu.lalibrary.AbsActivity;

import java.util.List;

/**
 * Created by liu on 2018/9/8.
 */

public class LocationUtil implements LocationListener
{
    public interface LocationListener
    {
        public void onLocation(Address addr);

        public void onLocationErr(String msg);
    }

    private static class SingletonHolder
    {
        private static final LocationUtil INSTANCE = new LocationUtil();
    }

    private Context context;
    private LocationManager locationManager;
    private LocationListener listener;

    private LocationUtil(){}

    public static final LocationUtil inst()
    {
        return LocationUtil.SingletonHolder.INSTANCE;
    }

    private void getLocation()
    {
        String locationProvider;
        List<String> providers = locationManager.getProviders(true);
        if (providers.contains(LocationManager.GPS_PROVIDER))
        {
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER))
        {
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else
        {
            listener.onLocationErr("没有可用的位置提供器,请打开网络或GPS");
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationProvider);
        if (location != null)
        {
            listener.onLocation(getAddress(context, location));
        }
        locationManager.requestLocationUpdates(locationProvider, 1000, 1, this);
    }

    public void getLocation(final AbsActivity activity, LocationListener listener)
    {
        this.listener = listener;
        context = activity.getApplicationContext();
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        activity.checkPermissions(new PermissionsUtil.PermissionCallback()
        {
            @Override
            public void onPermission(boolean isOK)
            {
                if (isOK)
                {
                    getLocation();
                }
            }
        }, android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    public Address getAddress(Context context, Location loc)
    {
        List<Address> addList = null;
        Geocoder ge = new Geocoder(context);
        try
        {
            addList = ge.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
        } catch (Exception e)
        {
            listener.onLocationErr(e.getMessage());
        }
        if (addList != null && addList.size() > 0)
        {
            Address addr = addList.get(0);
//            StringBuffer stringBuilder = new StringBuffer();
//            stringBuilder.append(addr.getCountryName()).append("_");//国家
//            stringBuilder.append(addr.getFeatureName()).append("_");//周边地址
//            stringBuilder.append(addr.getLocality()).append("_");//市
//            stringBuilder.append(addr.getPostalCode()).append("_");
//            stringBuilder.append(addr.getCountryCode()).append("_");//国家编码
//            stringBuilder.append(addr.getAdminArea()).append("_");//省份
//            stringBuilder.append(addr.getSubAdminArea()).append("_");
//            stringBuilder.append(addr.getThoroughfare()).append("_");//道路
//            stringBuilder.append(addr.getSubLocality()).append("_");//区
//            stringBuilder.append(addr.getLatitude()).append("_");//经度
//            stringBuilder.append(addr.getLongitude());//维度
            return addList.get(0);
        }
        return null;
    }

    @Override
    public void onLocationChanged(Location location)
    {
        if (location != null)
        {
            locationManager.removeUpdates(this);
            listener.onLocation(getAddress(context, location));
            listener = null;
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {

    }

    @Override
    public void onProviderEnabled(String provider)
    {

    }

    @Override
    public void onProviderDisabled(String provider)
    {

    }
}
