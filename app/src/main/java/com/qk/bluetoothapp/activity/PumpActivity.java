package com.qk.bluetoothapp.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.qk.bluetoothapp.R;
import com.qk.bluetoothapp.adapter.PumpAdapter;

import org.apache.commons.lang3.StringUtils;

public class PumpActivity extends Activity {

    private GridView gv_pump;
    private PumpAdapter adapter;
    private BluetoothAdapter mBluetoothAdapter;
    private String mDeviceAddress;

    //设备名称
    String[] names = new String[] { "凯龙", "博世12V", "博世24V", "博世2.0",
            "凯龙-1", "博世12V-1", "博世24V-1", "博世2.0-1",
            "凯龙-2", "博世12V-2", "博世24V-2", "博世2.0-2",};

    //设备图片编号
    int[] icons = new int[] { R.mipmap.pump01, R.mipmap.pump02,
            R.mipmap.pump03, R.mipmap.pump04, R.mipmap.pump05,
            R.mipmap.pump06, R.mipmap.pump07, R.mipmap.pump08,
            R.mipmap.pump09, R.mipmap.pump10, R.mipmap.pump11,
            R.mipmap.pump12};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pump);

        gv_pump = (GridView) findViewById(R.id.gv_pump);
        adapter = new PumpAdapter(this, names, icons);

        Intent intent = getIntent();
        mDeviceAddress = intent.getStringExtra("EXTRAS_DEVICE_ADDRESS");

        gv_pump.setAdapter(adapter);

        //给gridView的Item设置点击监听
        gv_pump.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //得到当前点击的名称
                String name = names[position];
                if(name.equals("凯龙") || name.equals("凯龙-1") || name.equals("凯龙-2")){
                    Toast.makeText(PumpActivity.this, "进入硬驱测试", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PumpActivity.this, HardPumpActivity.class);
                    intent.putExtra("icon", icons[position]);
                    intent.putExtra("EXTRAS_DEVICE_ADDRESS",mDeviceAddress);
                    if(StringUtils.isEmpty(mDeviceAddress)){
                        Toast.makeText(PumpActivity.this, "未连接到可用设备", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }else if(name.equals("博世12V") || name.equals("博世12V-1") || name.equals("博世12V-2")){
                    Toast.makeText(PumpActivity.this, "进入软驱测试", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PumpActivity.this, SoftPumpActivity.class);
                    intent.putExtra("icon", icons[position]);
                    intent.putExtra("EXTRAS_DEVICE_ADDRESS",mDeviceAddress);
                    if(StringUtils.isEmpty(mDeviceAddress)){
                        Toast.makeText(PumpActivity.this, "未连接到可用设备", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                //提示
                Toast.makeText(PumpActivity.this, name, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //退出动画
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
