package com.liu.alibrarytest;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.liu.app.JniApi;
import com.liu.app.network.NetResult;
import com.liu.app.pluginImpl.PluginContacts;
import com.liu.app.ui.tableview.BaseTableViewAdapter;
import com.liu.app.web.WebShellActivity;
import com.liu.lalibrary.AbsActivity;
import com.liu.lalibrary.plugins.IPluginEvent;
import com.liu.lalibrary.utils.FontLoader;
import com.refresh.PullToRefreshBase;
import com.refresh.PullToRefreshMenuView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by liu on 2017/11/24.
 */

public class TestActivity extends AbsActivity
{
    @BindView(R.id.tvHeart)
    TextView tvHeart;
    @BindView(R.id.tvActivity)
    TextView tvActivity;
    @BindView(R.id.rbHelpPoor)
    RadioButton rbHelpPoor;
    @BindView(R.id.rbActivity)
    RadioButton rbActivity;
    @BindView(R.id.rbCF)
    RadioButton rbCF;
    @BindView(R.id.prlHP)
    PullToRefreshMenuView prlHP;
    @BindView(R.id.prlAct)
    PullToRefreshMenuView prlAct;
    @BindView(R.id.prlCF)
    PullToRefreshMenuView prlCF;
    @BindView(R.id.btnSeller)
    Button btnSeller;
    PullToRefreshMenuView[] lvs;

    @Override
    protected int getRootViewId()
    {
        return R.layout.vp_cw;
    }

    @Override
    public void onInitView()
    {
        ButterKnife.bind(this);
        FontLoader.getInst().setFont(0, getRootViewGroup(), true);
        lvs = new PullToRefreshMenuView[]{prlHP,prlAct,prlCF};
        CWAdapter adapter;
        for (int i = 0;i < lvs.length;i++)
        {
            adapter = new CWAdapter(lvs[i], i);
            adapter.type = i;
            lvs[i].getRefreshableView().setAdapter(adapter);
            lvs[i].setTag(adapter);
            adapter.reqUrlData(true, lvs[i]);
        }
    }

    @Override
    public void onInitData(Intent data)
    {
    }

    private void updateTopData()
    {
    }

    @OnClick({R.id.btnSeller,R.id.rbHelpPoor, R.id.rbActivity, R.id.rbCF})
    public void onClick(View view)
    {
        lvs[0].setVisibility(View.GONE);
        lvs[1].setVisibility(View.GONE);
        lvs[2].setVisibility(View.GONE);
        switch (view.getId())
        {
            case R.id.btnSeller:
                break;
            case R.id.rbHelpPoor:
                lvs[0].setVisibility(View.VISIBLE);
                break;
            case R.id.rbActivity:
                lvs[1].setVisibility(View.VISIBLE);
                break;
            case R.id.rbCF:
                lvs[2].setVisibility(View.VISIBLE);
                break;
        }
    }

    //-------------------------------------------------------------------------------------

    class ViewHolder
    {
        RelativeLayout rlLeft;
        ImageView ivLPhoto;
        TextView tvLName;
        LinearLayout llLPU;
        TextView tvLAddr;
        TextView tvLPrice;
        TextView tvLUnit;
        //
        RelativeLayout rlRight;
        ImageView ivRPhoto;
        TextView tvRName;
        LinearLayout llRPU;
        TextView tvRAddr;
        TextView tvRPrice;
        TextView tvRUnit;
    }

    class CWAdapter extends BaseTableViewAdapter implements View.OnClickListener
    {

        private int type;
        public Class dataCls;

        public CWAdapter(PullToRefreshBase listview, int type)
        {
            super(listview);
            Class[] cs = new Class[]{Goods.class, CWActivity.class, BaseModel.class};
            dataCls = cs[type];
        }

        @Override
        protected String getReqUrl(int page)
        {
            String[] urls = new String[]{Config.HOST + "/api/helppool?",
                    Config.HOST + "/api/publicwelfare?",
                    Config.HOST + "/api/donation?"};
            String url = JniApi.inst().reqEncode(urls[type], null,
                    new String[]{"uid","shopId","page","t"},
                    new String[]{"259", "102",String.valueOf(page),
                            String.valueOf(System.currentTimeMillis())});
            return url;
        }

        @Override
        protected List getData(NetResult netResult)
        {
            if (netResult.state == Api.STATE_CODE_OK)
            {
                if (netResult.getObj().containsKey("totalcount"))
                {
                    totalPage = Integer.valueOf(netResult.getObj().getString("totalcount"));
                }
                return JSON.parseArray(netResult.getObj().getString("list"), dataCls);
            }
            return null;
        }

        @Override
        protected String decodeNetResult(String s)
        {
            return Uri.decode(JniApi.inst().decodeResult(s));
        }

        @Override
        protected AbsActivity getActivity()
        {
            return TestActivity.this;
        }

        @Override
        public long getItemId(int i)
        {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup)
        {
            ViewHolder vh;
            if (view == null)
            {
                view = loadLayout(R.layout.lv_cw, viewGroup, false);
                vh = new ViewHolder();
                vh.rlLeft = (RelativeLayout) view.findViewById(R.id.rlLeft);
                vh.ivLPhoto = (ImageView) view.findViewById(R.id.ivLPhoto);
                vh.tvLName = (TextView) view.findViewById(R.id.tvLName);
                vh.llLPU = (LinearLayout) view.findViewById(R.id.llLPU);
                vh.tvLAddr = (TextView) view.findViewById(R.id.tvLAddr);
                vh.tvLPrice = (TextView) view.findViewById(R.id.tvLPrice);
                vh.tvLUnit = (TextView) view.findViewById(R.id.tvLUnit);
                vh.rlLeft.setOnClickListener(this);
                //
                vh.rlRight = (RelativeLayout) view.findViewById(R.id.rlRight);
                vh.ivRPhoto = (ImageView) view.findViewById(R.id.ivRPhoto);
                vh.tvRName = (TextView) view.findViewById(R.id.tvRName);
                vh.llRPU = (LinearLayout) view.findViewById(R.id.llRPU);
                vh.tvRAddr = (TextView) view.findViewById(R.id.tvRAddr);
                vh.tvRPrice = (TextView) view.findViewById(R.id.tvRPrice);
                vh.tvRUnit = (TextView) view.findViewById(R.id.tvRUnit);
                vh.rlRight.setOnClickListener(this);
                view.setTag(vh);
            }else
            {
                vh = (ViewHolder) view.getTag();
            }
            setDataList(dataList, i, vh);
            return view;
        }

        private void setDataList(List<DataBase> list, int index, ViewHolder vh)
        {
            int ai;
            RelativeLayout[] rls = new RelativeLayout[]{vh.rlLeft,vh.rlRight};
            ImageView[] ps = new ImageView[]{vh.ivLPhoto,vh.ivRPhoto};
            TextView[] ns = new TextView[]{vh.tvLName,vh.tvRName};
            LinearLayout[] lls = new LinearLayout[]{vh.llLPU,vh.llRPU};
            TextView[] adds = new TextView[]{vh.tvLAddr,vh.tvRAddr};
            TextView[] prs = new TextView[]{vh.tvLPrice,vh.tvRPrice};
            TextView[] uns = new TextView[]{vh.tvLUnit,vh.tvRUnit};
            BaseModel bm;
            Goods g;
            CWActivity act;
            for (int i = index; i < list.size() && i < index + 2; i++)
            {
                ai = i - index;
                rls[ai].setVisibility(View.VISIBLE);
                rls[ai].setTag((BaseModel)list.get(i));
                switch (type)
                {
                    case 0:
                        g = (Goods)list.get(i);
                        Glide.with(getActivity()).load(g.photo).into(ps[ai]);
                        ns[ai].setText(g.title);
                        adds[ai].setText(g.address);
                        prs[ai].setText(g.price);
                        break;
                    case 1:
                        act = (CWActivity)list.get(i);
                        Glide.with(getActivity()).load(act.photo).into(ps[ai]);
                        ns[ai].setText(act.title);
                        adds[ai].setText(act.address);
                        prs[ai].setVisibility(View.GONE);
                        uns[ai].setVisibility(View.GONE);
                        break;
                    case 2:
                        bm = (BaseModel) list.get(i);
                        Glide.with(getActivity()).load(bm.photo).into(ps[ai]);
                        ns[ai].setText(bm.title);
                        lls[ai].setVisibility(View.GONE);
                        break;
                }
            }
        }

        @Override
        public void onClick(View view)
        {
            BaseModel bm = (BaseModel) view.getTag();
            WebShellImpl.openWindow(getActivity(), WebShellImpl.class, R.mipmap.btn_return_b, bm.url,bm.title,false);
        }
    }
}
