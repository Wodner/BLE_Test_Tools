package com.example.wwd.bletools.app;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.example.wwd.bletools.utils.BleThreadExecutor;
import com.example.wwd.bletools.utils.HexStringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

import cn.com.heaton.blelibrary.ble.Ble;
import cn.com.heaton.blelibrary.ble.BleDevice;
import cn.com.heaton.blelibrary.ble.callback.BleConnCallback;
import cn.com.heaton.blelibrary.ble.callback.BleNotiftCallback;
import cn.com.heaton.blelibrary.ble.callback.BleWriteCallback;

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

    private BleThreadExecutor mExecutor = null;

    private OnStepChangedListener mOnStepChangedListener = null;
    private OnHeartChangedListener mOnHeartChangedListener = null;



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
        mExecutor = BleThreadExecutor.getInstance();
        mExecutor.setBleManager(this);
    }


    /* ========================  连接与断开 ========start==================================*/

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
//        clearConenctedDevices();
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
                        setNotify(device);
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


    /* ========================  连接与断开 ========end==================================*/




    /* ========================  外部接口 ========start==================================*/

    public List<BleDevice> getConnectedDeviceLists() {
        return mConnectedLists;
    }


    public interface OnDevicesConnectListener{
        void onConnected(List<BleDevice> deviceList,int size);
    }

    public void setOnConnectListener(OnDevicesConnectListener listener){
        mOnDevicesConnectListener = listener;
    }

    public void unRegisterConnectListener(){
        mOnDevicesConnectListener = null;
    }




    public interface OnStepChangedListener{
        void OnStepChanged(BleDevice bleDevice,int step);
    }

    public void setOnStepChangedListener(OnStepChangedListener listener){
        mOnStepChangedListener = listener;
    }

    public void unRegisterStepChangedListener(){
        mOnStepChangedListener = null;
    }






    public interface OnHeartChangedListener{
        void OnHeartChanged(BleDevice bleDevice,int heartRate);
    }

    public void setOnHeartChangedListener(OnHeartChangedListener listener){
        mOnHeartChangedListener = listener;
    }

    public void unRegisterHeartChangedListener(){
        mOnHeartChangedListener = null;
    }

    /* ========================  外部接口 ========end==================================*/









    /**=========================  给设备写数据 ======== start =========================*/

    /**设置马达震动*/
    public void findBleDevices(){
        Log.d(TAG,"findBleDevices");
        byte [] msg = HexStringUtils.hexString2Bytes("5101");
        mExecutor.execute(msg);
    }



    public void checkGsensor(){
        Log.d(TAG,"checkGsensor");
        byte [] msg = HexStringUtils.hexString2Bytes("AAABAC");
        mExecutor.execute(msg);
    }

    public void checkDisplay(){
        Log.d(TAG,"checkDisplay");
        byte [] msg = HexStringUtils.hexString2Bytes("AAABAD73FF00313233E6B497E79086");
        mExecutor.execute(msg);
    }


    private final int GTM_8 = 28800;
    /**同步时间*/
    public void setSyncTime(){
        Log.d(TAG,"setSyncTime");
        int time = (int) (System.currentTimeMillis()/1000);

        Log.d(TAG,"毫秒数：" + System.currentTimeMillis() + "  总时间秒 ： " +  time);

        byte [] msg =  new byte[12];
        msg[0] = 0x01;
        msg[1] = (byte) ((time >> 24)& 0xff);
        msg[2] = (byte) ((time >> 16)& 0xff);
        msg[3] = (byte) ((time >> 8) & 0xff);
        msg[4] = (byte) (time & 0xff);

        msg[5] = (byte) ((GTM_8 >> 24)& 0xff);
        msg[6] = (byte) ((GTM_8 >> 16)& 0xff);
        msg[7] = (byte) ((GTM_8 >> 8) & 0xff);
        msg[8] = (byte) (GTM_8 & 0xff);
        msg[9] = 0x00;
        msg[10] = 0x00;
        msg[11] = 0x01;
        mExecutor.execute(msg);
    }


    /**恢复出厂设置*/
    public void setDeviceRecovery(){
        Log.d(TAG,"setDeviceRecovery ... ");
        byte [] msg = HexStringUtils.hexString2Bytes("71010203");
        mExecutor.execute(msg);
    }

    /**关机*/
    public void setPowerOff(){
        Log.d(TAG,"setPowerOff");
        byte [] msg = HexStringUtils.hexString2Bytes("AAABAE");
        mExecutor.execute(msg);
    }

    /**读取步数*/
    public void readStep(boolean isOn){
        Log.d(TAG,"readStep ... ");
        if(isOn){
            byte [] msg = HexStringUtils.hexString2Bytes("3101");
            mExecutor.execute(msg);
        }else{
            byte [] msg = HexStringUtils.hexString2Bytes("3100");
            mExecutor.execute(msg);
        }
    }


    /**读取步数*/
    public void readHeart(boolean isOn){
        Log.d(TAG,"readHeart ... " +  isOn);
        if(isOn){
            byte [] msg = HexStringUtils.hexString2Bytes("600001");
            mExecutor.execute(msg);
        }else{
            byte [] msg = HexStringUtils.hexString2Bytes("600000");
            mExecutor.execute(msg);
        }
    }

    /**写数据*/
    public void writeBytes(BleDevice bleDevice,byte[] data){
        Log.d(TAG,"writeBytes");
        if(mBle != null){
            mBle.write(bleDevice, data, mBleDeviceBleWriteCallback);
        }
    }

    private BleWriteCallback<BleDevice> mBleDeviceBleWriteCallback = new BleWriteCallback<BleDevice>() {
        @Override
        public void onWriteSuccess(BluetoothGattCharacteristic characteristic) {
            Log.d(TAG,"onWriteSuccess");
        }
    };

    /**=========================  给设备写数据 ======== end ========================*/



    /**=========================  回调数据处理 ======== start ========================*/


    private void setNotify(final BleDevice  bleDevice){
        if(mBle != null){
            mBle.startNotify(bleDevice, new BleNotiftCallback<BleDevice>() {
                @Override
                public void onChanged(BleDevice device, BluetoothGattCharacteristic characteristic) {
                    Log.d(TAG,"setNotify , onChanged..... " +  device.getBleAddress() + " ==>>> " +
                            HexStringUtils.toHexString(characteristic.getValue()));
                    final byte [] result =  characteristic.getValue();

                    if(result.length > 0){
                        byte TYPE_DATA = result[0];
                        if(TYPE_DATA == Constant.DEVICE_STEP){
                            byte step_1 = result[2];
                            byte step_2 = result[3];
                            byte step_3 = result[4];
                            byte step_4 = result[5];
                            int step =((step_1<<24 & 0xff000000) | (step_2<<16 & 0x00ff0000 ) | (step_3<<8 & 0x0000ff00) | (step_4 & 0xff));

                            byte[] dd = new byte[1];
                            dd[0] = result[4];
                            Log.d(TAG , (0x04<<8|0xf5) +" , " +  ((step_3<<8 & 0x00ff0000) | (step_4 & 0xff))+ " , " + " 实时步数 ==>>>> " + step);

                            if(mOnStepChangedListener != null){
                                mOnStepChangedListener.OnStepChanged(device,step);
                            }
                        }

                        else if(TYPE_DATA == Constant.DEVICE_HEART){
                            byte heart = result[1];
                            Log.d(TAG , " 实时心率 ==>>>> " +heart);
                            if(mOnHeartChangedListener != null){
                                mOnHeartChangedListener.OnHeartChanged(device,heart);
                            }
                        }
                    }
                }

                @Override
                public void onNotifySuccess(BluetoothGatt gatt) {
                    super.onNotifySuccess(gatt);
                    Log.d(TAG,"setNotify , onNotifySuccess.....");
                }

                @Override
                public void onReady(BleDevice device) {
                    super.onReady(device);
                    Log.d(TAG,"setNotify , onReady.....");
                }

                @Override
                public void onServicesDiscovered(BluetoothGatt gatt) {
                    super.onServicesDiscovered(gatt);
                    Log.d(TAG,"setNotify , onServicesDiscovered.....");
                }

            });
        }
    }

    private void processNotifyData(){

    }


    /**=========================  回调数据处理 ======== end ========================*/

    class BleHandlerThread extends HandlerThread{

        public BleHandlerThread(String name){
            super(name);
        }

        @Override
        protected void onLooperPrepared() {
            super.onLooperPrepared();
            if(mBleHandler == null){
                mBleHandler = new Handler(getLooper(),mBleHandlerCallback);
            }
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
