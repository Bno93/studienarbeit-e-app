package com.studienarbeit.dhbw.e_app.Main.Main;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.studienarbeit.dhbw.e_app.R;


public class MainActivity extends ActionBarActivity{

    private Button startButton;
    private Button pauseButton;
    private TextView timerValue;
    private long startTime = 0L;
    private Handler customHandler = new Handler();

    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    boolean isPaused = false;
    boolean isStoped = false;
    boolean running = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerValue = (TextView) findViewById(R.id.timerValue);

        startButton = (Button) findViewById(R.id.startButton);

        pauseButton = (Button) findViewById(R.id.pauseButton);



        //Start/Stop Button
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (!isPaused) {
                    // Start Button
                    if (startButton.getText() == getString(R.string.startButtonLabel)) {
                        isStoped = false;
                        running = true;
                        startButton.setText(R.string.stopButtonLabel);
                        startButton.setBackgroundColor(getResources().getColor(R.color.red));
                        startTime = SystemClock.uptimeMillis();
                        customHandler.postDelayed(updateTimerThread, 0);

                    // Stop Button
                    } else if (startButton.getText() == getString(R.string.stopButtonLabel)) {
                        isStoped = true;
                        running = false;
                        startButton.setText(R.string.startButtonLabel);
                        startButton.setBackgroundColor(getResources().getColor(R.color.green));
                        startTime = 0L;
                        updatedTime = 0L;
                        timeSwapBuff = 0L;
                        timerValue.setText("0:00:000");
                        customHandler.removeCallbacks(updateTimerThread);

                    }
                }
                else{
                    isPaused = false;
                    startButton.setText(R.string.stopButtonLabel);
                    startButton.setBackgroundColor(getResources().getColor(R.color.red));
                    startTime = SystemClock.uptimeMillis();
                    customHandler.postDelayed(updateTimerThread, 0);
                }
            }
        });



        //Pause Button
        pauseButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (!isStoped) {
                    isPaused = true;
                    startButton.setText(R.string.startButtonLabel);
                    startButton.setBackgroundColor(getResources().getColor(R.color.green));
                    timeSwapBuff += timeInMilliseconds;
                    customHandler.removeCallbacks(updateTimerThread);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public Runnable updateTimerThread = new Runnable() {

        public void run() {

            if (running)
            {
                timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

                updatedTime = timeSwapBuff + timeInMilliseconds;

                int secs = (int) (updatedTime / 1000);
                int mins = secs / 60;
                secs = secs % 60;
                int milliseconds = (int) (updatedTime % 1000);
                timerValue.setText("" + mins + ":"
                        + String.format("%02d", secs) + ":"
                        + String.format("%03d", milliseconds));
                customHandler.postDelayed(this, 0);
            }
        }

    };
}
