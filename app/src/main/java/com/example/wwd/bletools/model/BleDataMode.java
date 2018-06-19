package com.example.wwd.bletools.model;

/**
 * 作者：Created by WWD .
 * 邮箱：wuwende@live.cn
 * 日期：on 2018/6/19 .
 */

public class BleDataMode {

    private String mMac;
    private int mData;
    private String mName;

    public BleDataMode(){}

    public BleDataMode(String mac, String name, int data){
        this.mMac = mac;
        this.mName = name;
        this.mData = data;
    }


    public String getmMac() {
        return mMac;
    }

    public void setmMac(String mMac) {
        this.mMac = mMac;
    }

    public int getmData() {
        return mData;
    }

    public void setmData(int mData) {
        this.mData = mData;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    @Override
    public String toString() {
        return "BleDataMode{" +
                "mMac='" + mMac + '\'' +
                ", mData=" + mData +
                ", mName='" + mName + '\'' +
                '}';
    }
}
