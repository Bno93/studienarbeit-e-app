package com.studienarbeit.dhbw.e_app.Main.Main;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.TextView;

import com.studienarbeit.dhbw.e_app.Main.Common.ActivityHandler;
import com.studienarbeit.dhbw.e_app.R;

import org.w3c.dom.Text;

public class UpdateUiService extends Service {
    private final IBinder mBinder = new UpdateUiBinder();


    public UpdateUiService() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }

    public class UpdateUiBinder extends Binder {
        UpdateUiService getService() {
            // Return this instance of LocalService so clients can call public methods
            return UpdateUiService.this;
        }
    }

    public void update(TextView batteryText, long updateIntervall, ActivityHandler activityHandler)
    {


        try{

            Thread.sleep(updateIntervall);
            int capacity = activityHandler.getBattery();
            //capacity = ((capacity == 0) ? 0 : (int) ((capacity - SpeedoValues.C.getValue() * 1000) * 100 / capacity));
            //capacity = ((capacity < 0) ? 0 : capacity);

            if (batteryText != null) {
                batteryText.setText(String.valueOf(capacity) + "%");
            }

        }catch (InterruptedException e)
        {
            e.getStackTrace();
        }
    }
}
