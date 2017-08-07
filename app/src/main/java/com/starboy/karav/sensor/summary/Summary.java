package com.starboy.karav.sensor.summary;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.TextRoundCornerProgressBar;
import com.starboy.karav.sensor.R;

public class Summary extends AppCompatActivity {
    String TAG = "Summary";

    private TextRoundCornerProgressBar progressLeft;
    private TextRoundCornerProgressBar progressRight;
    private TextRoundCornerProgressBar progressUp;
    private TextRoundCornerProgressBar progressDown;

    private CardView cardLeft;
    private CardView cardRight;
    private CardView cardUp;
    private CardView cardDown;

    private TextView avgLeft;
    private TextView avgRight;
    private TextView avgUp;
    private TextView avgDown;

    private TextView maxLeft;
    private TextView maxRight;
    private TextView maxUp;
    private TextView maxDown;

    private TextView count;
    private Chronometer timer_total;

    private float[] angle_unbalance;
    private int[] unbalanceCount;
    private int[] totalCount;

    private int[] instructionSet;

    private long Time;
    private int countGoal;

    private TextView textLeft;
    private TextView textRight;
    private TextView textUp;
    private TextView textDown;


    private void assignBundle() {
        Bundle b = this.getIntent().getExtras();
        totalCount = b.getIntArray("t_count");
        unbalanceCount = b.getIntArray("u_count");
        angle_unbalance = b.getFloatArray("angle");
        countGoal = b.getInt("goal");
        Time = b.getLong("Time");
        instructionSet = b.getIntArray("inst");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        assignBundle();

        progressLeft = (TextRoundCornerProgressBar) findViewById(R.id.leftprocess);
        progressRight = (TextRoundCornerProgressBar) findViewById(R.id.rightprocess);
        progressUp = (TextRoundCornerProgressBar) findViewById(R.id.upprocess);
        progressDown = (TextRoundCornerProgressBar) findViewById(R.id.downprocess);

        cardLeft = (CardView) findViewById(R.id.leftcard);
        cardRight = (CardView) findViewById(R.id.rightcard);
        cardUp = (CardView) findViewById(R.id.upcard);
        cardDown = (CardView) findViewById(R.id.downcard);

        avgLeft = (TextView) findViewById(R.id.leftavg);
        avgRight = (TextView) findViewById(R.id.rightavg);
        avgUp = (TextView) findViewById(R.id.upavg);
        avgDown = (TextView) findViewById(R.id.downavg);

        textLeft = (TextView) findViewById(R.id.left_t);
        textRight = (TextView) findViewById(R.id.right_t);
        textUp = (TextView) findViewById(R.id.up_t);
        textDown = (TextView) findViewById(R.id.down_t);

        count = (TextView) findViewById(R.id.countsummary);
        timer_total = (Chronometer) findViewById(R.id.timesummary);
        count.setText(String.valueOf(countGoal));
        timer_total.setBase(SystemClock.elapsedRealtime() - Time);

        setProgressVisible();
        setprogress();
    }

    private void setProgressVisible() {
        cardUp.setVisibility(View.GONE);
        cardDown.setVisibility(View.GONE);
        cardLeft.setVisibility(View.GONE);
        cardRight.setVisibility(View.GONE);

        textUp.setVisibility(View.GONE);
        textDown.setVisibility(View.GONE);
        textLeft.setVisibility(View.GONE);
        textRight.setVisibility(View.GONE);
        for (int i : instructionSet) {
            switch (i) {
                case 0:
                    cardUp.setVisibility(View.VISIBLE);
                    textUp.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    cardDown.setVisibility(View.VISIBLE);
                    textDown.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    cardLeft.setVisibility(View.VISIBLE);
                    textLeft.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    cardRight.setVisibility(View.VISIBLE);
                    textRight.setVisibility(View.VISIBLE);
                    break;

            }
        }
    }

    private void setprogress() {
        Log.d(TAG, String.valueOf((angle_unbalance[2])));
        avgLeft.setText(String.valueOf(getavg(2)));
        updateProgress(progressLeft, 2);

        Log.d(TAG, String.valueOf(angle_unbalance[3]));
        avgRight.setText(String.valueOf(getavg(3)));
        updateProgress(progressRight, 3);

        avgDown.setText(String.valueOf(getavg(1)));
        updateProgress(progressDown, 1);

        avgUp.setText(String.valueOf(getavg(0)));
        updateProgress(progressUp, 0);
    }

    private void updateProgress(TextRoundCornerProgressBar progress, int numprogress) {
        progress.setProgress(getpercentage(numprogress));
        updateProgressColor(progress);
        updateTextProgress(progress);
    }

    private void updateProgressColor(TextRoundCornerProgressBar process) {
        float progress = process.getProgress();
        if (progress <= 25) {
            process.setProgressColor(getResources().getColor(R.color.custom_progress_red_progress));
        } else if (progress > 25 && progress <= 50) {
            process.setProgressColor(getResources().getColor(R.color.custom_progress_orange_progress));
        } else if (progress > 50 && progress <= 75) {
            process.setProgressColor(getResources().getColor(R.color.custom_progress_yellow_progress));
        } else if (progress > 75) {
            process.setProgressColor(getResources().getColor(R.color.custom_progress_green_progress));
        }
    }

    private float getpercentage(int numprogress) {
        return (100 - (unbalanceCount[numprogress] / (float) totalCount[numprogress]) * 100);
    }

    private void updateTextProgress(TextRoundCornerProgressBar process) {
        process.setProgressText((Math.round(process.getProgress() * 10) / 10.0) + "%");
    }

    private double getavg(int numprogress) {
        if (unbalanceCount[numprogress] > 0) {
            return (Math.abs(Math.round((angle_unbalance[numprogress] / unbalanceCount[numprogress]) * 10) / 10.0));
        } else return 0;
    }


}
