package com.uxcasuals.uxcasuals_waves.utils;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.uxcasuals.uxcasuals_waves.events.CallStateEvent;

/**
 * Created by Dhakchianandan on 27/09/15.
 */
public class CallStateHandler extends PhoneStateListener {

    public CallStateHandler() {
        EventHelper.getInstance().register(this);
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
            case TelephonyManager.CALL_STATE_OFFHOOK:
                EventHelper.getInstance().post(new CallStateEvent(CallStateEvent.PAUSE));
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                break;

        }
        super.onCallStateChanged(state, incomingNumber);
    }
}
