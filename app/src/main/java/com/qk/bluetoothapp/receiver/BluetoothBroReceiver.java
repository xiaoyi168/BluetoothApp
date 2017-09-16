package com.qk.bluetoothapp.receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.qk.bluetoothapp.activity.BaseActivity;
import com.qk.bluetoothapp.util.BluetoothUtil;

/**
 * Created by AN on 2017/8/29.
 */

public class BluetoothBroReceiver extends BroadcastReceiver {

    public BluetoothEvevt evevt = BaseActivity.evevt;

    @Override
    public void onReceive(Context context, Intent intent) {
        //判断是哪种广播
        if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            int workState = BluetoothUtil.getBluetoothState(context);
            // 接口回调传过去状态的类型
            evevt.onBluetoothChange(workState);
        }
    }


    // 自定义接口
    public interface BluetoothEvevt {
        public void onBluetoothChange(int state);
    }
}
