package com.liu.lalibrary.utils;

import android.content.Context;
import android.graphics.Typeface;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by liu on 16/9/14.
 */
public class FontLoader
{
    private ArrayList<Typeface>         _fontList = new ArrayList<>();
    private HashMap<String,Typeface>    _fontMap = new HashMap<>();

    private static FontLoader           _inst = new FontLoader();

    private FontLoader(){}

    public static FontLoader getInst()
    {
        return _inst;
    }

    public void addFontFromAsset(Context context, String path, String fontName)
    {
        Typeface tf = Typeface.createFromAsset(context.getAssets(), path);
        _fontMap.put(fontName, tf);
        _fontList.add(tf);
    }

    public void addFontFromAsset(Context context, String path)
    {
        addFontFromAsset(context, path, path);
    }

    public void addFontFromSdDir(String path)
    {
        File f = new File(SDCardUtils.getSDCardPath() + path);
        Typeface tf;
        if (f.isDirectory())
        {
            for (File file : f.listFiles())
            {
                tf = Typeface.createFromFile(file);
                _fontMap.put(file.getName().substring(0,file.getName().length() - 4), tf);
                _fontList.add(tf);
            }
        }else if (f.isFile())
        {
            tf = Typeface.createFromFile(f);
            _fontMap.put(f.getName().substring(0,f.getName().length() - 4), tf);
            _fontList.add(tf);
        }
    }

    public void clearFont()
    {
        _fontList.clear();
        _fontMap.clear();
    }

    public int getFontCount()
    {
        return _fontList.size();
    }

    public Typeface getFont(int index)
    {
        if (index < _fontList.size())
        {
            return _fontList.get(index);
        }
        return null;
    }

    public ArrayList<Typeface> getFontList()
    {
        return _fontList;
    }

    public HashMap<String, Typeface> getFontMap()
    {
        return _fontMap;
    }
}
