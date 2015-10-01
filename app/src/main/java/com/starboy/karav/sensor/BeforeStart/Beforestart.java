package com.starboy.karav.sensor.BeforeStart;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;

import com.starboy.karav.sensor.Meter.MainActivity;
import com.starboy.karav.sensor.R;

public class Beforestart extends AppCompatActivity {

	private RecyclerView mRecyclerView;
	private RecyclerView.Adapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_beforestart);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent myIntent = new Intent(Beforestart.this, MainActivity.class);
//				myIntent.putExtra("key", value); //Optional parameters
				Beforestart.this.startActivity(myIntent);
//				Snackbar.make(fab, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
			}
		});

		mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

		mRecyclerView.setHasFixedSize(true);

		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

		mLayoutManager = new CustomLayoutManager(Beforestart.this, Math.round(dpWidth), (int) (getResources().getDimension(R.dimen.max_recycler) / getResources().getDisplayMetrics().density));
		mRecyclerView.setLayoutManager(mLayoutManager);

		mAdapter = new CustomAdapter(Beforestart.this);
		mRecyclerView.setAdapter(mAdapter);
	}

}
