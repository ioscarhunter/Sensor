package com.starboy.karav.sensor.Meter;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.starboy.karav.sensor.R;
import com.starboy.karav.sensor.Summary;


public class MainActivity extends Activity implements SensorEventListener {
	String TAG = "MainActivity";

	private final int FNUM = 0;
	private final int BNUM = 1;
	private final int LNUM = 2;
	private final int RNUM = 3;


	private SensorManager senSensorManager;
	private Sensor senAccelerometer;
	private Sensor senMagnetic;
	private TextView tv;
	private TextView tv2;
	private Button cal;
	private SeekBar xbar;
	private SeekBar ybar;
	private Vibrator v;
	private TextView pit;
	private TextView rol;

	private Button FrontIndi;
	private Button BackIndi;
	private Button LeftIndi;
	private Button RightIndi;
	private TextView goal;
	private TextView count_tv;

	private FloatingActionButton fab;

	private Button stop_but;

	private RelativeLayout goalLayout;
	private RelativeLayout FBlayout;
	private RelativeLayout LRlayout;

	private Chronometer timer_total_countdown;

	private Chronometer timer_total;

	private int countgoal;
	private int[] insctuctionSet;

	private RoundCornerProgressBar progressBar;

	private int positionNum;
	private CountDownTimer handler;
	private boolean isHanderRunning;
	private Runnable runable;
	private long time_still = 3000;
	private long time_total = 3 * 1000 * 60;

	private float[] angle_unbalance;
	private int[] unbalancecount;

	float accels[];

	double pitch;
	double roll;

	double c_pitch = 0;
	double c_roll = 0;
	private boolean timeOn;
	private int rotation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Bundle b = this.getIntent().getExtras();

		insctuctionSet = b.getIntArray("inst");
		time_still = b.getLong("still");
		time_total = b.getLong("total");

		tv = (TextView) findViewById(R.id.textView);
		pit = (TextView) findViewById(R.id.pitch);
		rol = (TextView) findViewById(R.id.roll);
		cal = (Button) findViewById(R.id.button);

		FrontIndi = (Button) findViewById(R.id.front);
		BackIndi = (Button) findViewById(R.id.back);
		LeftIndi = (Button) findViewById(R.id.left);
		RightIndi = (Button) findViewById(R.id.right);

		FBlayout = (RelativeLayout) findViewById(R.id.FBLayout);
		LRlayout = (RelativeLayout) findViewById(R.id.LRLayout);

		goal = (TextView) findViewById(R.id.goal);
		count_tv = (TextView) findViewById(R.id.count);

		progressBar = (RoundCornerProgressBar) findViewById(R.id.timeProgressBar);

		setTimer();

		goalLayout = (RelativeLayout) findViewById(R.id.goalLayout);
		goalLayout.setBackgroundColor(getResources().getColor(R.color.green_500));

		stop_but = (Button) findViewById(R.id.stop_but);
		stop_but.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				stop();
			}
		});

		fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				toggletime();
			}
		});
		isHanderRunning = false;
//		positionNum = 0;

		timeOn = false;

		countgoal = 0;
		angle_unbalance = new float[4];
		unbalancecount = new int[4];

		progressBar.setMax(time_still);

		handler = new CountDownTimer(1, 1) {
			@Override
			public void onTick(long l) {
			}

			@Override
			public void onFinish() {
			}
		};
		getNewNum();
		changeIndicator();

		cal.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				c_pitch = pitch;
				c_roll = roll;
			}
		});

		xbar = (SeekBar) findViewById(R.id.xbar);
		ybar = (SeekBar) findViewById(R.id.ybar);

		count_tv.setText("Count:" + countgoal);

		senSensorManager = (SensorManager) this.getSystemService((SENSOR_SERVICE));
		senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//		senMagnetic = senSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_GAME);
//		senSensorManager.registerListener(this, senMagnetic, SensorManager.SENSOR_DELAY_NORMAL);
		rotation = getWindowManager().getDefaultDisplay().getRotation();

	}

	private void setTimer() {
		timer_total_countdown = (Chronometer) findViewById(R.id.timer_total_countdown);
		timer_total_countdown.setBase(SystemClock.elapsedRealtime() - time_total);

		timer_total = (Chronometer) findViewById(R.id.timer_total);
		timer_total.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
			@Override
			public void onChronometerTick(Chronometer chronometer) {
				long myElapsedMillis = SystemClock.elapsedRealtime() - timer_total.getBase();
				timer_total_countdown.setBase(SystemClock.elapsedRealtime() - (time_total - myElapsedMillis));
				if (myElapsedMillis >= time_total) {
//					if (minusSign.getVisibility() == View.INVISIBLE) {
//						minusSign.setVisibility(View.VISIBLE);
					timer_total_countdown.setTextColor(getResources().getColor(R.color.deep_orange500));
//						countdown.startAnimation(anim);
//						minusSign.startAnimation(anim);
					stop();
				}

				timer_total_countdown.setBase(SystemClock.elapsedRealtime() - Math.abs(myElapsedMillis - time_total));
			}

		});
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
					roll = Math.atan2(-accels[0], Math.sqrt(accels[1] * accels[1] + accels[2] * accels[2])) * 180 / Math.PI;
					break;
				case Surface.ROTATION_90:
					pitch = Math.atan2(-accels[0], accels[2]) * 180 / Math.PI;
					roll = Math.atan2(-accels[1], Math.sqrt(accels[0] * accels[0] + accels[2] * accels[2])) * 180 / Math.PI;
					break;
				case Surface.ROTATION_180:
					pitch = Math.atan2(accels[1], accels[2]) * 180 / Math.PI;
					roll = Math.atan2(accels[0], Math.sqrt(accels[1] * accels[1] + accels[2] * accels[2])) * 180 / Math.PI;
					break;
				default:
					pitch = Math.atan2(accels[0], accels[2]) * 180 / Math.PI;
					roll = Math.atan2(accels[1], Math.sqrt(accels[0] * accels[0] + accels[2] * accels[2])) * 180 / Math.PI;
					break;
			}
//
//			pitch = Math.atan2(-accels[1], accels[2]) * 180 / Math.PI;
//			roll = Math.atan2(-accels[0], Math.sqrt(accels[1] * accels[1] + accels[2] * accels[2])) * 180 / Math.PI;
			updateScreen();
		}
	}

	private void updateScreen() {

		ybar.setProgress(Calculate.getPercent(8, -4, c_pitch - pitch));
		xbar.setProgress(Calculate.getPercent(8, -8, c_roll - roll));
		pit.setText(Math.round((c_pitch - pitch) * 10) / 10.0 + "°");
		rol.setText(Math.round((c_roll - roll) * 10) / 10.0 + "°");

		setButtonColour();
	}


	private void setButtonColour() {
		if ((getPositionNum() == FNUM) || (getPositionNum() == BNUM)) {
			if (c_pitch - pitch >= 7) {
				if (c_pitch - pitch <= 8) {
					if (getPositionNum() == FNUM) {
						FrontIndi.setBackgroundResource(R.drawable.green_circle);

						goalLayout.setBackgroundColor(getResources().getColor(R.color.white_pure));
						goal.setText(getResources().getString(R.string.goal));

						if (!isHanderRunning) {
							isHanderRunning = true;
							stayStill();
						}
					}
				} else {
					if (getPositionNum() == FNUM) {
						handler.cancel();
						isHanderRunning = false;
						progressBar.setProgress(0);
					}
					if (FrontIndi.getVisibility() == View.INVISIBLE)
						FrontIndi.setVisibility(View.VISIBLE);
					FrontIndi.setBackgroundResource(R.drawable.red_circle);

					goalLayout.setBackgroundColor(getResources().getColor(R.color.red_A400));
					goal.setText(getResources().getString(R.string.unbalance));
					angle_unbalance[0] += c_pitch;
					unbalancecount[0]++;
				}
			} else if (c_pitch - pitch <= -3) {
				if (c_pitch - pitch >= -4) {
					if (getPositionNum() == BNUM) {
						BackIndi.setBackgroundResource(R.drawable.green_circle);

						goalLayout.setBackgroundColor(getResources().getColor(R.color.white_pure));
						goal.setText(getResources().getString(R.string.goal));

						if (!isHanderRunning) {
							isHanderRunning = true;
							stayStill();
						}
					}

				} else {
					if (getPositionNum() == BNUM) {
						handler.cancel();
						isHanderRunning = false;
						progressBar.setProgress(0);
					}
					if (BackIndi.getVisibility() == View.INVISIBLE)
						BackIndi.setVisibility(View.VISIBLE);
					BackIndi.setBackgroundResource(R.drawable.red_circle);

					goalLayout.setBackgroundColor(getResources().getColor(R.color.red_A400));
					goal.setText(getResources().getString(R.string.unbalance));
					angle_unbalance[1] += c_pitch;
					unbalancecount[1]++;
				}
			} else {

				handler.cancel();
				isHanderRunning = false;
				progressBar.setProgress(0);

				changeIndicator();
				FrontIndi.setBackgroundResource(R.drawable.white_circle);
				BackIndi.setBackgroundResource(R.drawable.white_circle);

				goalLayout.setBackgroundColor(getResources().getColor(R.color.blue_500));
				goal.setText(getResources().getString(R.string.balance));
			}
		}
		if ((getPositionNum() == LNUM) || (getPositionNum() == RNUM)) {
			if (c_roll - roll >= 7) {
				if (c_roll - roll <= 8) {
					if (getPositionNum() == RNUM) {
						RightIndi.setBackgroundResource(R.drawable.green_circle);

						goalLayout.setBackgroundColor(getResources().getColor(R.color.white_pure));
						goal.setText(getResources().getString(R.string.goal));
						if (!isHanderRunning) {
							isHanderRunning = true;
							stayStill();
						}
					}
				} else {
					if (getPositionNum() == RNUM) {
						handler.cancel();
						isHanderRunning = false;
						progressBar.setProgress(0);
					}
					if (RightIndi.getVisibility() == View.INVISIBLE)
						RightIndi.setVisibility(View.VISIBLE);

					RightIndi.setBackgroundResource(R.drawable.red_circle);

					goalLayout.setBackgroundColor(getResources().getColor(R.color.red_A400));
					goal.setText(getResources().getString(R.string.unbalance));
					angle_unbalance[2] += c_roll;
					unbalancecount[2]++;
				}
			} else if (c_roll - roll <= -7) {
				if (c_roll - roll >= -8) {
					if (getPositionNum() == LNUM) {
						LeftIndi.setBackgroundResource(R.drawable.green_circle);

						goalLayout.setBackgroundColor(getResources().getColor(R.color.white_pure));
						goal.setText(getResources().getString(R.string.goal));
						if (!isHanderRunning) {
							isHanderRunning = true;
							stayStill();
						}
					}
				} else {
					if (getPositionNum() == LNUM) {
						handler.cancel();
						isHanderRunning = false;
						progressBar.setProgress(0);
					}
					if (LeftIndi.getVisibility() == View.INVISIBLE)
						LeftIndi.setVisibility(View.VISIBLE);
					LeftIndi.setBackgroundResource(R.drawable.red_circle);

					goalLayout.setBackgroundColor(getResources().getColor(R.color.red_A400));
					goal.setText(getResources().getString(R.string.unbalance));
					angle_unbalance[3] += c_roll;
					unbalancecount[3]++;
				}
			} else {

				handler.cancel();
				isHanderRunning = false;
				progressBar.setProgress(0);

				changeIndicator();
				LeftIndi.setBackgroundResource(R.drawable.white_circle);
				RightIndi.setBackgroundResource(R.drawable.white_circle);

				goalLayout.setBackgroundColor(getResources().getColor(R.color.blue_500));
				goal.setText(getResources().getString(R.string.balance));
			}
		}
	}

	private void toggletime() {
		if (timeOn) {
			play();
		} else {
			pause();
		}
	}

	private long timeStopped() {
		//Holds the number of milliseconds paused
		long stoppedSeconds = 0;
		// Get time from the chronometer
		String chronoText = timer_total.getText().toString();
		String array[] = chronoText.split(":");
		if (array.length == 2) {
			// Find the seconds
			stoppedSeconds = Long.parseLong(array[0]) * 60 + Long.parseLong(array[1]);
		} else if (array.length == 3) {
			//Find the minutes
			stoppedSeconds = Long.parseLong(array[0]) * 60 * 60 + Long.parseLong(array[1]) * 60 + Integer.parseInt(array[2]);
		}
		return stoppedSeconds;
	}

	private void play() {
//			start.setText(getResources().getString(R.string.resume));
		timeOn = false;
		timer_total.stop();
//			setColourAnimation(start, R.color.clear, R.color.black, 100);//change to black
//			timer_total.startAnimation(anim);
		fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
	}

	private void pause() {
//			start.setText(getResources().getString(R.string.pause));
		timeOn = true;
		long elapsedMillis = timeStopped();
		// Amount of time elapsed since the start button was pressed, minus the time paused
		timer_total.setBase(SystemClock.elapsedRealtime() - elapsedMillis);
		timer_total.start();
		fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
//			setColourAnimation(start, R.color.black, R.color.clear, 100);
//			timer_total.clearAnimation();
//			if (!firstTime) {
//				receiverActivity.sendMessage("T:" + "R");
//			} else {
//				receiverActivity.sendMessage("T:" + "B:" + level);
//				firstTime = false;
//			}
	}

	private void stop() {
		// stop countdown
		timer_total.stop();
		Intent extra = new Intent(MainActivity.this, Summary.class);
		extra.putExtra("Time", timeStopped());
		extra.putExtra("count", countgoal);
		extra.putExtra("angle", angle_unbalance);
		extra.putExtra("u_count", unbalancecount);
		MainActivity.this.startActivity(extra);
	}

	private void stayStill() {
		if (isHanderRunning) {

			handler = new CountDownTimer(time_still, 10) {

				public void onTick(long millisUntilFinished) {
//				mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
//					Log.d(TAG, "seconds remaining: " + millisUntilFinished / 100);
//					isHanderRunning = true;
					progressBar.setProgress(time_still - millisUntilFinished);
				}

				public void onFinish() {
//			mTextField.setText("done!");
//					Log.d(TAG, "num = " + getPositionNum());
					countgoal++;
					count_tv.setText("Count:" + countgoal);
					getNewNum();
					progressBar.setProgress(0);
					changeIndicator();
					isHanderRunning = false;
				}
			}.start();
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
		senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		senSensorManager.registerListener(this, senMagnetic, SensorManager.SENSOR_DELAY_NORMAL);
	}

	private void getNewNum() {
		if (insctuctionSet.length != 1) {
			positionNum = insctuctionSet[countgoal % insctuctionSet.length];
		}
		changeIndicator();
	}

	private void changeIndicator() {
		handler.cancel();
		switch (positionNum) {
			case FNUM:
				if (LRlayout.getVisibility() == View.VISIBLE)
					LRlayout.setVisibility(View.INVISIBLE);

				if (FBlayout.getVisibility() == View.INVISIBLE)
					FBlayout.setVisibility(View.VISIBLE);

				if (BackIndi.getVisibility() == View.VISIBLE)
					BackIndi.setVisibility(View.INVISIBLE);

				if (FrontIndi.getVisibility() == View.INVISIBLE)
					FrontIndi.setVisibility(View.VISIBLE);

				break;
			case BNUM:
				if (LRlayout.getVisibility() == View.VISIBLE)
					LRlayout.setVisibility(View.INVISIBLE);

				if (FBlayout.getVisibility() == View.INVISIBLE)
					FBlayout.setVisibility(View.VISIBLE);

				if (FrontIndi.getVisibility() == View.VISIBLE)
					FrontIndi.setVisibility(View.INVISIBLE);

				if (BackIndi.getVisibility() == View.INVISIBLE)
					BackIndi.setVisibility(View.VISIBLE);

				break;
			case LNUM:
				if (LRlayout.getVisibility() == View.INVISIBLE)
					LRlayout.setVisibility(View.VISIBLE);

				if (FBlayout.getVisibility() == View.VISIBLE)
					FBlayout.setVisibility(View.INVISIBLE);

				if (RightIndi.getVisibility() == View.VISIBLE)
					RightIndi.setVisibility(View.INVISIBLE);

				if (LeftIndi.getVisibility() == View.INVISIBLE)
					LeftIndi.setVisibility(View.VISIBLE);

				break;
			case RNUM:
				if (LRlayout.getVisibility() == View.INVISIBLE)
					LRlayout.setVisibility(View.VISIBLE);

				if (FBlayout.getVisibility() == View.VISIBLE)
					FBlayout.setVisibility(View.INVISIBLE);

				if (LeftIndi.getVisibility() == View.VISIBLE)
					LeftIndi.setVisibility(View.INVISIBLE);

				if (RightIndi.getVisibility() == View.INVISIBLE)
					RightIndi.setVisibility(View.VISIBLE);

				break;
		}
	}

	private int getPositionNum() {
		return positionNum;
	}

}
