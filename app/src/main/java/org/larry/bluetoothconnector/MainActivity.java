package org.larry.bluetoothconnector;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SlidingDrawer;
import android.widget.Toast;

import org.larry.bluetoothconnector.adapter.BluetoothDeviceAdapter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends Activity {
    private final String LOG_TAG = getClass().getSimpleName();
    private Activity mActivity = this;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BroadcastReceiver mDeviceFoundReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(LOG_TAG, "onReceive");
            String action = intent.getAction();
            Log.v(LOG_TAG, "action : " + action);
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                mDeviceSearchProgress.setVisibility(View.GONE);
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mDeviceList.add(device);
                setDeviceAdapter();
            }
            Log.i(LOG_TAG, "-------------------------");
        }
    };

    private Button mDeviceSearchButton = null;
    private ListView mDeviceListView = null;
    private ProgressBar mDeviceSearchProgress = null;

    private final int REQUEST_ENABLE_BLUETOOTH = 1000;

    private ArrayList<BluetoothDevice> mDeviceList = null;
    private BluetoothDevice mSelectedDevice = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mDeviceFoundReceiver, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mDeviceFoundReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(LOG_TAG, "onActivityResult");
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == RESULT_OK) {
                searchBluetoothDevice();
            }
        }
        Log.i(LOG_TAG, "-------------------------");
    }

    private void initView() {
        Log.i(LOG_TAG, "initView");
        mDeviceSearchButton = (Button) findViewById(R.id.main_device_search);
        mDeviceListView = (ListView) findViewById(R.id.main_device_list);
        mDeviceSearchProgress = (ProgressBar) findViewById(R.id.main_device_search_progress);

        mDeviceSearchProgress.setVisibility(View.GONE);

        mDeviceSearchButton.setOnClickListener(onClickListener);
        mDeviceListView.setOnItemClickListener(onItemClickListener);
        Log.i(LOG_TAG, "-------------------------");
    }

    private void searchBluetoothDevice() {
        Log.i(LOG_TAG, "searchBluetoothDevice");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isEnabled()) {
                if (mBluetoothAdapter.startDiscovery()) {
                    mDeviceSearchProgress.setVisibility(View.VISIBLE);
                    mDeviceList = new ArrayList<BluetoothDevice>();
                    setDeviceAdapter();
                    Log.v(LOG_TAG, "startDiscovery");
                } else {
                    Log.v(LOG_TAG, "startDiscovery fail");
                }
            } else {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, REQUEST_ENABLE_BLUETOOTH);
            }
        } else {
            Toast.makeText(mActivity, "This device not support bluetooth.", Toast.LENGTH_SHORT).show();
        }
        Log.i(LOG_TAG, "-------------------------");
    }

    private void paireDevice() {
        Log.i(LOG_TAG, "paireDevice");
        try {
            Method method = mSelectedDevice.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(mSelectedDevice, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(LOG_TAG, "-------------------------");
    }

    private void startCommunication() {
        Log.i(LOG_TAG, "startCommunication");
        Intent intent = new Intent(mActivity, CommunicationActivity.class);
        Bundle bundle = new Bundle();

        bundle.putParcelable("DEVICE", mSelectedDevice);
        intent.putExtras(bundle);
        startActivity(intent);
        Log.i(LOG_TAG, "-------------------------");
    }

    private void setDeviceAdapter() {
        Log.i(LOG_TAG, "setDeviceAdapter");
        BluetoothDeviceAdapter adapter = new BluetoothDeviceAdapter(mActivity, mDeviceList);
        mDeviceListView.setAdapter(adapter);
        Log.i(LOG_TAG, "-------------------------");
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(LOG_TAG, "onClick");
            searchBluetoothDevice();
            Log.i(LOG_TAG, "-------------------------");
        }
    };

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.i(LOG_TAG, "onItemClick");
            mSelectedDevice = mDeviceList.get(position);
            String message = String.format("%s | %s | %s", mSelectedDevice.getName(), mSelectedDevice.getAddress(), mSelectedDevice.getBondState());
            Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
            if (mSelectedDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                startCommunication();
            } else {
                paireDevice();
            }
            Log.i(LOG_TAG, "-------------------------");
        }
    };
}
