package com.liu.lalibrary.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by liu on 2017/4/14.
 */

public class TimeUtils
{
    public static boolean isMiddle(String startTime, String endTime, String curTime)
    {
        long sT = new Date(startTime).getTime();
        long sE = new Date(endTime).getTime();
        long sC = new Date(curTime).getTime();
        return sC >= sT && sC <= sE;
    }

    public static boolean isMiddle(String startTime, String endTime) throws ParseException
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long sT = sdf.parse(startTime).getTime();
        long sE = sdf.parse(endTime).getTime();
        long sC = new Date().getTime();
        return sC >= sT && sC <= sE;
    }

    public static boolean isLessThan(String startTime, String curTime) throws ParseException
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.parse(curTime).getTime() < sdf.parse(startTime).getTime();
    }

    public static boolean isLessThan(String startTime) throws ParseException
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return new Date().getTime() < sdf.parse(startTime).getTime();
    }
}
