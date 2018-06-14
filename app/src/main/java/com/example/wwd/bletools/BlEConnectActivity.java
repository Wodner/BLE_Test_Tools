package com.example.wwd.bletools;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.heaton.blelibrary.ble.Ble;
import cn.com.heaton.blelibrary.ble.BleDevice;
import cn.com.heaton.blelibrary.ble.callback.BleScanCallback;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by WWD on 2018/6/11.
 */

public class BlEConnectActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{


    private final String TAG = "BlEConnectActivity";

    private List<BleDevice> mDevicesList = new ArrayList<>();
    private BLEListAdapter mBLEListAdapter = null;

    private List<BleDevice> mConnectLists = new ArrayList<>();



    @BindView(R.id.listview)
    RecyclerView mRecyclerView;

    private final int RC_LOACTION = 123;

    private Ble mBle = null;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        ButterKnife.bind(this);


        initBle();
        //设置RecyclerView管理器
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //初始化适配器
        mBLEListAdapter = new BLEListAdapter(this);
        //设置添加或删除item时的动画，这里使用默认动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //设置适配器
        mRecyclerView.setAdapter(mBLEListAdapter);
        mBLEListAdapter.setData(mDevicesList);
        mBLEListAdapter.setOnSelectListener(mListCheckBoxSelectorListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!mBle.isBleEnable()){
            mBle.turnOnBlueToothNo();
        }
        checkPermission();
    }

    public static void startAction(Context context, Bundle bundle) {
        Intent intent = new Intent(context, BlEConnectActivity.class);
        context.startActivity(intent);
    }



    private void initBle() {
        mBle = Ble.getInstance();
        Ble.Options options = new Ble.Options();
        options.logBleExceptions = true;//设置是否输出打印蓝牙日志
        options.throwBleException = true;//设置是否抛出蓝牙异常
        options.autoConnect = false;//设置是否自动连接
        options.scanPeriod = 12 * 1000;//设置扫描时长
        options.connectTimeout = 10 * 1000;//设置连接超时时长
        options.uuid_service = UUID.fromString("0000fee9-0000-1000-8000-00805f9b34fb");//设置主服务的uuid
        options.uuid_write_cha = UUID.fromString("d44bc439-abfd-45a2-b575-925416129600");//设置可写特征的uuid
        mBle.init(getApplicationContext(), options);
    }

    private BLEListAdapter.ListCheckBoxSelectorListener mListCheckBoxSelectorListener = new BLEListAdapter.ListCheckBoxSelectorListener() {
        @Override
        public void onSelected(BleDevice bleDevice, int position, boolean isCheck) {
            Log.d(TAG,isCheck  + "   " + position);
            if(isCheck){
                if(!mConnectLists.contains(bleDevice)){
                    mConnectLists.add(bleDevice);
                }
            }else{
                if(mConnectLists.contains(bleDevice)){
                    mConnectLists.remove(bleDevice);
                }
            }
            for(int i=0; i<mConnectLists.size(); i++){
                Log.d(TAG,TAG + mConnectLists.get(i).getBleAddress());
            }
        }
    };

    BleScanCallback<BleDevice> scanCallback = new BleScanCallback<BleDevice>() {
        @Override
        public void onLeScan(final BleDevice device, int rssi, byte[] scanRecord) {
            Toast.makeText(BlEConnectActivity.this, "mac:" + device.getBleAddress(), Toast.LENGTH_SHORT).show();
            synchronized (mBle.getLocker()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!mDevicesList.contains(device)) {
                            mDevicesList.add(device);
                            mBLEListAdapter.setData(mDevicesList);
                        }
                    }
                });
            }
        }
    };





    /*--------------android 6.0 起使用蓝牙搜索是需要申请定位功能------以下是权限申请----------------------------------------------------*/

    @AfterPermissionGranted(RC_LOACTION)
    public void checkPermission() {
        String perm[] = {Manifest.permission.ACCESS_COARSE_LOCATION};
        if (EasyPermissions.hasPermissions(this, perm)) {
            Toast.makeText(BlEConnectActivity.this, "开始扫描", Toast.LENGTH_SHORT).show();
            mBle.startScan(scanCallback);

        } else {
            EasyPermissions.requestPermissions(this, "应用需要使用位置权限", RC_LOACTION, perm);
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "权限不允许");
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.d(TAG, "权限允许");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

}
