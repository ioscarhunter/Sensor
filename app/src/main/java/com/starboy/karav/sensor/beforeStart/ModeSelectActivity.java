package com.starboy.karav.sensor.beforeStart;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.starboy.karav.sensor.R;
import com.starboy.karav.sensor.sensor.SensorActivity;

public class ModeSelectActivity extends AppCompatActivity {
    Button receiver_butt;
    Button sender_butt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_select);
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

        receiver_butt = (Button) findViewById(R.id.receiver_butt);
        sender_butt = (Button) findViewById(R.id.sender_butt);

        receiver_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ModeSelectActivity.this.startActivity(new Intent(ModeSelectActivity.this, BluetoothSelectActivity.class));
            }
        });
        sender_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ModeSelectActivity.this.startActivity(new Intent(ModeSelectActivity.this, SensorActivity.class));
            }
        });
    }

}
