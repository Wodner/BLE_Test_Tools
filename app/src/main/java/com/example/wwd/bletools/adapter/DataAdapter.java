package com.example.wwd.bletools.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.wwd.bletools.R;
import com.example.wwd.bletools.model.BleDataMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：Created by WWD .
 * 邮箱：wuwende@live.cn
 * 日期：on 2018/6/19 .
 */

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.MyViewHolder>{

    private final String TAG = "DataAdapter";

    private Context mContext = null;
    private int mListSize = 0;
    private List<BleDataMode> mBleDataModeList = new ArrayList<>();



    public DataAdapter(Context context){
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_data_list,parent,false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tvName.setText(mBleDataModeList.get(position).getmName() + "\n" +  mBleDataModeList.get(position).getmMac());
        holder.tvData.setText("" + mBleDataModeList.get(position).getmData());
    }


    class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView tvName;
        private TextView tvData;

        public MyViewHolder(View itemview){
           super(itemview);
           tvName = (TextView)itemview.findViewById(R.id.tv_name);
           tvData = (TextView)itemview.findViewById(R.id.tv_ble_data);
        }

    }

    public void setData(List<BleDataMode> list){
        mBleDataModeList.clear();
        mBleDataModeList.addAll(list);
        mListSize = mBleDataModeList.size();
        notifyDataSetChanged();
    }





}
