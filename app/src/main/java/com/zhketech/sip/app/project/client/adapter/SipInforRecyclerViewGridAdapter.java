package com.zhketech.sip.app.project.client.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhketech.sip.app.project.client.App;
import com.zhketech.sip.app.project.client.R;
import com.zhketech.sip.app.project.client.beans.SipBean;
import com.zhketech.sip.app.project.client.beans.SipClient;
import com.zhketech.sip.app.project.client.global.AppConfig;
import com.zhketech.sip.app.project.client.utils.Logutils;
import com.zhketech.sip.app.project.client.utils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Root on 2018/5/21.
 */

public class SipInforRecyclerViewGridAdapter extends RecyclerView.Adapter<SipInforRecyclerViewGridAdapter.GridViewHolder> {
    private Context mContext;
    private List<SipClient> mDateBeen;
    private MyItemClickListener mItemClickListener;


    public SipInforRecyclerViewGridAdapter(Context context, List<SipClient> dateBeen) {
        mContext = context;
        mDateBeen = dateBeen;
    }

    @Override
    public SipInforRecyclerViewGridAdapter.GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = View.inflate(mContext, R.layout.sipstatus_item, null);

        GridViewHolder gridViewHolder = new GridViewHolder(itemView, mItemClickListener);
        return gridViewHolder;
    }

    @Override
    public void onBindViewHolder(SipInforRecyclerViewGridAdapter.GridViewHolder holder, int position) {
        SipClient dateBean = mDateBeen.get(position);
        holder.setData(dateBean);

    }

    //决定RecyclerView有多少条item
    @Override
    public int getItemCount() {
//数据不为null，有几条数据就显示几条数据
        if (mDateBeen != null && mDateBeen.size() > 0) {
            return mDateBeen.size();
        }
        return 0;
    }

    //自动帮我们写的ViewHolder，参数：View布局对象
    public class GridViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private MyItemClickListener mListener;
        private final TextView item_name;
        private final LinearLayout linearLayout;
        private final LinearLayout main_layout;

        public GridViewHolder(View itemView, MyItemClickListener myItemClickListener) {

            super(itemView);
            item_name = (TextView) itemView.findViewById(R.id.item_name);
            linearLayout = (LinearLayout)itemView.findViewById(R.id.item_layout);
            main_layout = itemView.findViewById(R.id.sipstatus_main_layout);
            this.mListener = myItemClickListener;
            itemView.setOnClickListener(this);

        }

        public void setData(SipClient data) {

            String native_name = (String) SharedPreferencesUtils.getObject(mContext, AppConfig.SIP_NAME_NAVITE,"");

            if (!TextUtils.isEmpty(native_name)){
                if (data.getUsrname().equals(native_name)){
                    item_name.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG ); //中间横线
                    item_name.setTextColor(0xffDC143C);
                }
            }
            if (data.getState().equals("0")){
                item_name.setText("哨位名:"+data.getUsrname());
                linearLayout.setBackgroundResource(R.mipmap.btn_lixian);
            }else if (data.getState().equals("1")){
                item_name.setText("哨位名:"+data.getUsrname());
                linearLayout.setBackgroundResource(R.drawable.sip_call_select_bg);
            }else if (data.getState().equals("2")){
                item_name.setText("哨位名:"+data.getUsrname());
                linearLayout.setBackgroundResource(R.mipmap.btn_zhenling);
            }
            else if (data.getState().equals("3")){
                item_name.setText("哨位名:"+data.getUsrname());
                linearLayout.setBackgroundResource(R.mipmap.btn_tonghua);
            }

        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                main_layout.setBackgroundResource(R.drawable.sip_selected_bg);
                mListener.onItemClick(v, getPosition());
            }

        }
    }

    public interface MyItemClickListener {
        void onItemClick(View view, int position);
    }

    /**
     * 在activity里面adapter就是调用的这个方法,将点击事件监听传递过来,并赋值给全局的监听
     *
     * @param myItemClickListener
     */
    public void setItemClickListener(MyItemClickListener myItemClickListener) {
        this.mItemClickListener = myItemClickListener;
    }
}
