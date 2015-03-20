package com.studienarbeit.dhbw.e_app.Main.Bluetooth;


import com.studienarbeit.dhbw.e_app.R;
import com.studienarbeit.dhbw.e_app.Main.Common.ActivityHandler;


public enum BluetoothInfoState {

    NONE(R.string.bt_info_none),
    INITIALIZED(R.string.bt_info_initialized),
    ON(R.string.bt_info_on),
    DEVICE_KNOWN(R.string.bt_info_known_device),
    PAIRED(R.string.bt_info_paired),
    CONNECTION_FAILED(R.string.bt_info_connection_failed),
    ACL_CONNECTED(R.string.bt_info_acl_connected),
    LOGIN_TIMEOUT(R.string.bt_info_login_timeout),
    LOGGED_IN(R.string.bt_info_logged_in),
    ACL_DISCONNECTED(R.string.bt_info_acl_disconnected);

    private int textRes;

    // Constructor
    private BluetoothInfoState(int theText) {
        textRes = theText;
    }

    @Override
    // Returns the text
    public String toString() {
        return ActivityHandler.getInstance().getStr(textRes);
    }
}
