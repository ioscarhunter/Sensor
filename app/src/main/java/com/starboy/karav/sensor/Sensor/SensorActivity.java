package com.starboy.karav.sensor.Sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Surface;
import android.view.View;

import com.starboy.karav.sensor.Bluetooth.BluetoothActivity;
import com.starboy.karav.sensor.Bluetooth.Command;
import com.starboy.karav.sensor.Bluetooth.MessageManage;
import com.starboy.karav.sensor.Meter.Calculate;
import com.starboy.karav.sensor.R;

public class SensorActivity extends BluetoothActivity implements SensorEventListener {
    float accels[];

    double pitch;
    double roll;

    private int rotation;

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

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
        senSensorManager = (SensorManager) this.getSystemService((SENSOR_SERVICE));
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//		senMagnetic = senSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
//        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_GAME);
//		senSensorManager.registerListener(this, senMagnetic, SensorManager.SENSOR_DELAY_NORMAL);
        rotation = getWindowManager().getDefaultDisplay().getRotation();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER)
            accels = Calculate.lowPass(sensorEvent.values.clone(), accels);

        if (accels != null) {
            switch (rotation) {
                case Surface.ROTATION_0:
                    pitch = Math.atan2(-accels[1], accels[2]) * 180 / Math.PI;
                    roll = -Math.atan2(-accels[0], Math.sqrt(accels[1] * accels[1] + accels[2] * accels[2])) * 180 / Math.PI;
                    break;
                case Surface.ROTATION_90:
                    pitch = Math.atan2(-accels[0], accels[2]) * 180 / Math.PI;
                    roll = -Math.atan2(-accels[1], Math.sqrt(accels[0] * accels[0] + accels[2] * accels[2])) * 180 / Math.PI;
                    break;
                case Surface.ROTATION_180:
                    pitch = Math.atan2(accels[1], accels[2]) * 180 / Math.PI;
                    roll = -Math.atan2(accels[0], Math.sqrt(accels[1] * accels[1] + accels[2] * accels[2])) * 180 / Math.PI;
                    break;
                default:
                    pitch = Math.atan2(accels[0], accels[2]) * 180 / Math.PI;
                    roll = -Math.atan2(accels[1], Math.sqrt(accels[0] * accels[0] + accels[2] * accels[2])) * 180 / Math.PI;
                    break;
            }
            pitch += 90;
//			pitch = Math.atan2(-accels[1], accels[2]) * 180 / Math.PI;
//			roll = Math.atan2(-accels[0], Math.sqrt(accels[1] * accels[1] + accels[2] * accels[2])) * 180 / Math.PI;
            updateScreen();
        }
    }

    private void updateScreen() {
        sendMessage(MessageManage.Encode(Command.newCommand(2, pitch, roll)));
    }

    @Override
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
    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

}
