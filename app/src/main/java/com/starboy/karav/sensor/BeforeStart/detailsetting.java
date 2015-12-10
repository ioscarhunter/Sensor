package com.starboy.karav.sensor.BeforeStart;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
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
 * {@link DetailSetting.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailSetting#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailSetting extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int function;

    private int[] instruct;
    private double[] bound;
    private double difficult;

    private View view;

    private OnFragmentInteractionListener mListener;

    private RelativeLayout twowaysSetting;
    private RelativeLayout onewaySetting;
    private LinearLayout levelselector;
    private RelativeLayout stilltimer;

    private TextView stilltime_tv;
    private TextView totaltime_tv;

    private long time_total;
    private long time_still;

    private Chronometer chrono_total;
    private Chronometer chrono_still;

    private Button plus_total;
    private Button minus_total;

    private Button plus_still;
    private Button minus_still;

    private Button level1;
    private Button level2;
    private Button level3;
    private Button level4;
    private Button level5;

    private Button set_UD;
    private Button set_LR;

    private Button set_U;
    private Button set_D;
    private Button set_L;
    private Button set_R;

    private int level;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param function function set
     * @return A new instance of fragment DetailSetting.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailSetting newInstance(int function) {
        DetailSetting fragment = new DetailSetting();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, function);
        fragment.setArguments(args);
        return fragment;
    }

    public DetailSetting() {
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

        chrono_total = (Chronometer) view.findViewById(R.id.countdown_total);
        plus_total = (Button) view.findViewById(R.id.plus_but_total);
        minus_total = (Button) view.findViewById(R.id.minus_but_total);

        chrono_still = (Chronometer) view.findViewById(R.id.countdown_still);
        plus_still = (Button) view.findViewById(R.id.plus_but_still);
        minus_still = (Button) view.findViewById(R.id.minus_but_still);

        set_UD = (Button) view.findViewById(R.id.updown);
        set_LR = (Button) view.findViewById(R.id.leftright);

        set_L = (Button) view.findViewById(R.id.left);
        set_R = (Button) view.findViewById(R.id.right);
        set_U = (Button) view.findViewById(R.id.up);
        set_D = (Button) view.findViewById(R.id.down);

        difficult = 1;

        time_total = 3 * 1000 * 60;
        time_still = 3 * 1000;

        chrono_total.setBase(SystemClock.elapsedRealtime() - time_total);
        chrono_still.setBase(SystemClock.elapsedRealtime() - time_still);

        setButton();

//		instruct = new int[]{0, 1, 2, 3};
        switch (function) {
            case 0:
                onewaySetting.setVisibility(View.GONE);
                twowaysSetting.setVisibility(View.GONE);
                levelselector.setVisibility(View.GONE);
                instruct = new int[]{0, 1, 2, 3};
                bound = new double[]{8, -4, 8, -8};
                difficult = 0.5;
                updateSet();
                break;
            case 1:
                onewaySetting.setVisibility(View.GONE);
                levelselector.setVisibility(View.GONE);
                bound = new double[]{8, -4, 8, -8};
                difficult = 0.5;
                setTwo(0);
                break;
            case 2:
                twowaysSetting.setVisibility(View.GONE);
                levelselector.setVisibility(View.GONE);
                instruct = new int[]{0};
                difficult = 0.5;
                setOne(0);
                break;
            case 3:
                onewaySetting.setVisibility(View.GONE);
                twowaysSetting.setVisibility(View.GONE);
                stilltimer.setVisibility(View.GONE);
                stilltime_tv.setVisibility(View.GONE);
                setLevel(1);
                break;
        }

        return view;
    }

    private void setTwo(int i) {
        if (i == 0) {
            instruct = new int[]{0, 1};
//			set_LR.setBackgroundColor(getResources().getColor(R.color.white_pure));
            set_LR.setTextColor(getResources().getColor(R.color.black_alpha));

            set_UD.setTextColor(getResources().getColor(R.color.green_A400));
//			set_UD.setBackgroundResource(R.drawable.circle_l5);
            updateSet();
        } else if (i == 1) {
            instruct = new int[]{2, 3};
//			set_UD.setBackgroundColor(getResources().getColor(R.color.white_pure));
            set_UD.setTextColor(getResources().getColor(R.color.black_alpha));

            set_LR.setTextColor(getResources().getColor(R.color.green_A400));
//			set_LR.setBackgroundResource(R.drawable.circle_l5);
            updateSet();
        }

    }

    private void setOne(int i) {
        clearOne(instruct[0]);
        switch (i) {
            case 0:
                instruct = new int[]{0, 1};
                bound = new double[]{8, 0, 8, -8};
                set_U.setTextColor(getResources().getColor(R.color.green_A400));
//				set_U.setBackgroundResource(R.drawable.circle_l5);
                break;
            case 1:
                instruct = new int[]{1, 0};
                bound = new double[]{0, -4, 8, -8};
                set_D.setTextColor(getResources().getColor(R.color.green_A400));
//				set_D.setBackgroundResource(R.drawable.circle_l5);
                break;
            case 2:
                instruct = new int[]{2, 3};
                bound = new double[]{8, -4, 0, -8};
                set_L.setTextColor(getResources().getColor(R.color.green_A400));
//				set_L.setBackgroundResource(R.drawable.circle_l5);
                break;
            case 3:
                instruct = new int[]{3, 2};
                bound = new double[]{8, -4, 8, 0};
                set_R.setTextColor(getResources().getColor(R.color.green_A400));
//				set_R.setBackgroundResource(R.drawable.circle_l5);
                break;
        }
        updateSet();
    }

    private void clearOne(int i) {
        switch (i) {
            case 0:
                set_U.setTextColor(getResources().getColor(R.color.black_alpha));
//				set_U.setBackgroundColor(getResources().getColor(R.color.white_pure));
                break;
            case 1:
                set_D.setTextColor(getResources().getColor(R.color.black_alpha));
//				set_D.setBackgroundColor(getResources().getColor(R.color.white_pure));
                break;
            case 2:
                set_L.setTextColor(getResources().getColor(R.color.black_alpha));
//				set_L.setBackgroundColor(getResources().getColor(R.color.white_pure));
                break;
            case 3:
                set_R.setTextColor(getResources().getColor(R.color.black_alpha));
//				set_R.setBackgroundColor(getResources().getColor(R.color.white_pure));
                break;
        }
    }

    private void setButton() {

        set_LR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTwo(1);
            }
        });

        set_UD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTwo(0);
            }
        });

        set_L.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setOne(2);
            }
        });

        set_R.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setOne(3);
            }
        });
        set_U.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setOne(0);
            }
        });
        set_D.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setOne(1);
            }
        });

        level1 = (Button) view.findViewById(R.id.level1);
        level1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLevel(1);
            }
        });
        level2 = (Button) view.findViewById(R.id.level2);
        level2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLevel(2);
            }
        });
        level3 = (Button) view.findViewById(R.id.level3);
        level3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLevel(3);
            }
        });
        level4 = (Button) view.findViewById(R.id.level4);
        level4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLevel(4);
            }
        });
        level5 = (Button) view.findViewById(R.id.level5);
        level5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLevel(5);
            }
        });
//        start.setBackgroundColor(getResources().getColor(R.color.black));

        plus_total.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (time_total == (1 * 1000 * 60)) {
                    minus_total.setTextColor(getResources().getColor(R.color.blue_A400));
                }
                if (time_total < (59 * 1000 * 60)) {
                    time_total += (1 * 1000 * 60);
                    if (time_total == (59 * 1000 * 60)) {
                        plus_total.setTextColor(getResources().getColor(R.color.grey_500));
                    }
                    chrono_total.setBase(SystemClock.elapsedRealtime() - time_total);
                    updateSet();
                }
            }
        });

        minus_total.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (time_total == (59 * 1000 * 60)) {
                    plus_total.setTextColor(getResources().getColor(R.color.orange_A700));
                }
                if (time_total > (1 * 1000 * 60)) {
                    time_total -= 1 * 1000 * 60;
                    if (time_total == (1 * 1000 * 60)) {
                        minus_total.setTextColor(getResources().getColor(R.color.grey_500));
                    }
                    chrono_total.setBase(SystemClock.elapsedRealtime() - time_total);
                    updateSet();
                }
            }
        });

        plus_still.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (time_still == (1 * 1000)) {
                    minus_still.setTextColor(getResources().getColor(R.color.blue_A400));
                }
                if (time_still < (59 * 1000)) {
                    time_still += 1 * 1000;
                    if (time_still == (59 * 1000)) {
                        plus_still.setTextColor(getResources().getColor(R.color.grey_500));
                    }
                    chrono_still.setBase(SystemClock.elapsedRealtime() - time_still);
                    updateSet();
                }
            }
        });

        minus_still.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (time_still == (59 * 1000)) {
                    plus_still.setTextColor(getResources().getColor(R.color.orange_A700));
                }
                if (time_still > (1 * 1000)) {
                    time_still -= 1 * 1000;
                    if (time_still == (1 * 1000)) {
                        minus_still.setTextColor(getResources().getColor(R.color.grey_500));
                    }
                    chrono_still.setBase(SystemClock.elapsedRealtime() - time_still);
                    updateSet();
                }
            }
        });
    }

    private void updateSet() {
        ((BeforeStart) getActivity()).updateSetting(function, instruct, bound, difficult, time_still, time_total);
    }

    private void setLevel(int level) {
        resetColour();
        this.level = level;
        updateSet();
        switch (level) {
            case 1:
                level1.setBackgroundResource(R.drawable.circle_l1);
                level1.setTextColor(getResources().getColor(R.color.white_pure));
//				setColourAnimation(levelSelector, currentColour, R.color.c_l1, bgnAnimDuration);
//				currentColour = R.color.c_l1;
//                status_level.setBackgroundColor(getResources().getColor(R.color.c_l1));
//				setColourAnimation(level1, R.color.clear, R.color.c_l1d, buttonAnimDuration);
                break;
            case 2:
                level2.setBackgroundResource(R.drawable.circle_l2);
                level2.setTextColor(getResources().getColor(R.color.white_pure));
//				setColourAnimation(levelSelector, currentColour, R.color.c_l2, bgnAnimDuration);
//				currentColour = R.color.c_l2;
//				setColourAnimation(level2, R.color.clear, R.color.c_l2d, buttonAnimDuration);
                break;
            case 3:
                level3.setBackgroundResource(R.drawable.circle_l3);
                level3.setTextColor(getResources().getColor(R.color.white_pure));
//				setColourAnimation(levelSelector, currentColour, R.color.c_l3, bgnAnimDuration);
//				currentColour = R.color.c_l3;
//				setColourAnimation(level3, R.color.clear, R.color.c_l3d, buttonAnimDuration);
                break;
            case 4:
                level4.setBackgroundResource(R.drawable.circle_l4);
                level4.setTextColor(getResources().getColor(R.color.white_pure));
//				setColourAnimation(levelSelector, currentColour, R.color.c_l4, bgnAnimDuration);
//				currentColour = R.color.c_l4;
//				setColourAnimation(level4, R.color.clear, R.color.c_l4d, buttonAnimDuration);
                break;
            case 5:
                level5.setBackgroundResource(R.drawable.circle_l5);
                level5.setTextColor(getResources().getColor(R.color.white_pure));
//				setColourAnimation(levelSelector, currentColour, R.color.c_l5, bgnAnimDuration);
//				currentColour = R.color.c_l5;
//				setColourAnimation(level5, R.color.clear, R.color.c_l5d, buttonAnimDuration);
                break;
            default:
                break;
        }
    }

    private void resetColour() {
        level1.setBackgroundResource(0);
        level2.setBackgroundResource(0);
        level3.setBackgroundResource(0);
        level4.setBackgroundResource(0);
        level5.setBackgroundResource(0);
        level1.setTextColor(getResources().getColor(R.color.black));
        level2.setTextColor(getResources().getColor(R.color.black));
        level3.setTextColor(getResources().getColor(R.color.black));
        level4.setTextColor(getResources().getColor(R.color.black));
        level5.setTextColor(getResources().getColor(R.color.black));


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
