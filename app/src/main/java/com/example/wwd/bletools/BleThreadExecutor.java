package com.example.wwd.bletools;

import android.util.Log;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.com.heaton.blelibrary.ble.BleDevice;

public class BleThreadExecutor {

    private static final String TAG = "TcpExecutor";

    private static BleThreadExecutor instance = null;

    private ThreadPoolExecutor mExecutor = null;
    private Semaphore mSemaphore = null;
    private BleManager mBleManager = null;


    public static BleThreadExecutor getInstance(){
        if(instance == null){
            synchronized (BleThreadExecutor.class){
                if(instance == null){
                    instance = new BleThreadExecutor();
                }
                return instance;
            }
        }
        return instance;
    }


    /**
     * @param bleManager
     */
    public void setBleManager(BleManager bleManager){
        this.mBleManager = bleManager;
    }

    public BleThreadExecutor(){
        Log.d(TAG,"init BleThreadExecutor");
        mExecutor = new ThreadPoolExecutor(1,
                7,
                1,
                TimeUnit.DAYS,
                new ArrayBlockingQueue<Runnable>(6));
        mSemaphore = new Semaphore(1);
    }


    /**
     * @param chars
     */
    public void execute(final byte[] chars) {
        Log.d(TAG, "execute start" + mExecutor.getActiveCount());
        try {
            mSemaphore.acquire();
            if(mBleManager != null){
                for (int i = 0; i < mBleManager.getConnectedDeviceLists().size(); i++) {
                    final BleDevice bleDevice = mBleManager.getConnectedDeviceLists().get(i);
                    mExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                           //write data to ble
                            try {
                                mBleManager.writeBytes(bleDevice,chars);
                                Thread.sleep(25);
                            }catch (InterruptedException e){
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
            while (mExecutor.getActiveCount() != 0);
            mSemaphore.release();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
