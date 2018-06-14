package cn.com.heaton.blelibrary.ble.request;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Message;

import java.util.ArrayList;
import java.util.List;

import cn.com.heaton.blelibrary.ble.BleHandler;
import cn.com.heaton.blelibrary.ble.BleDevice;
import cn.com.heaton.blelibrary.ble.BleFactory;
import cn.com.heaton.blelibrary.ble.Ble;
import cn.com.heaton.blelibrary.ble.L;
import cn.com.heaton.blelibrary.ble.BleStates;
import cn.com.heaton.blelibrary.ble.BluetoothLeService;
import cn.com.heaton.blelibrary.ble.annotation.Implement;
import cn.com.heaton.blelibrary.ble.callback.BleConnCallback;

/**
 *
 * Created by LiuLei on 2017/10/21.
 */
@Implement(ConnectRequest.class)
public class ConnectRequest<T extends BleDevice> implements IMessage {

    private static final String TAG = "ConnectRequest";
    private List<BleConnCallback<T>> mConnectCallbacks = new ArrayList<>();
    private BleHandler mHandler;

    private ArrayList<T> mDevices = new ArrayList<>();
    private ArrayList<T> mConnetedDevices = new ArrayList<>();

    protected ConnectRequest() {
        mHandler = BleHandler.getHandler();
        mHandler.setHandlerCallback(this);
    }

    public boolean connect(T device, BleConnCallback<T> lisenter) {
        if (!addBleDevice(device)) {
            return false;
        }
        if(lisenter != null && !mConnectCallbacks.contains(lisenter)){
            this.mConnectCallbacks.add(lisenter);
        }
        boolean result = false;
        BluetoothLeService service = Ble.getInstance().getBleService();
        if (service != null) {
            result = service.connect(device.getBleAddress());
        }
        return result;
    }

    public boolean connect(String address, BleConnCallback<T> lisenter) {
        //check if address is vaild. if don't check it, getRemoteDevice(adress)
        //will throw exception when the address is invalid
        boolean isValidAddress = BluetoothAdapter.checkBluetoothAddress(address);
        if (!isValidAddress) {
            L.d(TAG, "the device address is invalid");
            return false;
        }
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            return false;
        }
        BluetoothDevice device = adapter.getRemoteDevice(address);
        T bleDevice = (T) BleFactory.create(BleDevice.class, Ble.getInstance(), device);
        return connect(bleDevice, lisenter);
    }

    /**
     * 无回调的断开
     * @param device 设备对象
     */
    public void disconnect(BleDevice device) {
        BluetoothLeService service = Ble.getInstance().getBleService();
        if (service != null) {
            service.disconnect(device.getBleAddress());
        }
    }

    /**
     * 带回调的断开
     * @param device 设备对象
     */
    public void disconnect(BleDevice device, BleConnCallback<T> lisenter) {
        if(!mConnectCallbacks.contains(lisenter)){
            this.mConnectCallbacks.add(lisenter);
        }
        BluetoothLeService service = Ble.getInstance().getBleService();
        if (service != null) {
            service.disconnect(device.getBleAddress());
        }
    }

    @Override
    public void handleMessage(Message msg) {
        L.e(TAG, "handleMessage: "+msg.arg1);
        T t = null;
        if (msg.obj instanceof BluetoothDevice) {
            t = getBleDevice((BluetoothDevice) msg.obj);
        }
        if (t == null) return;
        switch (msg.what) {
            case BleStates.BleStatus.ConnectException:
                int errorCode = msg.arg1;
                //Disconnect and clear the connection
                for (BleConnCallback<T> callback : mConnectCallbacks){
                    callback.onConnectException(t, errorCode);
                }
                mHandler.obtainMessage(BleStates.BleStatus.ConnectionChanged, 0, 0, msg.obj).sendToTarget();
                break;
            case BleStates.BleStatus.ConnectionChanged:
                if (msg.arg1 == 1) {
                    //connect
                    t.setConnectionState(BleStates.BleStatus.CONNECTED);
                    mConnetedDevices.add(t);
                    L.e(TAG, "handleMessage:++++CONNECTED ");
//                        //After the success of the connection can be considered automatically reconnect
//                        device.setAutoConnect(true);
//                        //If it is automatically connected device is removed from the automatic connection pool
//                        removeAutoPool(device);
                } else if (msg.arg1 == 0) {
                    //disconnect
                    t.setConnectionState(BleStates.BleStatus.DISCONNECT);
                    mConnetedDevices.remove(t);
                    mDevices.remove(t);
                    L.e(TAG, "handleMessage:++++DISCONNECT ");
//                    //移除通知
//                    Ble.getInstance().cancelNotify(t);
//                    addAutoPool(device);
                } else if (msg.arg1 == 2) {
                    //connectting
                    t.setConnectionState(BleStates.BleStatus.CONNECTING);
                }
                for (BleConnCallback<T> callback : mConnectCallbacks){
                    callback.onConnectionChanged(t);
                }
                break;
            default:
                break;
        }
    }

    private boolean addBleDevice(T device) {
        if (device == null || mDevices.contains(device)) {
            L.i(TAG, "addBleDevice" + "Already contains the device");
            return false;
        }
        mDevices.add(device);
        L.i(TAG, "addBleDevice" + "Added a device to the device pool");
        return true;
    }

    //自动连接相关   暂时屏蔽
    /*private void addBleDevice1(T device) {
        if (device == null || mDevices.contains(device)) {
            L.i(TAG, "addBleDevice" + "Already contains the device");
        }else {
            mDevices.add(device);
            L.i(TAG, "addBleDevice" + "Added a device to the device pool");
        }
    }*/

    public T getBleDevice(int index) {
        return mDevices.get(index);
    }

    public T getBleDevice(String address) {
        if(address == null){
            L.w(TAG,"By address to get BleDevice but address is null");
            return null;
        }
        synchronized (mDevices){
            if(mDevices.size() > 0){
                for (T bleDevice : mDevices){
                    if(bleDevice.getBleAddress().equals(address)){
                        L.e(TAG,"By address to get BleDevice and BleDevice is exist");
                        return bleDevice;
                    }
                }
            }
            L.w(TAG,"By address to get BleDevice and BleDevice isn't exist");
            return null;
        }

    }

    /**
     * 获取蓝牙对象
     *
     * @param device 原生蓝牙对象
     * @return 蓝牙对象
     */
    public T getBleDevice(BluetoothDevice device) {
        if (device == null) {
            L.w(TAG, "By BluetoothDevice to get BleDevice but BluetoothDevice is null");
            return null;
        }
        synchronized (mDevices) {
            if (mDevices.size() > 0) {
                for (T bleDevice : mDevices) {
                    if (bleDevice.getBleAddress().equals(device.getAddress())) {
                        L.e(TAG, "By BluetoothDevice to get BleDevice and device is exist");
                        return bleDevice;
                    }
                }
            }
//            T newDevice = (T) BleFactory.create(BleDevice.class, Ble.getInstance(), device);
//            L.e(TAG, "By BluetoothDevice to get BleDevice and device is new");
            L.e(TAG, "By BluetoothDevice to get BleDevice and isn't exist");
            return null;
        }
    }

    /**
     *
     * @return 已经连接的蓝牙设备集合
     */

    public ArrayList<T> getConnetedDevices() {
        return mConnetedDevices;
    }


}
