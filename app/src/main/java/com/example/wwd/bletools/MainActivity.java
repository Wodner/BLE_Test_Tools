package com.example.wwd.bletools;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.heaton.blelibrary.ble.Ble;
import cn.com.heaton.blelibrary.ble.BleDevice;
import cn.com.heaton.blelibrary.ble.callback.BleWriteCallback;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.btn_connect_bles)
    Button btnConnectBles;
    @BindView(R.id.btn_set_motor)
    Button btnSetMotor;


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

    @OnClick({R.id.btn_connect_bles, R.id.btn_set_motor})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_connect_bles:
                BleConnectActivity.startAction(this, null);
                break;
            case R.id.btn_set_motor:
                BleManager.getInstance().setVibrationMotor();
                break;

        }
    }
}
