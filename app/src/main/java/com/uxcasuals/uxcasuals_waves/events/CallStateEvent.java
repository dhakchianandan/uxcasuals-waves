package com.uxcasuals.uxcasuals_waves.events;

/**
 * Created by Dhakchianandan on 27/09/15.
 */
public class CallStateEvent {
    public static final int PAUSE = 0;
    public int STATE;

    public CallStateEvent(int STATE) {
        this.STATE = STATE;
    }
}
