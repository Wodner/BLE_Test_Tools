package com.example.wwd.bletools;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
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
    private Queue<BleDevice> mBleConnectDeviceQueues = null;//连接设备队列
    private List<BleDevice> mConnectedLists = new ArrayList<>();//已连接设备列表
    private Queue<BleDevice> mBleDisconnectDeviceQueues = null;//断开连接所需要的设备
    private OnDevicesConnectListener mOnDevicesConnectListener = null;
    private BleHandlerThread mBleHandlerThread = null;
    private Handler mBleHandler = null;

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
        mBleHandlerThread =  new BleHandlerThread("BleHandlerThread");
        mBleHandlerThread.start();
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
        Log.d(TAG,"removeDeviceFormConnectQueen ");
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
        if(mBleHandler!=null){
            mBleHandler.sendEmptyMessage(MSG_CONNECT_BLE_DEVICES);
        }
    }

    private void connect(){
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
    public void startDisConnect(){
        if(mBleHandler!=null){
            mBleHandler.sendEmptyMessage(MSG_DISCONNECT_BLE_DEVICES);
        }
    }

    private void disconnect(){
        for(int i=0; i< mConnectedLists.size(); i++){
            addDeviceToDisconnectQueue(mConnectedLists.get(i));
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
            Log.d(TAG, device.getBleAddress()+" onConnectionChanged : " + device.getConnectionState());
            if(mBleConnectDeviceQueues != null){
                if(device.getConnectionState() == 2505){//connected
                    if(mBleConnectDeviceQueues.contains(device)){
                        Log.d(TAG, " 已连接， 移除出连接队列 ");
                        mBleConnectDeviceQueues.remove(device);
                        mConnectedLists.add(device);
                        if(mOnDevicesConnectListener != null){
                            mOnDevicesConnectListener.onConnected(getConnectedDeviceLists(),mConnectedLists.size());
                        }
                        triggerConnectNextDevice();
                    }
                }else if(device.getConnectionState() == 2504){//connecting
                    Log.d(TAG, " 链接中 ");
                }else{//disconnect
                    Log.d(TAG, " 未连接 ");
                }
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




    interface OnDevicesConnectListener{
        void onConnected(List<BleDevice> deviceList,int size);
    }

    public void setOnConnectListener(OnDevicesConnectListener listener){
        mOnDevicesConnectListener = listener;
    }











    class BleHandlerThread extends HandlerThread{

        public BleHandlerThread(String name){
            super(name);
        }

        @Override
        protected void onLooperPrepared() {
            super.onLooperPrepared();
            mBleHandler = new Handler(getLooper(),mBleHandlerCallback);
        }
    }


    private final int MSG_CONNECT_BLE_DEVICES       = 100;
    private final int MSG_DISCONNECT_BLE_DEVICES    = 101;

    private Handler.Callback mBleHandlerCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch(msg.what){
                case MSG_CONNECT_BLE_DEVICES:
                    connect();
                    break;
                case MSG_DISCONNECT_BLE_DEVICES:
                    disconnect();
                    break;
            }
            return false;
        }
    };



}
