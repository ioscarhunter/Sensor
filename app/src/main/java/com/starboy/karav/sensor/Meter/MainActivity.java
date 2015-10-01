package com.starboy.karav.sensor.Meter;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

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


	private int positionNum;
	private final Handler handler = new Handler();
	private boolean isHanderRunning;
	private Runnable runable;
	int delayMillis = 3000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tv = (TextView) findViewById(R.id.textView);
		tv2 = (TextView) findViewById(R.id.textView2);
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

		isHanderRunning = false;
		positionNum = 0;
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
//		    ybar.setMax((int) (100));
//		    ybar.setOnTouchListener(new View.OnTouchListener() {
//			    @Override
//			    public boolean onTouch(View view, MotionEvent motionEvent) {
//				    return true;
//			    }
//		    });
//		    ybar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//
//			    @Override
//			    public void onStopTrackingTouch(SeekBar arg0) {
//			    }
//
//			    @Override
//			    public void onStartTrackingTouch(SeekBar arg0) {
//			    }
//
//			    @Override
//			    public void onProgressChanged(SeekBar timerBar, int arg1, boolean arg2) {
//				    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.plussign);
//				    Bitmap bmp = bitmap.copy(Bitmap.Config.ARGB_8888, true);
//				    Canvas c = new Canvas(bmp);
//				    String text = "12";//Integer.toString(timerBar.getProgress());
//				    Paint p = new Paint();
//				    p.setTypeface(Typeface.DEFAULT_BOLD);
//				    p.setTextSize(14);
//				    p.setColor(0xFFFF0000);
//				    int width = (int) p.measureText(text);
//				    int yPos = (int) ((c.getHeight() / 2) - ((p.descent() + p.ascent()) / 2));
//				    c.drawText(text, (bmp.getWidth() - width) / 2, yPos, p);
//				    timerBar.setThumb(new BitmapDrawable(getResources(), bmp));
//			    }
//
//		    });
//		    ybar.setProgress(0);
//	    }
//        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		flag = false;
		senSensorManager = (SensorManager) this.getSystemService((SENSOR_SERVICE));
		senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//		senMagnetic = senSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_GAME);
//		senSensorManager.registerListener(this, senMagnetic, SensorManager.SENSOR_DELAY_NORMAL);

	}

	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {
		Sensor mySensor = sensorEvent.sensor;
		if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER)
			accels = Calculate.lowPass(sensorEvent.values.clone(), accels);

//		if (mags != null && accels != null) {
//			Rot = new float[9];
//			I = new float[9];
//			SensorManager.getRotationMatrix(Rot, I, accels, mags);
//			// Correct if screen is in Landscape
//
//			float[] outR = new float[9];
//			SensorManager.remapCoordinateSystem(Rot, SensorManager.AXIS_X, SensorManager.AXIS_Z, outR);
//			SensorManager.getOrientation(outR, values);
//
//			temp_azimuth = values[0] * 57.2957795; //looks like we don't need this one
//			temp_pitch = values[1] * 57.2957795;
//			temp_roll = values[2] * 57.2957795;
//			mags = null; //retrigger the loop when things are repopulated
//			accels = null; ////retrigger the loop when things are repopulated
//
//			updateScreen();
//		}
		/*else */
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
						goal.setText(getResources().getString(R.string.goal));
						isHanderRunning = true;
						runable = new Runnable() {
							@Override
							public void run() {
								Log.d(TAG, "num = " + getPositionNum());
								//Do something after 100ms
								getNewNum(1);
								changeIndicator();
								isHanderRunning = false;
							}
						};
						handler.postDelayed(runable, delayMillis);
					}
				} else {
					if (getPositionNum() == FNUM) {
						handler.removeCallbacksAndMessages(null);
						isHanderRunning = false;
					}
					if (FrontIndi.getVisibility() == View.INVISIBLE)
						FrontIndi.setVisibility(View.VISIBLE);
					FrontIndi.setBackgroundResource(R.drawable.red_circle);
					goal.setText(getResources().getString(R.string.unbalance));
				}
			} else if (c_pitch - pitch <= -3) {
				if (c_pitch - pitch >= -5) {
					if (getPositionNum() == BNUM) {
						BackIndi.setBackgroundResource(R.drawable.green_circle);
						goal.setText(getResources().getString(R.string.goal));
						isHanderRunning = true;
						runable = new Runnable() {
							@Override
							public void run() {
								Log.d(TAG, "num = " + getPositionNum());
								//Do something after 100ms
								getNewNum(1);
								changeIndicator();
								isHanderRunning = false;
							}
						};
						handler.postDelayed(runable, delayMillis);
					}

				} else {
					if (getPositionNum() == BNUM) {
						handler.removeCallbacksAndMessages(null);
						isHanderRunning = false;
					}
					if (BackIndi.getVisibility() == View.INVISIBLE)
						BackIndi.setVisibility(View.VISIBLE);
					BackIndi.setBackgroundResource(R.drawable.red_circle);
					goal.setText(getResources().getString(R.string.unbalance));
				}
			} else {

				handler.removeCallbacksAndMessages(null);
				isHanderRunning = false;

				changeIndicator();
				FrontIndi.setBackgroundResource(R.drawable.white_circle);
				BackIndi.setBackgroundResource(R.drawable.white_circle);
				goal.setText(getResources().getString(R.string.balance));
			}
		}
		if ((getPositionNum() == LNUM) || (getPositionNum() == RNUM)) {
			if (c_roll - roll >= 7) {
				if (c_roll - roll <= 9) {
					if (getPositionNum() == RNUM) {
						RightIndi.setBackgroundResource(R.drawable.green_circle);
						goal.setText(getResources().getString(R.string.goal));

						isHanderRunning = true;
						runable = new Runnable() {
							@Override
							public void run() {
								//Do something after 100ms
								Log.d(TAG, "num = " + getPositionNum());
								getNewNum(1);
								changeIndicator();
								isHanderRunning = false;
							}
						};
						handler.postDelayed(runable, delayMillis);
					}
				} else {
					if (getPositionNum() == RNUM) {
						handler.removeCallbacksAndMessages(null);
						isHanderRunning = false;
					}
					if (RightIndi.getVisibility() == View.INVISIBLE)
						RightIndi.setVisibility(View.VISIBLE);
					RightIndi.setBackgroundResource(R.drawable.red_circle);
					goal.setText(getResources().getString(R.string.unbalance));
				}
			} else if (c_roll - roll <= -7) {
				if (c_roll - roll >= -9) {
					if (getPositionNum() == LNUM) {
						LeftIndi.setBackgroundResource(R.drawable.green_circle);
						goal.setText(getResources().getString(R.string.goal));

						isHanderRunning = true;
						runable = new Runnable() {
							@Override
							public void run() {
								//Do something after 100ms
								Log.d(TAG, "num = " + getPositionNum());
								getNewNum(1);
								changeIndicator();
								isHanderRunning = false;
							}
						};
						handler.postDelayed(runable, delayMillis);
					}
				} else {
					if (getPositionNum() == LNUM) {
						handler.removeCallbacksAndMessages(null);
						isHanderRunning = false;
					}
					if (LeftIndi.getVisibility() == View.INVISIBLE)
						LeftIndi.setVisibility(View.VISIBLE);
					LeftIndi.setBackgroundResource(R.drawable.red_circle);
					goal.setText(getResources().getString(R.string.unbalance));
				}
			} else {
				handler.removeCallbacksAndMessages(null);
				isHanderRunning = false;

				changeIndicator();
				LeftIndi.setBackgroundResource(R.drawable.white_circle);
				RightIndi.setBackgroundResource(R.drawable.white_circle);
//			if (goal.getText().equals(getResources().getString(R.string.balance)))
				goal.setText(getResources().getString(R.string.balance));
			}
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

	private void getNewNum(int mode) {
		if (mode == 1) {
			positionNum = ((positionNum + 1) % 4);
		}
		changeIndicator();
	}

	private void changeIndicator() {
		handler.removeCallbacksAndMessages(null);
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
