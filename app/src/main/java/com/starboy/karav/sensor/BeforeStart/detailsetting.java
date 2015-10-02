package com.starboy.karav.sensor.BeforeStart;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.starboy.karav.sensor.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link detailsetting.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link detailsetting#newInstance} factory method to
 * create an instance of this fragment.
 */
public class detailsetting extends Fragment {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	// TODO: Rename and change types of parameters
	private int function;

	private View view;

	private OnFragmentInteractionListener mListener;

	private RelativeLayout twowaysSetting;
	private RelativeLayout onewaySetting;
	private LinearLayout levelselector;
	private RelativeLayout stilltimer;

	private TextView stilltime_tv;
	private TextView totaltime_tv;
	private Button plus_still;
	private Button minus_still;

	private Button plus_total;
	private Button minus_total;

	private Chronometer stilltime;
	private Chronometer totaltime;

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param function function set
	 * @return A new instance of fragment detailsetting.
	 */
	// TODO: Rename and change types and number of parameters
	public static detailsetting newInstance(int function) {
		detailsetting fragment = new detailsetting();
		Bundle args = new Bundle();
		args.putInt(ARG_PARAM1, function);
		fragment.setArguments(args);
		return fragment;
	}

	public detailsetting() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments() != null) {
			function = getArguments().getInt(ARG_PARAM1);
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		view = inflater.inflate(R.layout.fragment_detailsetting, container, false);
		twowaysSetting = (RelativeLayout) view.findViewById(R.id.twoways);
		onewaySetting = (RelativeLayout) view.findViewById(R.id.oneway);
		levelselector = (LinearLayout) view.findViewById(R.id.noway);
		stilltime_tv = (TextView) view.findViewById(R.id.stilltime_tv);
		stilltimer = (RelativeLayout) view.findViewById(R.id.stilltimeset);

		switch (function) {
			case 0:
				onewaySetting.setVisibility(View.GONE);
				twowaysSetting.setVisibility(View.GONE);
				levelselector.setVisibility(View.GONE);
				break;
			case 1:
				onewaySetting.setVisibility(View.GONE);
				levelselector.setVisibility(View.GONE);
				break;
			case 2:
				twowaysSetting.setVisibility(View.GONE);
				levelselector.setVisibility(View.GONE);
				break;
			case 3:
				onewaySetting.setVisibility(View.GONE);
				twowaysSetting.setVisibility(View.GONE);
				stilltimer.setVisibility(View.GONE);
				stilltime_tv.setVisibility(View.GONE);
				break;
		}
		return view;
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			mListener.onFragmentInteraction(uri);
		}
	}


	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p/>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		void onFragmentInteraction(Uri uri);
	}

}
