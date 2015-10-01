package com.starboy.karav.sensor.BeforeStart;

/**
 * Created by Karav on 5/23/2015.
 * custom view on Database list activity
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.starboy.karav.sensor.R;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {


	private Context mContext;
	private View v;

	public CustomAdapter(Context context) {
		mContext = context;
	}

	public int getItemWidth() {
		return v.getWidth();
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		v = LayoutInflater.from(mContext).inflate(R.layout.content_funtionselect, parent, false);
		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int position) {
//		Flight flight = mPlayers.get(position);
//		viewHolder.level.setText(v.getResources().getString(getCharLevel(flight.getLevel())));
//		viewHolder.rating.setNumStars(flight.getRating());
//		viewHolder.timer.setBase(SystemClock.elapsedRealtime() - flight.getTakeTime());
//		viewHolder.colourIndicator.setBackgroundResource(getIndicator(flight.getRating()));
//		viewHolder.percent.setText(flight.getPercent() + "%");
//		viewHolder.date.setText(flight.getReadableDate());

	}

//	private int getCharLevel(int level) {
//		if (level == 1) return R.string.level_1;
//		if (level == 2) return R.string.level_2;
//		if (level == 3) return R.string.level_3;
//		if (level == 4) return R.string.level_4;
//		else return R.string.level_5;
//	}
//
//	private int getIndicator(int colour) {
//		if (colour == 1) return R.drawable.circle_l1;
//		if (colour == 2) return R.drawable.circle_l2;
//		if (colour == 3) return R.drawable.circle_l3;
//		if (colour == 4) return R.drawable.circle_l4;
//		else return R.drawable.circle_l5;
//
//	}

	@Override
	public int getItemCount() {
		return 0;
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		//		public TextView level;
//		public RatingBar rating;
//		public Chronometer timer;
//		public TextView colourIndicator;
//		public TextView percent;
//		public TextView date;
		private View v;

		public ViewHolder(View view) {
			super(view);
			v = view;
//			level = (TextView) v.findViewById(R.id.DBLevel);
//			rating = (RatingBar) v.findViewById(R.id.DBratingBar);
//			timer = (Chronometer) v.findViewById(R.id.DBTime);
//			colourIndicator = (TextView) v.findViewById(R.id.colour_indecator);
//			percent = (TextView) v.findViewById(R.id.percentTV);
//			date = (TextView) v.findViewById(R.id.dateTV);
		}


	}
}