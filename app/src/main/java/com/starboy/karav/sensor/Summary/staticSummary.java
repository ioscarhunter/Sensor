package com.starboy.karav.sensor.Summary;

import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Chronometer;
import android.widget.RatingBar;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.TextRoundCornerProgressBar;
import com.starboy.karav.sensor.R;

public class staticSummary extends AppCompatActivity {

    private int time;
    private TextView level;
    private Chronometer timer;
    private TextRoundCornerProgressBar rating;
    private long totalCount;
    private long balanceCount;
    private double difficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static_summary);
        rating = (TextRoundCornerProgressBar) findViewById(R.id.process);
        getBundle();
        level = (TextView) findViewById(R.id.s_level);
        timer = (Chronometer) findViewById(R.id.s_timer);
        setUI();
    }


    private void getBundle() {
        Bundle bundle = this.getIntent().getExtras();
        time = bundle.getInt("Time");
//		Log.d(TAG, Integer.toString(time));
        totalCount = bundle.getLong("total");
        balanceCount = bundle.getLong("balance");
        difficulty = bundle.getDouble("difficult");
    }

    private void setUI() {

        updateProgress(rating);
        setTime();

        if (difficulty == 0) {
            level.setText("Level A");
        } else if (difficulty == 1) {
            level.setText("Level B");
        } else if (difficulty == 2) {
            level.setText("Level C");
        } else if (difficulty == 2.5) {
            level.setText("Level D");
        } else if (difficulty == 3) {
            level.setText("Level E");
        }
    }

    private void setTime() {
        timer.setBase(SystemClock.elapsedRealtime() - (time * 1000));
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

    private float getpercentage() {
        return ((balanceCount / (float) totalCount) * 100);
    }

    private void updateTextProgress(TextRoundCornerProgressBar process) {
        process.setProgressText((Math.round(process.getProgress() * 10) / 10.0) + "%");
    }

    private void updateProgress(TextRoundCornerProgressBar progress) {
        progress.setProgress(getpercentage());
        updateProgressColor(progress);
        updateTextProgress(progress);
    }
}
