package com.qk.bluetoothapp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.qk.bluetoothapp.R;

/**
 * Created by AN on 2017/8/29.
 */

public class PumpAdapter extends BaseAdapter {
    private String[] names;
    private int[] icons;
    private Context context;

    public PumpAdapter(Context context, String[] names, int[] icons) {
        super();
        this.context = context;
        this.names = names;
        this.icons = icons;
    }

    @Override
    public int getCount() {
        return names.length;
    }

    @Override
    public Object getItem(int position) {
        return names[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView==null) {
            convertView = View.inflate(context, R.layout.item_pump, null);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_item_icon);
//        TextView textView = (TextView) convertView.findViewById(R.id.tv_item_name);
        imageView.setImageResource(icons[position]);
//        textView.setText(names[position]);

        return convertView;
    }
}
