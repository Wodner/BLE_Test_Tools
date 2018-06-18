package com.example.wwd.bletools;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *
 */
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.listview_step)
    RecyclerView mListviewStep;
    @BindView(R.id.listview_heart)
    RecyclerView mLstviewHeart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
    }


    private void initView() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
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

    @OnClick({R.id.btn_connect_bles, R.id.btn_set_motor, R.id.btn_sync_time, R.id.btn_set_off,
            R.id.btn_get_step, R.id.btn_get_heart, R.id.btn_device_recover})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_connect_bles:
                BleConnectActivity.startAction(this, null);
                break;
            case R.id.btn_set_motor:
                BleManager.getInstance().setVibrationMotor();
                break;
            case R.id.btn_sync_time:
                BleManager.getInstance().setSyncTime();
                break;
            case R.id.btn_set_off:
                setDeivcePowerOff();
                break;

            case R.id.btn_get_step:
                readStep();
                break;
            case R.id.btn_get_heart:
                readHeart();
                break;
            case R.id.btn_device_recover:
                setDeviceRecovery();
                break;
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
        }, 3000);
    }

    private void readHeart() {
        BleManager.getInstance().readHeart(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                BleManager.getInstance().readHeart(false);//5s后停止测试
            }
        }, 15000);
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
