package org.larry.bluetoothconnector.adapter;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.larry.bluetoothconnector.R;
import org.larry.bluetoothconnector.viewholder.BluetoothDeviceViewHolder;

import java.util.ArrayList;

/**
 * Created by Larry on 2015-10-04.
 */
public class BluetoothDeviceAdapter extends BaseAdapter {
	private final String LOG_TAG = getClass().getSimpleName();
	private Activity mActivity = null;
	private ArrayList<BluetoothDevice> mDataList = null;

	public BluetoothDeviceAdapter(Activity activity, ArrayList<BluetoothDevice> dataList) {
		mActivity = activity;
		mDataList = dataList;
	}

	@Override
	public int getCount() {
		return mDataList.size();
	}

	@Override
	public Object getItem(int position) {
		return mDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(mActivity);
		BluetoothDeviceViewHolder viewHolder = null;

		if (convertView != null) {
			viewHolder = (BluetoothDeviceViewHolder) convertView.getTag();
		} else {
			convertView = inflater.inflate(R.layout.item_bluetooth_device, null);
			viewHolder = new BluetoothDeviceViewHolder();
			viewHolder.deviceName = (TextView) convertView.findViewById(R.id.item_bluetooth_device_name);
			viewHolder.deviceAddress = (TextView) convertView.findViewById(R.id.item_bluetooth_device_address);
			convertView.setTag(viewHolder);
		}

		BluetoothDevice selectedItem = mDataList.get(position);
		viewHolder.deviceName.setText(selectedItem.getName());
		viewHolder.deviceAddress.setText(selectedItem.getAddress());
		return convertView;
	}
}
