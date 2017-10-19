package com.liu.lalibrary.camera;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.Image;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liu.lalibrary.AbsActivity;
import com.liu.lalibrary.R;
import com.liu.lalibrary.camera.adjust.AdjustOperView;
import com.liu.lalibrary.camera.background.BgCanvaLayout;
import com.liu.lalibrary.camera.filter.FilterOperView;
import com.liu.lalibrary.camera.text.TextInputView;
import com.liu.lalibrary.camera.text.TextOperView;
import com.liu.lalibrary.ui.touchview.TouchPlate;
import com.liu.lalibrary.ui.touchview.TouchTextView;
import com.liu.lalibrary.utils.Utils;
import com.liu.lalibrary.utils.imagecache.ImageTools;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liu on 2016/12/27.
 */

public class PhotoProcActivity extends AbsActivity implements View.OnClickListener,
                                                              AdjustOperView.AdjustOperListener,
                                                              FilterOperView.FilterOperViewListener,
                                                              TextInputView.TextInputListener,
                                                              TouchPlate.TouchPlateListener
{
    private static final String PHOTO_PATH = "photo_path";
    private static final String CANVA_RATION = "canva_ration";
    public static final String RESULT_FILE_NAME = "rfn";
    //title
    private Button                      btn_close;
    private TextView                    tvFinish;
    //content
    private CameraContentFrameLayout    ccfl;
    private BgCanvaLayout               fl_bg_canva;
    private TouchPlate                  touchPlate;
    //setp
    private RelativeLayout              rl_step;
    private Button                      btn_last_setp;
    private TextView                    tv_setp_name;
    private Button                      btn_next_setp;
    private String[]                    step_names = new String[]{"调整","文字"};
    //oper
    private AdjustOperView              adjustOperView;
    private TextOperView                textOperView;
    private FilterOperView              filterOperView;
    private TextInputView               textInputView;
    //
    private int                         cur_page;
    private int                         degree;
    private HashMap<Integer,Bitmap>     bmpCache = new HashMap<Integer, Bitmap>(4);
    private String                      filePath;

    @Override
    protected int getRootViewId()
    {
        return R.layout.activity_photo_proc;
    }

    @Override
    protected void onInitView()
    {
        initView();
        //
        filePath = getIntent().getStringExtra(PHOTO_PATH);
        Bitmap bmp = ImageTools.decodeFile(new File(filePath), 1000, 1000); //BitmapFactory.decodeFile(filePath);
        bmpCache.put(0, bmp);

        fl_bg_canva.setImage(bmp);
        fl_bg_canva.setRation(getIntent().getFloatExtra(CANVA_RATION, 1.0f));
        filterOperView.setImage(bmp);
    }

    @Override
    protected void onInitData(Intent data)
    {
    }

    public static void show(String imgFilePath, float ration, int reqCode, AbsActivity activity)
    {
        Intent i = new Intent(activity, PhotoProcActivity.class);
        i.putExtra(PHOTO_PATH, imgFilePath);
        i.putExtra(CANVA_RATION, ration);
        activity.startActivityForResult(i, reqCode);
    }

    private Bitmap getCacheBmp(int degree)
    {
        if (!bmpCache.containsKey(degree))
        {
            Bitmap tmp = ImageTools.photoRotation(bmpCache.get(0), degree);
            bmpCache.put(degree, tmp);
        }
        return bmpCache.get(degree);
    }

    private void initView()
    {
        //nav
        btn_close = (Button) findViewById(R.id.btn_close);
        btn_close.setOnClickListener(this);
        tvFinish = (TextView) findViewById(R.id.tvFinish);
        tvFinish.setOnClickListener(this);
        //content
        ViewGroup vg = getRootViewGroup();
        for (int i = 0;i < vg.getChildCount();i++)
        {
            View v = vg.getChildAt(i);
            Log.d("11",v.getId() + "");
        }

        ccfl = (CameraContentFrameLayout) findViewById(R.id.ccfl);
        fl_bg_canva = (BgCanvaLayout) findViewById(R.id.fl_bg_canva);
        touchPlate = (TouchPlate) findViewById(R.id.tp);
        touchPlate.setListener(this);
        //
        initStep();
        //oper
        adjustOperView = new AdjustOperView(this, (ViewGroup) findViewById(R.id.adjustLayout), 0);
        adjustOperView.setAdjustOperListener(this);

        textOperView = new TextOperView(this, (ViewGroup) findViewById(R.id.textLayout), 0);
        textOperView.setTouchPlate(touchPlate);

        filterOperView = new FilterOperView(this, (ViewGroup) findViewById(R.id.filterLayout), 0);
        filterOperView.setFilterViewListener(this);

        textInputView = new TextInputView(this, (ViewGroup) findViewById(R.id.text_input), 0);
        textInputView.setListener(this);
    }

    private void initStep()
    {
        rl_step = (RelativeLayout) findViewById(R.id.rl_step);
        btn_last_setp = (Button) findViewById(R.id.btn_last_setp);
        tv_setp_name = (TextView) findViewById(R.id.tv_setp_name);
        btn_next_setp = (Button) findViewById(R.id.btn_next_setp);
        //
        btn_last_setp.setOnClickListener(this);
        btn_next_setp.setOnClickListener(this);
    }

    private void closeWindow()
    {
        AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        dlg.setMessage("您确认要退出吗？").setTitle("提示").setPositiveButton("确定", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                finish();
            }
        }).setNegativeButton("取消",null).show();
    }

    @Override
    public void onClick(View v)
    {
        int i = v.getId();
        if (i == R.id.tvFinish)
        {
            exportImage();
        }else if (i == R.id.btn_close)
        {
            finish();

            /*******  step  *******/
        } else if (i == R.id.btn_last_setp)
        {
            if (cur_page == 0)
            {
                closeWindow();
                return;
            }
            cur_page--;
            tv_setp_name.setText(step_names[cur_page]);
            adjustOperView.setShow(true);
            filterOperView.setShow(false);
            textOperView.setShow(false);
            ccfl.decViewIndex();

        } else if (i == R.id.btn_next_setp)
        {
            if (cur_page == 1)
            {
                exportImage();
                return;
            }
            cur_page++;
            tv_setp_name.setText(step_names[cur_page]);
            adjustOperView.setShow(false);
            filterOperView.setShow(false);
            textOperView.setShow(true);
            ccfl.incViewIndex();

        }
    }

    private void exportImage()
    {
        Bitmap bmp = fl_bg_canva.exportImage();
        touchPlate.drawInBmp(bmp, new Rect(fl_bg_canva.getLeft(), fl_bg_canva.getTop(), fl_bg_canva.getRight(), fl_bg_canva.getBottom()));

        String saveFileName = "tmp." + Utils.getExtName(filePath);
        ImageTools.savePhotoToSDCard(bmp, Utils.getPath(filePath), saveFileName, true);

        Intent rs = new Intent();
        rs.putExtra(RESULT_FILE_NAME, Utils.getPath(filePath) + File.separator + saveFileName);
        setResult(RESULT_OK, rs);
        finish();
    }

    /*************  AdjustOperListener  *******************/
    @Override
    public void onOperFill(boolean isFill)
    {
        fl_bg_canva.setFill(isFill);
    }

    @Override
    public void onShowFilter()
    {
        adjustOperView.setShow(false);
        filterOperView.setShow(true);
        textOperView.setShow(false);
    }

    @Override
    public void onSetBgColor(int color)
    {
        fl_bg_canva.setBackgroundColor(color);
    }

    @Override
    public void onRotate()
    {
        degree = (degree + 90) % 360;
        Bitmap tmp = getCacheBmp(degree);
        fl_bg_canva.setImage(tmp);
        filterOperView.setImage(tmp);
    }

    /*************  FilterOperViewListener  *******************/
    @Override
    public void onFilterUpdate(Bitmap bmp)
    {
        fl_bg_canva.setImage(bmp);
    }

    @Override
    public void onFilterViewFinish(boolean bCancle, Bitmap bmp)
    {
        fl_bg_canva.setImage(bmp);
    }

    @Override
    public void onFilterClose()
    {
        adjustOperView.setShow(true);
        filterOperView.setShow(false);
        textOperView.setShow(false);
    }

    /*************  TextInputListener  *******************/
    @Override
    public void onTextInput(String text)
    {
        TouchTextView ttv = (TouchTextView) touchPlate.getLastView();
        if (ttv != null && !TextUtils.isEmpty(text))
        {
            ttv.setText(text);
        }
        textInputView.setShow(false);
    }

    /*************  TouchPlateListener  *******************/
    @Override
    public void onShowTextInput()
    {
        textInputView.setShow(true);
    }
    /****************************************************/
    public static void show(Activity act, String path)
    {
        Intent i = new Intent(act, PhotoProcActivity.class);
        i.putExtra(PHOTO_PATH, path);
        act.startActivity(i);
    }

    /****************************************************/
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        for (Map.Entry<Integer, Bitmap> entry : bmpCache.entrySet())
        {
            entry.getValue().recycle();
        }
        bmpCache.clear();
        filterOperView.onDestroy();
    }
}
