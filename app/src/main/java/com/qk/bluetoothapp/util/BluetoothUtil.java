package com.qk.bluetoothapp.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;

/**
 * Created by AN on 2017/8/29.
 */

public class BluetoothUtil {

    public static final int BLUETOOTH_CLOSE = -1;
    public static final int BLUETOOTH_OPEN = 0;

    public static int getBluetoothState(Context context){
        BluetoothManager bluetoothManager =
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();

        if(mBluetoothAdapter != null){
            if(mBluetoothAdapter.isEnabled()){
                return 0;
            }else{
                return -1;
            }
        }
        return -1;
    }
}
