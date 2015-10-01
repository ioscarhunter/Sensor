package com.starboy.karav.sensor.BeforeStart;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;

import com.starboy.karav.sensor.Meter.MainActivity;
import com.starboy.karav.sensor.R;

public class Beforestart extends AppCompatActivity {
	private final int NUMCARD = 4;
	final String TAG = "Befores";
	private RecyclerView mRecyclerView;
	private RecyclerView.Adapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;

	private CardView card[];
	private Button tog[];
	private FrameLayout frame[];

	private Button tog1;
	private Button tog2;
	private Button tog3;
	private Button tog4;

	private boolean cardon[];

	private int descriptionViewFullHeight;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_beforestart);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		card = new CardView[NUMCARD];
		tog = new Button[NUMCARD];
		frame = new FrameLayout[NUMCARD];
		descriptionViewFullHeight = getResources().getDimensionPixelSize(R.dimen.max_cardexpand);

		card[0] = (CardView) findViewById(R.id.card1);
		card[1] = (CardView) findViewById(R.id.card2);
		card[2] = (CardView) findViewById(R.id.card3);
		card[3] = (CardView) findViewById(R.id.card4);

		tog[0] = (Button) findViewById(R.id.object1);
		tog[1] = (Button) findViewById(R.id.object2);
		tog[2] = (Button) findViewById(R.id.object3);
		tog[3] = (Button) findViewById(R.id.object4);

		cardon = new boolean[NUMCARD];
		for (int i = 0; i < NUMCARD; i++) {
			final CardView cv = card[i];
			final Button Togi = tog[i];
			final int fi = i;
			cv.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
				@Override
				public boolean onPreDraw() {
					Togi.getViewTreeObserver().removeOnPreDrawListener(this);
					// save the full height


					// initially changing the height to min height
					ViewGroup.LayoutParams layoutParams = cv.getLayoutParams();
					layoutParams.height = (int) Beforestart.this.getResources().getDimension(R.dimen.min_cardexpand);
					cv.setLayoutParams(layoutParams);

					return true;
				}
			});
			tog[i].setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Log.d(TAG, "card" + fi);
					CardtoggleManager(fi);
				}
			});
		}

		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				switch (getCurrentOpen()) {
					case 0:

						Intent myIntent = new Intent(Beforestart.this, MainActivity.class);
//				myIntent.putExtra("key", value); //Optional parameters
						Beforestart.this.startActivity(myIntent);
//				Snackbar.make(fab, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
						break;
					case 1:
						Snackbar.make(fab, "ยังไม่ได้ทำ :D", Snackbar.LENGTH_LONG).setAction("Action", null).show();
						break;
					case 2:
						Snackbar.make(fab, "ยังไม่ได้ทำ :D", Snackbar.LENGTH_LONG).setAction("Action", null).show();
						break;
					case 3:
						Snackbar.make(fab, "ยังไม่ได้ทำ :D", Snackbar.LENGTH_LONG).setAction("Action", null).show();
						break;

					case -1:
						Snackbar.make(fab, ":D", Snackbar.LENGTH_LONG).setAction("Action", null).show();
						break;
					default:
						break;
				}

			}
		});

	}

	private void CardtoggleManager(int num) {
		for (int i = 0; i < NUMCARD; i++) {
			if (cardon[i]) {
				if (num == i) {
					toggleProductDescriptionHeight(card[i]);
					tog[i].setBackgroundColor(getResources().getColor(R.color.Deep_Purple_300));
					cardon[i] = false;
					return;
				} else {
					toggleProductDescriptionHeight(card[i]);
					tog[i].setBackgroundColor(getResources().getColor(R.color.Deep_Purple_300));
					cardon[i] = false;
				}
			} else if (num == i) {
				toggleProductDescriptionHeight(card[i]);
				tog[i].setBackgroundColor(getResources().getColor(R.color.Deep_Purple_500));
				cardon[i] = true;
			}
		}
	}

	private void toggleProductDescriptionHeight(final CardView cv) {

		int descriptionViewMinHeight = (int) Beforestart.this.getResources().getDimension(R.dimen.min_cardexpand);
		if (cv.getHeight() == descriptionViewMinHeight) {
			// expand
			ValueAnimator anim = ValueAnimator.ofInt(cv.getMeasuredHeightAndState(), descriptionViewFullHeight);
			anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator valueAnimator) {
					int val = (Integer) valueAnimator.getAnimatedValue();
					ViewGroup.LayoutParams layoutParams = cv.getLayoutParams();
					layoutParams.height = val;
					cv.setLayoutParams(layoutParams);
				}
			});
			anim.start();
		} else {
			// collapse
			ValueAnimator anim = ValueAnimator.ofInt(cv.getMeasuredHeightAndState(), descriptionViewMinHeight);
			anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator valueAnimator) {
					int val = (Integer) valueAnimator.getAnimatedValue();
					ViewGroup.LayoutParams layoutParams = cv.getLayoutParams();
					layoutParams.height = val;
					cv.setLayoutParams(layoutParams);
				}
			});
			anim.start();
		}
	}

	private int getCurrentOpen() {
		for (int i = 0; i < NUMCARD; i++) {
			if (cardon[i]) return i;
		}
		return -1;
	}
}



