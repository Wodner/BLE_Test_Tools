package com.example.wwd.bletools;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

import cn.com.heaton.blelibrary.ble.Ble;
import cn.com.heaton.blelibrary.ble.BleDevice;
import cn.com.heaton.blelibrary.ble.callback.BleConnCallback;

/**
 * Created by WWD on 2018/6/15.
 */

public class BleManager {

    private final static String TAG = "BleManager";
    private static BleManager instance = null;
    private Ble mBle = null;
    private Queue<BleDevice> mBleConnectDeviceQueues = null;
    private List<BleDevice> mConnectedLists = new ArrayList<>();//已连接设备列表
    private Queue<BleDevice> mBleDisconnectDeviceQueues = null;


    public BleManager(){
        init();
    }




    public static synchronized BleManager getInstance(){
        if(instance == null){
            synchronized (BleManager.class){
                if(instance == null){
                    instance = new BleManager();
                }
                return  instance;
            }

        }
        return instance;
    }


    private void init(){
        mBleConnectDeviceQueues = new ConcurrentLinkedDeque<>();
        mBleDisconnectDeviceQueues = new ConcurrentLinkedDeque<>();
        mBle = Ble.getInstance();
    }


    /**
     * @param device
     */
    public void addDeivceToConnectQueen(BleDevice device){
        Log.d(TAG,"addDeivceToConnectQueen ");
        if(mBleConnectDeviceQueues != null){
            if(!mBleConnectDeviceQueues.contains(device)){
                mBleConnectDeviceQueues.add(device);
            }
        }
    }

    /**
     * @param device
     */
    public void removeDeviceFormConnectQueen(BleDevice device){
        if(mBleConnectDeviceQueues != null){
            if(mBleConnectDeviceQueues.contains(device)){
                mBleConnectDeviceQueues.remove(device);
            }
        }
    }

    /**
     * @return
     */
    public Queue<BleDevice> getConnectQueen(){
        return mBleConnectDeviceQueues;
    }

    public void clearConnectQueue(){
        if(mBleConnectDeviceQueues != null){
            mBleConnectDeviceQueues.clear();
        }
    }


    /**
     * @param device 添加已连接的设备到断开连接的队列
     */
    private void addDeviceToDisconnectQueue(BleDevice device){
        Log.d(TAG,"addDeviceToDisconnectQueue");
        if(mBleDisconnectDeviceQueues != null){
            if(!mBleDisconnectDeviceQueues.contains(device)){
                mBleDisconnectDeviceQueues.add(device);
            }
        }
    }


    /**
     * 开始连接
     */
    public void startConnect(){
        clearConenctedDevices();
        if(mBleConnectDeviceQueues != null && mBleConnectDeviceQueues.size()>0){
            triggerConnectNextDevice();
        }else{
            Log.e(TAG,"ConnectDeviceQueues IS NULL OR ConnectDeviceQueues SIEZE IS ZERO!!!");
        }
    }


    /**
     * 断开连接
     */
    public void disConnect(){
        for(int i=0; i< mConnectedLists.size(); i++){
            mBleDisconnectDeviceQueues.add(mConnectedLists.get(i));
        }
        triggerDisconnectNextDevice();
        clearConenctedDevices();

    }

    private void clearConenctedDevices(){
        if(mConnectedLists != null){
            mConnectedLists.clear();
        }

    }

    /**
     * 断开下一个连接
     */
    private void triggerDisconnectNextDevice(){
        BleDevice bleDevice = mBleDisconnectDeviceQueues.peek();
        Log.d(TAG,"QUEUE :" + mBleDisconnectDeviceQueues.size());
        if (bleDevice != null){
            Log.i( TAG,"Start trigger disconnect device : " + bleDevice.getBleAddress());
            mBle.disconnect(bleDevice);
            mBle.refreshDeviceCache(bleDevice.getBleAddress());
            mBleDisconnectDeviceQueues.remove(bleDevice);
            try {
                Thread.sleep(25);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            triggerDisconnectNextDevice();
        }
    }


    /**
     * 连接下一个设备
     */
    private void triggerConnectNextDevice(){
        BleDevice bleDevice = mBleConnectDeviceQueues.peek();
        if (bleDevice != null){
            Log.i( TAG,"Start trigger connect device : " + bleDevice.getBleAddress());
            mBle.connect(bleDevice,mBleConnectCallback);
        }
    }


    /**
     * 连接设备回调
     */
    private BleConnCallback<BleDevice> mBleConnectCallback = new BleConnCallback<BleDevice>() {
        @Override
        public void onConnectionChanged(BleDevice device) {
            Log.d(TAG, " onConnectionChanged : " + mBleConnectDeviceQueues.size());
            if(mBleConnectDeviceQueues != null){
                if(mBleConnectDeviceQueues.contains(device)){
                    Log.d(TAG, " 已连接， 移除连接队列 ");
                    mBleConnectDeviceQueues.remove(device);
                }
                if(mBleConnectDeviceQueues.size() != 0){
                    addDeviceToDisconnectQueue(device);
                    mConnectedLists.add(device);
                }
                if(mOnDevicesConnectListener != null){
                    mOnDevicesConnectListener.onConnected(getConnectedDeviceLists(),mBleDisconnectDeviceQueues.size());
                }
                triggerConnectNextDevice();
            }
        }


        @Override
        public void onConnectException(BleDevice device, int errorCode) {
            super.onConnectException(device, errorCode);
            Log.e( TAG,"onConnectException : " + errorCode);
            if(mConnectedLists.contains(device)){
                mConnectedLists.remove(device);
            }
            triggerConnectNextDevice();
        }
    };










    /**写数据*/
    public void writeBytes(String data){
    }

    public List<BleDevice> getConnectedDeviceLists() {
        return mConnectedLists;
    }


    private OnDevicesConnectListener mOnDevicesConnectListener = null;

    interface OnDevicesConnectListener{
        void onConnected(List<BleDevice> deviceList,int size);
    }

    public void setOnConnectListener(OnDevicesConnectListener listener){
        mOnDevicesConnectListener = listener;
    }




}
