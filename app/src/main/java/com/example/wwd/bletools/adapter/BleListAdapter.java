package com.example.wwd.bletools.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.wwd.bletools.R;

import java.util.ArrayList;
import java.util.List;

import cn.com.heaton.blelibrary.ble.BleDevice;

/**
 * Created by WWD on 2018/6/11.
 */

public class BleListAdapter extends RecyclerView.Adapter<BleListAdapter.MyViewHolder> {

    private Context mContext = null;
    private List<BleDevice>  mDeviceList = new ArrayList<>();
    private int mListSize = 0;
    private ListCheckBoxSelectorListener mListCheckBoxSelectorListener  = null;

    public BleListAdapter(Context context) {
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
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.tvMac.setText(mDeviceList.get(position).getBleName()  + "\n" + mDeviceList.get(position).getBleAddress());
        holder.checkBox.setChecked(false);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(mListCheckBoxSelectorListener != null){
                    if(mDeviceList.size()>=position){
                        mListCheckBoxSelectorListener .onSelected(mDeviceList.get(position), position ,isChecked);
                    }
                }
            }
        });
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvMac;
        CheckBox checkBox;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvMac = (TextView)itemView.findViewById(R.id.tv_mac);
            checkBox = (CheckBox)itemView.findViewById(R.id.cb_select);
        }

    }

    public void setData(List<BleDevice> list){
        mDeviceList.clear();
        mDeviceList.addAll(list);
        mListSize = mDeviceList.size();
        notifyDataSetChanged();
    }


    public static abstract class ListCheckBoxSelectorListener implements CompoundButton.OnCheckedChangeListener{

        public abstract void onSelected(BleDevice bleDevice, int position, boolean isChecked);

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        }
    }


    public void setOnSelectListener(ListCheckBoxSelectorListener listener){
        mListCheckBoxSelectorListener = listener;
    }


}
