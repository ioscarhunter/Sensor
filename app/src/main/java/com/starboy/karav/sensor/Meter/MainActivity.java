package com.starboy.karav.sensor.Meter;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.starboy.karav.sensor.R;


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

	private RelativeLayout FBlayout;
	private RelativeLayout LRlayout;

	private boolean flag;

	private double[] pitchArr;
	private double[] rollArr;

	float Rot[] = null; //for gravity rotational data
	//don't use R because android uses that for other stuff
	float I[] = null; //for magnetic rotational data
	float accels[] /*= new float[3]*/;
	float mags[] = new float[3];
	float[] values = new float[3];

	double pitch;
	double roll;

	double temp_pitch;
	double temp_roll;

	double c_pitch = 0;
	double c_roll = 0;

	private int countgoal;
	private int[] insctuctionSet;

	private RoundCornerProgressBar progressBar;

	private int positionNum;
	private CountDownTimer handler;
	private boolean isHanderRunning;
	private Runnable runable;
	private int delayMillis = 3000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
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

		isHanderRunning = false;
		positionNum = 0;

		progressBar.setMax(delayMillis);
		handler = new CountDownTimer(1, 1) {
			@Override
			public void onTick(long l) {
			}

			@Override
			public void onFinish() {
			}
		};
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

		pitchArr = new double[4];
		rollArr = new double[4];
//	    if (ybar != null) {

		flag = false;
		senSensorManager = (SensorManager) this.getSystemService((SENSOR_SERVICE));
		senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//		senMagnetic = senSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_GAME);
//		senSensorManager.registerListener(this, senMagnetic, SensorManager.SENSOR_DELAY_NORMAL);

		countgoal = 0;
		insctuctionSet = new int[]{0, 1};

	}

	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {
		Sensor mySensor = sensorEvent.sensor;
		if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER)
			accels = Calculate.lowPass(sensorEvent.values.clone(), accels);

		if (/*mags == null &&*/ accels != null) {
			pitch = Math.atan2(accels[1], accels[2]) * 180 / Math.PI;
			roll = Math.atan2(-accels[0], Math.sqrt(accels[1] * accels[1] + accels[2] * accels[2])) * 180 / Math.PI;
			updateScreen();
		}
	}

	private void updateScreen() {
//		addNum(temp_roll, temp_pitch);
//		roll = Calculate.findMedian(rollArr.clone());
//		pitch = Calculate.findMedian(pitchArr.clone());
//
//		tv.setText("azimuth = " + (Math.round(azimuth * 10)) / 10.0 + "\npitch = " + (Math.round(pitch * 10) / 10.0) + "\nroll = " + (Math.round(roll * 10) / 10.0));
//		tv2.setText("azimuth = " + (Math.round((c_azimuth - azimuth) * 10)) / 10.0 + "\npitch = " + (Math.round((c_pitch - pitch) * 10) / 10.0) + "\nroll = " + (Math.round((c_roll - roll) * 10) / 10.0));
//	        Log.d(TAG, Arrays.toString(rollArr));

		ybar.setProgress(Calculate.getPercent(8, -4, c_pitch - pitch));
		xbar.setProgress(Calculate.getPercent(8, -8, c_roll - roll));
		pit.setText(Math.round((c_pitch - pitch) * 100) / 100.0 + "°");
		rol.setText(Math.round((c_roll - roll) * 100) / 100.0 + "°");

		setButtonColour();
	}


	private void setButtonColour() {
		if ((getPositionNum() == FNUM) || (getPositionNum() == BNUM)) {
			if (c_pitch - pitch >= 7) {
				if (c_pitch - pitch <= 8) {
					if (getPositionNum() == FNUM) {
						FrontIndi.setBackgroundResource(R.drawable.green_circle);

						goal.setTextColor(getResources().getColor(R.color.white_pure));
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
					}
					if (FrontIndi.getVisibility() == View.INVISIBLE)
						FrontIndi.setVisibility(View.VISIBLE);
					FrontIndi.setBackgroundResource(R.drawable.red_circle);

					goal.setTextColor(getResources().getColor(R.color.c_unbalance));
					goal.setText(getResources().getString(R.string.unbalance));
				}
			} else if (c_pitch - pitch <= -3) {
				if (c_pitch - pitch >= -4) {
					if (getPositionNum() == BNUM) {
						BackIndi.setBackgroundResource(R.drawable.green_circle);

						goal.setTextColor(getResources().getColor(R.color.white_pure));
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

					goal.setTextColor(getResources().getColor(R.color.c_unbalance));
					goal.setText(getResources().getString(R.string.unbalance));
				}
			} else {

				handler.cancel();
				isHanderRunning = false;
				progressBar.setProgress(0);

				changeIndicator();
				FrontIndi.setBackgroundResource(R.drawable.white_circle);
				BackIndi.setBackgroundResource(R.drawable.white_circle);

				goal.setTextColor(getResources().getColor(R.color.c_balance));
				goal.setText(getResources().getString(R.string.balance));
			}
		}
		if ((getPositionNum() == LNUM) || (getPositionNum() == RNUM)) {
			if (c_roll - roll >= 7) {
				if (c_roll - roll <= 8) {
					if (getPositionNum() == RNUM) {
						RightIndi.setBackgroundResource(R.drawable.green_circle);

						goal.setTextColor(getResources().getColor(R.color.white_pure));
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

					goal.setTextColor(getResources().getColor(R.color.c_unbalance));
					goal.setText(getResources().getString(R.string.unbalance));
				}
			} else if (c_roll - roll <= -7) {
				if (c_roll - roll >= -8) {
					if (getPositionNum() == LNUM) {
						LeftIndi.setBackgroundResource(R.drawable.green_circle);

						goal.setTextColor(getResources().getColor(R.color.white_pure));
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

					goal.setTextColor(getResources().getColor(R.color.c_unbalance));
					goal.setText(getResources().getString(R.string.unbalance));
				}
			} else {

				handler.cancel();
				isHanderRunning = false;
				progressBar.setProgress(0);


				changeIndicator();
				LeftIndi.setBackgroundResource(R.drawable.white_circle);
				RightIndi.setBackgroundResource(R.drawable.white_circle);

				goal.setTextColor(getResources().getColor(R.color.c_balance));
				goal.setText(getResources().getString(R.string.balance));
			}
		}
	}

	private void stayStill() {
		if (isHanderRunning) {

			handler = new CountDownTimer(delayMillis, 10) {

				public void onTick(long millisUntilFinished) {
//				mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
//					Log.d(TAG, "seconds remaining: " + millisUntilFinished / 100);
//					isHanderRunning = true;
					progressBar.setProgress(delayMillis - millisUntilFinished);
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


	private void addNum(double rl, double pl) {
		for (int i = 0; i < rollArr.length - 1; i++) {
			rollArr[i] = rollArr[i + 1];
			pitchArr[i] = pitchArr[i + 1];
		}
		rollArr[rollArr.length - 1] = rl;
		pitchArr[pitchArr.length - 1] = pl;
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
