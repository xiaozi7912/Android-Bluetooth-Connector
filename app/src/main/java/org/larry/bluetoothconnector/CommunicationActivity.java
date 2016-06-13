package org.larry.bluetoothconnector;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

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
    private Button mLeftButton = null;
    private Button mUpButton = null;
    private Button mDownButton = null;
    private Button mRightButton = null;

    private BluetoothDevice mSelectedDevice = null;
    private BluetoothSocket mBTSockect = null;

    private final static String MY_UUID = "dc7e95f2-310d-11e6-ac61-9e71128cae77";

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
        mLeftButton = (Button) findViewById(R.id.communication_left_button);
        mUpButton = (Button) findViewById(R.id.communication_up_button);
        mDownButton = (Button) findViewById(R.id.communication_down_button);
        mRightButton = (Button) findViewById(R.id.communication_right_button);

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
        mLeftButton.setOnClickListener(onClickListener);
        mUpButton.setOnClickListener(onClickListener);
        mDownButton.setOnClickListener(onClickListener);
        mRightButton.setOnClickListener(onClickListener);
    }

    private void connectToBT() {
        Log.d(LOG_TAG, "connectToBT");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(LOG_TAG, "mSelectedDevice.getUuids().length : " + mSelectedDevice.getUuids().length);
                    for (int i = 0; i < mSelectedDevice.getUuids().length; i++) {
                        Log.d(LOG_TAG, String.format("mSelectedDevice.getUuids()[%d].getUuid : %s", i, mSelectedDevice.getUuids()[i].getUuid()));
                    }
                    Log.d(LOG_TAG, "-------------------------");
                    mBTSockect = mSelectedDevice.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
                    mBTSockect.connect();
                    Log.d(LOG_TAG, "connected");

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

    private void sendData(String cmd) {
        if (mBTSockect != null && mBTSockect.isConnected()) {
            try {
                OutputStream outputStream = mBTSockect.getOutputStream();

                Log.d(LOG_TAG, "message : " + cmd);
                Log.d(LOG_TAG, "message.getBytes : " + cmd.getBytes());
                Log.d(LOG_TAG, "message.getBytes.toString : " + cmd.getBytes().toString());
                Log.d(LOG_TAG, "message.getBytes.length : " + cmd.getBytes().length);
                Log.d(LOG_TAG, "-------------------------");
                outputStream.write(cmd.getBytes());
                outputStream.flush();

                mMessageText.setText(mMessageText.getText().toString() + "\n" + cmd);
                mInputEditText.setText("");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.communication_left_button:
                    sendData(String.valueOf(KeyEvent.KEYCODE_DPAD_LEFT));
                    break;
                case R.id.communication_up_button:
                    sendData(String.valueOf(KeyEvent.KEYCODE_DPAD_UP));
                    break;
                case R.id.communication_down_button:
                    sendData(String.valueOf(KeyEvent.KEYCODE_DPAD_DOWN));
                    break;
                case R.id.communication_right_button:
                    sendData(String.valueOf(KeyEvent.KEYCODE_DPAD_RIGHT));
                    break;
            }
        }
    };
}
