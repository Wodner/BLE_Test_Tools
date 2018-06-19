package com.example.wwd.bletools;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.wwd.bletools.adapter.BleListAdapter;
import com.example.wwd.bletools.app.BleManager;
import com.example.wwd.bletools.app.Constant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.heaton.blelibrary.ble.Ble;
import cn.com.heaton.blelibrary.ble.BleDevice;
import cn.com.heaton.blelibrary.ble.callback.BleScanCallback;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by WWD on 2018/6/11.
 */

public class BleConnectActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks,
        SwipeRefreshLayout.OnRefreshListener {


    private final String TAG = "BleConnectActivity";


    private List<BleDevice> mDevicesList = new ArrayList<>();//扫描设备列表
    private BleListAdapter mBleListAdapter = null;
    private List<BleDevice> mToConnectLists = new ArrayList<>();//需要连接的设备
    private int mTotalConnectDevices = 0;



    @BindView(R.id.listview)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefresh;

    private final int RC_LOACTION = 123;
    private Ble mBle = null;
    private BleManager mBleManager = null;

    private Vibrator mVibrator = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        ButterKnife.bind(this);
        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        initBle();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mBle.isBleEnable()) {
            mBle.turnOnBlueToothNo();
        }
        clearDevicesList();
//        checkPermission();
        startRefresh();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mBle != null){
            mBle.stopScan();
        }
        if(mBleManager != null){
            mBleManager.unRegisterConnectListener();
        }

    }

    private void clearDevicesList(){
        if(mDevicesList != null){
            mDevicesList.clear();
        }
    }

    public static void startAction(Context context, Bundle bundle) {
        Intent intent = new Intent(context, BleConnectActivity.class);
        context.startActivity(intent);
    }


    private void initView(){
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //设置RecyclerView管理器
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //初始化适配器
        mBleListAdapter = new BleListAdapter(this);
        //设置添加或删除item时的动画，这里使用默认动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //设置适配器
        mRecyclerView.setAdapter(mBleListAdapter);
        mBleListAdapter.setData(mDevicesList);
        mBleListAdapter.setOnSelectListener(mListCheckBoxSelectorListener);


        mSwipeRefresh.setColorSchemeColors(Color.RED, Color.RED);//改变加载显示的颜色
        mSwipeRefresh.setBackgroundColor(Color.WHITE);//设置背景颜色
        mSwipeRefresh.setSize(SwipeRefreshLayout.LARGE);//设置初始时的大小
        mSwipeRefresh.setOnRefreshListener(this);//设置监听
        mSwipeRefresh.setDistanceToTriggerSync(100);//设置向下拉多少出现刷新
        mSwipeRefresh.setProgressViewEndTarget(false, 200);//设置刷新出现的位置

        mBleManager.setOnConnectListener(new BleManager.OnDevicesConnectListener() {
            @Override
            public void onConnected(List<BleDevice> deviceList, int size) {
                Log.d(TAG,"连接设备数 ： " + size);
                Toast.makeText(BleConnectActivity.this,"连接设备数：" + size,Toast.LENGTH_SHORT).show();


            }
        });
    }
    /**
     * 初始化
     */
    private void initBle() {
        mBle = Ble.getInstance();
        mBleManager = BleManager.getInstance();
        Ble.Options options = new Ble.Options();
        options.logBleExceptions = true;//设置是否输出打印蓝牙日志
        options.throwBleException = true;//设置是否抛出蓝牙异常
        options.autoConnect = true;//设置是否自动连接
        options.scanPeriod = 5 * 1000;//设置扫描时长
        options.connectTimeout = 10 * 1000;//设置连接超时时长
        options.uuid_service = Constant.SERVICE_UUID;//设置主服务的uuid
        options.uuid_write_cha = Constant.CHARACTERISTIC_WRITE_UUID;//设置可写特征的uuid
        options.uuid_notify =Constant.CHARACTERISTIC_NOTIFY_UUID;//设置通知特征的uuid
        options.uuid_read_cha = Constant.CHARACTERISTIC_READ_UUID;//设置可读特征的uuid
        mBle.init(getApplicationContext(), options);
    }


    @Override
    public void onRefresh() {
        startRefresh();
    }

    /**
     * 下拉刷新开始扫描BLE设备
     */
    private void startRefresh() {
        clearDevicesList();
        mBleListAdapter.setData(mDevicesList);
        mBle.stopScan();
        mBleManager.startDisConnect();
        mBleManager.clearConnectQueue();
        checkPermission();
    }

    /**
     * 选择要连接的设备
     */
    private BleListAdapter.ListCheckBoxSelectorListener mListCheckBoxSelectorListener = new BleListAdapter.ListCheckBoxSelectorListener() {
        @Override
        public void onSelected(BleDevice bleDevice, int position, boolean isCheck) {
            Log.d(TAG, isCheck + "  click : " + position);
            mVibrator.vibrate(Constant.VIRBRATOR_TIME);
            if(mBleManager.getConnectQueen().size()>=6){
                Toast.makeText(BleConnectActivity.this,getResources().getString(R.string.over_max_connect_devices),Toast.LENGTH_LONG).show();
                return;
            }else{
                if (isCheck) {
                    if(!mToConnectLists.contains(bleDevice)){
                        mToConnectLists.add(bleDevice);
                    }
                    mBleManager.addDeivceToConnectQueen(bleDevice);
                } else {
                    if(mToConnectLists.contains(bleDevice)){
                        mToConnectLists.remove(bleDevice);
                    }
                    mBleManager.removeDeviceFormConnectQueen(bleDevice);
                }
            }
            Log.d(TAG, " queen :" + mBleManager.getConnectQueen().size());

        }
    };

    /**
     * 扫描BLE设备回调
     */
    BleScanCallback<BleDevice> mScanCallback = new BleScanCallback<BleDevice>() {
        @Override
        public void onLeScan(final BleDevice device, int rssi, byte[] scanRecord) {
            synchronized (mBle.getLocker()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!mDevicesList.contains(device)) {
                            mDevicesList.add(device);
                            mBleListAdapter.setData(mDevicesList);
                        }
                    }
                });
            }
        }

        @Override
        public void onStop() {
            super.onStop();
            mSwipeRefresh.setRefreshing(false);
        }
    };





    /*--------------android 6.0 起使用蓝牙搜索是需要申请定位功能------以下是权限申请----------------------------------------------------*/

    @AfterPermissionGranted(RC_LOACTION)
    public void checkPermission() {
        String perm[] = {Manifest.permission.ACCESS_COARSE_LOCATION};
        if (EasyPermissions.hasPermissions(this, perm)) {
            Toast.makeText(BleConnectActivity.this, getResources().getString(R.string.start_scan), Toast.LENGTH_SHORT).show();
            mBle.startScan(mScanCallback);
        } else {
            EasyPermissions.requestPermissions(this, getResources().getString(R.string.need_location_permission), RC_LOACTION, perm);
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

    @OnClick({R.id.btn_connect, R.id.btn_disconnect})
    public void onViewClicked(View view) {
        mVibrator.vibrate(Constant.VIRBRATOR_TIME);
        switch (view.getId()) {
            case R.id.btn_connect:
                if(mBleManager.getConnectQueen().size() > 0){
                    mBleManager.startConnect();
                }else {
                    Toast.makeText(BleConnectActivity.this, "请先选择要连接的设备", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btn_disconnect:
                if(mBleManager.getConnectedDeviceLists().size() > 0){
                    mTotalConnectDevices = mBleManager.getConnectQueen().size();
                    mBleManager.startDisConnect();
                    mBleListAdapter.setData(mDevicesList);
                }else{
                    Toast.makeText(BleConnectActivity.this,"当前没有连接设备",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
