package com.qk.bluetoothapp.activity;

import android.bluetooth.BluetoothAdapter;
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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qk.bluetoothapp.R;
import com.qk.bluetoothapp.service.BluetoothLeService;
import com.qk.bluetoothapp.util.BluetoothUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SensorActivity extends BaseActivity implements View.OnClickListener {

	private final static String TAG = SensorActivity.class.getSimpleName();

	private ImageView iv_sensor_icon;
	private Button btn_sensor_start;
	private TextView tv_sensor_state;

	private BluetoothAdapter mBluetoothAdapter;
	private String mDeviceAddress;

	//蓝牙
	private BluetoothLeService mBluetoothLeService;
	private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
			new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
	private BluetoothGattCharacteristic mNotifyCharacteristic;
	private Handler mHandler;
	boolean connect_status_bit = false;
	private Timer timer = new Timer();
	private boolean mConnected = false;

	private ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public final void onServiceConnected(ComponentName componentName, IBinder service) {
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

	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();

			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
				//连接服务
				connect_status_bit = true;
			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
				//失去连接
				connect_status_bit = false;
				mConnected = false;
				tv_sensor_state.setText("断开连接");
			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
				// Show all the supported services and characteristics on the user interface.
				displayGattServices(mBluetoothLeService.getSupportedGattServices());

			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));

			}
		}
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensor);

		initUI();

		//设置图片
		Intent intent = getIntent();
		int icon = intent.getIntExtra("icon", R.mipmap.ic_launcher);
		iv_sensor_icon.setImageResource(icon);
		mDeviceAddress = intent.getStringExtra("EXTRAS_DEVICE_ADDRESS");

		timer.schedule(task, 1000, 1000); // 1s后执行task,经过1s再次执行

		boolean sg;

		System.out.println("初始化");
		Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
		sg = bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

	}

	private void initUI(){
		iv_sensor_icon = findViewById(R.id.iv_sensor_icon);
		btn_sensor_start = findViewById(R.id.btn_sensor_start);
		tv_sensor_state = findViewById(R.id.tv_sensor_state);

		btn_sensor_start.setOnClickListener(this);
	}

	@Override
	public void onBluetoothChange(int state) {
		super.onBluetoothChange(state);
		System.out.println("MainAcitivity的："+state);
		if (state== BluetoothUtil.BLUETOOTH_OPEN){
			tv_sensor_state.setText("正在连接..");
		}else if (state== BluetoothUtil.BLUETOOTH_CLOSE) {
//			finish();
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		//退出动画
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}

	//展示获取的shuju
	private void displayData(String data1) {
		System.out.println("接受的数据："+data1);

	}

	// Demonstrates how to iterate through the supported GATT Services/Characteristics.
	// In this sample, we populate the data structure that is bound to the ExpandableListView
	// on the UI.
	private void displayGattServices(List<BluetoothGattService> gattServices) {
		if (gattServices == null) return;
		if (gattServices.size() > 0 && mBluetoothLeService.get_connected_status(gattServices) >= 4) {
			if (connect_status_bit) {
				mConnected = true;
				tv_sensor_state.setText("连接设备");
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
				Toast toast = Toast.makeText(SensorActivity.this, "设备没有连接！", Toast.LENGTH_SHORT);
				toast.show();
			}
		}
	}

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		return intentFilter;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()){
			case R.id.btn_sensor_start:
				if (connect_status_bit) {
					//发送数据
					String tx_string = "0011";
					mBluetoothLeService.txxx(tx_string);
				} else {
					Toast toast = Toast.makeText(SensorActivity.this, "设备没有连接！", Toast.LENGTH_SHORT);
					toast.show();
				}
				break;
		}
	}

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

	private void updateConnectionState(final int resourceId) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				tv_sensor_state.setText(resourceId);
			}
		});
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				if (mBluetoothLeService != null) {
					if (mConnected == false) {
						tv_sensor_state.setText("正在连接..");
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
}
