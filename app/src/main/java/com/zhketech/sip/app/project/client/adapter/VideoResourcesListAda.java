package com.zhketech.sip.app.project.client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zhketech.sip.app.project.client.R;
import com.zhketech.sip.app.project.client.beans.VideoBen;

import java.util.List;

/**
 * Created by Root on 2018/4/5.
 * list展示数据的适配器
 */

public class VideoResourcesListAda extends BaseAdapter {

    List<VideoBen> listResources;
    Context mContext;

    public VideoResourcesListAda(List<VideoBen> listResources, Context mContext) {
        this.listResources = listResources;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return listResources.size();
    }

    @Override
    public Object getItem(int i) {
        return listResources.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.listview_item, null);
            viewHolder.tv = (TextView) view.findViewById(R.id.list_item_text);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        String name = listResources.get(i).getName();
        viewHolder.tv.setText(name);
        return view;
    }

    class ViewHolder {
        TextView tv;
    }

}
