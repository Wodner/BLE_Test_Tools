package com.example.wwd.bletools;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.wwd.bletools.app.BleManager;
import com.example.wwd.bletools.app.Constant;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *
 */
public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getName();

//    @BindView(R.id.listview_step)
//    RecyclerView mListviewStep;
//    @BindView(R.id.listview_heart)
//    RecyclerView mLstviewHeart;

    private Activity mContext = null;
    private final int  REQUEST_CODE = 100;

//    private DataAdapter mStepAdapter = null;
//    private DataAdapter mHeartAdapter = null;

//    private List<BleDataMode> mBleStepModeList = new ArrayList<>();
//    private List<BleDataMode> mBleHeartModeList = new ArrayList<>();

    private Vibrator mVibrator = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        ButterKnife.bind(this);
        initView();
    }


    private void initView() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
//        //设置RecyclerView管理器
//        mListviewStep.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//        //初始化适配器
//        mStepAdapter = new DataAdapter(this);
//        //设置添加或删除item时的动画，这里使用默认动画
//        mListviewStep.setItemAnimator(new DefaultItemAnimator());
//        //设置适配器
//        mListviewStep.setAdapter(mStepAdapter);
//        mStepAdapter.setData(mBleStepModeList);
//
//        //设置RecyclerView管理器
//        mLstviewHeart.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//        //初始化适配器
//        mHeartAdapter = new DataAdapter(this);
//        //设置添加或删除item时的动画，这里使用默认动画
//        mLstviewHeart.setItemAnimator(new DefaultItemAnimator());
//        //设置适配器
//        mLstviewHeart.setAdapter(mHeartAdapter);
//        mHeartAdapter.setData(mBleHeartModeList);
    }


//    private boolean isContainsStep = false;
//    private boolean isContainsHeart = false;
//    private BleManager.OnStepChangedListener mOnStepChangedListener = new BleManager.OnStepChangedListener() {
//        @Override
//        public void OnStepChanged(BleDevice bleDevice, int step) {
//            String mac = bleDevice.getBleAddress();
//            if(mBleStepModeList.size()==0){
//                BleDataMode bleDataMode = new BleDataMode(bleDevice.getBleAddress(),bleDevice.getBleName(),step);
//                mBleStepModeList.add(bleDataMode);
//            }else{
//                isContainsStep = false;
//                for(int i=0;i<mBleStepModeList.size();i++){
//                    Log.d(TAG,(!mBleStepModeList.get(i).getmMac().equals(mac)) +" <<<<=== >>>> " + mBleStepModeList.get(i).getmMac() + " == " +mac);
//                    if(mBleStepModeList.get(i).getmMac().equals(mac)){
//                        isContainsStep = true;
//                        break;
//                    }
//                }
//                if(!isContainsStep){
//                    BleDataMode bleDataMode_1 = new BleDataMode(bleDevice.getBleAddress(),bleDevice.getBleName(),step);
//                    mBleStepModeList.add(bleDataMode_1);
//                }
//            }
//            Log.d(TAG," step count === >>>> " + mBleStepModeList.size());
//            if(mHandler != null){
//                mHandler.sendEmptyMessage(MSG_UPDATE_STEP);
//            }
//        }
//    };
//
//    private BleManager.OnHeartChangedListener mOnHeartChangedListener = new BleManager.OnHeartChangedListener() {
//        @Override
//        public void OnHeartChanged(BleDevice bleDevice, int heartRate) {
//            String mac = bleDevice.getBleAddress();
//            if(mBleHeartModeList.size()==0){
//                BleDataMode bleDataMode = new BleDataMode(bleDevice.getBleAddress(),bleDevice.getBleName(),heartRate);
//                mBleHeartModeList.add(bleDataMode);
//            }else{
//                isContainsHeart = false;
//                for(int i=0;i<mBleHeartModeList.size();i++){
//                    Log.d(TAG,(!mBleHeartModeList.get(i).getmMac().equals(mac)) +" <<<<=== >>>> " + mBleHeartModeList.get(i).getmMac() + " == " +mac);
//                    if(mBleHeartModeList.get(i).getmMac().equals(mac)){
//                        isContainsHeart = true;
//                        break;
//                    }
//                }
//                if(!isContainsHeart){
//                    BleDataMode bleDataMode_1 = new BleDataMode(bleDevice.getBleAddress(),bleDevice.getBleName(),heartRate);
//                    mBleHeartModeList.add(bleDataMode_1);
//                }
//            }
//            Log.d(TAG," heart count === >>>> " + mBleHeartModeList.size());
//            if(mHandler != null){
//                mHandler.sendEmptyMessage(MSG_UPDATE_HEART);
//            }
//        }
//    };



//    private final int MSG_UPDATE_STEP  = 100;
//    private final int MSG_UPDATE_HEART = 101;
//
//    private Handler mHandler = new Handler(){
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what){
//                case MSG_UPDATE_STEP:
//                    updateStepList();
//                    break;
//                case MSG_UPDATE_HEART:
//                    updataHeartList();
//                    break;
//            }
//        }
//    };

//    private void updateStepList(){
//        if(mStepAdapter != null){
//            mStepAdapter.setData(mBleStepModeList);
//        }
//    }
//
//    private void updataHeartList(){
//        if(mHeartAdapter != null){
//            mHeartAdapter.setData(mBleHeartModeList);
//        }
//    }



    @Override
    protected void onResume() {
        super.onResume();
//        BleManager.getInstance().setOnStepChangedListener(mOnStepChangedListener);
//        BleManager.getInstance().setOnHeartChangedListener(mOnHeartChangedListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        BleManager.getInstance().unRegisterHeartChangedListener();
//        BleManager.getInstance().unRegisterStepChangedListener();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mVibrator.vibrate(Constant.VIRBRATOR_TIME);
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.btn_connect_bles, R.id.btn_find_device, R.id.btn_check_display,R.id.btn_check_sensor,
            R.id.btn_set_off, R.id.btn_check_heart,R.id.btn_check_heart_off})
    public void onViewClicked(View view) {
        mVibrator.vibrate(Constant.VIRBRATOR_TIME);
        switch (view.getId()) {
            case R.id.btn_connect_bles:
                BleConnectActivity.startAction(mContext, null,REQUEST_CODE);
                break;
            case R.id.btn_find_device:
                BleManager.getInstance().findBleDevices();
                break;
//            case R.id.btn_sync_time:
//                BleManager.getInstance().setSyncTime();
//                break;
            case R.id.btn_set_off:
                setDeivcePowerOff();
                break;
            case R.id.btn_check_sensor:
                BleManager.getInstance().checkGsensor();
                break;

            case R.id.btn_check_display:
                BleManager.getInstance().checkDisplay();
                break;
            case R.id.btn_check_heart:
                BleManager.getInstance().readHeart(true);
                break;
            case R.id.btn_check_heart_off:
                BleManager.getInstance().readHeart(false);
                break;
//            case R.id.btn_get_step:
//                readStep();
//                break;
//            case R.id.btn_get_heart:
//                readHeart();
//                break;
//            case R.id.btn_device_recover:
//                setDeviceRecovery();
//                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Toast.makeText(mContext,"连接设备完成",Toast.LENGTH_LONG).show();
        }else if(resultCode == RESULT_CANCELED){
            Toast.makeText(mContext,"设备尚未连接",Toast.LENGTH_LONG).show();
        }
    }

    /**==============================控制设备======start===========================**/

    private void readStep() {
        BleManager.getInstance().readStep(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                BleManager.getInstance().readStep(false);
                ;//5s后停止测试
            }
        }, 5000);
    }

    private void readHeart() {
        BleManager.getInstance().readHeart(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                BleManager.getInstance().readHeart(false);//5s后停止测试
            }
        }, 5000);
    }


    private void setDeviceRecovery() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("恢复出厂设置");
        builder.setMessage("是否确认恢复出厂设置?");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BleManager.getInstance().setDeviceRecovery();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void setDeivcePowerOff() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("关闭设备");
        builder.setMessage("是否确认关闭?");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BleManager.getInstance().setPowerOff();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**==============================控制设备======end===========================**/

}
