package com.liu.lalibrary.camera.text;

import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.liu.lalibrary.AbsActivity;
import com.liu.lalibrary.R;
import com.liu.lalibrary.ui.view.BaseView;
import com.liu.lalibrary.utils.KeyBoardUtils;

/**
 * Created by liu on 2017/1/18.
 */

public class TextInputView extends BaseView implements View.OnClickListener
{
    private EditText edt_text;
    private ImageButton ib_ok;
    private TextInputListener listener;

    public interface TextInputListener
    {
        public void onTextInput(String text);
    }

    public TextInputView(AbsActivity activity, ViewGroup vg, int rid)
    {
        super(activity, vg, rid);
        edt_text = (EditText) rootViewGroup.findViewById(R.id.edt_text);
        ib_ok = (ImageButton) rootViewGroup.findViewById(R.id.ib_ok);
        ib_ok.setOnClickListener(this);
    }

    public void setListener(TextInputListener listener)
    {
        this.listener = listener;
    }

    @Override
    public void setShow(boolean bShow)
    {
        super.setShow(bShow);
        if (bShow)
        {
            edt_text.requestFocus();
        }else
        {
            edt_text.clearFocus();
        }
    }

    @Override
    public void onClick(View v)
    {
        String text = edt_text.getText().toString();
        if (listener != null)
        {
            listener.onTextInput(text);
        }
        KeyBoardUtils.closeKeybord(edt_text, edt_text.getContext());
    }
}
