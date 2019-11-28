package com.liu.app.ui.tableview;

import android.text.TextUtils;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.liu.app.network.LjhHttpUtils;
import com.liu.lalibrary.AbsActivity;
import com.refresh.PullToRefreshBase;
import com.refresh.PullToRefreshBase.OnRefreshListener;

import java.util.List;

/**
 * Created by liu on 2018/3/22.
 */

public abstract class BaseTableViewAdapter extends BaseAdapter implements OnRefreshListener
{
    protected List dataList;
    protected int page = 1;
    protected int totalPage = 1;

    public BaseTableViewAdapter(PullToRefreshBase listview)
    {
        listview.setOnRefreshListener(this);
    }

    public BaseTableViewAdapter(PullToRefreshBase listview, List dataList)
    {
        listview.setOnRefreshListener(this);
        this.dataList = dataList;
    }

    @Override
    public int getCount()
    {
        return dataList != null ? dataList.size() : 0;
    }

    @Override
    public Object getItem(int position)
    {
        return dataList != null ? dataList.get(position) : null;
    }

    public void setData(List list, boolean isAdd)
    {
        if (list == null || list.size() == 0)return;
        if (dataList == null)
        {
            dataList = list;
            return;
        }
        if (!isAdd)
        {
            dataList.clear();
        }
        dataList.addAll(list);
    }

    public void reqUrlData(final boolean isPullDownToRefresh, final PullToRefreshBase pullToRefreshBase)
    {
        String url = getReqUrl(isPullDownToRefresh ? 1 : page + 1);
        if (!TextUtils.isEmpty(url))
        {
            LjhHttpUtils.inst().get(url, new LjhHttpUtils.IHttpRespListener()
            {
                @Override
                public void onHttpReqResult(int state, String result)
                {
                    final AbsActivity activity = getActivity();
                    if (activity == null)return;
                    boolean isUpdate = false;
                    if (state == LjhHttpUtils.HU_STATE_OK && !TextUtils.isEmpty(result))
                    {
                        result = decodeNetResult(result);
                        List list = getData(result);
                        if (list != null && list.size() > 0)
                        {
                            if (isPullDownToRefresh) BaseTableViewAdapter.this.page = 1;
                            else BaseTableViewAdapter.this.page++;

                            setData(list, !isPullDownToRefresh);
                            pullToRefreshBase.setPullLoadEnabled(BaseTableViewAdapter.this.totalPage > BaseTableViewAdapter.this.page);
                            isUpdate = true;
                        }
                    }else
                    {
                        final String finalResult = result;
                        activity.runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Toast.makeText(activity, finalResult, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    final boolean finalIsUpdate = isUpdate;
                    activity.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if (isPullDownToRefresh) pullToRefreshBase.onPullDownRefreshComplete();
                            else pullToRefreshBase.onPullUpRefreshComplete();

                            if (finalIsUpdate) notifyDataSetChanged();
                        }
                    });
                }

                @Override
                public void onHttpReqProgress(float progress)
                {
                }
            });
        }
    }

    @Override
    public void onPullDownToRefresh(final PullToRefreshBase pullToRefreshBase)
    {
        reqUrlData(true, pullToRefreshBase);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase pullToRefreshBase)
    {
        reqUrlData(false, pullToRefreshBase);
    }

    protected abstract String getReqUrl(int page);
    protected abstract List getData(String result);
    protected abstract String decodeNetResult(String result);
    protected abstract AbsActivity getActivity();
}
