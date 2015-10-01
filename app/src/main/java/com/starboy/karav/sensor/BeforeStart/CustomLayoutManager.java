package com.starboy.karav.sensor.BeforeStart;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

public class CustomLayoutManager extends LinearLayoutManager {
	private int mParentWidth;
	private int mItemWidth;

	public CustomLayoutManager(Context context, int parentWidth, int itemWidth) {
		super(context);
		mParentWidth = parentWidth;
		mItemWidth = itemWidth;
	}

	@Override
	public int getPaddingLeft() {
		if (mParentWidth > mItemWidth) return Math.round(mParentWidth / 2f - mItemWidth / 2f);
		else return 0;
	}

	@Override
	public int getPaddingRight() {
		return getPaddingLeft();
	}
}