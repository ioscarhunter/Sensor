package com.starboy.karav.sensor.Bluetooth;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.starboy.karav.sensor.R;

/**
 * Created by Karav on 5/19/2015.
 * Activity that have ability for bluetooth
 */
public class BluetoothActivity extends AppCompatActivity {
    protected static final int REQUEST_ENABLE_BT = 3;
    protected boolean connect = false;

    /**
     * Name of the connected device
     */

    protected String mConnectedDeviceName = null;
    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    protected final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Activity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    if (msg.arg1 == BluetoothChatService.STATE_LOST) {
                        mConnectedDeviceName = null;
                        setStatusBarColour(R.color.status_noconnected);
                        setActionBarColour(getResources().getString(R.string.not_connected), R.color.title_noconnected);
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
                        setStatusBarColour(R.color.title_connected);
                        setActionBarColour(getResources().getString(R.string.title_connected_to) + " " + mConnectedDeviceName, R.color.status_connected);
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

    protected void messageReceive(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

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
    protected void onDestroy() {
        super.onDestroy();
        if (mChatService != null) {
            mChatService.stop();
        }
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

    /**
     * Makes this device discoverable.
     */
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
    public void sendMessage(String message) {
//		// Check that we're actually connected before trying anything
        if (!isConnect()) {
            Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        else if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);
        }
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

    private Activity getActivity() {
        return BluetoothActivity.this;
    }

    protected boolean isConnect() {
        return mChatService.getState() == BluetoothChatService.STATE_CONNECTED;
    }

    public void setStatusBarColour(int colour) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(colour));
        }
    }

    private ActionBar setAColour(int colour) {
        ActionBar actionBar = getActionBar();
        if (actionBar != null)
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(colour)));
        return actionBar;
    }

    public void setActionBarColour(int colour) {
        setAColour(colour);
    }

    public void setActionBarColour(String heading, int colour) {
        ActionBar actionBar = setAColour(colour);
        if (actionBar != null) actionBar.setTitle(heading);

    }
}
