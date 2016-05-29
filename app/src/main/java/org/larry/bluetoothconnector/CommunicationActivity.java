package org.larry.bluetoothconnector;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Larry on 2015-10-04.
 */
public class CommunicationActivity extends Activity {
    private final String LOG_TAG = getClass().getSimpleName();
    private Handler mHandler = new Handler();

    private TextView mDeviceNameInfoText = null;
    private TextView mMessageText = null;
    private EditText mInputEditText = null;
    private Button mSendButton = null;

    private BluetoothDevice mSelectedDevice = null;
    private BluetoothSocket mBTSockect = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communication);

        mSelectedDevice = getIntent().getExtras().getParcelable("DEVICE");

        initView();
        connectToBT();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mBTSockect.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        mDeviceNameInfoText = (TextView) findViewById(R.id.communication_device_info_text);
        mMessageText = (TextView) findViewById(R.id.communication_message_text);
        mInputEditText = (EditText) findViewById(R.id.communication_edittext);
        mSendButton = (Button) findViewById(R.id.communication_send_button);

        mDeviceNameInfoText.setText(String.format("%s | %s | isConnected : %s",
                mSelectedDevice.getName(),
                mSelectedDevice.getAddress(),
                "false"));

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBTSockect != null && mBTSockect.isConnected()) {
                    try {
                        String message = String.format("*%s*", mInputEditText.getText().toString());
                        OutputStream outputStream = mBTSockect.getOutputStream();

                        Log.d(LOG_TAG, "message : " + message);
                        Log.d(LOG_TAG, "message.getBytes : " + message.getBytes());
                        Log.d(LOG_TAG, "message.getBytes.toString : " + message.getBytes().toString());
                        Log.d(LOG_TAG, "message.getBytes.length : " + message.getBytes().length);
                        Log.d(LOG_TAG, "-------------------------");
                        outputStream.write(message.getBytes());
                        outputStream.flush();

                        mMessageText.setText(mMessageText.getText().toString() + "\n" + message);
                        mInputEditText.setText("");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void connectToBT() {
        Log.d(LOG_TAG, "connectToBT");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(LOG_TAG, "mSelectedDevice.getUuids().length : " + mSelectedDevice.getUuids().length);
                    Log.d(LOG_TAG, "mSelectedDevice.getUuids()[0].getUuid : " + mSelectedDevice.getUuids()[0].getUuid());
                    Log.d(LOG_TAG, "-------------------------");
                    mBTSockect = mSelectedDevice.createRfcommSocketToServiceRecord(mSelectedDevice.getUuids()[0].getUuid());
                    mBTSockect.connect();

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mDeviceNameInfoText.setText(String.format("%s | %s | isConnected : %s",
                                    mSelectedDevice.getName(),
                                    mSelectedDevice.getAddress(),
                                    mBTSockect.isConnected()));
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
