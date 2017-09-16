package com.qk.bluetoothapp.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.qk.bluetoothapp.R;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity implements OnClickListener {

    //	//蓝牙管理器
//    private BluetoothManager mBluetoothManager;
    //蓝牙适配器
    private BluetoothAdapter mBluetoothAdapter;

    //requestCode
    private static final int REQUEST_ENABLE_BT = 1;

    //初始化图片
    private ImageView iv_pump;
    private ImageView iv_sensor;
    private ImageView iv_upgrade;
    private ImageView iv_setting;
    private ImageView iv_personal;
    private ImageView iv_help;
    private Button btn_test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 是否支持低功耗的BLE蓝牙（不支持，直接finish程序）
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // 蓝牙适配器的管理者，通过它获取蓝牙适配器
        BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // 检查设备是否支持蓝牙
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initialize();

        //开始扫描设备
        mBluetoothAdapter.startLeScan(mLeScanCallback);
        //Caused by: java.lang.SecurityException: Need BLUETOOTH permission: Neither user 10207 nor current process has android.permission.BLUETOOTH.
        //没有蓝牙权限

//		mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//		Log.e("TGA", mBluetoothManager.toString());
//		
//		mBluetoothAdapter = mBluetoothManager.getAdapter();
//		Log.e("TAG", mBluetoothAdapter.toString());
//		
//		 //得到一个连接的蓝牙设备
//        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
//        Log.e("TAG", device.getName());

    }

    private void initialize() {
        iv_pump = (ImageView) findViewById(R.id.iv_pump);
        iv_sensor = (ImageView) findViewById(R.id.iv_sensor);
        iv_upgrade = (ImageView) findViewById(R.id.iv_upgrade);
        iv_setting = (ImageView) findViewById(R.id.iv_setting);
        iv_personal = (ImageView) findViewById(R.id.iv_personal);
        iv_help = (ImageView) findViewById(R.id.iv_help);

        btn_test = findViewById(R.id.btn_test);

        iv_pump.setOnClickListener(this);
        iv_sensor.setOnClickListener(this);
        iv_upgrade.setOnClickListener(this);
        iv_setting.setOnClickListener(this);
        iv_personal.setOnClickListener(this);
        iv_help.setOnClickListener(this);
        btn_test.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == iv_pump) {
            Toast.makeText(this, "泵测试", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, PumpActivity.class);
            intent.putExtra("EXTRAS_DEVICE_ADDRESS",getDeviceAddress());
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        } else if (v == iv_sensor) {
            Toast.makeText(this, "传感器测试", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, SensorActivity.class);
            intent.putExtra("EXTRAS_DEVICE_ADDRESS",getDeviceAddress());
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        } else if (v == iv_upgrade) {
            Toast.makeText(this, "在线升级", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, UpgradeActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        } else if (v == iv_setting) {
            Toast.makeText(this, "系统设置", Toast.LENGTH_SHORT).show();

        } else if (v == iv_personal) {
            Toast.makeText(this, "个人中心", Toast.LENGTH_SHORT).show();

        } else if (v == iv_help) {
            Toast.makeText(this, "帮助中心", Toast.LENGTH_SHORT).show();

        } else if (v == btn_test){
            startTest();
        }
        else {
            Toast.makeText(this, "其他", Toast.LENGTH_SHORT).show();
        }
    }

    //获取设备地址
    private String getDeviceAddress(){
        //匹配设备前，停止扫描
        mBluetoothAdapter.stopLeScan(mLeScanCallback);

        String mDeviceAddress = "";
        if(lists.size() > 0){
            for (BluetoothDevice ble: lists) {
                if(ble.getName().equals("HC-08")){
                    mDeviceAddress = ble.getAddress();
                }
            }
        }

        System.out.println("设备地址："+mDeviceAddress);
        if(StringUtils.isEmpty(mDeviceAddress)){
            Toast.makeText(this, "设备不在可用范围内", Toast.LENGTH_SHORT).show();
            return "";
        }
        return mDeviceAddress;
    }

    //测试方法
    private void startTest(){

        //匹配设备前，停止扫描
        mBluetoothAdapter.stopLeScan(mLeScanCallback);

        String mDeviceAddress = "";
        if(lists.size() > 0){
            for (BluetoothDevice ble: lists) {
                if(ble.getName().equals("HC-08")){
                    mDeviceAddress = ble.getAddress();
                }
            }
        }else{
            Toast.makeText(this, "没有设备", Toast.LENGTH_SHORT).show();
            return;
        }

        System.out.println("设备地址："+mDeviceAddress);
//        if(StringUtils.isEmpty(mDeviceAddress)){
//            Toast.makeText(this, "设备不在可用范围内", Toast.LENGTH_SHORT).show();
//            return;
//        }
        //蓝牙测试
        Intent intent1 = new Intent(MainActivity.this, DeviceTestActivity.class);;
        intent1.putExtra(DeviceTestActivity.EXTRAS_DEVICE_NAME, "HC-08");
        intent1.putExtra(DeviceTestActivity.EXTRAS_DEVICE_ADDRESS, mDeviceAddress);
        startActivity(intent1);
    }

    //蓝牙设备集合
    private List<BluetoothDevice> lists = new ArrayList<>();
    //蓝牙扫描
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi,
                             byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    lists.add(device);
                }
            });
        }
    };

    /**
     * 程序开启会调用的方法，作用：
     * 1.询问用户是否开启蓝牙
     * 2.
     * "允许"：初始化列表视图适配器，开启扫描设备
     * "拒绝"：finish掉程序
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // 确保设备上启用蓝牙功能。如果当前不启用蓝牙
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        // 引发一个意图，显示一个对话框，请求用户授予启用它的权限,程序开启的时候调用
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //开启一个需要返回结果的activity
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    //当用户选择不启用来呀，直接finish掉程序
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // User chose not to enable Bluetooth.
        // 用户选择不启用蓝牙
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
    }

    //程序即将被关闭前，释放说有资源
    @Override
    protected void onPause() {
        super.onPause();
    }

}
