package com.qk.bluetoothapp.activity;

import android.app.Activity;
import android.os.Bundle;

import com.qk.bluetoothapp.receiver.BluetoothBroReceiver;
import com.qk.bluetoothapp.util.BluetoothUtil;

/**
 * Created by cheng on 2016/11/28.
 */
public class BaseActivity extends Activity implements BluetoothBroReceiver.BluetoothEvevt {

    public static BluetoothBroReceiver.BluetoothEvevt evevt;

    private int state;

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        evevt = this;
        inspect();
    }

    //初始化判断当前的状态
    public void inspect() {
        //检查网络
        this.state = BluetoothUtil.getBluetoothState(BaseActivity.this);
    }

    /**
     * 信号变化之后的类型
     */
    @Override
    public void onBluetoothChange(int n) {
        // TODO Auto-generated method stub
        this.state = state;
        isOpen();
    }

    /**
     * 判断是否打开蓝牙 。
     *
     * @return true 打开, false 关闭.
     */
    public boolean isOpen() {
        if(state == BluetoothUtil.BLUETOOTH_OPEN){
            return true;
        }else if(state == BluetoothUtil.BLUETOOTH_CLOSE){
            return false;
        }
        return false;
    }

}
