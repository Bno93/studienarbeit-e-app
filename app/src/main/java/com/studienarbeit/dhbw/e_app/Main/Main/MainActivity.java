package com.studienarbeit.dhbw.e_app.Main.Main;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toast;


import com.studienarbeit.dhbw.e_app.Main.Bluetooth.DeviceProvider;
import com.studienarbeit.dhbw.e_app.Main.Common.ActivityHandler;
import com.studienarbeit.dhbw.e_app.Main.Settings.SettingsActivity;
import com.studienarbeit.dhbw.e_app.Main.Settings.SettingsElements;
import com.studienarbeit.dhbw.e_app.R;


public class MainActivity extends ActionBarActivity{

    private Button startButton;
    private Button pauseButton;
    private Button connectionButton;
    private TextView timerValue;
    private long startTime = 0L;
    private Handler customHandler = new Handler();
    private ActivityHandler activityHandler = ActivityHandler.getInstance();
    private DeviceProvider deviceProvider = DeviceProvider.getInstance();
    private UpdateUiService updateUiService;

    private final long updateIntervall = 500;

    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    boolean isPaused = false;
    boolean isStoped = false;
    final int REQUEST_ENABLE_BT = 2;
    boolean mBound = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Init des ActivityHandlers und des DeviceProviders
        activityHandler.setMainContext(this);
        deviceProvider.init();

        timerValue = (TextView) findViewById(R.id.timerValue);

        startButton = (Button) findViewById(R.id.startButton);
        pauseButton = (Button) findViewById(R.id.pauseButton);
        connectionButton = (Button) findViewById(R.id.bluetooth_connection);

        isPaused = false;
        isStoped = false;

        //Start\Stop Button
        startButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (!isPaused) {
                    // Start Button
                    if (startButton.getText() == getString(R.string.startButtonLabel)) {
                        isStoped = false;
                        startButton.setText(R.string.stopButtonLabel);
                        startButton.setBackgroundColor(getResources().getColor(R.color.red));
                        startTime = SystemClock.uptimeMillis();
                        customHandler.postDelayed(updateTimerThread, 0);

                    // Stop Button
                    } else if (startButton.getText() == getString(R.string.stopButtonLabel)) {
                        isStoped = true;
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

        // Button zum Verbinden mit dem Controller
        connectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBattery();
                // If already logged in asking for logout
//                if (deviceProvider.isLoggedIn()) {
//                    logout();
//
//                }
//                deviceProvider.login();

            }

            private void logout() {
                new AlertDialog.Builder(activityHandler.getMainContext())
                        .setTitle(R.string.dialog_disconnect_title)
                        .setMessage(R.string.dialog_disconnect)
                        .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                deviceProvider.logoutAndResetDevice();
                            }
                        }).setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).show();
            }
        });


//
    }//onCreate

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
            //Hier muss die SettingsActivity gestartet werden

            Intent intentSetting = new Intent(this, SettingsActivity.class);

            startActivity(intentSetting);
            return true;
        }
        else if (id == R.id.action_bluetooth){
            bluetoothTurnOn();
            //Toast.makeText(this, "Bluetooth einschalten", Toast.LENGTH_SHORT).show();

        }

        return super.onOptionsItemSelected(item);
    }//onOptionsItemSelected


    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            UpdateUiService.UpdateUiBinder binder = (UpdateUiService.UpdateUiBinder) service;
            updateUiService = binder.getService();
            Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
            mBound = false;

            TextView batteryText = (TextView)findViewById(R.id.battery_text);
            updateUiService.update(batteryText, updateIntervall, activityHandler);

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


    @Override
    protected void onStart() {
        super.onStart();
        activityHandler.add(this);
        Intent intent = new Intent(this, UpdateUiService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);




    }


    @Override
    protected void onResume() {
        super.onResume();
        activityHandler.add(this);

    }


    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
        activityHandler.del(this);
        if(mBound)
        {
            unbindService(mConnection);
            mBound = false;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (deviceProvider != null) {
            deviceProvider.unregisterReceiver();
            deviceProvider = null;
        }
    }

    //Timer Code
    public Runnable updateTimerThread = new Runnable() {

        public void run() {

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

    };

    private void bluetoothTurnOn()
    {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null)
        {
            if (!mBluetoothAdapter.isEnabled())
            {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }




    private void showBattery()
    {
        int capacity = activityHandler.getBattery();
        TextView battery_text = (TextView) findViewById(R.id.battery_text);
        capacity = ((capacity == 0) ? 0 : (int) ((capacity - SpeedoValues.C.getValue() * 1000) * 100 / capacity));
        Toast.makeText(this, capacity + "%", Toast.LENGTH_LONG).show();
        capacity = ((capacity < 0) ? 0 : capacity);
        battery_text.setText(capacity+" %");

    }


    private class UpdateGui extends Thread{
        //TODO Binded Service
        @Override
        public void run() {
            while(true)
            {
                try{
                    Thread.sleep(updateIntervall);
                    int capacity = activityHandler.getBattery();
                    //capacity = ((capacity == 0) ? 0 : (int) ((capacity - SpeedoValues.C.getValue() * 1000) * 100 / capacity));
                    //capacity = ((capacity < 0) ? 0 : capacity);
                    TextView battery_text = (TextView) findViewById(R.id.battery_text);
                    if (battery_text != null) {
                        battery_text.setText(String.valueOf(capacity) + "%");
                    }

                }catch (InterruptedException e)
                {
                    break;
                }
            }
        }
    }
}
