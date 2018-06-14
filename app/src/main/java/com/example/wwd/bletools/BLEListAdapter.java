package com.example.wwd.bletools;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by WWD on 2018/6/11.
 */

public class BLEListAdapter extends RecyclerView.Adapter<BLEListAdapter.MyViewHolder> {

    private Context mContext = null;
    private List<String> mDeviceList = new ArrayList<>();
    private int mListSize = 0;

    public BLEListAdapter(Context context) {
        mContext = context;
    }


    @Override
    public int getItemCount() {
        return mListSize;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device_list, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tvMac.setText(mDeviceList.get(position));
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_mac)
        TextView tvMac;

        public MyViewHolder(View itemView) {
            super(itemView);
        }

    }

    public void setData(List<String> list){
        mDeviceList.clear();
        mDeviceList.addAll(list);
        mListSize = mDeviceList.size();
        notifyDataSetChanged();
    }


}
