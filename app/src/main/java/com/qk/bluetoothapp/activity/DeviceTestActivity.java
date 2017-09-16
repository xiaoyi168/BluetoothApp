/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qk.bluetoothapp.activity;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qk.bluetoothapp.R;
import com.qk.bluetoothapp.service.BluetoothLeService;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class DeviceTestActivity extends Activity {
    private final static String TAG = DeviceTestActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private TextView mConnectionState;
    private TextView mDataField;
    private String mDeviceName;
    private String mDeviceAddress;
//    private ExpandableListView mGattServicesList;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    boolean connect_status_bit = false;

    private Handler mHandler;

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "初始化蓝牙失败");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                //mConnected = true;


                connect_status_bit = true;

                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;

                updateConnectionState(R.string.disconnected);
                connect_status_bit = false;
                show_view(false);
                invalidateOptionsMenu();
                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    // If a given GATT characteristic is selected, check for supported features.  This sample
    // demonstrates 'Read' and 'Notify' features.  See
    // http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for the complete
    // list of supported characteristic features.
//    private final ExpandableListView.OnChildClickListener servicesListClickListner =
//            new ExpandableListView.OnChildClickListener() {
//                @Override
//                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
//                                            int childPosition, long id) {
//
////                	Log.i("tag", "uu");
////                    if (mGattCharacteristics != null) {
////                        final BluetoothGattCharacteristic characteristic =
////                                mGattCharacteristics.get(groupPosition).get(childPosition);
////                        final int charaProp = characteristic.getProperties();
////                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
////                            // If there is an active notification on a characteristic, clear
////                            // it first so it doesn't update the data field on the user interface.
////                            if (mNotifyCharacteristic != null) {
////                                mBluetoothLeService.setCharacteristicNotification(
////                                        mNotifyCharacteristic, false);
////                                mNotifyCharacteristic = null;
////                            }
////                            mBluetoothLeService.readCharacteristic(characteristic);
////                        }
////                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
////                            mNotifyCharacteristic = characteristic;
////                            mBluetoothLeService.setCharacteristicNotification(
////                                    characteristic, true);
////                        }
////                        return true;
////                    }
//                    return false;
//                }
//            };

    private void clearUI() {
        //mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
        mDataField.setText(R.string.no_data);
    }

    Button send_button;

    EditText txd_txt, rx_data_id_1;

    Button clear_button;

    Timer timer = new Timer();

    void show_view(boolean p) {
        if (p) {
            send_button.setEnabled(true);
        } else {
            send_button.setEnabled(false);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_services_characteristics);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        // Sets up UI references.
        ((TextView)findViewById(R.id.device_name)).setText(mDeviceName);
        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);

        mConnectionState = (TextView) findViewById(R.id.connection_state);
        mDataField = (TextView) findViewById(R.id.data_value);


        send_button = (Button) findViewById(R.id.tx_button);//send data 1002
        send_button.setOnClickListener(listener);//设置监听

        clear_button = (Button) findViewById(R.id.clear_button);//send data 1002
        clear_button.setOnClickListener(listener);//设置监听

        txd_txt = (EditText) findViewById(R.id.tx_text);//1002 data
        txd_txt.setText("0102030405060708090A");

        rx_data_id_1 = (EditText) findViewById(R.id.rx_data_id_1);//1002 data
        rx_data_id_1.setText("");


        show_view(false);
        mHandler = new Handler();

        timer.schedule(task, 1000, 1000); // 1s后执行task,经过1s再次执行

        boolean sg;

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        sg = bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        //getActionBar().setTitle( "="+BluetoothLeService );
        //mDataField.setText("="+sg );
        updateConnectionState(R.string.connecting);
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                //tvShow.setText(Integer.toString(i++));
                //scanLeDevice(true);
                if (mBluetoothLeService != null) {
                    if (mConnected == false) {
                        updateConnectionState(R.string.connecting);
                        final boolean result = mBluetoothLeService.connect(mDeviceAddress);
                        Log.d(TAG, "Connect request result=" + result);
                    }
                }
            }
            super.handleMessage(msg);
        }

        ;
    };
    TimerTask task = new TimerTask() {

        @Override
        public void run() {
            // 需要做的事:发送消息
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };


    Button.OnClickListener listener = new Button.OnClickListener() {//创建监听对象
        public void onClick(View v) {
            //String strTmp="点击Button02";
            //Ev1.setText(strTmp);
            switch (v.getId()) {
                case R.id.tx_button://uuid1002 数传通道发送数据
                    if (connect_status_bit) {
                        String tx_string = txd_txt.getText().toString().trim();
                        mBluetoothLeService.txxx(tx_string);
                    } else {
                        //Toast.makeText(this, "Deleted Successfully!", Toast.LENGTH_LONG).show();
                        Toast toast = Toast.makeText(DeviceTestActivity.this, "设备没有连接！", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    break;
                case R.id.clear_button: {
                    len_g = 0;
                    da = "";
                    rx_data_id_1.setText(da);
                    mDataField.setText("" + len_g);
                }
                break;
                default:
                    break;
            }
        }

    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {

            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
        timer.cancel();
        timer = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    String da = "";
    int len_g = 0;

    private void displayData(String data1) {
        //String head1,data_0;
        /*
		head1=data1.substring(0,2);
		data_0=data1.substring(2);
		*/
        //da = da+data1+"\n";
        if (data1 != null && data1.length() > 0) {
            //mDataField.setText( data1 );
            len_g += data1.length() / 2;
            //da = data1+da;

            rx_data_id_1.setText(data1);
            mDataField.setText("" + len_g);

            //rx_data_id_1.setGravity(Gravity.BOTTOM);
            //rx_data_id_1.setSelection(rx_data_id_1.getText().length());


        }

    }

    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;


        if (gattServices.size() > 0 && mBluetoothLeService.get_connected_status(gattServices) >= 4) {
            if (connect_status_bit) {
                mConnected = true;
                show_view(true);
                mBluetoothLeService.enable_JDY_ble(true);
                try {
                    Thread.currentThread();
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mBluetoothLeService.enable_JDY_ble(true);
                updateConnectionState(R.string.connected);
            } else {
                //Toast.makeText(this, "Deleted Successfully!", Toast.LENGTH_LONG).show();
                Toast toast = Toast.makeText(DeviceTestActivity.this, "设备没有连接！", Toast.LENGTH_SHORT);
                toast.show();
            }
        }


//        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
//                this,
//                gattServiceData,
//                android.R.layout.simple_expandable_list_item_2,
//                new String[] {LIST_NAME, LIST_UUID},
//                new int[] { android.R.id.text1, android.R.id.text2 },
//                gattCharacteristicData,
//                android.R.layout.simple_expandable_list_item_2,
//                new String[] {LIST_NAME, LIST_UUID},
//                new int[] { android.R.id.text1, android.R.id.text2 }
//        );
//
//        mGattServicesList.setAdapter(gattServiceAdapter);

    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
}
