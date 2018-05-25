package com.zhketech.sip.app.project.client.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhketech.sip.app.project.client.R;
import com.zhketech.sip.app.project.client.beans.SipBean;
import com.zhketech.sip.app.project.client.beans.SipClient;
import com.zhketech.sip.app.project.client.utils.Logutils;

import java.util.List;

/**
 * Created by Root on 2018/5/21.
 */

public class SipInforRecyclerViewGridAdapter extends RecyclerView.Adapter<SipInforRecyclerViewGridAdapter.GridViewHolder> {
    private Context mContext;
    private List<SipClient> mDateBeen;
    private MyItemClickListener mItemClickListener;
    private List<SipBean> sipListResources ;

    public SipInforRecyclerViewGridAdapter(Context context, List<SipClient> dateBeen, List<SipBean> sipList) {
        mContext = context;
        mDateBeen = dateBeen;
        sipListResources = sipList;
    }

    @Override
    public SipInforRecyclerViewGridAdapter.GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = View.inflate(mContext, R.layout.sipstatus_item, null);
        GridViewHolder gridViewHolder = new GridViewHolder(itemView,mItemClickListener);
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
        private final TextView item_title;
        private final TextView item_name;
        private final TextView isline;

        public GridViewHolder(View itemView, MyItemClickListener myItemClickListener) {

            super(itemView);
            item_title = (TextView) itemView.findViewById(R.id.item_title);
            item_name = (TextView) itemView.findViewById(R.id.item_name);
            isline = (TextView)itemView.findViewById(R.id.isline);
            this.mListener = myItemClickListener;
            itemView.setOnClickListener(this);

        }

        public void setData(SipClient data) {


            for (SipBean s:sipListResources){
                if (s.getNumber().equals(data.getUsrname())){
                    Logutils.i("number:"+data.getUsrname());
                    if (data.getState().equals("0")){
                        isline.setText("离线");
                        item_title.setBackgroundColor(0xff929393);
                    }else if (data.getState().equals("1")){
                        isline.setText("在线");
                        item_title.setBackgroundColor(0xff00AB50);
                    }else if (data.getState().equals("2")){
                        isline.setText("响铃");
                        item_title.setBackgroundColor(0xff8F5AFF);
                    }
                    else if (data.getState().equals("3")){
                        isline.setText("通话");
                        item_title.setBackgroundColor(0xffFD563C);

                    }
                    item_name.setText(data.getUsrname());
                }
            }


       //     Logutils.i("sipListResources----->"+sipListResources.toString());

        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
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
