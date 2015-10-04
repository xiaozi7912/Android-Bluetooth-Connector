package org.larry.bluetoothconnector;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Larry on 2015-10-04.
 */
public class CommunicationActivity extends Activity {
	private final String LOG_TAG = getClass().getSimpleName();

	private TextView mDeviceName = null;
	private TextView mDeviceAddress = null;

	private BluetoothDevice mSelectedDevice = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_communication);

		mSelectedDevice = getIntent().getExtras().getParcelable("DEVICE");
		initView();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void initView() {
		mDeviceName = (TextView) findViewById(R.id.communication_device_name);
		mDeviceAddress = (TextView) findViewById(R.id.communication_device_address);

		mDeviceName.setText(mSelectedDevice.getName());
		mDeviceAddress.setText(mSelectedDevice.getAddress());
	}
}
