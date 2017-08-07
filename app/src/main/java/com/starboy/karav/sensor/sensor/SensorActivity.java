package com.starboy.karav.sensor.sensor;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Surface;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.starboy.karav.sensor.bluetooth.BluetoothChatService;
import com.starboy.karav.sensor.bluetooth.Command;
import com.starboy.karav.sensor.bluetooth.Constants;
import com.starboy.karav.sensor.bluetooth.MessageManage;
import com.starboy.karav.sensor.meter.Calculate;
import com.starboy.karav.sensor.R;

public class SensorActivity extends AppCompatActivity implements SensorEventListener {
    float accels[];

    double pitch;
    double roll;

    private int rotation;

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    private RelativeLayout bt_bg;
    private TextView bt_status;

    protected static final int REQUEST_ENABLE_BT = 3;
    protected boolean connect = false;

    /**
     * Name of the connected device
     */

    protected String mConnectedDeviceName = null;

    /**
     * Member object for the chat services
     */
    protected BluetoothChatService mChatService = null;
    /**
     * Local Bluetooth adapter
     */
    protected BluetoothAdapter mBluetoothAdapter = null;
    private String TAG = "BluetoothActivity";
    /**
     * String buffer for outgoing messages
     */
    private StringBuffer mOutStringBuffer;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mChatService != null) {
            mChatService.stop();
        }
    }

    protected void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    public boolean sendMessage(String message) {
//		// Check that we're actually connected before trying anything
        if (!isConnect()) {
            Toast.makeText(SensorActivity.this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check that there's actually something to send
        else if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);
            return true;
        }
        return true;
    }

    protected boolean isConnect() {
        return mChatService.getState() == BluetoothChatService.STATE_CONNECTED;
    }

    /**
     * Establish connection with other device
     */
    protected void connectDevice(String address) {

//		Log.d(TAG, address);
        // Get the BluetoothDevice object
//		Log.d(TAG, "getRemoteDevice");
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, false);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        bt_bg = (RelativeLayout) findViewById(R.id.bt_status_bg);
        bt_status = (TextView) findViewById(R.id.bt_status_text);
        senSensorManager = (SensorManager) this.getSystemService((SENSOR_SERVICE));
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//		senMagnetic = senSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
//        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_GAME);
//		senSensorManager.registerListener(this, senMagnetic, SensorManager.SENSOR_DELAY_NORMAL);
        rotation = getWindowManager().getDefaultDisplay().getRotation();
        mLockScreenRotation();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER)
            accels = Calculate.lowPass(sensorEvent.values.clone(), accels);

        if (accels != null) {
            switch (rotation) {
                case Surface.ROTATION_0:
                    pitch = -Math.atan2(-accels[1], accels[2]) * 180 / Math.PI;
                    roll = -Math.atan2(-accels[0], Math.sqrt(accels[1] * accels[1] + accels[2] * accels[2])) * 180 / Math.PI;
                    break;
                case Surface.ROTATION_90:
                    pitch = -Math.atan2(-accels[0], accels[2]) * 180 / Math.PI;
                    roll = -Math.atan2(-accels[1], Math.sqrt(accels[0] * accels[0] + accels[2] * accels[2])) * 180 / Math.PI;
                    break;
                case Surface.ROTATION_180:
                    pitch = -Math.atan2(accels[1], accels[2]) * 180 / Math.PI;
                    roll = -Math.atan2(accels[0], Math.sqrt(accels[1] * accels[1] + accels[2] * accels[2])) * 180 / Math.PI;
                    break;
                default:
                    pitch = -Math.atan2(accels[0], accels[2]) * 180 / Math.PI;
                    roll = -Math.atan2(accels[1], Math.sqrt(accels[0] * accels[0] + accels[2] * accels[2])) * 180 / Math.PI;
                    break;
            }
            pitch -= 90;
//			pitch = Math.atan2(-accels[1], accels[2]) * 180 / Math.PI;
//			roll = Math.atan2(-accels[0], Math.sqrt(accels[1] * accels[1] + accels[2] * accels[2])) * 180 / Math.PI;
            updateScreen();
        }
    }

    private void updateScreen() {
        if (!sendMessage(MessageManage.Encode(Command.newCommand(2, pitch, roll)))) {
            senSensorManager.unregisterListener(this);
        }
    }

    protected void messageReceive(String s) {
        Command c = MessageManage.Decode(s);
        switch (c.what) {
            case 0:
                senSensorManager.unregisterListener(this);
                break;
            case 1:
                senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_GAME);
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (mChatService == null) {
            // Initialize the BluetoothChatService to perform bluetooth connections
            mChatService = new BluetoothChatService(this, mHandler);
        }
        connect = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();

            }
        }
    }

    protected final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Activity activity = SensorActivity.this;
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    if (msg.arg1 == BluetoothChatService.STATE_LOST) {
                        mConnectedDeviceName = null;
                        bt_bg.setBackgroundColor(getResources().getColor(R.color.grey_700));
                        bt_status.setText(getResources().getString(R.string.not_connected));
                        connect = false;
                        break;
                    }
                    break;

                case Constants.MESSAGE_READ:    //get the message
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    messageReceive(new String(readBuf, 0, msg.arg1));
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != activity) {
//						Toast.makeText(activity, "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                        bt_bg.setBackgroundColor(getResources().getColor(R.color.blue_500));
                        bt_status.setText(getResources().getString(R.string.title_connected_to) + " " + mConnectedDeviceName);
                        connect = true;
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST), Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    private void mLockScreenRotation() {
        // Stop the screen orientation changing during an event
        this.setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_LOCKED);
//        switch (getWindowManager().getDefaultDisplay().getRotation()){
//            case Surface.ROTATION_0:
//                this.setRequestedOrientation(
//                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                break;
//            case Surface.ROTATION_90:
//                this.setRequestedOrientation(
//                        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                break;
//            case Surface.ROTATION_180:
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
//                break;
//            case Surface.ROTATION_270:
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
//                break;
//        }
    }
}
