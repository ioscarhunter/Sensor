package com.starboy.karav.sensor.Meter;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.starboy.karav.sensor.R;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class staticMode extends Activity implements SensorEventListener {

    private TextView display;
    private Button discover;
    private TextView status_tv;
    private RelativeLayout status_level;
    private Button start;
    private Button stop;

    private Chronometer countdown;
    private Chronometer timer;
    private TextView minusSign;
    private RatingBar rating;
    private RelativeLayout status_layout;


    private int grade;
    private int level;
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

    double c_pitch = 0;
    double c_roll = 0;
    private int rotation;

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static_mode);

        assignBundle();

        setupUI();
        setupAnimate();

        senSensorManager = (SensorManager) this.getSystemService((SENSOR_SERVICE));
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//		senMagnetic = senSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_GAME);
//		senSensorManager.registerListener(this, senMagnetic, SensorManager.SENSOR_DELAY_NORMAL);
        rotation = getWindowManager().getDefaultDisplay().getRotation();
    }

    private void assignBundle() {
        Bundle b = this.getIntent().getExtras();

        time_total = b.getLong("total");
        difficulty = b.getDouble("difficult");
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

    private void startTime() {
        if (timeOn) {
            start.setText(getResources().getString(R.string.resume));
            timeOn = false;
            timer.stop();
            setColourAnimation(start, R.color.clear, R.color.black, 100);//change to black
            timer.startAnimation(anim);
//            receiverActivity.sendMessage("T:" + "P");
        } else {
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
        }
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
//		Log.d(TAG, totalTime + " ");
        // sent stop message to sender
//        receiverActivity.sendMessage("T:S:" + totalTime);
//        Fragment summary = new SummaryFragment();
//        Bundle extra = new Bundle();
//        extra.putInt("Time", totalTime);
//        extra.putInt("Level", grade);
//        summary.setArguments(extra);
//        receiverActivity.replaceFragment(summary);
    }


    private void setupUI() {
        firstTime = true;
        status_tv = (TextView) findViewById(R.id.status);
        status_level = (RelativeLayout) findViewById(R.id.ratingContainer);
        minusSign = (TextView) findViewById(R.id.minusSign);
        currentColour = R.color.c_l5;

        start = (Button) findViewById(R.id.start_but);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//				Log.d(TAG, "start click");
                startTime();
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
        rating = (RatingBar) findViewById(R.id.level_rating);
        timeOn = false;
        grade = 5;
        setupTimer();
        setGrade(grade);
        setStatus(0);
        startTime();
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

    private void setGrade(int grade) {
        rating.setProgress(grade);
        switch (grade) {
            case 1:
                setColourAnimation(status_level, currentColour, R.color.c_l1, 300);
                currentColour = R.color.c_l1;
                break;
            case 2:
                setColourAnimation(status_level, currentColour, R.color.c_l2, 300);
                currentColour = R.color.c_l2;
                break;
            case 3:
                setColourAnimation(status_level, currentColour, R.color.c_l3, 300);
                currentColour = R.color.c_l3;
                break;
            case 4:
                setColourAnimation(status_level, currentColour, R.color.c_l4, 300);
                currentColour = R.color.c_l4;
                break;
            case 5:
                setColourAnimation(status_level, currentColour, R.color.c_l5, 300);
                currentColour = R.color.c_l5;
                break;
            default:
                break;
        }
    }

    private void setColourAnimation(final ViewGroup textView, int from, int to, int duration) {
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

    public void updateData(int status, int level) {//function receive data from activity
        this.grade = level;
        setGrade(level);
        setStatus(status);
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
        double r = sqrt(pow(pitch, 2) + pow(roll, 2));
        int status;
        int level;
        totalCount++;
        if (r < (8 - difficulty) && pitch > (-4)) {
            balanceCount++;
            status = 0;
        } else {
            status = 1;
        }
        level = rate(totalCount, balanceCount);
        updateData(status, level);
    }

    private int rate(long all_num, long balance_num) {
        int grade;
        double percentage = ((double) balance_num / (double) all_num) * 100;
        Log.d("SensorProcess", percentage + " %");
        if (percentage >= 80) {
            grade = 5;
        } else if (percentage >= 60) {
            grade = 4;
        } else if (percentage >= 40) {
            grade = 3;
        } else if (percentage >= 20) {
            grade = 2;
        } else {
            grade = 1;
        }
        return grade;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    @Override
    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }
}