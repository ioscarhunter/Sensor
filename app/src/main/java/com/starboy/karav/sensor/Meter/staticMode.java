package com.starboy.karav.sensor.Meter;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.akexorcist.roundcornerprogressbar.RoundCornerTextView;
import com.akexorcist.roundcornerprogressbar.TextRoundCornerProgressBar;
import com.starboy.karav.sensor.Bluetooth.BluetoothActivity;
import com.starboy.karav.sensor.Bluetooth.Command;
import com.starboy.karav.sensor.Bluetooth.MessageManage;
import com.starboy.karav.sensor.R;
import com.starboy.karav.sensor.Summary.Summary;
import com.starboy.karav.sensor.Summary.staticSummary;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class staticMode extends BluetoothActivity {


    public static String EXTRA_DEVICE_ADDRESS = "address";
    private TextView display;
    private Button discover;
    private TextView status_tv;
    private Button start;
    private Button stop;

    private Chronometer countdown;
    private Chronometer timer;
    private TextView minusSign;
    private TextRoundCornerProgressBar rating;
    private RelativeLayout status_layout;

    private long totalCount;
    private long balanceCount;


    private boolean firstTime;
    private boolean timeOn;

    private int currentColour;

    private Animation anim;

    long time_total;

    private double difficulty;

    float accels[];

    double pitch;
    double roll;

    private String macaddr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static_mode);

        assignBundle();

        setupUI();
        setupAnimate();

    }

    private void assignBundle() {
        Bundle b = this.getIntent().getExtras();

        time_total = b.getLong("total");
        difficulty = b.getDouble("difficult");
        macaddr = b.getString(EXTRA_DEVICE_ADDRESS);
    }


    private void setStatus(int status) {
        if (status == 1) {
            status_tv.setText(getResources().getString(R.string.status_unbalance));
            status_layout.setBackgroundColor(getResources().getColor(R.color.c_unbalance));
        } else {
            status_tv.setText(getResources().getString(R.string.status_balance));
            status_layout.setBackgroundColor(getResources().getColor(R.color.c_balance));
        }
    }

    private void toggleTime() {
        if (timeOn) {
            pause();
        } else {
            play();
        }

    }

    private void play() {
        start.setText(getResources().getString(R.string.pause));
        timeOn = true;
        int stoppedSeconds = timeStopped();
        // Amount of time elapsed since the start button was pressed, minus the time paused
        timer.setBase(SystemClock.elapsedRealtime() - stoppedSeconds * 1000);
        timer.start();
        setColourAnimation(start, R.color.black, R.color.clear, 100);
        timer.clearAnimation();
        if (!firstTime) {
//                receiverActivity.sendMessage("T:" + "R");
        } else {
//                receiverActivity.sendMessage("T:" + "B:" + level);
            firstTime = false;
        }
        startsensor();
    }

    private void pause() {
        start.setText(getResources().getString(R.string.resume));
        timeOn = false;
        timer.stop();
        setColourAnimation(start, R.color.clear, R.color.black, 100);//change to black
        timer.startAnimation(anim);
        stopsensor();
    }

    private int timeStopped() {
        //Holds the number of milliseconds paused
        int stoppedSeconds = 0;
        // Get time from the chronometer
        String chronoText = timer.getText().toString();
        String array[] = chronoText.split(":");
        if (array.length == 2) {
            // Find the seconds
            stoppedSeconds = Integer.parseInt(array[0]) * 60 + Integer.parseInt(array[1]);
        } else if (array.length == 3) {
            //Find the minutes
            stoppedSeconds = Integer.parseInt(array[0]) * 60 * 60 + Integer.parseInt(array[1]) * 60 + Integer.parseInt(array[2]);
        }
        return stoppedSeconds;
    }

    private void stopTime() {

        // stop countdown
        countdown.stop();
        int totalTime = timeStopped();
        Intent extra = new Intent(staticMode.this, staticSummary.class);
        extra.putExtra("Time", totalTime);
        extra.putExtra("total", totalCount);
        extra.putExtra("balance", balanceCount);
        extra.putExtra("difficult", difficulty);

        stopsensor();
        staticMode.this.finish();
        staticMode.this.startActivity(extra);

    }


    private void setupUI() {
        firstTime = true;
        status_tv = (TextView) findViewById(R.id.status);
        minusSign = (TextView) findViewById(R.id.minusSign);
        currentColour = R.color.c_l5;

        start = (Button) findViewById(R.id.start_but);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//				Log.d(TAG, "start click");
                toggleTime();
            }
        });
        stop = (Button) findViewById(R.id.stop_but);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopTime();
            }
        });

        status_layout = (RelativeLayout) findViewById(R.id.status_layout);

        start.setBackgroundColor(getResources().getColor(R.color.black));
        rating = (TextRoundCornerProgressBar) findViewById(R.id.process);
        timeOn = false;
        balanceCount = 1;
        totalCount = 1;

        setupTimer();
        setStatus(0);
        updateProgress(rating);

    }

    private void setupTimer() {
//        Bundle bundle = this.getArguments();
//        final long setedTime = bundle.getLong("time");
//        level = bundle.getInt("level");
        countdown = (Chronometer) findViewById(R.id.countdown);
        countdown.setBase(SystemClock.elapsedRealtime() - time_total);

        timer = (Chronometer) findViewById(R.id.timer);
        timer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long myElapsedMillis = SystemClock.elapsedRealtime() - timer.getBase();
                countdown.setBase(SystemClock.elapsedRealtime() - (time_total - myElapsedMillis));
                if (myElapsedMillis >= time_total) {
                    if (minusSign.getVisibility() == View.INVISIBLE) {
                        minusSign.setVisibility(View.VISIBLE);
                        countdown.setTextColor(getResources().getColor(R.color.deep_orange500));
                        countdown.startAnimation(anim);
                        minusSign.startAnimation(anim);
                    }

                    //TODO alarm
                }
                countdown.setBase(SystemClock.elapsedRealtime() - Math.abs(myElapsedMillis - time_total));
            }

        });
        timer.setBase(SystemClock.elapsedRealtime());
    }

    private void setupAnimate() {
        //blink animation
        anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(100); //You can manage the time of the blink with this parameter
        anim.setStartOffset(250);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
    }


    private void setColourAnimation(final TextView textView, int from, int to, int duration) {
        Integer colorFrom = getResources().getColor(from);
        Integer colorTo = getResources().getColor(to);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(duration);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                textView.setBackgroundColor((Integer) animator.getAnimatedValue());
            }

        });
        colorAnimation.start();
    }

    public void updateData(int status) {//function receive data from activity
        updateProgress(rating);
        setStatus(status);
    }

    @Override
    protected void messageReceive(String s) {
        Command c = MessageManage.Decode(s);
        pitch = c.pitch;
        roll = c.row;
        updateScreen();
    }

    private void startsensor() {
        sendMessage(MessageManage.Encode(Command.newCommand(1)));
    }

    private void stopsensor() {
        sendMessage(MessageManage.Encode(Command.newCommand(0)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        connectDevice(macaddr);

    }

    private void updateScreen() {
        double r = sqrt(pow(pitch, 2) + pow(roll, 2));
        int status;
        totalCount++;
        if (r < (8 - difficulty) && pitch > (-4)) {
            balanceCount++;
            status = 0;
        } else {

            status = 1;
        }

        updateData(status);
    }


    private void updateProgressColor(TextRoundCornerProgressBar process) {
        float progress = process.getProgress();
        if (progress <= 25) {
            process.setProgressColor(getResources().getColor(R.color.custom_progress_red_progress));
        } else if (progress > 25 && progress <= 50) {
            process.setProgressColor(getResources().getColor(R.color.custom_progress_orange_progress));
        } else if (progress > 50 && progress <= 75) {
            process.setProgressColor(getResources().getColor(R.color.custom_progress_yellow_progress));
        } else if (progress > 75) {
            process.setProgressColor(getResources().getColor(R.color.custom_progress_green_progress));
        }
    }

    private float getpercentage() {
        return ((balanceCount / (float) totalCount) * 100);
    }

    private void updateTextProgress(TextRoundCornerProgressBar process) {
        process.setProgressText((Math.round(process.getProgress() * 10) / 10.0) + "%");
    }

    private void updateProgress(TextRoundCornerProgressBar progress) {
        progress.setProgress(getpercentage());
        updateProgressColor(progress);
        updateTextProgress(progress);
    }
}